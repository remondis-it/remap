package com.remondis.remap.records;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MappingRecordsTest {

  @Test
  public void test_mapping_from_record() {
    Mapper<TestRecord, TestClass> mapper = Mapping.from(TestRecord.class)
        .to(TestClass.class)
        .mapper();

    TestRecord input = new TestRecord("123");
    TestClass actual = mapper.map(input);

    assertThat(actual).isNotNull()
        .extracting(TestClass::getNummer)
        .isEqualTo("123");
  }

  @Test
  public void test_mapping_into_record() {
    Mapper<TestClass, TestRecord> mapper = Mapping.from(TestClass.class)
        .to(TestRecord.class)
        .mapper();
    TestClass input = new TestClass();
    input.setNummer("123");
    TestRecord actual = mapper.map(input);

    assertThat(actual).isNotNull()
        .extracting(TestRecord::nummer)
        .isEqualTo("123");
  }

  @Test
  public void test_mapping_into_nested_record() {

    Mapper<TestClassChild, TestRecord> mapperNested = Mapping.from(TestClassChild.class)
        .to(TestRecord.class)
        .mapper();
    Mapper<TestClassSourceParent, TestClassDestinationParent> mapperParent = Mapping.from(TestClassSourceParent.class)
        .to(TestClassDestinationParent.class)
        .useMapper(mapperNested)
        .mapper();

    String expected = "123";

    TestClassChild child = new TestClassChild();
    child.setNummer(expected);
    TestClassSourceParent input = new TestClassSourceParent();
    input.setChild(child);
    TestClassDestinationParent actual = mapperParent.map(input);

    assertThat(actual).isNotNull()
        .extracting(TestClassDestinationParent::getChild)
        .extracting(TestRecord::nummer)
        .isEqualTo("123");

  }

  @Test
  public void test_mapping_into_nested_record_List() {

    Mapper<TestClassChild, TestRecord> mapperNested = Mapping.from(TestClassChild.class)
        .to(TestRecord.class)
        .mapper();
    Mapper<TestClassListSourceParent, TestClassListDestinationParent> mapperParent = Mapping
        .from(TestClassListSourceParent.class)
        .to(TestClassListDestinationParent.class)
        .useMapper(mapperNested)
        .mapper();

    String expected1 = "1";
    String expected2 = "2";
    String expected3 = "3";

    TestClassListSourceParent input = new TestClassListSourceParent();
    input
        .setChild(List.of(new TestClassChild(expected1), new TestClassChild(expected2), new TestClassChild(expected3)));
    TestClassListDestinationParent actual = mapperParent.map(input);

    assertThat(actual).isNotNull();
    assertThat(actual.getChild()).extracting(TestRecord::nummer)
        .contains("1", "2", "3");

  }

  public record TestRecord(String nummer) {
  }

  public static class TestClass {
    private String nummer;

    public String getNummer() {
      return nummer;
    }

    public void setNummer(String nummer) {
      this.nummer = nummer;
    }
  }

  public static class TestClassDestinationParent {
    private TestRecord child;

    public TestRecord getChild() {
      return child;
    }

    public void setChild(TestRecord child) {
      this.child = child;
    }
  }

  public static class TestClassSourceParent {
    private TestClassChild child;

    public TestClassChild getChild() {
      return child;
    }

    public void setChild(TestClassChild child) {
      this.child = child;
    }
  }

  public static class TestClassChild {
    private String nummer;

    public TestClassChild() {
      super();
    }

    public TestClassChild(String nummer) {
      super();
      this.nummer = nummer;
    }

    public String getNummer() {
      return nummer;
    }

    public void setNummer(String nummer) {
      this.nummer = nummer;
    }
  }

  public static class TestClassListDestinationParent {
    private Set<TestRecord> child;

    public Set<TestRecord> getChild() {
      return child;
    }

    public void setChild(Set<TestRecord> child) {
      this.child = child;
    }

  }

  public static class TestClassListSourceParent {
    private List<TestClassChild> child;

    public List<TestClassChild> getChild() {
      return child;
    }

    public void setChild(List<TestClassChild> child) {
      this.child = child;
    }

  }
}