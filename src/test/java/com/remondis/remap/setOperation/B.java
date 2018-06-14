package com.remondis.remap.setOperation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class B {

  private String string;
  private Integer integerRef;
  private int integer;

  private String valueSet;

}
