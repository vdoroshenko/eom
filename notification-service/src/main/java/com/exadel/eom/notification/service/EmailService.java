package com.exadel.eom.notification.service;

import com.exadel.eom.notification.domain.NotificationType;
import com.exadel.eom.notification.domain.Recipient;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailService {

	void send(NotificationType type, Recipient recipient, String attachment) throws MessagingException, IOException;

}
