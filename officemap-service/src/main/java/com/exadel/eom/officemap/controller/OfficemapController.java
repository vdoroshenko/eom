package com.exadel.eom.officemap.controller;

import com.exadel.eom.officemap.domain.Officemap;
import com.exadel.eom.officemap.service.OfficemapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OfficemapController {

	@Autowired
	private OfficemapService officemapService;

	@RequestMapping(path = "/{name}", method = RequestMethod.GET)
	public Officemap getOfficemapByName(@PathVariable String name) {
		return officemapService.findByName(name);
	}

}
