package com.remondis.remap.records;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;

public class RecordMappingTest {

  // ==================== POJO -> Record ====================

  @Test
  public void shouldMapPojoToRecord_implicit() {
    Mapper<PersonPojo, PersonRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonRecord.class)
        .mapper();

    PersonPojo source = new PersonPojo("Alice", 30, "alice@example.com", true);
    PersonRecord result = mapper.map(source);

    assertEquals("Alice", result.name());
    assertEquals(30, result.age());
    assertEquals("alice@example.com", result.email());
    assertTrue(result.active());
  }

  @Test
  public void shouldMapPojoToRecord_withReassign() {
    Mapper<PersonPojo, PersonResourceRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonResourceRecord.class)
        .reassign(PersonPojo::getName)
        .to(PersonResourceRecord::fullName)
        .reassign(PersonPojo::getAge)
        .to(PersonResourceRecord::yearsOld)
        .reassign(PersonPojo::getEmail)
        .to(PersonResourceRecord::emailAddress)
        .reassign(PersonPojo::isActive)
        .to(PersonResourceRecord::isActive)
        .mapper();

    PersonPojo source = new PersonPojo("Bob", 25, "bob@example.com", false);
    PersonResourceRecord result = mapper.map(source);

    assertEquals("Bob", result.fullName());
    assertEquals(25, result.yearsOld());
    assertEquals("bob@example.com", result.emailAddress());
    assertFalse(result.isActive());
  }

  @Test
  public void shouldMapPojoToRecord_withReplace() {
    Mapper<PersonPojo, PersonResourceRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonResourceRecord.class)
        .replace(PersonPojo::getName, PersonResourceRecord::fullName)
        .with(name -> name.toUpperCase())
        .reassign(PersonPojo::getAge)
        .to(PersonResourceRecord::yearsOld)
        .reassign(PersonPojo::getEmail)
        .to(PersonResourceRecord::emailAddress)
        .reassign(PersonPojo::isActive)
        .to(PersonResourceRecord::isActive)
        .mapper();

    PersonPojo source = new PersonPojo("Charlie", 35, "charlie@example.com", true);
    PersonResourceRecord result = mapper.map(source);

    assertEquals("CHARLIE", result.fullName());
  }

  @Test
  public void shouldMapPojoToRecord_withOmitInSource() {
    Mapper<PersonPojo, PersonRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonRecord.class)
        .omitInSource(PersonPojo::getEmail)
        .omitInDestination(PersonRecord::email)
        .mapper();

    PersonPojo source = new PersonPojo("Alice", 30, "alice@example.com", true);
    PersonRecord result = mapper.map(source);

    assertEquals("Alice", result.name());
    assertEquals(30, result.age());
    assertNull(result.email());
    assertTrue(result.active());
  }

  @Test
  public void shouldMapPojoToRecord_withOmitInDestination() {
    Mapper<PersonPojo, PersonWithExtraRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonWithExtraRecord.class)
        .omitInSource(PersonPojo::getEmail)
        .omitInSource(PersonPojo::isActive)
        .omitInDestination(PersonWithExtraRecord::extra)
        .mapper();

    PersonPojo source = new PersonPojo("Alice", 30, "alice@example.com", true);
    PersonWithExtraRecord result = mapper.map(source);

    assertEquals("Alice", result.name());
    assertEquals(30, result.age());
    assertNull(result.extra());
  }

  // ==================== Record -> POJO ====================

  @Test
  public void shouldMapRecordToPojo_implicit() {
    Mapper<PersonRecord, PersonPojo> mapper = Mapping.from(PersonRecord.class)
        .to(PersonPojo.class)
        .mapper();

    PersonRecord source = new PersonRecord("Diana", 28, "diana@example.com", false);
    PersonPojo result = mapper.map(source);

    assertEquals("Diana", result.getName());
    assertEquals(28, result.getAge());
    assertEquals("diana@example.com", result.getEmail());
    assertFalse(result.isActive());
  }

  @Test
  public void shouldMapRecordToPojo_withReassign() {
    Mapper<PersonRecord, PersonResourcePojo> mapper = Mapping.from(PersonRecord.class)
        .to(PersonResourcePojo.class)
        .reassign(PersonRecord::name)
        .to(PersonResourcePojo::getFullName)
        .reassign(PersonRecord::age)
        .to(PersonResourcePojo::getYearsOld)
        .reassign(PersonRecord::email)
        .to(PersonResourcePojo::getEmailAddress)
        .reassign(PersonRecord::active)
        .to(PersonResourcePojo::isIsActive)
        .mapper();

    PersonRecord source = new PersonRecord("Eve", 40, "eve@example.com", true);
    PersonResourcePojo result = mapper.map(source);

    assertEquals("Eve", result.getFullName());
    assertEquals(40, result.getYearsOld());
    assertEquals("eve@example.com", result.getEmailAddress());
    assertTrue(result.isIsActive());
  }

  @Test
  public void shouldMapRecordToPojo_withReplace() {
    Mapper<PersonRecord, PersonResourcePojo> mapper = Mapping.from(PersonRecord.class)
        .to(PersonResourcePojo.class)
        .replace(PersonRecord::name, PersonResourcePojo::getFullName)
        .with(name -> "Dr. " + name)
        .reassign(PersonRecord::age)
        .to(PersonResourcePojo::getYearsOld)
        .reassign(PersonRecord::email)
        .to(PersonResourcePojo::getEmailAddress)
        .reassign(PersonRecord::active)
        .to(PersonResourcePojo::isIsActive)
        .mapper();

    PersonRecord source = new PersonRecord("Frank", 50, "frank@example.com", false);
    PersonResourcePojo result = mapper.map(source);

    assertEquals("Dr. Frank", result.getFullName());
  }

  // ==================== Record -> Record ====================

  @Test
  public void shouldMapRecordToRecord_implicit() {
    Mapper<PersonRecord, PersonRecord> mapper = Mapping.from(PersonRecord.class)
        .to(PersonRecord.class)
        .mapper();

    PersonRecord source = new PersonRecord("Grace", 22, "grace@example.com", true);
    PersonRecord result = mapper.map(source);

    assertEquals("Grace", result.name());
    assertEquals(22, result.age());
    assertEquals("grace@example.com", result.email());
    assertTrue(result.active());
  }

  @Test
  public void shouldMapRecordToRecord_withReassign() {
    Mapper<PersonRecord, PersonResourceRecord> mapper = Mapping.from(PersonRecord.class)
        .to(PersonResourceRecord.class)
        .reassign(PersonRecord::name)
        .to(PersonResourceRecord::fullName)
        .reassign(PersonRecord::age)
        .to(PersonResourceRecord::yearsOld)
        .reassign(PersonRecord::email)
        .to(PersonResourceRecord::emailAddress)
        .reassign(PersonRecord::active)
        .to(PersonResourceRecord::isActive)
        .mapper();

    PersonRecord source = new PersonRecord("Hank", 60, "hank@example.com", false);
    PersonResourceRecord result = mapper.map(source);

    assertEquals("Hank", result.fullName());
    assertEquals(60, result.yearsOld());
    assertEquals("hank@example.com", result.emailAddress());
    assertFalse(result.isActive());
  }

  @Test
  public void shouldMapRecordToRecord_withReplace() {
    Mapper<PersonRecord, PersonResourceRecord> mapper = Mapping.from(PersonRecord.class)
        .to(PersonResourceRecord.class)
        .replace(PersonRecord::name, PersonResourceRecord::fullName)
        .with(n -> n + " (verified)")
        .replace(PersonRecord::age, PersonResourceRecord::yearsOld)
        .with(a -> a + 1)
        .reassign(PersonRecord::email)
        .to(PersonResourceRecord::emailAddress)
        .reassign(PersonRecord::active)
        .to(PersonResourceRecord::isActive)
        .mapper();

    PersonRecord source = new PersonRecord("Ivy", 29, "ivy@example.com", true);
    PersonResourceRecord result = mapper.map(source);

    assertEquals("Ivy (verified)", result.fullName());
    assertEquals(30, result.yearsOld());
  }

  // ==================== Nested / Hierarchical Mapping ====================

  @Test
  public void shouldMapPojoToRecord_withNestedMapper() {
    Mapper<AddressPojo, AddressRecord> addressMapper = Mapping.from(AddressPojo.class)
        .to(AddressRecord.class)
        .mapper();

    Mapper<PersonWithAddressPojo, PersonWithAddressRecord> mapper = Mapping.from(PersonWithAddressPojo.class)
        .to(PersonWithAddressRecord.class)
        .useMapper(addressMapper)
        .mapper();

    PersonWithAddressPojo source = new PersonWithAddressPojo("John", new AddressPojo("Main St", "Springfield"));
    PersonWithAddressRecord result = mapper.map(source);

    assertEquals("John", result.name());
    assertNotNull(result.address());
    assertEquals("Main St", result.address()
        .street());
    assertEquals("Springfield", result.address()
        .city());
  }

  @Test
  public void shouldMapRecordToPojo_withNestedMapper() {
    Mapper<AddressRecord, AddressPojo> addressMapper = Mapping.from(AddressRecord.class)
        .to(AddressPojo.class)
        .mapper();

    Mapper<PersonWithAddressRecord, PersonWithAddressPojo> mapper = Mapping.from(PersonWithAddressRecord.class)
        .to(PersonWithAddressPojo.class)
        .useMapper(addressMapper)
        .mapper();

    PersonWithAddressRecord source = new PersonWithAddressRecord("Jane", new AddressRecord("Oak Ave", "Shelbyville"));
    PersonWithAddressPojo result = mapper.map(source);

    assertEquals("Jane", result.getName());
    assertNotNull(result.getAddress());
    assertEquals("Oak Ave", result.getAddress()
        .getStreet());
    assertEquals("Shelbyville", result.getAddress()
        .getCity());
  }

  @Test
  public void shouldMapRecordToRecord_withNestedMapper() {
    Mapper<AddressRecord, AddressRecord> addressMapper = Mapping.from(AddressRecord.class)
        .to(AddressRecord.class)
        .mapper();

    Mapper<PersonWithAddressRecord, PersonWithAddressRecord> mapper = Mapping.from(PersonWithAddressRecord.class)
        .to(PersonWithAddressRecord.class)
        .useMapper(addressMapper)
        .mapper();

    PersonWithAddressRecord source = new PersonWithAddressRecord("Kate", new AddressRecord("Elm St", "Capital City"));
    PersonWithAddressRecord result = mapper.map(source);

    assertEquals("Kate", result.name());
    assertEquals("Elm St", result.address()
        .street());
    assertEquals("Capital City", result.address()
        .city());
  }

  // ==================== AssertMapping ====================

  @Test
  public void shouldAssertPojoToRecordMapping() {
    Mapper<PersonPojo, PersonRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonRecord.class)
        .mapper();

    AssertMapping.of(mapper)
        .expectOthersToBeOmitted()
        .ensure();
  }

  @Test
  public void shouldAssertRecordToPojoMapping() {
    Mapper<PersonRecord, PersonPojo> mapper = Mapping.from(PersonRecord.class)
        .to(PersonPojo.class)
        .mapper();

    AssertMapping.of(mapper)
        .expectOthersToBeOmitted()
        .ensure();
  }

  @Test
  public void shouldAssertRecordToRecordMapping_withReassign() {
    Mapper<PersonRecord, PersonResourceRecord> mapper = Mapping.from(PersonRecord.class)
        .to(PersonResourceRecord.class)
        .reassign(PersonRecord::name)
        .to(PersonResourceRecord::fullName)
        .reassign(PersonRecord::age)
        .to(PersonResourceRecord::yearsOld)
        .reassign(PersonRecord::email)
        .to(PersonResourceRecord::emailAddress)
        .reassign(PersonRecord::active)
        .to(PersonResourceRecord::isActive)
        .mapper();

    AssertMapping.of(mapper)
        .expectReassign(PersonRecord::name)
        .to(PersonResourceRecord::fullName)
        .expectReassign(PersonRecord::age)
        .to(PersonResourceRecord::yearsOld)
        .expectReassign(PersonRecord::email)
        .to(PersonResourceRecord::emailAddress)
        .expectReassign(PersonRecord::active)
        .to(PersonResourceRecord::isActive)
        .ensure();
  }

  @Test
  public void shouldAssertRecordMapping_withReplace() {
    Mapper<PersonPojo, PersonResourceRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonResourceRecord.class)
        .replace(PersonPojo::getName, PersonResourceRecord::fullName)
        .with(name -> name == null ? null : name.toUpperCase())
        .reassign(PersonPojo::getAge)
        .to(PersonResourceRecord::yearsOld)
        .reassign(PersonPojo::getEmail)
        .to(PersonResourceRecord::emailAddress)
        .reassign(PersonPojo::isActive)
        .to(PersonResourceRecord::isActive)
        .mapper();

    AssertMapping.of(mapper)
        .expectReplace(PersonPojo::getName, PersonResourceRecord::fullName)
        .andTest(name -> name == null ? null : name.toUpperCase())
        .expectReassign(PersonPojo::getAge)
        .to(PersonResourceRecord::yearsOld)
        .expectReassign(PersonPojo::getEmail)
        .to(PersonResourceRecord::emailAddress)
        .expectReassign(PersonPojo::isActive)
        .to(PersonResourceRecord::isActive)
        .ensure();
  }

  // ==================== Error Cases ====================

  // ==================== MapInto (map with existing record) ====================

  @Test
  public void shouldMapIntoRecord_overwriteAllFields() {
    Mapper<PersonPojo, PersonRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonRecord.class)
        .mapper();

    PersonPojo source = new PersonPojo("Alice", 30, "alice@example.com", true);
    PersonRecord existing = new PersonRecord("Old", 0, "old@example.com", false);

    PersonRecord result = mapper.map(source, existing);

    assertEquals("Alice", result.name());
    assertEquals(30, result.age());
    assertEquals("alice@example.com", result.email());
    assertTrue(result.active());
  }

  @Test
  public void shouldMapIntoRecord_preserveOmittedFields() {
    Mapper<PersonPojo, PersonRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonRecord.class)
        .omitInSource(PersonPojo::getAge)
        .omitInDestination(PersonRecord::age)
        .omitInSource(PersonPojo::isActive)
        .omitInDestination(PersonRecord::active)
        .mapper();

    PersonPojo source = new PersonPojo("Alice", 99, "alice@example.com", true);
    PersonRecord existing = new PersonRecord("Old", 42, "old@example.com", true);

    PersonRecord result = mapper.map(source, existing);

    // Mapped fields are overwritten
    assertEquals("Alice", result.name());
    assertEquals("alice@example.com", result.email());
    // Omitted fields are preserved from existing record
    assertEquals(42, result.age());
    assertTrue(result.active());
  }

  @Test
  public void shouldMapIntoRecord_returnsNewInstance() {
    Mapper<PersonPojo, PersonRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonRecord.class)
        .mapper();

    PersonPojo source = new PersonPojo("Alice", 30, "alice@example.com", true);
    PersonRecord existing = new PersonRecord("Old", 0, "old@example.com", false);

    PersonRecord result = mapper.map(source, existing);

    // Records are immutable — result must be a new instance
    assertFalse(result == existing);
  }

  @Test
  public void shouldMapIntoRecord_recordToRecord_preserveOmitted() {
    Mapper<PersonRecord, PersonResourceRecord> mapper = Mapping.from(PersonRecord.class)
        .to(PersonResourceRecord.class)
        .reassign(PersonRecord::name)
        .to(PersonResourceRecord::fullName)
        .reassign(PersonRecord::email)
        .to(PersonResourceRecord::emailAddress)
        .omitInSource(PersonRecord::age)
        .omitInSource(PersonRecord::active)
        .omitInDestination(PersonResourceRecord::yearsOld)
        .omitInDestination(PersonResourceRecord::isActive)
        .mapper();

    PersonRecord source = new PersonRecord("New", 10, "new@example.com", false);
    PersonResourceRecord existing = new PersonResourceRecord("Old", 50, "old@example.com", true);

    PersonResourceRecord result = mapper.map(source, existing);

    // Mapped fields overwritten
    assertEquals("New", result.fullName());
    assertEquals("new@example.com", result.emailAddress());
    // Omitted fields preserved from existing
    assertEquals(50, result.yearsOld());
    assertTrue(result.isActive());
  }

  @Test
  public void shouldMapIntoRecord_withNullDestination_createsNew() {
    Mapper<PersonPojo, PersonRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonRecord.class)
        .mapper();

    PersonPojo source = new PersonPojo("Alice", 30, "alice@example.com", true);
    PersonRecord result = mapper.map(source, null);

    assertEquals("Alice", result.name());
    assertEquals(30, result.age());
  }

  @Test
  public void shouldMapIntoRecord_withNestedMapper() {
    Mapper<AddressPojo, AddressRecord> addressMapper = Mapping.from(AddressPojo.class)
        .to(AddressRecord.class)
        .mapper();

    Mapper<PersonWithAddressPojo, PersonWithAddressRecord> mapper = Mapping.from(PersonWithAddressPojo.class)
        .to(PersonWithAddressRecord.class)
        .useMapper(addressMapper)
        .mapper();

    PersonWithAddressPojo source = new PersonWithAddressPojo("New", new AddressPojo("New St", "New City"));
    PersonWithAddressRecord existing = new PersonWithAddressRecord("Old", new AddressRecord("Old St", "Old City"));

    PersonWithAddressRecord result = mapper.map(source, existing);

    assertEquals("New", result.name());
    assertNotNull(result.address());
    assertEquals("New St", result.address()
        .street());
    assertEquals("New City", result.address()
        .city());
  }

  @Test
  public void shouldMapIntoRecord_withReplace() {
    Mapper<PersonPojo, PersonResourceRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonResourceRecord.class)
        .replace(PersonPojo::getName, PersonResourceRecord::fullName)
        .with(name -> name.toUpperCase())
        .reassign(PersonPojo::getAge)
        .to(PersonResourceRecord::yearsOld)
        .reassign(PersonPojo::getEmail)
        .to(PersonResourceRecord::emailAddress)
        .reassign(PersonPojo::isActive)
        .to(PersonResourceRecord::isActive)
        .mapper();

    PersonPojo source = new PersonPojo("Bob", 25, "bob@example.com", false);
    PersonResourceRecord existing = new PersonResourceRecord("Old", 99, "old@example.com", true);

    PersonResourceRecord result = mapper.map(source, existing);

    assertEquals("BOB", result.fullName());
    assertEquals(25, result.yearsOld());
  }

  @Test(expected = MappingException.class)
  public void shouldThrowOnUnmappedRecordField() {
    Mapping.from(PersonPojo.class)
        .to(PersonWithExtraRecord.class)
        .mapper();
  }

  @Test(expected = MappingException.class)
  public void shouldDenyMappingNullToRecord() {
    Mapper<PersonPojo, PersonRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonRecord.class)
        .mapper();

    mapper.map((PersonPojo) null);
  }

  // ==================== Collection Mapping ====================

  @Test
  public void shouldMapCollectionOfPojosToRecords() {
    Mapper<PersonPojo, PersonRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonRecord.class)
        .mapper();

    java.util.List<PersonPojo> sources = new java.util.ArrayList<>();
    sources.add(new PersonPojo("A", 1, "a@x.com", true));
    sources.add(new PersonPojo("B", 2, "b@x.com", false));

    java.util.List<PersonRecord> results = mapper.map(sources);

    assertEquals(2, results.size());
    assertEquals("A", results.get(0)
        .name());
    assertEquals("B", results.get(1)
        .name());
  }

  // ==================== Primitive Defaults ====================

  @Test
  public void shouldSetPrimitiveDefaultsForOmittedRecordFields() {
    Mapper<PersonPojo, PersonRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonRecord.class)
        .omitInSource(PersonPojo::getAge)
        .omitInDestination(PersonRecord::age)
        .omitInSource(PersonPojo::isActive)
        .omitInDestination(PersonRecord::active)
        .mapper();

    PersonPojo source = new PersonPojo("Alice", 30, "alice@example.com", true);
    PersonRecord result = mapper.map(source);

    assertEquals("Alice", result.name());
    assertEquals(0, result.age());
    assertFalse(result.active());
  }

  // ==================== Set Operation ====================

  @Test
  public void shouldMapPojoToRecord_withSet() {
    Mapper<PersonPojo, PersonWithExtraRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonWithExtraRecord.class)
        .omitInSource(PersonPojo::getEmail)
        .omitInSource(PersonPojo::isActive)
        .set(PersonWithExtraRecord::extra)
        .with(source -> source.getName() + "-extra")
        .mapper();

    PersonWithExtraRecord result = mapper.map(new PersonPojo("John", 30, "john@example.com", true));

    assertEquals("John", result.name());
    assertEquals(30, result.age());
    assertEquals("John-extra", result.extra());
  }

  // ==================== Restructure Operation ====================

  @Test
  public void shouldMapPojoToRecord_withRestructure() {
    Mapper<PersonPojo, PersonWithAddressRecord> mapper = Mapping.from(PersonPojo.class)
        .to(PersonWithAddressRecord.class)
        .omitOtherSourceProperties()
        .restructure(PersonWithAddressRecord::address)
        .applying(config -> config.replace(PersonPojo::getEmail, AddressRecord::street)
            .withSkipWhenNull(email -> email)
            .replace(PersonPojo::getName, AddressRecord::city)
            .withSkipWhenNull(name -> name))
        .mapper();

    PersonWithAddressRecord result = mapper.map(new PersonPojo("Dortmund", 30, "Main Street 1", false));

    assertEquals("Dortmund", result.name());
    assertNotNull(result.address());
    assertEquals("Main Street 1", result.address()
        .street());
    assertEquals("Dortmund", result.address()
        .city());
  }

  // ==================== Selector Validation ====================

  @Test
  public void shouldThrowOnInlineLambdaForRecordSelector() {
    try {
      Mapping.from(PersonPojo.class)
          .to(PersonRecord.class)
          .omitInDestination(record -> record.email())
          .mapper();
      fail("MappingException expected for inline lambda on record type");
    } catch (MappingException e) {
      assertTrue("Expected a hint to use method references, but was: " + e.getMessage(), e.getMessage()
          .contains("method references"));
      assertTrue("Expected the inline lambda to be reported, but was: " + e.getMessage(), e.getMessage()
          .contains("inline lambda"));
    }
  }

}
