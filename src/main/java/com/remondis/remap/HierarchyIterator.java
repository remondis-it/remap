package com.remondis.remap;

import static java.util.Objects.nonNull;

import java.util.*;

public class HierarchyIterator implements Iterator<Class<?>> {
  private Queue<Class<?>> remaining = new LinkedList<>();
  private Set<Class<?>> visited = new LinkedHashSet<>();

  public HierarchyIterator(Class<?> initial) {
    append(initial);
  }

  private void append(Class<?> toAppend) {
    if (nonNull(toAppend) && !visited.contains(toAppend)) {
      remaining.add(toAppend);
      visited.add(toAppend);
    }
  }

  @Override
  public boolean hasNext() {
    return remaining.size() > 0;
  }

  @Override
  public Class<?> next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    Class<?> polled = remaining.poll();
    append(polled.getSuperclass());
    for (Class<?> superInterface : polled.getInterfaces()) {
      append(superInterface);
    }
    return polled;
  }
}
