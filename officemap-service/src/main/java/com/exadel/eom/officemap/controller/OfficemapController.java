package com.exadel.eom.officemap.controller;

import com.exadel.eom.officemap.domain.Officemap;
import com.exadel.eom.officemap.service.OfficemapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class OfficemapController {

	@Autowired
	private OfficemapService officemapService;

	@RequestMapping(path = "/{name}", method = RequestMethod.GET)
	public Officemap getOfficemapByName(@PathVariable String name) {
		return officemapService.findByName(name);
	}

	@RequestMapping(path = "/{name}", method = RequestMethod.POST)
	public void saveOfficemap(@PathVariable String name, @Valid @RequestBody Officemap update) {
		officemapService.saveChanges(name, update);
	}
}
