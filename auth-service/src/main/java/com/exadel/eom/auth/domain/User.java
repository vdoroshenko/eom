package com.exadel.eom.auth.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Document(collection = "users")
public class User implements UserDetails {

	@Id
	private String username;

	private String password;

	private boolean accountNonExpired;

	private boolean accountNonLocked;

	private boolean credentialsNonExpired;

	private boolean enabled;

    @DBRef
    @NotNull
    private List<Role> roles;

	public User() {
	    super();
		accountNonExpired = accountNonLocked = credentialsNonExpired = enabled = true;
        roles = new ArrayList<>();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public List<GrantedAuthority> getAuthorities() {
		return new ArrayList<>(roles);
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public User accountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
        return this;
    }

    public User accountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
        return this;
    }

    public User credentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
        return this;
    }

    public User enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }

    public void removeRole(Role role) {
        //use iterator to avoid java.util.ConcurrentModificationException with foreach
        for (Iterator<Role> iter = this.roles.iterator(); iter.hasNext(); )
        {
            if (iter.next().equals(role))
                iter.remove();
        }
    }

    public String getRolesCSV() {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Role> iter = this.roles.iterator(); iter.hasNext(); )
        {
            sb.append(iter.next().getAuthority());
            if (iter.hasNext()) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        User rhs = (User) obj;
        return new EqualsBuilder().append(username, rhs.username).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(username).toHashCode();
    }
}
