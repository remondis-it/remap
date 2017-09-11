# ReMap - Ein deklarativer Object-Mapper
  
ReMap ist ein Mapping-Framework zum automatischen Konvertieren von Objekten mit ähnlichen Feldern. Diese Bibliothek wurde entwickelt, um die Konvertierung von Datenbank-Entitäten in Data Transfer Objekte zu automatisieren und somit den Aufwand für die Erstellung von Konvertierungsklassen und deren Unit-Tests zu minimieren. Dieses Ziel wird erreicht, indem die Mapping-Operationen deklarativ konfiguriert werden, die eigentliche Zuweisung und Transformation der Werte aber über die Bibliothek ausgeführt werden. So ist es nicht mehr nötig, Zuweisungen manuell zu programmieren und zu testen. 

ReMap benötigt die Information von welchem Quelltyp in welchen Zieltyp übertragen wird. Standardmäßg wird der Mapper alle Felder mit gleichem Typ und gleichem Namen automatisch übertragen. __Es müssen nur Ausnahmen konfiguriert werden!__

# Operationen

Folgende Operationen können für das Mapping deklariert werden:
* `omitInSource`: überspringt ein Feld im Quellobjekt und führt kein Mapping aus.
* `omitInDestination`: überspringt ein Feld im Zielobjekt und führt kein Mapping aus. 
* `reassing`: überträgt ein Feld aus dem Quellobjekt auf ein Feld gleichen Typs im Zielobjekt wobei sich die Feldnamen unterscheiden.
* `replace`: überträgt ein Feld aus dem Quellobjekt auf ein Feld im Zielobjekt wobei sich Feldnamen und Typen unterscheiden. Eine angegebene Transformationsfunktion überführt die Datentypen.  
* `useMapper`: Registriert eine weitere Mapper-Instanz, die f�r die Konvertierung weiterer Datentypen verwendet wird. 

# Validierung

Die Mapper validiert die Mapping-Konfiguration und lehnt in den folgenden Situationen ab:
* Ein Feld im Quellobjekt hat kein passendes Feld im Zielobjekt
* Ein Feld im Zielobjekt hat kein passendes Feld im Quellobjekt
* Mehrere Operationen werden auf einem Zielfeld ausgeführt
* `omit` wird auf Quellfeld angewendet obwohl bereits ein anderes Mapping definiert wurde.

Mit diesen Validierungsregeln ist beim Erstellen der Mapper-Instanz sicher gestellt, dass jedes Quell/Zielfeld vom Mapping berücksichtigt bzw. übersprungen wird.

# Funktionen

ReMap unterstützt
* Vererbungshierarchien
* Mapping von Objektassoziationen auf Felder 
* Restriktive Sichtbarkeiten
* Mapping von verschachtelten Collections (Achtung: Maps sind keine Collections!)
* manuelle Konvertierung von Maps durch die Operation `replace` und einer Transformation

# Einschränkungen
* Objekte in Quelle und Ziel die den gleichen Typ aufweisen werden nicht kopiert.
* Die Objekte die am Mapping beteiligt sind müssen der Java Bean Konvention entsprechen
  * Felder können beliebige Sichtbarkeit aufweisen
  * Felder haben öffentliche Getter/Setter-Methoden mit korrekter Benamung
  * Boolean-Felder öffentliche haben Is/Setter-Methoden
  * Das Objekt hat einen parameterlosen Standard-Konstruktor 
  * Schlüsselwörter wie `transient` haben keine Auswirkungen auf das Mapping
* Multi-Classloader-Umgebungen sind nicht unterstützt. Sämtliche Typen müssen durch den selben Klassloader geladen sein


# Verwendung

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

 