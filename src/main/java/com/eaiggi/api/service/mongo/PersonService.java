package com.eaiggi.api.service.mongo;

import java.util.List;

import com.eaiggi.api.mongo.model.Person;

public interface PersonService {
    public void create(Person person);
    
    public void update(Person person);
 
    public void delete(Person person);
 
    public void deleteAll();
 
    public Person find(Person person);
 
    public List <Person> findAll();
    
    public List <Person> findByFirstname(String firstname);
       
}
