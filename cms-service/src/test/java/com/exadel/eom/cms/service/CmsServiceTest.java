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

	@Before
	public void setup() {
		initMocks(this);
	}

	@Test
	public void shouldGetStorageByName() {
		/*
		final Officemap Officemap = new Officemap();
		Officemap.setName("test");

		when(CmsService.findByName(Officemap.getName())).thenReturn(Officemap);
		Officemap found = CmsService.findByName(Officemap.getName());

		assertEquals(Officemap, found);
		*/
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenNameIsEmpty() {
		//CmsService.findByName("");
	}


}
