package com.exadel.eom.auth.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ldap")
public class ContextSourceProperties {
    public static class ContextSource {

        private String url;

        private String userDn;

        private String password;

        public String getUrl() {
            return url;
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

        public void setUserDn(String userDn) {
            this.userDn = userDn;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class Search {
        private String userSearchBase;

        private String userSearchFilter;

        private String groupSearchBase;

        private String groupSearchFilter;

        private String groupRoleAttribute;

        public String getUserSearchBase() {
            return userSearchBase;
        }

        public void setUserSearchBase(String userSearchBase) {
            this.userSearchBase = userSearchBase;
        }

        public String getUserSearchFilter() {
            return userSearchFilter;
        }

        public void setUserSearchFilter(String userSearchFilter) {
            this.userSearchFilter = userSearchFilter;
        }

        public String getGroupSearchBase() {
            return groupSearchBase;
        }

        public void setGroupSearchBase(String groupSearchBase) {
            this.groupSearchBase = groupSearchBase;
        }

        public String getGroupSearchFilter() {
            return groupSearchFilter;
        }

        public void setGroupSearchFilter(String groupSearchFilter) {
            this.groupSearchFilter = groupSearchFilter;
        }

        public String getGroupRoleAttribute() {
            return groupRoleAttribute;
        }

        public void setGroupRoleAttribute(String groupRoleAttribute) {
            this.groupRoleAttribute = groupRoleAttribute;
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
