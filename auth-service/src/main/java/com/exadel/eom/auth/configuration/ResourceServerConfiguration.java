package com.exadel.eom.auth.configuration;

import com.exadel.eom.auth.provider.CustomAuthenticationProvider;
import com.exadel.eom.auth.service.security.MongoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private final MongoUserDetailsService userDetailsService;

    private final CustomAuthenticationProvider customAuthProvider;

    private final ContextSourceProperties conf;

    @Autowired
    public ResourceServerConfiguration(MongoUserDetailsService userDetailsService, CustomAuthenticationProvider customAuthProvider, ContextSourceProperties conf) {
        this.userDetailsService = userDetailsService;
        this.customAuthProvider = customAuthProvider;
        this.conf = conf;
    }

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        // @formatter:off

        // Multi-providers configuration

        // First provider - backdoor
        auth.authenticationProvider(customAuthProvider);

        // Second provider - MongoDB
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());

        // Third provider - LDAP
        auth.ldapAuthentication()
                .userSearchBase(conf.getSearch().getUserSearchBase())
                .userSearchFilter(conf.getSearch().getUserSearchFilter())
                .groupSearchBase(conf.getSearch().getGroupSearchBase())
                .groupSearchFilter(conf.getSearch().getGroupSearchFilter())
                .groupRoleAttribute(conf.getSearch().getGroupRoleAttribute())
                .contextSource()
                .url(conf.getContextSource().getUrl())
                .managerDn(conf.getContextSource().getUserDn())
                .managerPassword(conf.getContextSource().getPassword());

        // @formatter:on
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeRequests()
                    .anyRequest()
                        .authenticated()
                .and()
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .csrf().disable();
        // @formatter:on
    }

}
