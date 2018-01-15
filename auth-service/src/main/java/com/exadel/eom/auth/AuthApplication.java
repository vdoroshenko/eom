package com.exadel.eom.auth;

import com.exadel.eom.auth.service.security.ContextSourceProperties;
import com.exadel.eom.auth.service.security.CustomAuthenticationSuccessHandler;
import com.exadel.eom.auth.service.security.MongoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.LdapShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.security.KeyPair;

@SpringBootApplication
@EnableDiscoveryClient
@EnableResourceServer
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

/*
    @Bean
    FilterRegistrationBean forwardedHeaderFilter() {
        FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
        filterRegBean.setFilter(new ForwardedHeaderFilter());
        filterRegBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegBean;
    }
*/
    @Configuration
    @Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER)
    //@Order(Ordered.HIGHEST_PRECEDENCE+1)
    protected static class LoginConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

        @Autowired
        private ContextSourceProperties conf;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off

            http
                        .formLogin().loginPage("/login")
                        //.successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
                    .and()
                    //.requestMatchers().antMatchers("/login", "/oauth/confirm_access", "/oauth/authorize")
                        .requestMatchers()
                        .antMatchers("/login", "/logout", "/oauth/authorize", "/oauth/confirm_access")
                    .and()
                        .logout()
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login")
                    .and()
                        .authorizeRequests().anyRequest().authenticated();
            /*
            http
                    .requestMatchers()
                    .antMatchers("/login","/logout", "/oauth/authorize", "/oauth/confirm_access")
                    .and()
                    .authorizeRequests()
                    .antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access").anonymous()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    //.defaultSuccessUrl("/homepage.html")
                    //.failureUrl("/login?error=true")
                    .permitAll()
                    .and()
                    .logout().logoutSuccessUrl("/login");
             */
            // @formatter:on
        }


        @Override
        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            // @formatter:off
            /*
            auth.inMemoryAuthentication()
                    .withUser("user").password("password").roles("USER")
                    .and()
                    .withUser("admin").password("admin").roles("ADMIN");
            */
//            auth.parentAuthenticationManager(authenticationManager);

			auth
			.ldapAuthentication()
				.userDnPatterns(conf.getSearch().getUserDnPatterns())
				.groupSearchBase(conf.getSearch().getGroupSearchBase())
				.contextSource()
					.url(conf.getContextSource().getUrl()).managerDn(conf.getContextSource().getUserDn()).managerPassword(conf.getContextSource().getPassword())
					.and()
				.passwordCompare()
					.passwordEncoder(new LdapShaPasswordEncoder())
					.passwordAttribute("userPassword");

            // @formatter:on
        }

    }
}
