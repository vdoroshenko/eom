package com.exadel.eom.auth.service.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ldap")
public class ContextSourceProperties {
    public static class ContextSource {

        private String url;

        private String base;

        private String userDn;

        private String password;


        public String getUrl() {
            return url;
        }

        public String getBase() {
            return base;
        }

        public String getUserDn() {
            return userDn;
        }

        public String getPassword() {
            return password;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setBase(String base) {
            this.base = base;
        }

        public void setUserDn(String userDn) {
            this.userDn = userDn;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class Search {
        private String userDnPatterns;

        private String groupSearchBase;

        public String getUserDnPatterns() {
            return userDnPatterns;
        }

        public String getGroupSearchBase() {
            return groupSearchBase;
        }

        public void setUserDnPatterns(String userDnPatterns) {
            this.userDnPatterns = userDnPatterns;
        }

        public void setGroupSearchBase(String groupSearchBase) {
            this.groupSearchBase = groupSearchBase;
        }
    }

    private ContextSource contextSource;

    private Search search;

    public ContextSource getContextSource() {
        return contextSource;
    }

    public Search getSearch() {
        return search;
    }

    public void setContextSource(ContextSource contextSource) {
        this.contextSource = contextSource;
    }

    public void setSearch(Search search) {
        this.search = search;
    }
}
