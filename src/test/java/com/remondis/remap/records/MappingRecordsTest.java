package com.remondis.remap.records;

import static org.assertj.core.api.Assertions.assertThat;

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

}