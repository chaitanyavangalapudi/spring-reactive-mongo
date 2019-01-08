package com.eaiggi.api.service.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eaiggi.api.mongo.dao.person.PersonDao;
import com.eaiggi.api.mongo.model.Person;

@Service("personService")
public class PersonServiceImpl implements PersonService {

	@Autowired
	PersonDao personDao;

	@Override
	public void create(Person person) {
		personDao.create(person);
	}

	@Override
	public void update(Person person) {
		personDao.update(person);
	}

	@Override
	public void delete(Person person) {
		personDao.delete(person);
	}

	@Override
	public void deleteAll() {
		personDao.deleteAll();
	}

	@Override
	public Person find(Person person) {
		return personDao.find(person);
	}

	@Override
	public List<Person> findAll() {
		return personDao.findAll();
	}

	@Override
	public List<Person> findByFirstname(String firstname) {
		// TODO Auto-generated method stub
		return null;
	}
}
