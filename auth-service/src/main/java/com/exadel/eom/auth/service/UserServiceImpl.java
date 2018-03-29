package com.exadel.eom.auth.service;

import com.exadel.eom.auth.domain.User;
import com.exadel.eom.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	private UserRepository repository;

	@Override
	public void create(User user) {

		Optional<User> existing = repository.findById(user.getUsername());
		Assert.isTrue(!existing.isPresent(), "user already exists: " + user.getUsername());

		String hash = encoder.encode(user.getPassword());
		user.setPassword(hash);

		repository.save(user);

		log.info("new user has been created: {}", user.getUsername());
	}

    @Override
    public void update(User user) {
        Optional<User> existing = repository.findById(user.getUsername());
        Assert.notNull(existing, "user doesn't exist: " + user.getUsername());
        String hash = encoder.encode(user.getPassword());
        if(!existing.get().getPassword().equals(user.getPassword()) && !existing.get().getPassword().equals(hash)) {
            user.setPassword(hash);
            log.info("password hash was updated for: {}", user.getUsername());
        }
        repository.save(user);
    }

    @Override
    public User find(String username) {
        return repository.findById(username).get();
    }

	@Override
	public void delete(String username) {
		User u = new User();
		u.setUsername(username);
		repository.delete(u);
	}

	@Override
	public void upsert(User user) {
		Optional<User> existing = repository.findById(user.getUsername());
		String hash = encoder.encode(user.getPassword());
		if (!existing.isPresent()) {

			user.setPassword(hash);

			repository.save(user);

			log.info("new user has been created: {}", user.getUsername());
		} else {
            log.info("user already exists: {}", user.getUsername());
			if(!existing.get().getPassword().equals(hash)) {
				existing.get().setPassword(hash);
				repository.save(existing.get());
                log.info("password hash was updated for: {}", user.getUsername());
			}
		}
	}
}
