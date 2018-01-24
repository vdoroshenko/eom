package com.exadel.eom.cms.service;

import com.exadel.eom.cms.service.storage.Storage;

public interface CmsService {

	/**
	 * Finds storage by given name
	 *
	 * @param name given name
	 * @return found storage
	 */
	Storage getStorage(String name);

}
