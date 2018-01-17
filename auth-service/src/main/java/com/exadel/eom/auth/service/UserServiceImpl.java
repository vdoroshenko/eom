package com.exadel.eom.auth.service;

import com.exadel.eom.auth.domain.User;
import com.exadel.eom.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserServiceImpl implements UserService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	private UserRepository repository;

	@Override
	public void create(User user) {

		User existing = repository.findOne(user.getUsername());
		Assert.isNull(existing, "user already exists: " + user.getUsername());

		String hash = encoder.encode(user.getPassword());
		user.setPassword(hash);

		repository.save(user);

		log.info("new user has been created: {}", user.getUsername());
	}

    @Override
    public void update(User user) {
        User existing = repository.findOne(user.getUsername());
        Assert.notNull(existing, "user doesn't exist: " + user.getUsername());
        String hash = encoder.encode(user.getPassword());
        if(!existing.getPassword().equals(user.getPassword()) && !existing.getPassword().equals(hash)) {
            user.setPassword(hash);
            log.info("password hash was updated for: {}", user.getUsername());
        }
        repository.save(user);
    }

    @Override
    public User find(String username) {
        return repository.findOne(username);
    }

	@Override
	public void delete(String username) {
		repository.delete(username);
	}

	@Override
	public void upsert(User user) {
		User existing = repository.findOne(user.getUsername());
		String hash = encoder.encode(user.getPassword());
		if (existing == null) {

			user.setPassword(hash);

			repository.save(user);

			log.info("new user has been created: {}", user.getUsername());
		} else {
            log.info("user already exists: {}", user.getUsername());
			if(!existing.getPassword().equals(hash)) {
				existing.setPassword(hash);
				repository.save(existing);
                log.info("password hash was updated for: {}", user.getUsername());
			}
		}
	}
}
