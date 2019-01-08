package com.eaiggi.api.mongodb.repository.reactive.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.eaiggi.api.mongo.model.Person;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import rx.RxReactiveStreams;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("reactive")
//Taken from https://github.com/spring-projects/spring-data-examples/blob/master/mongodb/reactive
public class ReactiveMongoTemplateIntegrationTest {

	@Autowired ReactiveMongoTemplate template;

	@Before
	public void setUp() {

		StepVerifier.create(template.dropCollection(Person.class)).verifyComplete();

		Flux<Person> insertAll = template
				.insertAll(Flux.just(Person.builder().firstname("Walter").lastname("White").age(50).build(),
						Person.builder().firstname("Skyler").lastname("White").age(45).build(),
						Person.builder().firstname("Saul").lastname("Goodman").age(42).build(),
						Person.builder().firstname("Jesse").lastname("Pinkman").age(27).build()).collectList());

		StepVerifier.create(insertAll).expectNextCount(4).verifyComplete();
	}

	/**
	 * This sample performs a count, inserts data and performs a count again using reactive operator chaining. It prints
	 * the two counts ({@code 4} and {@code 6}) to the console.
	 */
	@Test
	public void shouldInsertAndCountData() {

		Mono<Long> count = template.count(new Query(), Person.class) //
				.doOnNext(System.out::println) //
				.thenMany(template.insertAll(Arrays.asList(Person.builder().firstname("Hank").lastname("Schrader").age(43).build(),
						Person.builder().firstname("Mike").lastname("Ehrmantraut").age(62).build())))				
				.last() //
				.flatMap(v -> template.count(new Query(), Person.class)) //
				.doOnNext(System.out::println);//

		StepVerifier.create(count).expectNext(6L).verifyComplete();
	}

	/**
	 * Note that the all object conversions are performed before the results are printed to the console.
	 */
	@Test
	public void convertReactorTypesToRxJava2() {

		Flux<Person> flux = template.find(Query.query(Criteria.where("lastname").is("White")), Person.class);

		long count = RxReactiveStreams.toObservable(flux).count().toSingle().toBlocking().value();

		assertThat(count).isEqualTo(2);
	}
}