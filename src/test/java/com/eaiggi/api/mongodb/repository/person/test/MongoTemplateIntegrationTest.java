package com.eaiggi.api.mongodb.repository.person.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.eaiggi.api.mongo.model.Person;
import com.eaiggi.api.service.mongo.PersonService;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MongoTemplateIntegrationTest {
	@Autowired
	PersonService personService;

	@Test
	public void testMongoConnection() {
		Person person = Person.builder().firstname("Hank").lastname("Schrader").age(43).build();
		Person person1 = Person.builder().firstname("Mike").lastname("Ehrmantraut").age(62).build();		
		personService.create(person);
		personService.create(person1);
				
		log.debug("Find One:- " + personService.find(person));
		
		person.setFirstname("Hary");
		personService.update(person);
		
		log.debug("Find One:- " + personService.find(person));		
	}	
}
