package com.eaiggi.api.mongo.dao.person;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.eaiggi.api.mongo.model.Person;

@Repository
@Qualifier("personDao")
public class PersonDaoImpl implements PersonDao {

	@Autowired
	MongoTemplate mongoTemplate;

	final String COLLECTION = "person";

	@Override
	public void create(Person person) {
		mongoTemplate.insert(person);
	}

	@Override
	public void update(Person person) {
		mongoTemplate.save(person);
	}

	@Override
	public void delete(Person person) {
		mongoTemplate.remove(person);
	}

	@Override
	public void deleteAll() {
		mongoTemplate.remove(new Query(), COLLECTION);
	}

	@Override
	public Person find(Person person) {
		Query query = new Query(Criteria.where("_id").is(person.getId()));
		return mongoTemplate.findOne(query, Person.class, COLLECTION);
	}

	@Override
	public List<Person> findAll() {
		return (List<Person>) mongoTemplate.findAll(Person.class);
	}
}
