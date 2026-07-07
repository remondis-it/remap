package com.remondis.remap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * Verifies that transformations are performed in configuration order. Before mappings were held in a
 * {@link java.util.LinkedHashSet}, the iteration order depended on hash codes, so the mapping order and the order of
 * transformations in error messages were not deterministic.
 */
public class DeterministicMappingOrderTest {

  @Test
  public void shouldKeepConfigurationOrderOfTransformations() {
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .reassign(Source::getThree)
        .to(Destination::getThird)
        .reassign(Source::getOne)
        .to(Destination::getFirst)
        .reassign(Source::getFive)
        .to(Destination::getFifth)
        .reassign(Source::getTwo)
        .to(Destination::getSecond)
        .reassign(Source::getFour)
        .to(Destination::getFourth)
        .mapper();

    List<String> destinationPropertyOrder = mapper.getMapping()
        .getMappings()
        .stream()
        .map(Transformation::getDestinationPropertyName)
        .collect(Collectors.toList());

    assertThat(destinationPropertyOrder).containsExactly("third", "first", "fifth", "second", "fourth");
  }

  public static class Source {
    private String one;
    private String two;
    private String three;
    private String four;
    private String five;

    public String getOne() {
      return one;
    }

    public void setOne(String one) {
      this.one = one;
    }

    public String getTwo() {
      return two;
    }

    public void setTwo(String two) {
      this.two = two;
    }

    public String getThree() {
      return three;
    }

    public void setThree(String three) {
      this.three = three;
    }

    public String getFour() {
      return four;
    }

    public void setFour(String four) {
      this.four = four;
    }

    public String getFive() {
      return five;
    }

    public void setFive(String five) {
      this.five = five;
    }
  }

  public static class Destination {
    private String first;
    private String second;
    private String third;
    private String fourth;
    private String fifth;

    public String getFirst() {
      return first;
    }

    public void setFirst(String first) {
      this.first = first;
    }

    public String getSecond() {
      return second;
    }

    public void setSecond(String second) {
      this.second = second;
    }

    public String getThird() {
      return third;
    }

    public void setThird(String third) {
      this.third = third;
    }

    public String getFourth() {
      return fourth;
    }

    public void setFourth(String fourth) {
      this.fourth = fourth;
    }

    public String getFifth() {
      return fifth;
    }

    public void setFifth(String fifth) {
      this.fifth = fifth;
    }
  }
}
