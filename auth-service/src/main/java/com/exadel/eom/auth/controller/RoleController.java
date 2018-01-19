package com.exadel.eom.auth.controller;

import com.exadel.eom.auth.domain.Role;
import com.exadel.eom.auth.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    RoleRepository roleRepository;

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(method = RequestMethod.POST)
    public void createRole(@Valid @RequestBody Role role) {
        roleRepository.insert(role);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteRole(@Valid @RequestBody Role role) {
        roleRepository.delete(role);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(method = RequestMethod.GET)
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }
}
