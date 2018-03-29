package com.exadel.eom.auth.service.security;

import com.exadel.eom.auth.domain.User;
import com.exadel.eom.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MongoUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Optional<User> user = repository.findById(username);

		if (!user.isPresent()) {
			throw new UsernameNotFoundException(username);
		}

		return user.get();
	}
}

