package com.remondis.remap.demo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Person {

  private String salutation;
  private String forname;
  private String name;
  private Gender gender;
  private double bodyHeight;
}
