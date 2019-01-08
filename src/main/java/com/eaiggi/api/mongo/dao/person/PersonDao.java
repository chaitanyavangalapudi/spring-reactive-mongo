package com.eaiggi.api.mongo.dao.person;

import java.util.List;

import com.eaiggi.api.mongo.model.Person;

public interface PersonDao {
    public void create(Person person);
    
    public void update(Person person);
 
    public void delete(Person person);
 
    public void deleteAll();
 
    public Person find(Person person);
 
    public List <Person> findAll();
}
