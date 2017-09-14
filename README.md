# ReMap - A declarative object mapper
  
ReMap is a library that simplifies conversion of objects field by field. It was developed to make conversion of database entities to DTOs (data transfer objects) easier. The use of ReMap makes converter classes and unit tests for converters obsolete: ReMap only needs a specification of what fields are to be mapped, but the amount of code that actually performs the assignments and transformations is minimized. Therefore the code that must be unit-tested is also minimized. 

ReMap maps a objects of a source to a destination type. As per default ReMap tries to map all fields from the source to the destination object if the fields have equal name and type. __Only differences must be specified when creating a mapper.__
  
# Mapping operations

The following operations can be declared on a mapper:
* `omitInSource`: omits a field in the source type and skips the mapping.
* `omitInDestination`: omits a field in the destination type and skips the mapping.
* `reassing`: converts a source field to the destination field of the same type while changing the field name.
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
* type inheritance
* mapping object references to fields 
* restrictive visibilities
* mapping of nested collections (Attention: maps are not collections!)
* mapping of maps using `replace` and a transformation function that maps key and values

# Limitations
* values of source and destination fields with equal types are not copied. Only references are copied. This may change in a future release.
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
    <version>0.0.4</version>
</dependency>
```

Der ReMap erzeugt zunächst Mapper-Instanzen, die dann mehrfach verwendet werden können. Folgender Codeausschnitt zeigt die Verwendung der API:

```java
Mapper<A, AResource> mapper = Mapping
                                     // Quelltyp ist A
                                     .from(A.class)
                                     // Zieltyp ist AResource
                                     .to(AResource.class)
                                     // A beinhaltet ein Feld, dass in AResource keine Entsprechung hat.
                                     .omitInSource(A::getMoreInA)
                                     // AResource beinhaltet ein Feld, dass in A keine Entsprechung hat.
                                     .omitInDestination(AResource::getMoreInAResource)
                                     // Das Feld zahlInA in A...
                                     .reassign(A::getZahlInA)
                                     // ...wird auf Feld zahlInAResource in AResource �bertragen
                                     .to(AResource::getZahlInAResource)
                                     // Da das Feld b in A und AResource vorhanden ist, wird ein implizites Mapping durchgeführt. Allerdings unterscheiden sich die Feldtypen: B und BResource. Dieses implizite Mapping wird durchgeführt, sofern ein Mapper für das Mapping B->BResource registriert wurde.
                                     .useMapper(Mapping.from(B.class)
                                                       .to(BResource.class)
                                                       .mapper())
                                     // Der folgende Aufruf validiert die Konfiguration und erzeugt im positiven Fall einen Mapper. Alle implizit möglichen Mappings werden zusätzlich zu den Konfigurationen durchgeführt. Werden die oben genannten Voraussetzungen nicht erfüllt, wird eine MappingException geworfen. 
                                     .mapper();
```

## Assoziationen

ReMap kann verwendet werden, um Objektassoziationen in "flache" Objekte zu konvertieren. Im folgenden Beispiel liegt eine Assoziaton von `A` nach `B` vor, allerdings sollen die Felder von `B` nach `AResource` übertragen werden. 

```
Mapper<A, AResource> mapper = Mapping
                                         .from(A.class)
                                         .to(AResource.class)
                                         .replace(A::getB, AResource::getInteger)
                                         .with(B::getInteger)
                                         .replace(A::getB, AResource::getNumber)
                                         .with(B::getNumber)
                                         .replace(A::getB, AResource::getString)
                                         .with(B::getString)
                                         .mapper();
```

Der Vorteil dieser Methode ist, dass die einzelnen Zuweisungen nicht getestet werden müssen.

# Tests

Durch die Verwendung der ReMap Bibliothek kann das programmieren von Konvertern eingespart werden. Damit entfallen auch die Tests der einzelnen Zuweisungen. ReMap validiert beim Aufruf der Methode `mapper()` die Mappingkonfiguration und prüft ob jedes Quell/Ziel-Feld in der Konfiguration berücksichtigt wurde. Da die einzelnen Zuweisungen nicht mehr getestet werden müssen, reicht es aus
* die Mapping-Konfiguration in einem Unit-Test unter der Erwartugn zu prüfen, dass die Methode `mapper()` keine `MappingException`geworfen wird.  
* die Transformationsfunktionen bei der Replace-Operation über einen Unit-Test abzutesten.

Es ist nicht nötig in einem Unit-Test konkrete Objekte zu mappen und das Zielobjekt vollständig zu prüfen.

# Spring-Integration

ReMap kann auf folgende Weise in Spring integriert werden, sodass Mapper-Instancen mit `@Autowired` injiziert werden können. Dabei wird die Typisierung des Mappers mit berücksichtigt.

Folgende Klasse konfiguriert eine Mapper-Instanz:

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

Für andere Komponenten stehen die konfigurierten Mapper-Instanzen mit folgendem Code-Snippet zur Verfügung:

```Java
  @Autowired
  Mapper<Person, Human> mapper1;

  @Autowired
  Mapper<Human, Person> mapper2;

`````
