package com.eaiggi.api.service.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eaiggi.api.mongo.model.Person;
import com.eaiggi.api.mongo.repository.person.PersonRepository;

import lombok.extern.slf4j.Slf4j;

@Service("personRepositoryService")
@Slf4j
public class PersonRepositoryServiceImpl implements PersonService {

	@Autowired
	PersonRepository personRepository;
	
	@Override
	public void create(Person person) {
		Person person1= personRepository.insert(person);
		log.debug("Created:- " + person1);
	}

	@Override
	public void update(Person person) {
		Person person1= personRepository.save(person);
		log.debug("Updated:- " + person1);
		
	}

	@Override
	public void delete(Person person) {
		personRepository.delete(person);
		log.debug("Updated:- " + person.getId());
		
	}

	@Override
	public void deleteAll() {
		personRepository.deleteAll();
		
	}

	@Override
	public Person find(Person person) {
		return personRepository.findById(person.getId()).get();
	}

	@Override
	public List<Person> findAll() {		
		return personRepository.findAll();
	}

	@Override
	public List<Person> findByFirstname(String firstname) {
		return personRepository.findByFirstname(firstname);
	}

}
