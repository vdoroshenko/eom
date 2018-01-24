package com.exadel.eom.cms.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CmsServiceTest {

	@InjectMocks
	private CmsServiceImpl cmsService;
/*
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

		when(CmsService.findByName(Officemap.getName())).thenReturn(Officemap);
		Officemap found = CmsService.findByName(Officemap.getName());

		assertEquals(Officemap, found);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenNameIsEmpty() {
		CmsService.findByName("");
	}

	@Test
	public void shouldCreateOfficemap() {

		Officemap omap = new Officemap();
		omap.setName("test");

		Officemap omap2 = CmsService.create(omap);

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

		when(CmsService.findByName("test")).thenReturn(omap);
		CmsService.saveChanges("test", update);

		assertEquals(update.getNote(), omap.getNote());
		assertNotNull(omap.getLastSeen());

		verify(repository, times(1)).save(omap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenNoOfficemapsExistedWithGivenName() {
		final Officemap update = new Officemap();

		when(CmsService.findByName("test")).thenReturn(null);
		CmsService.saveChanges("test", update);
	}
	*/
}
