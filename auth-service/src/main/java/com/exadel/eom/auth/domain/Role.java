package com.exadel.eom.auth.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

@Document(collection = "roles")
public class Role implements GrantedAuthority {

    @Id
    private String authority;

    public Role() {
        super();
    }

    public Role(String authority) {
        super();
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Role authority(String authority) {
        this.authority = authority;
        return this;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Role)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        Role rhs = (Role) obj;
        return new EqualsBuilder().append(authority, rhs.authority).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(authority).toHashCode();
    }

}

