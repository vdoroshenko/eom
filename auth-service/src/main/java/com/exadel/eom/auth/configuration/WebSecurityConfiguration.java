package com.exadel.eom.auth.configuration;

import com.exadel.eom.auth.service.security.ContextSourceProperties;
import com.exadel.eom.auth.service.security.CustomAuthenticationSuccessHandler;
import com.exadel.eom.auth.service.security.MongoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.LdapShaPasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private MongoUserDetailsService userDetailsService;

    @Autowired
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private ContextSourceProperties conf;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new LdapShaPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off

        http.cors().and().csrf().disable().exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and().authorizeRequests().antMatchers("/**").authenticated()
                .and().httpBasic();

            /*
			http.cors().and().csrf().disable().authorizeRequests()
					.antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                    //.antMatchers(HttpMethod.GET, CHECK_URL).permitAll()
					.anyRequest().authenticated()
					.and()
					.addFilter(new JWTAuthenticationFilter(authenticationManager()))
					.addFilter(new JWTAuthorizationFilter(authenticationManager()))
					// this disables session creation on Spring Security
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

			http
                    //configure CORS -- uses a Bean by the name of     corsConfigurationSource (see method below)
                    //CORS must be configured prior to Spring Security
                    .cors()
                    .and()
                    //configuring security
					.authorizeRequests().anyRequest().fullyAuthenticated()
					.and()
					.formLogin().usernameParameter("username").passwordParameter("password").successHandler(customAuthenticationSuccessHandler)
                    .and()
                    .logout().deleteCookies("JSESSIONID").invalidateHttpSession(true).logoutUrl("/logout").logoutSuccessUrl("/");

			http
						.httpBasic()
					.and()
						.authorizeRequests()
						.anyRequest()
						.authenticated()
					.and()
						.csrf().disable();

			http
				.authorizeRequests().anyRequest().authenticated()
			.and()
				.csrf().disable();
			*/
        // @formatter:on
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
			/*
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
            */
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //The CORS filter bean - Configures allowed CORS any (source) to any
    //(api route and method) endpoint

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
            final CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            config.addAllowedOrigin("*");
            config.addAllowedHeader("*");
            config.addAllowedMethod("*");

            final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);
            return source;
           }
    /*
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
    */
}
