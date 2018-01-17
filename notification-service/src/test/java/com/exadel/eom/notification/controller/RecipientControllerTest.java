package com.exadel.eom.notification.controller;

import com.exadel.eom.notification.domain.Frequency;
import com.exadel.eom.notification.domain.NotificationSettings;
import com.exadel.eom.notification.domain.NotificationType;
import com.exadel.eom.notification.domain.Recipient;
import com.exadel.eom.notification.service.RecipientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.sun.security.auth.UserPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecipientControllerTest {

	private static final ObjectMapper mapper = new ObjectMapper();

	@InjectMocks
	private RecipientController recipientController;

	@Mock
	private RecipientService recipientService;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(recipientController).build();
	}

	@Test
	public void shouldSaveCurrentRecipientSettings() throws Exception {

		Recipient recipient = getStubRecipient();
		String json = mapper.writeValueAsString(recipient);

		mockMvc.perform(put("/recipients/current").principal(new UserPrincipal(recipient.getUserName())).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	public void shouldGetCurrentRecipientSettings() throws Exception {

		Recipient recipient = getStubRecipient();
		when(recipientService.findByName(recipient.getUserName())).thenReturn(recipient);

		mockMvc.perform(get("/recipients/current").principal(new UserPrincipal(recipient.getUserName())))
				.andExpect(jsonPath("$.userName").value(recipient.getUserName()))
				.andExpect(status().isOk());
	}

	private Recipient getStubRecipient() {

		NotificationSettings remind = new NotificationSettings();
		remind.setActive(true);
		remind.setFrequency(Frequency.WEEKLY);
		remind.setLastNotified(null);

		NotificationSettings backup = new NotificationSettings();
		backup.setActive(false);
		backup.setFrequency(Frequency.MONTHLY);
		backup.setLastNotified(null);

		Recipient recipient = new Recipient();
		recipient.setUserName("test");
		recipient.setEmail("test@test.com");
		recipient.setScheduledNotifications(ImmutableMap.of(
				NotificationType.BACKUP, backup,
				NotificationType.REMIND, remind
		));

		return recipient;
	}
}