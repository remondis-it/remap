package com.remondis.remap.regression.cyclicRestructureBug;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingConfiguration;
import com.remondis.remap.MappingException;

/**
 * Reproduces a crash for self-referential/tree-like types: {@code restructure(...).applying(...)} naturally invites
 * a recursive {@link Consumer} to describe a recursive structure (e.g. {@code Node.child} of type {@code Node}
 * mapped to {@code NodeDto.child} of type {@code NodeDto}, arbitrarily deep). Since every level of the restructure
 * chain builds and configures a fresh, nested {@link MappingConfiguration} synchronously and eagerly, a
 * self-referential consumer recurses forever already while configuring the mapping - long before {@code mapper()}
 * is ever called - and used to crash the calling thread with an undiagnosable {@link StackOverflowError}. ReMap
 * builds a finite mapping tree at configuration time and cannot support unbounded recursive types this way; the fix
 * is to fail fast with a clear {@link MappingException} instead of blowing the stack.
 */
class MapperTest {

  @Test
  void shouldFailFastInsteadOfStackOverflowOnRecursiveRestructure() {
    Consumer<MappingConfiguration<Node, NodeDto>> configureChild = new Consumer<MappingConfiguration<Node, NodeDto>>() {
      @Override
      public void accept(MappingConfiguration<Node, NodeDto> config) {
        config.restructure(NodeDto::getChild)
            .applying(this);
      }
    };

    assertThatThrownBy(() -> Mapping.from(Node.class)
        .to(NodeDto.class)
        .omitOtherSourceProperties()
        .restructure(NodeDto::getChild)
        .applying(configureChild)
        .mapper()).isInstanceOf(MappingException.class)
        .hasMessageContaining("cyclic")
        .hasMessageContaining("useMapper()");
  }

  @Test
  void shouldNotFalsePositiveOnSiblingFieldsOfTheSameType() {
    // Two independent (sibling, not nested) restructure() calls that happen to restructure the same flat source
    // property into the same destination field type must not be mistaken for a cycle: they run one after another,
    // not while one another is still in progress.
    Mapper<Flat, TwoNestedDto> mapper = Mapping.from(Flat.class)
        .to(TwoNestedDto.class)
        .omitOtherSourceProperties()
        .restructure(TwoNestedDto::getFirst)
        .implicitly()
        .restructure(TwoNestedDto::getSecond)
        .implicitly()
        .mapper();

    Flat source = new Flat();
    source.setName("shared");

    TwoNestedDto dto = mapper.map(source);

    assertThat(dto.getFirst()
        .getName()).isEqualTo("shared");
    assertThat(dto.getSecond()
        .getName()).isEqualTo("shared");
  }

  public static class Flat {
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public static class LeafDto {
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public static class TwoNestedDto {
    private LeafDto first;
    private LeafDto second;

    public LeafDto getFirst() {
      return first;
    }

    public void setFirst(LeafDto first) {
      this.first = first;
    }

    public LeafDto getSecond() {
      return second;
    }

    public void setSecond(LeafDto second) {
      this.second = second;
    }
  }
}
