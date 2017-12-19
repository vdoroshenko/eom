package com.exadel.eom.officemap.service;

import com.exadel.eom.officemap.domain.Officemap;

public interface OfficemapService {

	/**
	 * Finds account by given name
	 *
	 * @param name
	 * @return found account
	 */
	Officemap findByName(String name);

	/**
	 * Checks if office map with the same name already exists
	 * Creates new office map with default parameters
	 *
	 * @param omap
	 * @return created account
	 */
	Officemap create(Officemap omap);

	/**
	 * Validates and applies incoming officemap updates
	 *
	 * @param name
	 * @param update
	 */
	void saveChanges(String name, Officemap update);
}
