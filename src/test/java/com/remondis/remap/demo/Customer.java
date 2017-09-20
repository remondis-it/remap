package com.remondis.remap.demo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Customer {

  private String title;
  private String forname;
  private String name;
  private String gender;
  private String address;
}
