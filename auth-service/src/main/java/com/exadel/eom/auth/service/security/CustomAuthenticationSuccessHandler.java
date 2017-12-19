package com.exadel.eom.auth.service.security;

import com.exadel.eom.auth.domain.User;
import com.exadel.eom.auth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class CustomAuthenticationSuccessHandler  implements AuthenticationSuccessHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        //set our response to OK status
        response.setStatus(HttpServletResponse.SC_OK);
        log.info("AT onAuthenticationSuccess(...) function!");
        User user = new User();
        user.setUsername(authentication.getName());
        user.setPassword(authentication.getCredentials().toString());
        userService.createIfNotExistsUpdatePassword(user);
    }
}