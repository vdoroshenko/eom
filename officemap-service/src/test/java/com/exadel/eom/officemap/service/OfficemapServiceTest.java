package com.exadel.eom.officemap.service;

import com.exadel.eom.officemap.domain.Officemap;
import com.exadel.eom.officemap.repository.OfficemapRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OfficemapServiceTest {

	@InjectMocks
	private OfficemapServiceImpl OfficemapService;

	@Mock
	private OfficemapRepository repository;

	@Before
	public void setup() {
		initMocks(this);
	}

	@Test
	public void shouldFindByName() {

		final Officemap Officemap = new Officemap();
		Officemap.setName("test");

		when(OfficemapService.findByName(Officemap.getName())).thenReturn(Officemap);
		Officemap found = OfficemapService.findByName(Officemap.getName());

		assertEquals(Officemap, found);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenNameIsEmpty() {
		OfficemapService.findByName("");
	}

	@Test
	public void shouldCreateOfficemap() {

		Officemap omap = new Officemap();
		omap.setName("test");

		Officemap omap2 = OfficemapService.create(omap);

		assertEquals(omap.getName(), omap2.getName());
		assertNotNull(omap2.getLastSeen());

		verify(repository, times(1)).save(omap2);
	}

	@Test
	public void shouldSaveChangesWhenUpdatedOfficemapGiven() {

		final Officemap update = new Officemap();
		update.setName("test");
		update.setNote("test note");

		final Officemap omap = new Officemap();

		when(OfficemapService.findByName("test")).thenReturn(omap);
		OfficemapService.saveChanges("test", update);

		assertEquals(update.getNote(), omap.getNote());
		assertNotNull(omap.getLastSeen());

		verify(repository, times(1)).save(omap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenNoOfficemapsExistedWithGivenName() {
		final Officemap update = new Officemap();

		when(OfficemapService.findByName("test")).thenReturn(null);
		OfficemapService.saveChanges("test", update);
	}
}
