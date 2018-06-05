# ReMap - A declarative object mapper

# Table of Contents
1. [Long story short](#long-story-short)
2. [About ReMap](#about-remap)
3. [Mapping operations](#mapping-operations)
4. [Validation](#validation)
5. [Features](#features)
6. [Limitations](#limitations)
7. [How to use](#how-to-use)
   1. [Object references](#object-references)
   2. [Mapping maps](#mapping-maps)
   2. [Transforming collections](#transforming-collections)
   3. [Bidirectional mapping](#bidirectional-mapping)
   4. [Tests](#tests)
8. [Spring integration](#spring-integration)
   1. [Spring Boot Issue](#spring-boot-issue)
9. [Migration guide](#migration-guide)
10. [How to contribute](#how-to-contribute)

## Long story short

ReMap is a library that simplifies conversion of objects field by field and greatly reduces the efforts for unit testing mapper classes. You can get this library via Maven Central using the following coordinates

```xml
<dependency>
    <groupId>com.remondis</groupId>
    <artifactId>remap</artifactId>
    <version>3.0.2</version>
</dependency>
```

...or in Gradle using `compile "com.remondis:remap:3.0.2"`.

The following code snippet shows how to map a source type to a destination type:

```java
Mapping.from(Customer.class)
    .to(Person.class)
    .omitInSource(Customer::getAddress)
    .omitInDestination(Person::getBodyHeight)
    .reassign(Customer::getTitle)
        .to(Person::getSalutation)
    .replace(Customer::getGender, Person::getGender)
        .withSkipWhenNull(Gender::valueOf)
    .mapper();
```

The resulting mapper does the following:

* maps `Customer` objects to `Person` objects
* `Customer` has the field `address`, `Person` does not, hence it is omitted
* `Person` has the field `bodyHeight` and `Customer` does not, hence it is omitted
* the field `Customer.title` is mapped to the field `Person.salutation`
* the field `Customer.gender` of type `String` is mapped to the Field `Customer.gender` of enum type `Gender`
  using `Gender.valueOf()` as a transformation function

You can find this demo and the involved classes [here](src/test/java/com/remondis/remap/demo/DemoTest.java)

## About ReMap

ReMap is a library that simplifies conversion of objects field by field. It was developed to make conversion of database entities to DTOs (data transfer objects) easier. The use of ReMap makes converter classes and unit tests for converters obsolete: ReMap only needs a specification of what fields are to be mapped, but the amount of code that actually performs the assignments and transformations is minimized. Therefore the code that must be unit-tested is also minimized.

ReMap maps a objects of a source to a destination type. As per default ReMap tries to map all fields from the source to the destination object if the fields have equal name and type. __Only differences between the source type and the target type must be specified when creating a mapper.__

## Mapping operations

The following operations can be declared on a mapper:
* `omitInSource`: omits a field in the source type and skips the mapping.
* `omitInDestination`: omits a field in the destination type and skips the mapping.
* `reassign`: maps a source field to the destination field of the same type while changing the field name.
* `replace`: converts a source field to the destination field while changing the field name and the type. To transform the source object into the destination type a transformation function is to be specified.
* `useMapper`: registers a specific mapper instance that is used to convert referenced types.

## Validation

ReMap validates the mapping configuration of a mapper **at instantiation time** and denies the following states:
* A source field was not mapped to a destination field
* A destination field is not covered by a source field
* Multiple mappings where defined for a destination field
* `omit` is specified for a source field that already has a mapping configuration

These validation rules make sure that all fields are covered by the mapping configuration when a mapper instance is created.

## Unit Testing

Since ReMap relies on getter and setter references like `Address::getId` to specify a mapping,
the compiler does not allow mappings between fields that are incompatible. When a mapper is
instantiated, ReMap performs the above-mentioned validations.

Thus, the only things you need to test in a unit test are:
* that `Mapping.mapper()` does not throw a `MappingException` telling you that one of the validations failed
* any transformation functions specified for `replace` operations

Optionally, you may want to assert that your specification matches certain expectations to prevent regressions
to creep into your codebase (see [Tests](#tests)).

## Features

ReMap supports
* out-of-the-box mapping of primitives, built-in types, custom Java Beans and enumeration values
* type inheritance
* mapping object references to fields
* restrictive visibilities
* mapping from interface to Java Bean type
* mapping of nested collections (Attention: maps are not collections!)
* mapping of maps using `replace` and a transformation function that maps key and values
* unit testing of mapping specifications
* mapping without invasively changing code of involved objects
* overwrite fields in an instance by specifying the target instance for the mapping

## Limitations

* objects that are part of the mapping process must meet the Java Bean convention
  * fields can have any visibility
  * fields have properly named public get/set methods
    * getter methods are mandatory for source and destination properties
    * setter methods are only mandatory for destination properties
  * fields of primitive type boolean comply with is-method convention as getter.
  * the declaring type has a public default constructor (this is only necessary for the destination object)
  * keywords like `transient` do not have an effect on the mapping
* non-static inner classes are not supported (they do not have a parameterless default constructor!)
* circular references are currently not supported
* mapping equal types does not copy object instances!
* multi-classloader environments are currently not supported. All types must be loaded by the same classloader.
* Generics cannot be used without limitations: It is possible to build a mapper for generic types, but due to the class literals used when declaring the mapping, the generic type informations gets lost.

## How to use

The short mapping shown under [Long story short](#long-story-short) uses all common operations. When a mapping becomes a little more complex the following code snippets may help.

### Object references

ReMap can be used to flatten object references. The following example maps the field `OrderEntity.address`
of type `Address` to the field `OrderDTO.addressId` of type `long`.

```java
Mapper<OrderEntity, OrderDTO> mapper = Mapping
    .from(OrderEntity.class)
    .to(OrderDTO.class)
    .replace(OrderEntity::getAddress, OrderDTO::getAddressId)
        .with(Addresss::getId)
    .mapper();
```

### Mapping maps

As mentioned above ReMap does not directly support the mapping of `java.util.Map`. The following example maps a map in `A` to a map of different key-value-types in `AResource`. The field `bmap` in `A` is a map that may look like this `Map<Integer, B>` while the target field `bmap` in `AResource` is a map of type `Map<String, BResource>`. For this mapping we need a function that transforms the map into another map of the specified type and a mapper to map `B` to `BResource`.

Use the following code snippet to map maps using the `replace` operation:

```java
Mapper<B, BResource> bMapper = Mapping.from(B.class)
    .to(BResource.class)
    .mapper();

 Mapper<A, AResource> mapper = Mapping.from(A.class)
    .to(AResource.class)
    // specify a replace operation involving the source and the destination field holding the map
    .replace(A::getBmap, AResource::getBmap)
        // specify a transformation function (Map<Integer, B>) -> Map<String, BResource>
        .with(iToBMap -> {
          return iToBMap.entrySet()
            .stream()
            .map(e -> {
              // Perform the type conversion while iterating over the entry set
              return new AbstractMap.SimpleEntry<String, BResource>(String.valueOf(e.getKey()),
                  bMapper.map(e.getValue()));
            })
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        })
    .useMapper(bMapper)
    .mapper();
```

### Transforming collections

When performing a `replace` operation on collections in earlier versions of ReMap you had to manually iterate over the collection to apply the conversion. Since ReMap version `1.0.0` you can use the operation `replaceCollection` to apply the transformation function automatically on the collection items.

The following code snippet shows how to use `replaceCollection`:

```java
Mapper<Source, Destination> mapper = Mapping.from(Source.class)
      .to(Destination.class)
      .replaceCollection(Source::getIds, Destination::getIds)
      .with(id -> Id.builder()
        .id(id)
        .build())
      .mapper();
```

The following code asserts the above mapping:

```
AssertMapping.of(mapper)
      .expectReplaceCollection(Source::getIds, Destination::getIds)
      .andTest(id -> Id.builder()
        .id(id)
        .build())
      .ensure();
```

You can find this demo and the involved classes [here](src/test/java/com/remondis/remap/flatCollectionMapping/DemoTest.java)

### Bidirectional mapping

ReMap provides a class to combine two mapper instances to a bidirectional mapping. Given the following mappings:

```
Mapper<Person, Human> to = Mapping.from(Person.class)
    .to(Human.class)
    .mapper();
Mapper<Human, Person> from = Mapping.from(Human.class)
    .to(Person.class)
    .mapper();
```

a bidirectional mapper can be created to map a `Person` to `Human` and vice-versa:

```
BidirectionalMapper<Person, Human> bidirectionalMapper = BidirectionalMapper.of(to, from);

Person person = new Person("Peter");
Human human = bidirectionalMapper.map(person);
Person mappedBackToPerson = bidirectionalMapper.mapFrom(human);
```

You can find this demo and the involved classes [here](src/test/java/com/remondis/remap/bidirectional/BidirectionalDemo.java)


### Tests

ReMap provides an easy way to assert the mapping specification for a mapper instance. These assertions should be used in unit tests to provide regression tests for your mapping configuration. The following example shows how to assert a mapping specification:

Given the following mapping...

```java
Mapper<B, BResource> bMapper = Mapping.from(B.class)
    .to(BResource.class)
    .mapper();

Mapper<A, AResource> mapper = Mapping.from(A.class)
    .to(AResource.class)
    .reassign(A::getString)
        .to(AResource::getAnotherString)
    .replace(A::getInteger, AResource::getIntegerAsString)
        .with(String::valueOf)
    .omitInSource(A::getOmitted)
    .omitInDestination(AResource::getOmitted)
    .useMapper(bMapper)
    .mapper();
```

...the following assertion can be made to ensure regression validity for the mapping specification:

```java
AssertMapping.of(mapper)
    .expectReassign(A::getString)
        .to(AResource::getAnotherString)
    .expectReplace(A::getInteger, AResource::getIntegerAsString)
        .andTest(String::valueOf)
    .expectOmitInSource(A::getOmitted)
    .expectOmitInDestination(AResource::getOmitted)
    .ensure();
```

The asserts check that the expected mappings are also configured on the specified mapper. If there are differences, the `ensure()` method will throw an assertion error.

Note: The `replace` operation supports two null-strategies and the mapper needs to specify the same strategy as the asserts! The transformation function in this example is checked against a `null` when `ensure()` is invoked. If the `replace` operation was added using `withSkipWhenNull()` the specified transformation function is not checked against `null`.

## Spring Integration

ReMap can be nicely integrated in Spring Applications so that mapper instances can be injected using `@Autowired`. Spring also checks the generic type of the mapper to autowire the correct mapping requested.

The following bean configuration creates mappers to convert a `Person` into `Human` and vice versa:

```java
@Configuration
static class TestConfiguration {
  @Bean
  BidirectionalMapper<Person, Human> bidiPersonHumanMapper(Mapper<Person, Human> personHumanMapper,
                                                           Mapper<Human, Person> humanPersonMapper) {
    return BidirectionalMapper.of(personHumanMapper, humanPersonMapper);
  }

  @Bean
  Mapper<Person, Human> personHumanMapper(){
    return Mapping.from(Person.class)
        .to(Human.class)
        .mapper();
  }
  @Bean
  Mapper<Human, Person> humanPersonMapper(){
    return Mapping.from(Human.class)
        .to(Person.class)
        .mapper();
  }
}
```

Use the following code snippet in components to inject the mapper instances:

```Java
  @Autowired
  Mapper<Person, Human> mapper1;

  @Autowired
  Mapper<Human, Person> mapper2;

`````

### Spring Boot Issue

When using the Spring Boot Framework in version <= 1.5.13.RELEASE in combination with ReMap 4.0.0 a known issue can occur:

If you get the following exception...
```
        Caused by:
        java.lang.IllegalArgumentException
            at org.objectweb.asm.ClassVisitor.<init>(Unknown Source)
            at net.sf.cglib.core.DebuggingClassWriter.<init>(DebuggingClassWriter.java:49)
            at net.sf.cglib.core.DefaultGeneratorStrategy.getClassVisitor(DefaultGeneratorStrategy.java:30)
            at net.sf.cglib.core.DefaultGeneratorStrategy.generate(DefaultGeneratorStrategy.java:24)
            at net.sf.cglib.core.AbstractClassGenerator.generate(AbstractClassGenerator.java:329)
            at net.sf.cglib.core.AbstractClassGenerator$ClassLoaderData$3.apply(AbstractClassGenerator.java:93)
            at net.sf.cglib.core.AbstractClassGenerator$ClassLoaderData$3.apply(AbstractClassGenerator.java:91)
            at net.sf.cglib.core.internal.LoadingCache$2.call(LoadingCache.java:54)
            at java.util.concurrent.FutureTask.run(FutureTask.java:266)
            at net.sf.cglib.core.internal.LoadingCache.createEntry(LoadingCache.java:61)
            at net.sf.cglib.core.internal.LoadingCache.get(LoadingCache.java:34)
            at net.sf.cglib.core.AbstractClassGenerator$ClassLoaderData.get(AbstractClassGenerator.java:116)
            at net.sf.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:291)
            at net.sf.cglib.core.KeyFactory$Generator.create(KeyFactory.java:221)
            at net.sf.cglib.core.KeyFactory.create(KeyFactory.java:174)
            at net.sf.cglib.core.KeyFactory.create(KeyFactory.java:153)
            at net.sf.cglib.proxy.Enhancer.<clinit>(Enhancer.java:73)
            ... 1 more
```
...you can workaround this by adding the dependency `compile "com.jayway.jsonpath:json-path:2.4.0"` to your project. This error is caused by a library that ships classes of a dependency in an incompatible version.

#### Why does this happen?

The library `net.minidev:accessors-smart:1.1` which is used as a transitive dependency requires `org.ow2.asm:asm:5.0.3`. ReMap declares `org.ow2.asm` in version 6.0 as dependecy so this newer version is chosen by dependency management of Maven or Gradle.

The problem is that this library does not only declare this dependency but ships it's own copy of the package `org.objectweb.asm` in this older version. Even if your dependecy management seems to choose `org.ow2.asm:asm:6.0`, the classes of `5.0.3` stay in the classpath.


```
+--- org.springframework.boot:spring-boot-starter-test:1.5.13.RELEASE
|    +--- com.jayway.jsonpath:json-path:2.2.
|    |    +--- net.minidev:json-smart:2.2.
|    |    |    \--- net.minidev:accessors-smart:1.
|    |    |         \--- org.ow2.asm:asm:5.0.3 -> 6.0 // Looks like 6.0 is chosen, but the classes of 5.0.3 stay in the classpath.
```

This bug was fixed in `net.minidev:accessors-smart:1.2` but is still present in Spring Boot Framework in version <= 1.5.13.RELEASE. Since `com.jayway.jsonpath:json-path:2.4.0` depends on this newer version it seems to be the safest way to overwrite the version of this library.

This workaround was tested and should work for most cases. Please file an issue if you are experiencing problems.

# Migration guide

## Migration from 3.x.x to 4.x.x

Since the source property rules relaxed a little bit, no setter methods are required for the source type of the mapping. This was done to support mapping from read-only properties. This has one drawback: In earlier version read-only properties were not recognized as properties used for the mapping. This changed now and mappings that used an earlier version of ReMap in combination with read-only properties now need an `omitInSource` specification.

## Migration from 2.x.x to 3.x.x

There were API changes that break backward compatibility:

* The method `com.remondis.remap.ReplaceCollectionAssertBuilder.andTestButSkipWhenNull(Transform<RD, RS> transformation)` changed  to `com.remondis.remap.ReplaceCollectionAssertBuilder.andSkipWhenNull()`. Specifying transform function is not longer required. Please check your mapping assertions to match the new API.

## Migration from 1.x.x to 2.x.x

There were API changes that break backward compatibility:

* The method `com.remondis.remap.ReplaceAssertBuilder.andTestButSkipWhenNull(Transform<RD, RS> transformation)` changed  to `com.remondis.remap.ReplaceAssertBuilder.andSkipWhenNull()`. Specifying transform function is not longer required. Please check your mapping assertions to match the new API.
* The generic type in `com.remondis.remap.Transform<D, S>` changed to `com.remondis.remap.Transform<S, D>`
* According to the above change please check your mapping configuration and assertions for the `replace` operation to match the new API. The affected classes are:
    * `com.remondis.remap.ReplaceBuilder`
    * `com.remondis.remap.ReplaceAssertBuilder`
    * `com.remondis.remap.ReplaceCollectionBuilder`
    * `com.remondis.remap.ReplaceCollectionAssertBuilder`
    * `com.remondis.remap.ReplaceTransformation`
    * `com.remondis.remap.Transformation`

# How to contribute
Please refer to the project's [contribution guide](CONTRIBUTE.md)