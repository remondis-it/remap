package com.remondis.remap.inheritance.reuseParentMapper;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;
import com.remondis.remap.TypeMapping;

public class ReuseMapperTest {

  public static final String string = "stringmit18Zeichen";

  private Mapper<Parent, ParentMapped> mapper;

  private Mapper<Child, ChildMapped> childMapper;

  @Before
  public void setup() {
    this.mapper = Mapping.from(Parent.class)
        .to(ParentMapped.class)
        .useMapper(TypeMapping.from(String.class)
            .to(int.class)
            .applying(String::length))
        .reassign(Parent::getParent)
        .to(ParentMapped::getLength)
        .mapper();

    this.childMapper = mapper.derive(Child.class, ChildMapped.class)
        .reassign(Child::getChildString)
        .to(ChildMapped::getChildInt)
        .mapper();
  }

  @Test
  public void shouldModifyMappingOfParentFields() {
    Mapper<Child, ChildMapped> modifyParentMapper = mapper.derive(Child.class, ChildMapped.class)
        .replace(Child::getParent, ChildMapped::getLength)
        .withSkipWhenNull(str -> 1000)
        .reassign(Child::getChildString)
        .to(ChildMapped::getChildInt)
        .mapper();

  }

  @Test
  public void shouldComplainAboutUnmappedChildPropertiesWhenDeriveFromParentMapper() {
    assertThatThrownBy(() -> mapper.derive(Child.class, ChildMapped.class)
        .mapper()).isInstanceOf(MappingException.class)
            .hasMessageContaining("The following properties are unmapped:");
  }

  @Test
  public void shouldDeriveFromParentMapper() {
    Child c = new Child(string, string);
    ChildMapped childMapped = childMapper.map(c);
    assertEquals(18, childMapped.getLength());
    assertEquals(18, childMapped.getChildInt());
  }

  @Test
  public void shouldMapChildWithParentMapper() {
    Parent p = new Parent(string);
    ParentMapped mappedParent = mapper.map(p);
    assertEquals(18, mappedParent.getLength());

    Child c = new Child(string, string);
    ParentMapped mappedChild = mapper.map(c);
    assertEquals(18, mappedChild.getLength());
  }

}
