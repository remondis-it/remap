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
public class PersonLite {

  private String forename;
  private String lastname;

  private List<AddressLite> addresses;
}
