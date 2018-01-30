package com.exadel.eom.cms.service;

import com.exadel.eom.cms.service.storage.Storage;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CmsServiceTest {

	@Autowired
	private CmsService cmsService;

	@Before
	public void setup() {

	}

	@Test
	public void shouldGetFsStorage() {
		//Storage storage = cmsService.getStorage("image");
		//storage.getResource();
	}

	/*
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenNameIsEmpty() {
		//CmsService.findByName("");
	}
	*/
}
