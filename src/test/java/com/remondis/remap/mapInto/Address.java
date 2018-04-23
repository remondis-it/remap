package com.remondis.remap.mapInto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Address {

  private int houseNumber;
  private String street;
  private String city;

}
