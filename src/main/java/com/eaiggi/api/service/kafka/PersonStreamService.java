package com.eaiggi.api.service.kafka;

import com.eaiggi.api.mongo.model.Person;

public interface PersonStreamService {

	public void send(final Person person);

}