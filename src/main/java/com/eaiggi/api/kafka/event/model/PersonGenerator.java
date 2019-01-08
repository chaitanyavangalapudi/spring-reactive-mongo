package com.eaiggi.api.kafka.event.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.eaiggi.api.mongo.model.Person;

@Component
public class PersonGenerator {
	private long nextPersonId;
	private Integer nextPersonAge;

	public PersonGenerator() {
		this.nextPersonId = 1001l;
		this.nextPersonAge = 1;
	}

	public List<Person> getBooks() {
		List<Person> persons = new ArrayList<>();
		for (int i = 0; i < 10; i++)
			persons.add(createPerson());
		return persons;
	}

	public Person createPerson() {
		Person person = null;
		String personId = Long.toString(nextPersonId++);
		Integer personAge = nextPersonAge++ % 70;
		String fistName = "# FN" + personId;
		String lastName = "# LN" + personId;
		person = Person.builder().age(personAge).firstname(fistName).lastname(lastName).id(personId).build();

		return person;
	}
}
