package com.eaiggi.api.mongodb.repository.reactive.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.eaiggi.api.mongo.model.Person;
import com.eaiggi.api.mongo.repository.reactive.person.ReactivePersonRepository;
import com.mongodb.reactivestreams.client.MongoCollection;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Integration test for {@link ReactivePersonRepository} using Project Reactor types and operators.
 *
 * Taken from https://github.com/spring-projects/spring-data-examples/blob/master/mongodb/reactive
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("reactive")
public class ReactivePersonRepositoryIntegrationTest {

	@Autowired ReactivePersonRepository repository;
	@Autowired ReactiveMongoOperations operations;

	@Before
	public void setUp() {

		Mono<MongoCollection<Document>> recreateCollection = operations.collectionExists(Person.class) //
				.flatMap(exists -> exists ? operations.dropCollection(Person.class) : Mono.just(exists)) //
				.then(operations.createCollection(Person.class, CollectionOptions.empty() //
						.size(1024 * 1024) //
						.maxDocuments(100) //
						.capped()));

		StepVerifier.create(recreateCollection).expectNextCount(1).verifyComplete();

		Flux<Person> insertAll = operations.insertAll(Flux.just(Person.builder().firstname("Walter").lastname("White").age(50).build(),
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

		Mono<Long> saveAndCount = repository.count() //
				.doOnNext(System.out::println) //
				.thenMany(repository.saveAll(Flux.just(Person.builder().firstname("Hank").lastname("Schrader").age(43).build(),
						Person.builder().firstname("Mike").lastname("Ehrmantraut").age(62).build()))) //
				.last() //
				.flatMap(v -> repository.count()) //
				.doOnNext(System.out::println);

		StepVerifier.create(saveAndCount).expectNext(6L).verifyComplete();
	}

	/**
	 * Note that the all object conversions are performed before the results are printed to the console.
	 */
	@Test
	public void shouldPerformConversionBeforeResultProcessing() {

		StepVerifier.create(repository.findAll().doOnNext(System.out::println)) //
				.expectNextCount(4) //
				.verifyComplete();
	}

	/**
	 * A tailable cursor streams data using {@link Flux} as it arrives inside the capped collection.
	 */
	@Test
	public void shouldStreamDataWithTailableCursor() throws Exception {

		Queue<Person> people = new ConcurrentLinkedQueue<>();

		Disposable disposable = repository.findWithTailableCursorBy() //
				.doOnNext(System.out::println) //
				.doOnNext(people::add) //
				.doOnComplete(() -> System.out.println("Complete")) //
				.doOnTerminate(() -> System.out.println("Terminated")) //
				.subscribe();

		Thread.sleep(100);

		StepVerifier.create(repository.save(Person.builder().firstname("Tuco").lastname("Salamanca").age(33).build())) //
				.expectNextCount(1) //
				.verifyComplete();
		Thread.sleep(100);

		StepVerifier.create(repository.save(Person.builder().firstname("Mike").lastname("Ehrmantraut").age(62).build())) //
				.expectNextCount(1) //
				.verifyComplete();
		Thread.sleep(100);

		disposable.dispose();

		StepVerifier.create(repository.save(Person.builder().firstname("Gus").lastname("Fring").age(53).build())) //
				.expectNextCount(1) //
				.verifyComplete();
		Thread.sleep(100);

		assertThat(people).hasSize(6);
	}

	/**
	 * Fetch data using query derivation.
	 */
	@Test
	public void shouldQueryDataWithQueryDerivation() {
		StepVerifier.create(repository.findByLastname("White")).expectNextCount(2).verifyComplete();
	}

	/**
	 * Fetch data using a string query.
	 */
	@Test
	public void shouldQueryDataWithStringQuery() {
		StepVerifier.create(repository.findByFirstnameAndLastname("Walter", "White")).expectNextCount(1).verifyComplete();
	}

	/**
	 * Fetch data using query derivation.
	 */
	@Test
	public void shouldQueryDataWithDeferredQueryDerivation() {
		StepVerifier.create(repository.findByLastname(Mono.just("White"))).expectNextCount(2).verifyComplete();
	}

	/**
	 * Fetch data using query derivation and deferred parameter resolution.
	 */
	@Test
	public void shouldQueryDataWithMixedDeferredQueryDerivation() {

		StepVerifier.create(repository.findByFirstnameAndLastname(Mono.just("Walter"), "White")) //
				.expectNextCount(1) //
				.verifyComplete();
	}
}