package com.exadel.eom.auth.repository;

import com.exadel.eom.auth.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = AuthApplication.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRepositoryTest {

	@Autowired
	private UserRepository repository;

	@Test
	public void shouldSaveAndFindUserByName() {

		User user = new User();
		user.setUsername("name");
		user.setPassword("password");
		repository.save(user);

		Optional<User> found = repository.findById(user.getUsername());
		assertEquals(user.getUsername(), found.get().getUsername());
		assertEquals(user.getPassword(), found.get().getPassword());
	}
}
