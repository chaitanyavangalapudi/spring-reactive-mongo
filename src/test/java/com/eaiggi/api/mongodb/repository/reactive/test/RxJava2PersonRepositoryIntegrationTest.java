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
import com.eaiggi.api.mongo.repository.reactive.person.RxJava2PersonRepository;
import com.mongodb.reactivestreams.client.MongoCollection;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Integration test for {@link RxJava2PersonRepository} using RxJava2 types. Note that {@link ReactiveMongoOperations}
 * is only available using Project Reactor types as the native Template API implementation does not come in multiple
 * reactive flavors.
 *
 * Taken from https://github.com/spring-projects/spring-data-examples/blob/master/mongodb/reactive
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("reactive")
public class RxJava2PersonRepositoryIntegrationTest {

	@Autowired RxJava2PersonRepository repository;
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

		repository.saveAll(Flowable.just(Person.builder().firstname("Walter").lastname("White").age(50).build(),
				Person.builder().firstname("Skyler").lastname("White").age(45).build(),
				Person.builder().firstname("Saul").lastname("Goodman").age(42).build(),
				Person.builder().firstname("Jesse").lastname("Pinkman").age(27).build())) //
				.test() //
				.awaitCount(4) //
				.assertNoErrors() //
				.awaitTerminalEvent();
	}

	/**
	 * This sample performs a count, inserts data and performs a count again using reactive operator chaining. It prints
	 * the two counts ({@code 4} and {@code 6}) to the console.
	 */
	@Test
	public void shouldInsertAndCountData() {

		Flowable<Person> people = Flowable.just(Person.builder().firstname("Hank").lastname("Schrader").age(43).build(),
				Person.builder().firstname("Mike").lastname("Ehrmantraut").age(62).build());

		repository.count() //
				.doOnSuccess(System.out::println) //
				.toFlowable() //
				.switchMap(count -> repository.saveAll(people)) //
				.lastElement() //
				.toSingle() //
				.flatMap(v -> repository.count()) //
				.doOnSuccess(System.out::println) //
				.test() //
				.awaitCount(1) //
				.assertValue(6L) //
				.assertNoErrors() //
				.awaitTerminalEvent();
	}

	/**
	 * Note that the all object conversions are performed before the results are printed to the console.
	 */
	@Test
	public void shouldPerformConversionBeforeResultProcessing() {

		repository.findAll() //
				.doOnNext(System.out::println) //
				.test() //
				.awaitCount(4) //
				.assertNoErrors() //
				.awaitTerminalEvent();

	}

	/**
	 * A tailable cursor streams data using {@link Flowable} as it arrives inside the capped collection.
	 */
	@Test
	public void shouldStreamDataWithTailableCursor() throws Exception {

		Queue<Person> people = new ConcurrentLinkedQueue<>();

		Disposable subscription = repository.findWithTailableCursorBy() //
				.doOnNext(System.out::println) //
				.doOnNext(people::add) //
				.doOnComplete(() -> System.out.println("Complete")) //
				.doOnTerminate(() -> System.out.println("Terminated")) //
				.subscribe();

		Thread.sleep(100);

		repository.save(Person.builder().firstname("Tuco").lastname("Salamanca").age(33).build()).test().awaitTerminalEvent();
		Thread.sleep(100);

		repository.save(Person.builder().firstname("Mike").lastname("Ehrmantraut").age(62).build()).test().awaitTerminalEvent();
		Thread.sleep(100);

		subscription.dispose();

		repository.save(Person.builder().firstname("Gus").lastname("Fring").age(53).build()).test().awaitTerminalEvent();
		Thread.sleep(100);

		assertThat(people).hasSize(6);
	}

	/**
	 * Fetch data using query derivation.
	 */
	@Test
	public void shouldQueryDataWithQueryDerivation() {

		repository.findByLastname("White") //
				.test() //
				.awaitCount(2) //
				.assertNoErrors() //
				.awaitTerminalEvent();

	}

	/**
	 * Fetch data using a string query.
	 */
	@Test
	public void shouldQueryDataWithStringQuery() {

		repository.findByFirstnameAndLastname("Walter", "White") //
				.test() //
				.awaitCount(1) //
				.assertNoErrors() //
				.awaitTerminalEvent();
	}

	/**
	 * Fetch data using query derivation.
	 */
	@Test
	public void shouldQueryDataWithDeferredQueryDerivation() {

		repository.findByLastname(Single.just("White")) //
				.test() //
				.awaitCount(2) //
				.assertNoErrors() //
				.awaitTerminalEvent();
	}

	/**
	 * Fetch data using query derivation and deferred parameter resolution.
	 */
	@Test
	public void shouldQueryDataWithMixedDeferredQueryDerivation() {

		repository.findByFirstnameAndLastname(Single.just("Walter"), "White") //
				.test() //
				.awaitCount(1) //
				.assertNoErrors() //
				.awaitTerminalEvent();
	}
}