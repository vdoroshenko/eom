package com.exadel.eom.officemap.service;

import com.exadel.eom.officemap.client.AuthServiceClient;
import com.exadel.eom.officemap.domain.Officemap;
import com.exadel.eom.officemap.repository.OfficemapRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

@Service
public class OfficemapServiceImpl implements OfficemapService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private AuthServiceClient authClient;

	@Autowired
	private OfficemapRepository repository;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Officemap findByName(String name) {
		Assert.hasLength(name);
		return repository.findByName(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Officemap create(Officemap omap) {

		Officemap existing = repository.findByName(omap.getName());
		Assert.isNull(existing, "account already exists: " + omap.getName());

		Officemap map = new Officemap();
		map.setName(omap.getName());
		map.setLastSeen(new Date());

		repository.save(map);

		log.info("new office map has been created: " + map.getName());

		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveChanges(String name, Officemap update) {

		Officemap omap = repository.findByName(name);
		Assert.notNull(omap, "can't find account with name " + name);

		omap.setNote(update.getNote());
		omap.setLastSeen(new Date());
		repository.save(omap);

		log.debug("officemap {} changes has been saved", name);
	}
}
