package com.remondis.remap.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

/**
 * In a web application a {@link Mapper} is typically built lazily, e.g. behind a singleton bean whose factory method
 * can run concurrently on the first few requests before the singleton is cached. This test builds many independent
 * {@link Mapper} instances - for the same source/destination type pair and using explicit field selectors, which
 * drive the shared, per-type {@code InvocationSensor} proxy and its thread-local interaction tracking - concurrently
 * from many threads and verifies that every configuration and every mapping is correct, with no cross-thread
 * interference between selector evaluations (the class of bug fixed by the stale-tracked-properties and
 * classloader-leak regressions).
 */
class ConcurrentMapperConfigurationTest {

  private static final int THREAD_COUNT = 16;
  private static final int BUILDS_PER_THREAD = 200;

  private ExecutorService executor;

  @AfterEach
  void shutdown() {
    if (executor != null) {
      executor.shutdownNow();
    }
  }

  @Test
  void shouldBuildManyMappersForTheSameTypeConcurrently() throws InterruptedException {
    executor = Executors.newFixedThreadPool(THREAD_COUNT);
    CyclicBarrier startingGate = new CyclicBarrier(THREAD_COUNT);

    List<Callable<Void>> tasks = new ArrayList<>();
    for (int t = 0; t < THREAD_COUNT; t++) {
      int threadIndex = t;
      tasks.add(() -> {
        startingGate.await(30, TimeUnit.SECONDS);
        for (int i = 0; i < BUILDS_PER_THREAD; i++) {
          // Uses explicit field selectors (reassign/omit) on every build, exercising the shared, per-type
          // InvocationSensor proxy and its thread-local property tracking concurrently with all other threads.
          Mapper<Address, AddressDto> mapper = Mapping.from(Address.class)
              .to(AddressDto.class)
              .omitInSource(Address::getCity)
              .omitInDestination(AddressDto::getCity)
              .reassign(Address::getStreet)
              .to(AddressDto::getStreet)
              .mapper();

          Address address = new Address();
          address.setStreet("street-" + threadIndex + "-" + i);
          address.setCity("city-" + threadIndex + "-" + i);

          AddressDto dto = mapper.map(address);

          assertThat(dto.getStreet()).isEqualTo(address.getStreet());
          assertThat(dto.getCity()).isNull();
        }
        return null;
      });
    }

    List<Future<Void>> futures = executor.invokeAll(tasks, 60, TimeUnit.SECONDS);
    for (Future<Void> future : futures) {
      try {
        future.get();
      } catch (ExecutionException e) {
        throw new AssertionError("A concurrent mapper build failed.", e.getCause());
      }
    }
  }

  @Test
  void shouldBuildMappersForDifferentTypesConcurrently() throws InterruptedException {
    executor = Executors.newFixedThreadPool(THREAD_COUNT);
    CyclicBarrier startingGate = new CyclicBarrier(THREAD_COUNT);

    List<Callable<Void>> tasks = new ArrayList<>();
    for (int t = 0; t < THREAD_COUNT; t++) {
      int threadIndex = t;
      tasks.add(() -> {
        startingGate.await(30, TimeUnit.SECONDS);
        for (int i = 0; i < BUILDS_PER_THREAD; i++) {
          Mapper<Address, AddressDto> addressMapper = Mapping.from(Address.class)
              .to(AddressDto.class)
              .mapper();

          Mapper<Person, PersonDto> personMapper = Mapping.from(Person.class)
              .to(PersonDto.class)
              .omitInSource(Person::getTags)
              .omitInDestination(PersonDto::getTags)
              .useMapper(addressMapper)
              .mapper();

          Person person = new Person();
          person.setName("name-" + threadIndex + "-" + i);
          person.setAge(threadIndex * 1000 + i);
          Address address = new Address();
          address.setStreet("street-" + threadIndex + "-" + i);
          address.setCity("city-" + threadIndex + "-" + i);
          person.setAddress(address);

          PersonDto dto = personMapper.map(person);

          assertThat(dto.getName()).isEqualTo(person.getName());
          assertThat(dto.getAge()).isEqualTo(person.getAge());
          assertThat(dto.getAddress()
              .getStreet()).isEqualTo(address.getStreet());
          assertThat(dto.getAddress()
              .getCity()).isEqualTo(address.getCity());
          assertThat(dto.getTags()).isNull();
        }
        return null;
      });
    }

    List<Future<Void>> futures = executor.invokeAll(tasks, 60, TimeUnit.SECONDS);
    for (Future<Void> future : futures) {
      try {
        future.get();
      } catch (ExecutionException e) {
        throw new AssertionError("A concurrent mapper build failed.", e.getCause());
      }
    }
  }
}
