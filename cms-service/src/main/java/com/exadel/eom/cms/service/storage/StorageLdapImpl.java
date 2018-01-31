package com.exadel.eom.cms.service.storage;

import com.exadel.eom.cms.util.Consts;
import com.exadel.eom.cms.util.CopyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

public class StorageLdapImpl implements Storage {

    private static final Integer MRT_SECONDS = 10000;

    private static final String PHOTO = "jpegPhoto";

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
        // nothing to do
    }

    @Override
    public InputStream getResource(String path) {
        String[] pathArr = path.split(Consts.PATH_DELIMITER);
        if(pathArr.length == 2) {
            return getLdapAttribute(pathArr[0], pathArr[1]);
        } else {
            log.error("Bad resource path: [" + path + "] LDAP url:" + url);
            return null;
        }
    }

    protected InputStream getLdapAttribute(final String uid, final String attrName ) {
        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        sc.setTimeLimit(MRT_SECONDS);
        sc.setCountLimit(1);
        sc.setReturningAttributes(new String[]{attrName});

        StringBuilder filter = new StringBuilder();
        filter.append("(&(objectclass=person)").append(MessageFormat.format(userSearchFilter,uid)).append(")");

        if (PHOTO.equalsIgnoreCase(attrName)) {
            List<byte[]> value = ldapTemplate.search(LdapUtils.emptyLdapName(), filter.toString(), sc, new AttributesMapper<byte[]>() {
                public byte[] mapFromAttributes(Attributes attrs)
                        throws NamingException {
                    try {
                        return (byte[])attrs.get(attrName).get();
                    } catch(Exception e) {
                        log.error("Can't get attribute: [" + attrName + "], uid:" + uid);
                        return null;
                    }
                }
            });
            if(value.size() == 0) {
                log.info("Can't find attribute: [" + attrName + "], uid:" + uid);
                return null;
            } else {
                return new ByteArrayInputStream(value.get(0));
            }
        } else {
            List<String> value = ldapTemplate.search(LdapUtils.emptyLdapName(),filter.toString(), sc, new AttributesMapper<String>() {
                public String mapFromAttributes(Attributes attrs)
                        throws NamingException {
                    try {
                        return (String) attrs.get(attrName).get();
                    } catch (Exception e) {
                        log.error("Can't get attribute: [" + attrName + "], uid:" + uid);
                        return null;
                    }
                }
            });
            if(value.size() == 0) {
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
                    log.info("Can't find attribute: [" + attrName + "], uid:" + uid);
                    return null;
                }
            }
        }
    }

    @Override
    public String getMimeType(String path) {
        String[] pathArr = path.split(Consts.PATH_DELIMITER);
        if(pathArr.length == 2) {
            if (PHOTO.equalsIgnoreCase(pathArr[1])) {
                return Consts.JPEG_MIMETYPE;
            } else {
                return Consts.TEXT_MIMETYPE;
            }
        }
        return Consts.BIN_MIMETYPE;
    }

    @Override
    public String getHash(String path) {
        String[] pathArr = path.split(Consts.PATH_DELIMITER);
        if(pathArr.length == 2) {
            InputStream is = getLdapAttribute(pathArr[0], pathArr[1]);
            if(is != null) {
                try {
                    StringBuilder hashb = new StringBuilder();
                    CopyUtil.calcHexHash(is, Consts.DIGEST_ALG, hashb);
                    return hashb.toString();
                } catch(Exception e) {
                    log.error("Read stream fail for LDAP attribute ["+pathArr[1]+"] uid: "+pathArr[0], e);
                } finally {
                    try {
                        is.close();
                    } catch (Exception e) {
                        log.error("Close stream fail for LDAP attribute ["+pathArr[1]+"] uid: "+pathArr[0], e);
                    }
                }
            }
        } else {
            log.error("Bad resource path: [" + path + "] LDAP url:" + url);
        }

        return null;
    }

}
