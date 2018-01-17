package com.exadel.eom.officemap.controller;

import com.exadel.eom.officemap.domain.Officemap;
import com.exadel.eom.officemap.service.OfficemapService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
