package com.remondis.remap.restructure;

import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.resample.Samples;

/**
 * The restructure operation allows to map different properties from the source object to a bean hold by the destination
 * object using a mapper.
 * <p>
 * Currently the restructure operation is an operation that has to be configured explicitly.
 * <p>
 * The following rules might allow to add an implicit restructure operation automatically, but this list is not verified
 * at the moment.
 * </p>
 * <h2>Implicit restructure operation</h2>
 * <p>
 * DRAFT: Add a restructure operation for all properties of destination that:
 * <ul>
 * <li>are currently unmapped</li>
 * <li>do not have a property in the source type with the same name</li>
 * <li>have no projection for their property type registered</li>
 * <li>have at least one parameter in their respective type with the same name</li>
 * </ul>
 * </p>
 */
public class RestructureTest {
  @Test
  public void shouldRestructure_implicit_mapping_operations() {

    Mapper<Bean, RestructuredBean> mapper = Mapping.from(Bean.class)
        .to(RestructuredBean.class)
        .omitOtherSourceProperties()
        .restructure(RestructuredBean::getAddress)
        .implicitly()
        .mapper();

    Bean bean = Samples.Default.of(Bean.class)
        .get();
    RestructuredBean restructured = mapper.map(bean);

    assertSame(bean.getStreet(), restructured.getAddress()
        .getStreet());
    assertSame(bean.getHouseNumber(), restructured.getAddress()
        .getHouseNumber());
    assertSame(bean.getCity(), restructured.getAddress()
        .getCity());

  }

  @Test
  public void shouldRestructure_explicit_mapping_operations() {

    Mapper<Bean, RestructuredBean> mapper = Mapping.from(Bean.class)
        .to(RestructuredBean.class)
        .omitOtherSourceProperties()
        .restructure(RestructuredBean::getAddress)
        .applying(config -> config.omitOtherSourceProperties()
            .reassign(Bean::getHouseNumber)
            .to(Address::getHouseNumber)
            .reassign(Bean::getStreet)
            .to(Address::getStreet)
            .reassign(Bean::getCity)
            .to(Address::getCity))
        .mapper();

    Bean bean = Samples.Default.of(Bean.class)
        .get();
    RestructuredBean restructured = mapper.map(bean);

    assertSame(bean.getStreet(), restructured.getAddress()
        .getStreet());
    assertSame(bean.getHouseNumber(), restructured.getAddress()
        .getHouseNumber());
    assertSame(bean.getCity(), restructured.getAddress()
        .getCity());

  }

}
