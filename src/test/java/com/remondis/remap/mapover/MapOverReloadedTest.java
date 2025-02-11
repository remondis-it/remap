package com.remondis.remap.mapover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.mapover.data.Child;
import com.remondis.remap.mapover.data.Root;

public class MapOverReloadedTest {

  @Test
  public void shouldMapOverAndDoNotTouchUnmappedFields() {
    Mapper<Root, Root> mapper = Mapping.from(Root.class)
        .to(Root.class)
        .useCollectionElementIdentifier(Root::getChildren)
        .identifyBy(Child::getAttribute1)
        .useMapper(Mapping.from(Child.class)
            .to(Child.class)
            .noImplicitMappings()
            .reassign(Child::getAttribute2)
            .to(Child::getAttribute2)
            .omitOthers()
            .mapper())
        .mapper();

    Child rootChild = new Child(0L, "rootChild");
    Child childElement1 = new Child(1L, "ChildElement1");
    Child childElement2 = new Child(2L, "ChildElement2");
    Root root = new Root(rootChild, List.of(childElement1, childElement2));

    Child mappedRootChild = new Child(-1L, "empty");
    Child mappedChildElement1 = new Child(-1L, "empty1");
    Child mappedChildElement2 = new Child(-1L, "empty2");
    Root mappedRoot = new Root(mappedRootChild, List.of(mappedChildElement1, mappedChildElement2));

    // Assert before
    assertEquals(-1L, (long) mappedRoot.getChild()
        .getAttribute1());
    assertEquals("empty", mappedRoot.getChild()
        .getAttribute2());

    Root rootResult = mapper.map(root, mappedRoot);

    // Map result is same as destination
    assertSame(mappedRoot, rootResult);
    assertSame(mappedRootChild, rootResult.getChild());
    assertSame(mappedChildElement1, rootResult.getChildren()
        .get(0));
    assertSame(mappedChildElement2, rootResult.getChildren()
        .get(1));

    // Assert map over
    // Leave Attribute 1 untouched during mapping
    assertEquals(-1L, (long) mappedRoot.getChild()
        .getAttribute1());
    // Only map attribute 2 according to mapping configuration
    assertEquals("rootChild", mappedRoot.getChild()
        .getAttribute2());
  }

}
