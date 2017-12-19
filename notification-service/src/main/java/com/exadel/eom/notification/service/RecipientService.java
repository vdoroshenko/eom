package com.exadel.eom.notification.service;

import com.exadel.eom.notification.domain.NotificationType;
import com.exadel.eom.notification.domain.Recipient;

import java.util.List;

public interface RecipientService {

	/**
	 * Finds recipient by account name
	 *
	 * @param name
	 * @return recipient
	 */
	Recipient findByName(String name);

	/**
	 * Finds recipients, which are ready to be notified
	 * at the moment
	 *
	 * @param type
	 * @return recipients to notify
	 */
	List<Recipient> findReadyToNotify(NotificationType type);

	/**
	 * Creates or updates recipient settings
	 *
	 * @param name
	 * @param recipient
	 * @return updated recipient
	 */
	Recipient save(String name, Recipient recipient);

	/**
	 * Updates {@link NotificationType} {@code lastNotified} property with current date
	 * for given recipient.
	 *
	 * @param type
	 * @param recipient
	 */
	void markNotified(NotificationType type, Recipient recipient);
}
