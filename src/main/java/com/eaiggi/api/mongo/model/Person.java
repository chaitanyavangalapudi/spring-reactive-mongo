package com.eaiggi.api.mongo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@Document(collection = "person")
public class Person {
	private final @Id @NonNull String id;
	private @NonNull String firstname;
	private @NonNull String lastname;
	private @NonNull Integer age;
}