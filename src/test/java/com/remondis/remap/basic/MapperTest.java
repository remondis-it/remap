package com.remondis.remap.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;
import com.remondis.remap.test.MapperTests.PersonWithAddress;
import com.remondis.remap.test.MapperTests.PersonWithFoo;

public class MapperTest {

  public static final String MORE_IN_A = "moreInA";
  public static final Long ZAHL_IN_A = -88L;
  public static final Integer B_INTEGER = -999;
  public static final int B_NUMBER = 222;
  public static final String B_STRING = "b string";
  public static final Integer INTEGER = 310;
  public static final int NUMBER = 210;
  public static final String STRING = "a string";

  @Test(expected = MappingException.class)
  public void shouldDenyMapNull() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .reassign(A::getMoreInA)
        .to(AResource::getMoreInAResource)
        .reassign(A::getZahlInA)
        .to(AResource::getZahlInAResource)
        .useMapper(Mapping.from(B.class)
            .to(BResource.class)
            .mapper())
        .mapper();
    mapper.map((A) null);
  }

  @Test
  public void shouldFailDueToNoRegisteredMapper() {
    assertThatThrownBy(() -> Mapping.from(A.class)
        .to(AResource.class)
        .reassign(A::getMoreInA)
        .to(AResource::getMoreInAResource)
        .reassign(A::getZahlInA)
        .to(AResource::getZahlInAResource)
        .mapper()).isInstanceOf(MappingException.class)
            .hasMessageStartingWith("No mapper found for type mapping");
  }

  /**
   * This is the happy-path test for mapping {@link A} to {@link AResource} with a nested mapping. This test does not
   * check the inherited fields.
   */
  @Test
  public void shouldMapCorrectly() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .omitInSource(A::getMoreInA)
        .omitInDestination(AResource::getMoreInAResource)
        .reassign(A::getZahlInA)
        .to(AResource::getZahlInAResource)
        .useMapper(Mapping.from(B.class)
            .to(BResource.class)
            .mapper())
        .mapper();

    B b = new B(B_STRING, B_NUMBER, B_INTEGER);
    A a = new A(MORE_IN_A, STRING, NUMBER, INTEGER, ZAHL_IN_A, b);
    a.setZahlInA(ZAHL_IN_A);

    AResource ar = mapper.map(a);

    assertNull(ar.getMoreInAResource());
    assertEquals(STRING, a.getString());
    assertEquals(STRING, ar.getString());
    assertEquals(NUMBER, a.getNumber());
    assertEquals(NUMBER, ar.getNumber());
    assertEquals(INTEGER, a.getInteger());
    assertEquals(INTEGER, ar.getInteger());
    assertEquals(ZAHL_IN_A, a.getZahlInA());
    assertEquals(ZAHL_IN_A, ar.getZahlInAResource());

    BResource br = ar.getB();
    assertEquals(B_STRING, b.getString());
    assertEquals(B_STRING, br.getString());
    assertEquals(B_NUMBER, b.getNumber());
    assertEquals(B_NUMBER, br.getNumber());
    assertEquals(B_INTEGER, b.getInteger());
    assertEquals(B_INTEGER, br.getInteger());
  }

  /**
   * Ensures that the {@link Mapper} detects one more property in the source object that is not omitted by the mapping
   * configuration. The {@link Mapper} is expected to throw a {@link MappingException}.
   */
  @Test
  public void oneMoreSourceFieldInA() {
    assertThatThrownBy(() -> Mapping.from(AWithOneMoreSourceField.class)
        .to(AResourceWithOneMoreSourceField.class)
        .mapper()).isInstanceOf(MappingException.class)
            .hasMessageContaining("- Property 'onlyInA' in AWithOneMoreSourceField");
  }

  /**
   * Ensures that an unmatched source field is omitted.
   */
  @Test
  public void oneMoreSourceFieldInAButItIsOmitted() {
    Mapper<AWithOneMoreSourceField, AResourceWithOneMoreSourceField> mapper = Mapping
        .from(AWithOneMoreSourceField.class)
        .to(AResourceWithOneMoreSourceField.class)
        .omitInSource(a -> a.getOnlyInA())
        .mapper();

    AWithOneMoreSourceField aWithOneMoreSourceField = new AWithOneMoreSourceField(1, 10, "text");
    AResourceWithOneMoreSourceField map = mapper.map(aWithOneMoreSourceField);

    assertEquals(aWithOneMoreSourceField.getText(), map.getText());
    assertEquals(aWithOneMoreSourceField.getZahl(), map.getZahl());
  }

  /**
   * Ensures that the {@link Mapper} detects one more property in the destination object that is not omitted by the
   * mapping
   * configuration. The {@link Mapper} is expected to throw a {@link MappingException}.
   */
  @Test
  public void oneMoreDestinationFieldInAResource() {
    assertThatThrownBy(() -> Mapping.from(AWithOneMoreDestinationField.class)
        .to(AResourceWithOneMoreDestinationField.class)
        .mapper()).isInstanceOf(MappingException.class)
            .hasMessageContaining("- Property 'onlyInAResource' in AResourceWithOneMoreDestinationField");
  }

  /**
   * Ensures that an unmatched destination field is omitted.
   */
  @Test
  public void oneMoreDestinationFieldInAResourceButItsOmmited() {
    Mapper<AWithOneMoreDestinationField, AResourceWithOneMoreDestinationField> mapper = Mapping
        .from(AWithOneMoreDestinationField.class)
        .to(AResourceWithOneMoreDestinationField.class)
        .omitInDestination(ar -> ar.getOnlyInAResource())
        .mapper();

    AWithOneMoreDestinationField aWithOneMoreDestinationField = new AWithOneMoreDestinationField(10, "text");
    AResourceWithOneMoreDestinationField map = mapper.map(aWithOneMoreDestinationField);

    assertEquals(aWithOneMoreDestinationField.getText(), map.getText());
    assertEquals(aWithOneMoreDestinationField.getZahl(), map.getZahl());
  }

  /**
   * Ensures that the mapper performs a correct reassigment of fields.
   */
  @Test
  public void reassign() {
    Mapper<AReassign, AResourceReassign> mapper = Mapping.from(AReassign.class)
        .to(AResourceReassign.class)
        .reassign(AReassign::getFirstNumberInA)
        .to(AResourceReassign::getFirstNumberInAResource)
        .reassign(AReassign::getSecondNumberInA)
        .to(AResourceReassign::getSecondNumberInAResource)
        .mapper();

    AReassign aReassgin = new AReassign(1, 2, 3);
    AResourceReassign map = mapper.map(aReassgin);

    assertEquals(aReassgin.getZahl(), map.getZahl());
    assertEquals(aReassgin.getFirstNumberInA(), map.getFirstNumberInAResource());
    assertEquals(aReassgin.getSecondNumberInA(), map.getSecondNumberInAResource());
  }

  /**
   * Ensures that the mapper does not allow an omitted field in the source to be reassigned.
   */
  @Test(expected = MappingException.class)
  public void reassignAnOmmitedFieldInSource() {
    Mapping.from(AReassign.class)
        .to(AResourceReassign.class)
        .omitInSource(AReassign::getFirstNumberInA)
        .reassign(AReassign::getFirstNumberInA)
        .to(AResourceReassign::getFirstNumberInAResource)
        .reassign(AReassign::getSecondNumberInA)
        .to(AResourceReassign::getSecondNumberInAResource)
        .mapper();
  }

  /**
   * Ensures that the mapper does not allow an omitted field in the destination to be reassigned.
   */
  @Test(expected = MappingException.class)
  public void reassignToAnOmmitedFieldInDestination() {
    Mapping.from(AReassign.class)
        .to(AResourceReassign.class)
        .omitInDestination(ar -> ar.getFirstNumberInAResource())
        .reassign(AReassign::getFirstNumberInA)
        .to(AResourceReassign::getFirstNumberInAResource)
        .reassign(AReassign::getSecondNumberInA)
        .to(AResourceReassign::getSecondNumberInAResource)
        .mapper();
  }

  /**
   * Ensures that the mapper detects an unmapped field in the destination while the all source fields are mapped.
   */
  @Test(expected = MappingException.class)
  public void reassignAndOneDestinationFieldIsUnmapped() {
    Mapping.from(AReassign.class)
        .to(AResourceReassign.class)
        .reassign(AReassign::getFirstNumberInA)
        .to(AResourceReassign::getSecondNumberInAResource)
        .omitInSource(AReassign::getSecondNumberInA)
        .mapper();
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void shouldMapToNewList() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .omitInSource(A::getMoreInA)
        .omitInDestination(AResource::getMoreInAResource)
        .reassign(A::getZahlInA)
        .to(AResource::getZahlInAResource)
        .useMapper(Mapping.from(B.class)
            .to(BResource.class)
            .mapper())
        .mapper();

    B b = new B(B_STRING, B_NUMBER, B_INTEGER);
    A a = new A(MORE_IN_A, STRING, NUMBER, INTEGER, ZAHL_IN_A, b);
    a.setZahlInA(ZAHL_IN_A);

    A[] aarr = new A[] {
        a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a
    };

    List<A> aList = Arrays.asList(aarr);
    List<AResource> arCollection = mapper.map(aList);

    // Make sure this is a new collection
    assertFalse((List) aList == (List) arCollection);

    assertEquals(aarr.length, aList.size());
    assertEquals(aarr.length, arCollection.size());

    for (AResource ar : arCollection) {
      assertNull(ar.getMoreInAResource());
      assertEquals(STRING, a.getString());
      assertEquals(STRING, ar.getString());
      assertEquals(NUMBER, a.getNumber());
      assertEquals(NUMBER, ar.getNumber());
      assertEquals(INTEGER, a.getInteger());
      assertEquals(INTEGER, ar.getInteger());
      assertEquals(ZAHL_IN_A, a.getZahlInA());
      assertEquals(ZAHL_IN_A, ar.getZahlInAResource());

      BResource br = ar.getB();
      assertEquals(B_STRING, b.getString());
      assertEquals(B_STRING, br.getString());
      assertEquals(B_NUMBER, b.getNumber());
      assertEquals(B_NUMBER, br.getNumber());
      assertEquals(B_INTEGER, b.getInteger());
      assertEquals(B_INTEGER, br.getInteger());

    }
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void shouldMapToNewSet() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .omitInSource(A::getMoreInA)
        .omitInDestination(AResource::getMoreInAResource)
        .reassign(A::getZahlInA)
        .to(AResource::getZahlInAResource)
        .useMapper(Mapping.from(B.class)
            .to(BResource.class)
            .mapper())
        .mapper();

    int max = 10;
    A[] aarr = new A[max];
    for (int i = 0; i < max; i++) {
      B b = new B(B_STRING, B_NUMBER, B_INTEGER);
      A a = new A(MORE_IN_A, STRING, NUMBER, INTEGER, ZAHL_IN_A, b);
      a.setZahlInA(ZAHL_IN_A);
      aarr[i] = a;
    }

    Set<A> aList = new HashSet<>(Arrays.asList(aarr));
    Set<AResource> arCollection = mapper.map(aList);

    // Make sure this is a new collection
    assertFalse((Set) aList == (Set) arCollection);

    assertEquals(max, aList.size());
    assertEquals(max, arCollection.size());

    Iterator<A> as = aList.iterator();
    Iterator<AResource> ars = arCollection.iterator();

    while (as.hasNext()) {
      A a = as.next();
      AResource ar = ars.next();
      assertNull(ar.getMoreInAResource());
      assertEquals(STRING, a.getString());
      assertEquals(STRING, ar.getString());
      assertEquals(NUMBER, a.getNumber());
      assertEquals(NUMBER, ar.getNumber());
      assertEquals(INTEGER, a.getInteger());
      assertEquals(INTEGER, ar.getInteger());
      assertEquals(ZAHL_IN_A, a.getZahlInA());
      assertEquals(ZAHL_IN_A, ar.getZahlInAResource());

      B b = a.getB();
      BResource br = ar.getB();
      assertEquals(B_STRING, b.getString());
      assertEquals(B_STRING, br.getString());
      assertEquals(B_NUMBER, b.getNumber());
      assertEquals(B_NUMBER, br.getNumber());
      assertEquals(B_INTEGER, b.getInteger());
      assertEquals(B_INTEGER, br.getInteger());
    }
  }

  @Test
  public void shouldDenyIllegalArguments() {

    assertThatThrownBy(() -> {
      Mapping.from(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      Mapping.from(A.class)
          .to(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      Mapping.from(A.class)
          .to(AResource.class)
          .omitInSource(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      Mapping.from(A.class)
          .to(AResource.class)
          .omitInSource(A::getMoreInA)
          .omitInDestination(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      Mapping.from(A.class)
          .to(AResource.class)
          .omitInSource(A::getMoreInA)
          .omitInDestination(AResource::getMoreInAResource)
          .reassign(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      Mapping.from(A.class)
          .to(AResource.class)
          .omitInSource(A::getMoreInA)
          .omitInDestination(AResource::getMoreInAResource)
          .reassign(A::getZahlInA)
          .to(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      Mapping.from(A.class)
          .to(AResource.class)
          .omitInSource(A::getMoreInA)
          .omitInDestination(AResource::getMoreInAResource)
          .reassign(A::getZahlInA)
          .to(AResource::getZahlInAResource)
          .useMapper((Mapper<?, ?>) null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    // Perform the API test on replace
    assertThatThrownBy(() -> {
      Mapping.from(PersonWithAddress.class)
          .to(PersonWithFoo.class)
          .replace(null, PersonWithFoo::getFoo);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      Mapping.from(PersonWithAddress.class)
          .to(PersonWithFoo.class)
          .replace(PersonWithAddress::getAddress, null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      Mapping.from(PersonWithAddress.class)
          .to(PersonWithFoo.class)
          .replace(PersonWithAddress::getAddress, PersonWithFoo::getFoo)
          .with(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

  }
}
