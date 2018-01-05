package com.exadel.eom.auth.service.security;

import com.exadel.eom.auth.domain.User;
import com.exadel.eom.auth.repository.UserRepository;
import com.exadel.eom.auth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MongoUserDetailsService implements UserDetailsService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		log.info("User name: " + username);

        User user = userService.find(username);
		log.info("User find pass");
		user = new User();
		user.setUsername(username);
		user.setPassword(username);

		if (user == null) {
			//throw new UsernameNotFoundException(username);
            log.debug("Create new user");
            user = new User();
            user.setUsername(username);
            userService.create(user);
            user = userService.find(username);
		}
		log.info("User check pass");
		return user;
	}
}
