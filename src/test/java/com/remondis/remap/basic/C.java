package com.remondis.remap.basic;

import java.util.Collection;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class C {
  private Collection<B> collection;
  private Map<Object, B> map;
}
