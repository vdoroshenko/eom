package com.exadel.eom.auth.service;

import com.exadel.eom.auth.domain.User;

public interface UserService {

	void create(User user);

	void update(User user);

	User find(String username);

	void delete(String username);

	void upsert(User user);
}
