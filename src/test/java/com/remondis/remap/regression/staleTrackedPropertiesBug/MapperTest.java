package com.remondis.remap.regression.staleTrackedPropertiesBug;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

/**
 * Reproduces a configuration state corruption: when a field selector threw an exception, the properties tracked up to
 * that point remained in the thread local state of the invocation sensor, which is shared per type. The next selector
 * evaluation on the same thread then saw the stale property in addition to its own and failed with a "multiple
 * interactions" error.
 */
class MapperTest {

  @Test
  void shouldNotTrackPropertiesOfFailedSelectorInvocation() {
    assertThatThrownBy(() -> Mapping.from(A.class)
        .to(B.class)
        .omitInSource(a -> {
          a.getString();
          throw new IllegalStateException("selector failure");
        })).isInstanceOf(IllegalStateException.class);

    // Without resetting the tracking state, the property tracked by the failed selector above corrupts the
    // following configuration with a "multiple interactions" MappingException.
    Mapper<A, B> mapper = Mapping.from(A.class)
        .to(B.class)
        .omitInSource(A::getString)
        .mapper();

    assertThat(mapper.map(new A())).isNotNull();
  }
}
