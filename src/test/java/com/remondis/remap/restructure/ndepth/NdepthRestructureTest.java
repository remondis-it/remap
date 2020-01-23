package com.remondis.remap.restructure.ndepth;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.restructure.Bean;
import com.remondis.resample.Samples;
import org.junit.Test;

import static org.junit.Assert.assertSame;

public class NdepthRestructureTest {

  @Test
  public void shouldRestructure_n_depth() {

    Mapper<Bean, Bean2> mapper = Mapping.from(Bean.class)
        .to(Bean2.class)
        .omitOtherSourceProperties()
        .restructure(Bean2::getPerson)
        .applying(config -> config.restructure(Person::getAddress)
            .implicitly())
        .mapper();

    Bean bean = Samples.Default.of(Bean.class)
        .get();
    Bean2 bean2 = mapper.map(bean);

    assertSame(bean.getForename(), bean2.getPerson()
        .getForename());
    assertSame(bean.getName(), bean2.getPerson()
        .getName());

    assertSame(bean.getStreet(), bean2.getPerson()
        .getAddress()
        .getStreet());
    assertSame(bean.getHouseNumber(), bean2.getPerson()
        .getAddress()
        .getHouseNumber());
    assertSame(bean.getCity(), bean2.getPerson()
        .getAddress()
        .getCity());
  }

}
