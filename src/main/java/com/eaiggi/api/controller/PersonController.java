package com.eaiggi.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.eaiggi.api.mongo.model.Person;
import com.eaiggi.api.service.kafka.PersonStreamService;

public class PersonController {

	private final PersonStreamService personStreamService;

	@Autowired
	public PersonController(PersonStreamService personStreamService) {
		super();
		this.personStreamService = personStreamService;
	}

	@GetMapping("/person")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void person(@RequestParam("fname") String fname, @RequestParam("lname") String lname) {
		Person person = Person.builder().age(50).firstname(fname).lastname(lname).build();
		personStreamService.send(person);
	}
}
