package com.exadel.eom.officemap.controller;

import com.exadel.eom.officemap.OfficemapApplication;
import com.exadel.eom.officemap.domain.Officemap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.exadel.eom.officemap.service.OfficemapService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OfficemapApplication.class)
@WebAppConfiguration
public class OfficemapControllerTest {

	private static final ObjectMapper mapper = new ObjectMapper();

	@InjectMocks
	private OfficemapController officemapController;

	@Mock
	private OfficemapService officemapService;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(officemapController).build();
	}

	@Test
	public void shouldGetOfficemapByName() throws Exception {

		final Officemap omap = new Officemap();
		omap.setName("test");

		when(officemapService.findByName(omap.getName())).thenReturn(omap);

		mockMvc.perform(get("/" + omap.getName()))
				.andExpect(jsonPath("$.name").value(omap.getName()))
				.andExpect(status().isOk());
	}

}
