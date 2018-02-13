package com.exadel.eom.cms.service.storage;

import com.exadel.eom.cms.util.Consts;
import com.exadel.eom.cms.util.CopyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapUtils;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.uid;

public final class StorageLdapImpl implements Storage {

    private static final Integer MRT_MS = 10000;

    public static final String PHOTO = "jpegPhoto";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private LdapTemplate ldapTemplate;

    private String url;

    private String baseDn;

    private String userSearchFilter;

    @Override
    public void initialize(Map<String, String> params) {
        url = params.get("url");
        baseDn = params.get("userSearchBase");
        userSearchFilter = params.get("userSearchFilter");
        try {
            // Setup the LDAP client (normally done via Spring context file).
            LdapContextSource contextSource = new LdapContextSource();
            contextSource.setUrl(url);
            contextSource.setBase(baseDn);
            contextSource.setUserDn(params.get("userDn"));
            contextSource.setPassword(params.get("password"));
            contextSource.afterPropertiesSet();

            ldapTemplate = new LdapTemplate(contextSource);
            ldapTemplate.afterPropertiesSet();
        } catch (Exception e) {
            log.error("Can't connect to LDAP url: " + url, e);
        }
    }

    @Override
    public void close() {
        // stub
    }

    @Override
    public InputStream getResource(String path) {
        String[] pathArr = path.split(Consts.File.PATH_DELIMITER);
        if (pathArr.length == 2) {
            return getLdapAttribute(pathArr[0], pathArr[1]);
        } else {
            if(log.isWarnEnabled()) log.warn("Bad resource path: [" + path + "] LDAP url:" + url);
        }
        return null;
    }

    private InputStream getLdapAttribute(final String uid, final String attrName) {
        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        sc.setTimeLimit(MRT_MS);
        sc.setCountLimit(1);
        sc.setReturningAttributes(new String[]{attrName});

        StringBuilder filter = new StringBuilder();
        filter.append("(&(objectclass=person)").append(MessageFormat.format(userSearchFilter, uid)).append(")");

        if (PHOTO.equalsIgnoreCase(attrName)) {
            List<byte[]> value = ldapTemplate.search(LdapUtils.emptyLdapName(), filter.toString(), sc,
                    (Attributes attrs) -> {
                        try {
                            return (byte[]) attrs.get(attrName).get();
                        } catch (Exception e) {
                            log.error("Can't get attribute: [" + attrName + "], uid:" + uid);
                            return null;
                        }
                    });
            if (value.size() == 0) {
                log.info("Can't find attribute: [" + attrName + "], uid:" + uid);
                return null;
            } else {
                return new ByteArrayInputStream(value.get(0));
            }
        } else {
            List<String> value = ldapTemplate.search(LdapUtils.emptyLdapName(), filter.toString(), sc,
                    (Attributes attrs) -> {
                        try {
                            return (String) attrs.get(attrName).get();
                        } catch (Exception e) {
                            log.error("Can't get attribute: [" + attrName + "], uid:" + uid);
                            return null;
                        }
                    }
            );
            if (value.size() == 0) {
                return null;
            } else {
                String s = value.get(0);
                if (s != null) {
                    try {
                        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8.name()));
                    } catch (Exception e) {
                        log.error("Can't encode attribute: [" + attrName + "] to UTF-8 string, uid:" + uid);
                        return null;
                    }
                } else {
                    if(log.isInfoEnabled()) log.info("Can't find attribute: [" + attrName + "], uid:" + uid);
                    return null;
                }
            }
        }
    }

    @Override
    public String getMimeType(String path) {
        String[] pathArr = path.split(Consts.File.PATH_DELIMITER);
        if (pathArr.length == 2) {
            if (PHOTO.equalsIgnoreCase(pathArr[1])) {
                return Consts.MimeType.JPEG;
            } else {
                return Consts.MimeType.TEXT;
            }
        }
        return Consts.MimeType.BIN;
    }

    @Override
    public String getHash(String path) {
        String[] pathArr = path.split(Consts.File.PATH_DELIMITER);
        if (pathArr.length == 2) {
            InputStream is = getLdapAttribute(pathArr[0], pathArr[1]);
            if (is != null) {
                try {
                    StringBuilder hashb = new StringBuilder();
                    CopyUtil.calcHexHash(is, Consts.DIGEST_ALG, hashb);
                    return hashb.toString();
                } catch (Exception e) {
                    log.error("Read stream fail for LDAP attribute [" + pathArr[1] + "] uid: " + pathArr[0], e);
                } finally {
                    try {
                        is.close();
                    } catch (Exception e) {
                        log.error("Close stream fail for LDAP attribute [" + pathArr[1] + "] uid: " + pathArr[0], e);
                    }
                }
            }
        } else {
            if(log.isWarnEnabled()) log.warn("Bad resource path: [" + path + "] LDAP url:" + url);
        }

        return null;
    }

    @Override
    public String list(String path) {
        String[] pathArr = new String[0];
        if (!path.isEmpty()) {
            pathArr = path.split(Consts.File.PATH_DELIMITER);
        }

        if (pathArr.length == 1) {
            final String uid = pathArr[0];
            SearchControls sc = new SearchControls();
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            sc.setTimeLimit(MRT_MS);
            sc.setCountLimit(1);
            //sc.setReturningAttributes(new String[]{attrName});

            StringBuilder filter = new StringBuilder();
            filter.append("(&(objectclass=person)").append(MessageFormat.format(userSearchFilter, uid)).append(")");
            List<Map<String, String>> value = ldapTemplate.search(LdapUtils.emptyLdapName(), filter.toString(), sc,
                    (Attributes attrs) -> {
                            Map<String, String> attrMap = new HashMap<>();
                            NamingEnumeration<String> attrIds = attrs.getIDs();
                            while (attrIds.hasMoreElements()) {
                                String attrName = "";
                                try {
                                    attrName = attrIds.next();
                                    Object data = attrs.get(attrName).get();
                                    if (data instanceof String) {
                                        attrMap.put(attrName, (String) attrs.get(attrName).get());
                                    } else {
                                        if (data == null) {
                                            attrMap.put(attrName, "null");
                                        } else {
                                            attrMap.put(attrName, "data");
                                        }
                                    }
                                } catch (Exception e) {
                                    log.error("Can't get attribute: [" + attrName + "], uid:" + uid);
                                    return null;
                                }
                            }
                            return attrMap;
                    }
            );
            final StringBuilder sb = new StringBuilder();
            for (Map<String,String> v : value) {
                sb.append(Consts.Json.BRT_OPN);

                boolean bComma = false;
                for (Map.Entry<String, String> entry : v.entrySet()) {
                    if(bComma) {
                        sb.append(Consts.Json.CMA);
                    } else {
                        bComma = true;
                    }
                    sb.append(Consts.Json.BR_OPN);

                    sb.append(Consts.Json.QT).append(entry.getKey()).append(Consts.Json.QT).append(Consts.Json.COL)
                            .append(Consts.Json.QT).append(entry.getValue()).append(Consts.Json.QT);

                    sb.append(Consts.Json.BR_CLS);
                }
                sb.append(Consts.Json.BRT_CLS);
            }
            return sb.toString();
        } else if (pathArr.length == 0) {
            final String attrName = "uid";
            SearchControls sc = new SearchControls();
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            sc.setTimeLimit(MRT_MS);
            //sc.setCountLimit(1);
            sc.setReturningAttributes(new String[]{attrName});

            StringBuilder filter = new StringBuilder();
            filter.append("(&(objectclass=person))");

            List<String> value = ldapTemplate.search(LdapUtils.emptyLdapName(), filter.toString(), sc,
                    (Attributes attrs) -> {
                        try {
                            return (String) attrs.get(attrName).get();
                        } catch (Exception e) {
                            log.error("Can't get attribute: [" + attrName + "]");
                            return null;
                        }
                    }
            );

            final StringBuilder sb = new StringBuilder();
            sb.append(Consts.Json.BRT_OPN);

            boolean bComma = false;
            for (String v: value) {
                if(bComma) {
                    sb.append(Consts.Json.CMA);
                } else {
                    bComma = true;
                }
                sb.append(Consts.Json.BR_OPN);

                sb.append(Consts.Json.QT).append(attrName).append(Consts.Json.QT).append(Consts.Json.COL)
                        .append(Consts.Json.QT).append(v).append(Consts.Json.QT);

                sb.append(Consts.Json.BR_CLS);
            }
            sb.append(Consts.Json.BRT_CLS);
            return sb.toString();
        } else {
            if(log.isWarnEnabled()) log.warn("Bad resource path: [" + path + "] LDAP url:" + url);
        }
        return null;
    }
}
