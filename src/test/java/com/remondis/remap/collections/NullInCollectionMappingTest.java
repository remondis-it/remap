package com.remondis.remap.collections;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class NullInCollectionMappingTest {

  static class Source {
    private List<String> stringList;

    public Source(List<String> stringList) {
      this.stringList = stringList;
    }

    public List<String> getStringList() {
      return stringList;
    }
  }

  static class Destination {
    private List<String> stringList;

    public Destination() {
    }

    public void setStringList(List<String> stringList) {
      this.stringList = stringList;
    }

    public List<String> getStringList() {
      return stringList;
    }
  }

  @Test
  public void testMappingWithNullInCollection() {
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .mapper();

    Source source = new Source(singletonList(null));

    MappingException exception = assertThrows(MappingException.class, () -> mapper.map(source));

    String expectedMessage = "Cannot map null element in collection field 'stringList' from source type 'Source' to destination type 'Destination'.";
    assertEquals("Unerwartete Exception-Nachricht", expectedMessage, exception.getMessage());
  }
}