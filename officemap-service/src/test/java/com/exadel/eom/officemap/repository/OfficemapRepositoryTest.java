package com.exadel.eom.officemap.repository;

import com.exadel.eom.officemap.domain.Officemap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

		Officemap map = new Officemap();
		map.setName("test");
		map.setNote("test note");
		map.setLastSeen(new Date());
		map.setEmployeeLocationList(new ArrayList<>());

		return map;
	}
}
