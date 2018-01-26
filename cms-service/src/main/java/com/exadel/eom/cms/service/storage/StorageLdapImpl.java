package com.exadel.eom.cms.service.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQuery;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

public class StorageLdapImpl implements Storage {
    private static final String PATH_DELIMITER = "/";

    private static final String PHOTO = "jpegPhoto";

    private static final String BIN_MIMETYPE = "application/octet-stream";

    private static final String JPEG_MIMETYPE = "image/jpeg";

    private static final String TEXT_MIMETYPE = "text/plain";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private LdapTemplate ldapTemplate;

    private String url;

    private String baseDn;

    @Override
    public void initialize(Map<String, String> params) {
        url = params.get("url");
        baseDn = params.get("userSearchBase");
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
    public InputStream getResource(String path) {
        String[] pathArr = path.split(PATH_DELIMITER);
        try {
            if(pathArr.length == 2) {
                String attrName = pathArr[1];
                LdapQuery query = query()
                        .attributes(attrName)
                        .where("objectclass").is("person")
                        .and("uid").is(pathArr[0]);
                if (PHOTO.equalsIgnoreCase(attrName)) {
                    List<byte[]> value = ldapTemplate.search(query, new AttributesMapper<byte[]>() {
                        public byte[] mapFromAttributes(Attributes attrs)
                                throws NamingException {
                            try {
                                return (byte[])attrs.get(attrName).get();
                            } catch(Exception e) {
                                return null;
                            }
                        }
                    });
                    if(value.size() == 0) {
                        return null;
                    } else {
                        return new ByteArrayInputStream(value.get(0));
                    }
                } else {
                    List<String> value = ldapTemplate.search(query, new AttributesMapper<String>() {
                        public String mapFromAttributes(Attributes attrs)
                                throws NamingException {
                            try {
                                return (String) attrs.get(attrName).get();
                            } catch (Exception e) {
                                return null;
                            }
                        }
                    });
                    if(value.size() == 0) {
                        return null;
                    } else {
                        String s = value.get(0);
                        if (s != null) {
                            return new ByteArrayInputStream(
                                    s.getBytes(StandardCharsets.UTF_8.name()));
                        } else {
                            return null;
                        }
                    }
                }

            } else {
                log.error("Bad resource path: [" + path + "] LDAP url:" + url);
            }
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public String getMimeType(String path) {
        String[] pathArr = path.split(PATH_DELIMITER);
        if(pathArr.length == 2) {
            if (PHOTO.equalsIgnoreCase(pathArr[1])) {
                return JPEG_MIMETYPE;
            } else {
                return TEXT_MIMETYPE;
            }
        }
        return BIN_MIMETYPE;
    }

    @Override
    public String getHash(String path) {
        return null;
    }

}
