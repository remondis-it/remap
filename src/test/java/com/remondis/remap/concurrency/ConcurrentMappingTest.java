package com.remondis.remap.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
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
 * A single {@link Mapper} instance is designed to be built once (e.g. as a singleton bean) and then shared between
 * many request-handling threads of a web application. This test hammers a single, shared {@link Mapper} - including
 * a nested mapper and a collection field - from many threads concurrently and verifies that every mapping is
 * correct, i.e. that no thread ever observes a value written for a different thread's object.
 */
class ConcurrentMappingTest {

  private static final int THREAD_COUNT = 16;
  private static final int TASKS_PER_THREAD = 500;

  private ExecutorService executor;

  @AfterEach
  void shutdown() {
    if (executor != null) {
      executor.shutdownNow();
    }
  }

  @Test
  void sharedMapperShouldMapCorrectlyUnderConcurrentLoad() throws InterruptedException {
    Mapper<Address, AddressDto> addressMapper = Mapping.from(Address.class)
        .to(AddressDto.class)
        .mapper();

    Mapper<Person, PersonDto> personMapper = Mapping.from(Person.class)
        .to(PersonDto.class)
        .useMapper(addressMapper)
        .mapper();

    executor = Executors.newFixedThreadPool(THREAD_COUNT);

    List<Callable<Void>> tasks = new ArrayList<>();
    for (int t = 0; t < THREAD_COUNT; t++) {
      int threadIndex = t;
      tasks.add(() -> {
        for (int i = 0; i < TASKS_PER_THREAD; i++) {
          Person person = new Person();
          String name = "thread-" + threadIndex + "-task-" + i;
          int age = threadIndex * 100_000 + i;
          person.setName(name);
          person.setAge(age);
          Address address = new Address();
          address.setStreet("street-" + threadIndex + "-" + i);
          address.setCity("city-" + threadIndex + "-" + i);
          person.setAddress(address);
          List<String> tags = Arrays.asList("tag-" + threadIndex + "-" + i + "-a",
              "tag-" + threadIndex + "-" + i + "-b");
          person.setTags(tags);

          PersonDto dto = personMapper.map(person);

          assertThat(dto.getName()).isEqualTo(name);
          assertThat(dto.getAge()).isEqualTo(age);
          assertThat(dto.getAddress()
              .getStreet()).isEqualTo(address.getStreet());
          assertThat(dto.getAddress()
              .getCity()).isEqualTo(address.getCity());
          assertThat(dto.getTags()).containsExactlyElementsOf(tags);
        }
        return null;
      });
    }

    List<Future<Void>> futures = executor.invokeAll(tasks, 60, TimeUnit.SECONDS);
    for (Future<Void> future : futures) {
      try {
        future.get();
      } catch (ExecutionException e) {
        throw new AssertionError("A concurrent mapping task failed.", e.getCause());
      }
    }
  }
}
