# ReMap - A declarative object mapper

ReMap is a library that simplifies conversion of objects field by field. It was developed to make conversion of database entities to DTOs (data transfer objects) easier. The use of ReMap makes converter classes and unit tests for converters obsolete: ReMap only needs a specification of what fields are to be mapped, but the amount of code that actually performs the assignments and transformations is minimized. Therefore the code that must be unit-tested is also minimized.

ReMap maps a objects of a source to a destination type. As per default ReMap tries to map all fields from the source to the destination object if the fields have equal name and type. __Only differences must be specified when creating a mapper.__

# Mapping operations

The following operations can be declared on a mapper:
* `omitInSource`: omits a field in the source type and skips the mapping.
* `omitInDestination`: omits a field in the destination type and skips the mapping.
* `reassign`: converts a source field to the destination field of the same type while changing the field name.
* `replace`: converts a source field to the destination field while changing the field name and the type. To transform the source object into the destination type a transformation function is to be specified.
* `useMapper`: Registers a specific mapper instance that is used to convert referenced types.

# Validation

ReMap validates the mapping configuration und denies the following states:
* A source field was not mapped to a destination field
* A destination field is not covered by a source field
* Multiple mappings where defined for a destination field
* `omit` is specified for a source field that already has a mapping configuration

This validation rules make sure that all fields are covered by the mapping configuration when a mapper instance is created.

# Features

ReMap supports
* mapping of primitives, build-in types, custom Java Beans and enumeration values
* type inheritance
* mapping object references to fields
* restrictive visibilities
* mapping of nested collections (Attention: maps are not collections!)
* mapping of maps using `replace` and a transformation function that maps key and values

# Limitations

* objects that are part of the mapping process must meet the Java Bean convention
  * fields can have any visibility
  * fields have properly named public get/set methods
  * fields of type Boolean/boolean have public is/set methods
  * the declaring type has a public default constructor
  * keywords like `transient` do not have an effect on the mapping
* multi-classloader environments are currently not supported. All types must be loaded by the same classloader.

# How to use

ReMap is available through Maven Central using the following Maven dependency:

```
<dependency>
    <groupId>com.remondis</groupId>
    <artifactId>remap</artifactId>
    <version>0.0.5</version>
</dependency>
```

ReMap creates mapper instances that can be reused. The following code shows how to use the API:

```java
Mapper<A, AResource> mapper = Mapping
                                     // source type is A
                                     .from(A.class)
                                     // destination type is AResource
                                     .to(AResource.class)
                                     // A has a field that does not have a counterpart in AResource
                                     .omitInSource(A::getMoreInA)
                                     // AResource has a field that does not have a counterpart in A
                                     .omitInDestination(AResource::getMoreInAResource)
                                     // the field zahlInA in A...
                                     .reassign(A::getZahlInA)
                                     // ...will be mapped to field zahlInAResource in AResource
                                     .to(AResource::getZahlInAResource)
                                     // if A references an object of type B that is to be mapped to BResource in AResource,
                                     // we have to register a mapper that specifies how to map B to BResource
                                     .useMapper(Mapping.from(B.class)
                                                       .to(BResource.class)
                                                       .mapper())
                                     // the following method validates the mapping configuration and returns a Mapper instance
                                     .mapper();
```

## Object references

ReMap can be used to flatten object references. The following example shows how to map fields of `B` referenced by `A` to the type `AResource`.

```
Mapper<A, AResource> mapper = Mapping
                                     .from(A.class)
                                     .to(AResource.class)
                                     // Get B referenced by A and map it to field integer in AResource
                                     .replace(A::getB, AResource::getInteger)
                                     // use the value of field integer in B
                                     .with(B::getInteger)
                                     // same example, different fields
                                     .replace(A::getB, AResource::getNumber)
                                     .with(B::getNumber)
                                     .mapper();
```

One advantage here is that the actual mapping must not be tested with unit tests.

## Mapping maps

As mentioned above ReMap does not directly support the mapping of `java.util.Map`. The following example maps a map in `A` to a map of different key-value-types in `AResource`. The field `bmap` in `A` is a map that may look like this `Map<Integer, B>` while the target field `bmap` in `AResource` is a map of type `Map<String, BResource>`. For this mapping we need a function that transforms the map into another map of the specified type and a mapper to map `B` to `BResource`.

Use the following code snippet to map maps using the `replace` operation:

```
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




## Tests

ReMap makes converter classes and corresponding unit tests obsolete because the actual get/set calls must not be tested. To ensure that the mapping configuration covers all fields ReMap validates the configuration when `mapper()` is invoked. The only things that must be tested in unit tests are
* that the mapping configurations are valid. You only have to write a simple unit test that asserts that `mapper()` does not throw a `MappingException`.
* the transformation functions specified for `replace` operations
* that the mapping specification is correct. See "Asserting the mapping"

### Asserting the mapping

ReMap provides an easy way to assert the mapping specification for a mapper instance. This assertions should be used in unit-tests to provide regression tests for your mapping configuration. The following example shows how to assert a mapping specification:

Given the following mapping...

```
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

```
AssertMapping.of(mapper)
             .expectReassign(A::getString)
             .to(AResource::getAnotherString)
             .expectReplace(A::getInteger, AResource::getIntegerAsString)
             .andTest(String::valueOf)
             .expectOmitInSource(A::getOmitted)
             .expectOmitInDestination(AResource::getOmitted)
             .ensure();
```

The asserts check that the expected mappings are also configured on the specified mapper. If there occur differences, the `ensure()` method will throw an assertion error.

Note: The `replace` operation supports two null-strategies and the mapper needs to specify the same strategy as the asserts! The transformation function in this example is checked against a `null` when `ensure()` is invoked. If the `replace` operation was added using `withSkipWhenNull()` the specified transformation function is not checked against `null`.

## Spring-Integration

ReMap can be nicely integrated in Spring Applications so that mapper instances can be injected using `@Autowired`. Spring also checks the generic type of the mapper to autowire the correct mapping requested.

The following bean configuration creates a mappers to convert a `Person` into `Human` and vice-versa:

```java
@Configuration
static class TestConfiguration {
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


# How to contribute

Please refer to the project's [contribution guide](CONTRIBUTE.md)