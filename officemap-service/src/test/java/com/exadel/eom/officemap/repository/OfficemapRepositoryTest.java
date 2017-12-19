package com.exadel.eom.officemap.repository;

import com.exadel.eom.officemap.OfficemapApplication;
import com.exadel.eom.officemap.domain.Officemap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OfficemapApplication.class)
public class OfficemapRepositoryTest {

	@Autowired
	private OfficemapRepository repository;

	@Test
	public void shouldFindAccountByName() {

		Officemap stub = getStubOfficemap();
		repository.save(stub);

		Officemap found = repository.findByName(stub.getName());
		assertEquals(stub.getLastSeen(), found.getLastSeen());
		assertEquals(stub.getNote(), found.getNote());
	}

	private Officemap getStubOfficemap() {

		Officemap account = new Officemap();
		account.setName("test");
		account.setNote("test note");
		account.setLastSeen(new Date());

		return account;
	}
}
