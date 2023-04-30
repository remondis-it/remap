package com.remondis.remap.basic;

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
public class A {

  private String moreInA;
  private String string;
  private int number;
  private Integer integer;
  private Long zahlInA;

  private B b;
}
