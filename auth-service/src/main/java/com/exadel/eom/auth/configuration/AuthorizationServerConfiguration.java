package com.exadel.eom.auth.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.KeyPair;
import java.util.Optional;

@Configuration
@RestController
@RequestMapping("/oauth")
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private TokenStore tokenStore = new InMemoryTokenStore();

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private Environment env;

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        String password = Optional.ofNullable(env.getProperty("KEYSTORE_PASSWORD")).orElse("foobar");
        String jwtKey = Optional.ofNullable(env.getProperty("KEYSTORE_JWT_KEY")).orElse("test");
        KeyPair keyPair = new KeyStoreKeyFactory(
                new ClassPathResource("keystore.jks"), password.toCharArray())
                .getKeyPair(jwtKey);
        converter.setKeyPair(keyPair);
        return converter;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // TODO: here needs serialization clients in some database
        // @formatter:off
        clients.inMemory()
                    .withClient("api-client")
                    .secret(env.getProperty("API_CLIENT_PASSWORD"))
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
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {
        // @formatter:off
        endpoints.tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                .accessTokenConverter(jwtAccessTokenConverter());

        // @formatter:on
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer)
            throws Exception {
        // @formatter:off
        oauthServer.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
        // @formatter:on
    }


    @RequestMapping(value = "/revoke", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity revoke(@RequestBody MultiValueMap<String,String> paramMap) {
        if (paramMap == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        String tokenType = paramMap.getFirst("token_type_hint");
        String tokenValue = paramMap.getFirst("token");
        if ("access_token".equalsIgnoreCase(tokenType)) {
            revokeToken(tokenValue);
        } else if("refresh_token".equalsIgnoreCase(tokenType)) {
            revokeRefreshToken(tokenValue);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    public void revokeToken(String tokenValue) {
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
        tokenStore.removeAccessToken(accessToken);
    }

    public void revokeRefreshToken(String tokenValue) {
        OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(tokenValue);
        tokenStore.removeRefreshToken(refreshToken);
    }
}