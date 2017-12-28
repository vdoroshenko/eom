package com.exadel.eom.auth.controller;

import com.exadel.eom.auth.domain.User;
import com.exadel.eom.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/current", method = RequestMethod.GET)
	public Principal getUser(Principal principal) {
		return principal;
	}

    @RequestMapping(value = "/check/{name}", method = RequestMethod.GET)
    public ResponseEntity checkUser(@PathVariable("name") String name) {
        return userService.find(name) == null ? new ResponseEntity(HttpStatus.NOT_FOUND)
                : new ResponseEntity(HttpStatus.OK);
    }

	@PreAuthorize("#oauth2.hasScope('server')")
	@RequestMapping(value = "/adm", method = RequestMethod.POST)
	public void createUser(@Valid @RequestBody User user) {
		userService.create(user);
	}

	@PreAuthorize("#oauth2.hasScope('server')")
	@RequestMapping(value = "/adm",method = RequestMethod.PUT)
	public void updateUser(@Valid @RequestBody User user) {
		userService.update(user);
	}

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/adm/{name}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable("name") String name) {
        userService.delete(name);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/adm/find/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User findUser(@PathVariable("name") String name) {
        return userService.find(name);
    }
}
