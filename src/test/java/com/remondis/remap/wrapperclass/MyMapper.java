package com.remondis.remap.wrapperclass;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MyMapper extends Mapper<Customer, Person> {

  public MyMapper(Mapping<Customer, Person> mapping) {
    super(mapping);
  }
}
