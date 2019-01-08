package com.eaiggi.api.mongo.repository.person;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.eaiggi.api.mongo.model.Person;

@Repository
public interface PersonRepository extends MongoRepository <Person, String>{
	
    @Query("{ 'lastname' : ?0 }")
    Person findByLastname(String lastname);
 
    @Query(value = "{ 'firstname' : ?0}")
    List <Person> findByFirstname(String firstname);

}
