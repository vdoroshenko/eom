package com.exadel.eom.auth;

import com.exadel.eom.auth.service.security.ContextSourceProperties;
import com.exadel.eom.auth.service.security.CustomAuthenticationSuccessHandler;
import com.exadel.eom.auth.service.security.MongoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.LdapShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@SpringBootApplication
@EnableDiscoveryClient
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

	@Configuration
	@EnableWebSecurity
	protected static class webSecurityConfig extends WebSecurityConfigurerAdapter {
        /*
		@Autowired
		private MongoUserDetailsService userDetailsService;
        */
        @Autowired
        CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

        @Autowired
        private ContextSourceProperties conf;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
                    //configure CORS -- uses a Bean by the name of     corsConfigurationSource (see method below)
                    //CORS must be configured prior to Spring Security
                    .cors()
                    .and()
                    //configuring security
					.authorizeRequests().anyRequest().fullyAuthenticated()
					.and()
					.formLogin().successHandler(customAuthenticationSuccessHandler)
                    .and()
                    .logout().deleteCookies("JSESSIONID").invalidateHttpSession(true).logoutUrl("/logout").logoutSuccessUrl("/");
			/*
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
		    /*
			auth.userDetailsService(userDetailsService)
					.passwordEncoder(new BCryptPasswordEncoder());
			*/
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
            config.addAllowedMethod("OPTIONS");
            config.addAllowedMethod("HEAD");
            config.addAllowedMethod("GET");
            config.addAllowedMethod("PUT");
            config.addAllowedMethod("POST");
            config.addAllowedMethod("DELETE");
            config.addAllowedMethod("PATCH");

            final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);
            return source;
        }
	}

	@Configuration
	@EnableAuthorizationServer
	protected static class OAuth2AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

		private TokenStore tokenStore = new InMemoryTokenStore();

		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;

		@Autowired
		private MongoUserDetailsService userDetailsService;

		@Autowired
		private Environment env;

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

			// TODO persist clients details

			// @formatter:off
			clients.inMemory()
					.withClient("browser")
					.authorizedGrantTypes("refresh_token", "password")
					.scopes("ui")
					.and()
					.withClient("officemap-service")
					.secret(env.getProperty("OFFICEMAP_SERVICE_PASSWORD"))
					.authorizedGrantTypes("client_credentials", "refresh_token")
					.scopes("server")
					.and()
					.withClient("notification-service")
					.secret(env.getProperty("NOTIFICATION_SERVICE_PASSWORD"))
					.authorizedGrantTypes("client_credentials", "refresh_token")
					.scopes("server");
			// @formatter:on
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints
					.tokenStore(tokenStore)
					.authenticationManager(authenticationManager)
					.userDetailsService(userDetailsService);
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer
					.tokenKeyAccess("permitAll()")
					.checkTokenAccess("isAuthenticated()");
		}
	}
}
