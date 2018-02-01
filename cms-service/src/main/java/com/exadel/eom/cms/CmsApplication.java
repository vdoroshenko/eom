package com.exadel.eom.cms;

import com.exadel.eom.cms.configuration.StorageSource;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

import java.util.Map;

@SpringBootApplication
@EnableResourceServer
@EnableDiscoveryClient
@EnableOAuth2Client
@EnableFeignClients
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties
public class CmsApplication extends ResourceServerConfigurerAdapter {

    private static final String PUBLIC_ACCESS = "publicAccess";
    private static final String GET_STRING = "get";
    private static final String PUT_STRING = "put";
    private static final String ALL_STRING = "all";

	@Autowired
	private ResourceServerProperties sso;

	@Autowired
	private StorageSource storageSource;

	public static void main(String[] args) {
		SpringApplication.run(CmsApplication.class, args);
	}

	@Bean
	@ConfigurationProperties(prefix = "security.oauth2.client")
	public ClientCredentialsResourceDetails clientCredentialsResourceDetails() {
		return new ClientCredentialsResourceDetails();
	}

	@Bean
	public RequestInterceptor oauth2FeignRequestInterceptor(){
		return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), clientCredentialsResourceDetails());
	}

	@Bean
	public OAuth2RestTemplate clientCredentialsRestTemplate() {
		return new OAuth2RestTemplate(clientCredentialsResourceDetails());
	}

	/*
	@Bean
	public ResourceServerTokenServices tokenServices() {
		return new CustomUserInfoTokenServices(sso.getUserInfoUri(), sso.getClientId());
	}
	*/
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.anyRequest().authenticated();

        Map<String, Map<String, String>> configuration = storageSource.getConfiguration();
        if(configuration != null) {
            configuration.forEach((key, params) -> {
                final String access = params.get(PUBLIC_ACCESS);
                if(access != null) {
                    try {
                        if(ALL_STRING.equalsIgnoreCase(access)) {
                            http.authorizeRequests().antMatchers("/" + key).permitAll();
                        } else {
                            if (GET_STRING.equalsIgnoreCase(access) || PUT_STRING.equalsIgnoreCase(access)) {
                                http.authorizeRequests().antMatchers(HttpMethod.GET, "/" + key).permitAll();
                            }
                            if (PUT_STRING.equalsIgnoreCase(access)) {
                                http.authorizeRequests().antMatchers(HttpMethod.POST, "/" + key).permitAll();
                                http.authorizeRequests().antMatchers(HttpMethod.PUT, "/" + key).permitAll();
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
	}
}
