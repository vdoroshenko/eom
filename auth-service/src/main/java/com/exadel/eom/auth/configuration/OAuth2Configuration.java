package com.exadel.eom.auth.configuration;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.exadel.eom.auth.domain.User;
import com.exadel.eom.auth.service.security.MongoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;


@Configuration
@EnableAuthorizationServer
public class OAuth2Configuration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    private Environment env;

    // TODO externalize token related data to configuration, store clients in DB
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        /*
        clients.inMemory().withClient("webapp").authorizedGrantTypes("implicit", "refresh_token", "password")
                .authorities("ROLE_TRUSTED").resourceIds("ms/user").scopes("read", "write").autoApprove(true)
                .accessTokenValiditySeconds(60000).refreshTokenValiditySeconds(60000).and().withClient("server")
                .secret("secret").authorizedGrantTypes("refresh_token", "authorization_code")
                .authorities("ROLE_TRUSTED").resourceIds("app/admin").scopes("read", "write").autoApprove(true);
        */
        // @formatter:off
        clients.inMemory()
                .withClient("browser")
                .authorizedGrantTypes("implicit", "refresh_token", "password")
                .authorities("ROLE_TRUSTED")
                .scopes("ui").autoApprove(true)
                .and()
                .withClient("officemap-service")
                .secret(env.getProperty("OFFICEMAP_SERVICE_PASSWORD"))
                .authorizedGrantTypes("client_credentials", "refresh_token")
                .authorities("ROLE_TRUSTED")
                .scopes("server").autoApprove(true)
                .and()
                .withClient("notification-service")
                .secret(env.getProperty("NOTIFICATION_SERVICE_PASSWORD"))
                .authorizedGrantTypes("client_credentials", "refresh_token")
                .authorities("ROLE_TRUSTED")
                .scopes("server").autoApprove(true);
        // @formatter:on
    }

    /*
     * The endpoints can only be accessed by a not logged in user or a user with
     * the specified role
     */
    // TODO externalise configuration
    /*
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED')")
                .checkTokenAccess("hasAuthority('ROLE_TRUSTED')");
    }
    */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore()).tokenEnhancer(jwtAccessTokenConverter())
                .authenticationManager(authenticationManager);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    // TODO encrypt password
    @Bean
    protected JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new CustomTokenEnhancer();
        converter.setKeyPair(
                new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "password".toCharArray()).getKeyPair("jwt"));
        return converter;
    }

    /*
     * Add custom user principal information to the JWT token
     */
    // TODO additional information fields should be get from configuration
    protected static class CustomTokenEnhancer extends JwtAccessTokenConverter {
        @Override
        public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
            User user = (User) authentication.getPrincipal();

            Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());

            //info.put("email", user.getEmail());

            DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);

            // Get the authorities from the user
            Set<GrantedAuthority> authoritiesSet = new HashSet<>(authentication.getAuthorities());

            // Generate String array
            String[] authorities = new String[authoritiesSet.size()];

            int i = 0;
            for (GrantedAuthority authority : authoritiesSet)
                authorities[i++] = authority.getAuthority();

            info.put("authorities", authorities);
            customAccessToken.setAdditionalInformation(info);

            return super.enhance(customAccessToken, authentication);
        }
    }

    /*
     * Setup the refresh_token functionality to work with the custom
     * UserDetailsService
     */
    @Configuration
    protected static class GlobalAuthenticationManagerConfiguration extends GlobalAuthenticationConfigurerAdapter {
        @Autowired
        private MongoUserDetailsService userDetailsService;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        }
    }
}
