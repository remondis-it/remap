package com.remondis.remap;

import java.util.ArrayList;
import java.util.List;

final class ValidationAggregator {

  private static final List<RuntimeException> PROBLEMS = new ArrayList<>();

  private ValidationAggregator() {
  }

  static void add(Class<?> source, Class<?> destination, MappingException cause) {
    String msg = "Mapping " + source.getSimpleName() + " -> " + destination.getSimpleName() + " failed:\n"
        + cause.getMessage();
    MappingException perMapper = new MappingException(msg, cause);
    PROBLEMS.add(perMapper);
  }

  static void throwIfAnyAndClear() {
    if (PROBLEMS.isEmpty())
      return;
    IllegalStateException aggregated = new IllegalStateException("ReMap aggregated validation failed");
    PROBLEMS.forEach(aggregated::addSuppressed);
    PROBLEMS.clear();
    throw aggregated;
  }
}
