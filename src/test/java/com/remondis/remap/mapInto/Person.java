package com.remondis.remap.mapInto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Person {

  private Integer age;
  private String forename;
  private String lastname;

  private List<Address> addresses;

}