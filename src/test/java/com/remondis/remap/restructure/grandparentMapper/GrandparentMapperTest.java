package com.remondis.remap.restructure.grandparentMapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

/**
 * Covers a two-level deep {@code restructure()} chain where the innermost level needs a type mapper that is only
 * registered on the outermost (grandparent) mapping configuration, i.e. two levels up from where it is needed. Each
 * {@code restructure()} nesting level links to its immediate parent configuration for mapper lookups, so this also
 * exercises that the lookup chain is followed transitively across more than one nesting level.
 */
class GrandparentMapperTest {

  @Test
  void shouldResolveMapperRegisteredOnGrandparentConfiguration() {
    Mapper<Money, MoneyDto> moneyMapper = Mapping.from(Money.class)
        .to(MoneyDto.class)
        .mapper();

    Mapper<Outer, OuterDto> mapper = Mapping.from(Outer.class)
        .to(OuterDto.class)
        .omitOtherSourceProperties()
        .useMapper(moneyMapper)
        .restructure(OuterDto::getMid)
        .applying(midConfig -> midConfig.restructure(MidDto::getLeaf)
            .implicitly())
        .mapper();

    Outer outer = new Outer();
    outer.setOuterName("outer");
    outer.setMidName("mid");
    Money price = new Money();
    price.setAmount(new BigDecimal("19.99"));
    outer.setPrice(price);

    OuterDto dto = mapper.map(outer);

    assertThat(dto.getOuterName()).isEqualTo("outer");
    assertThat(dto.getMid()
        .getMidName()).isEqualTo("mid");
    assertThat(dto.getMid()
        .getLeaf()
        .getPrice()
        .getAmount()).isEqualByComparingTo("19.99");
  }

  @Test
  void shouldResolveMapperRegisteredAfterTheEntireNestedRestructureChain() {
    Mapper<Money, MoneyDto> moneyMapper = Mapping.from(Money.class)
        .to(MoneyDto.class)
        .mapper();

    // Both nesting levels are configured first; useMapper() on the grandparent configuration only happens
    // afterwards. The parent-registry link is resolved lazily, so this must work just as well as registering the
    // mapper up front.
    Mapper<Outer, OuterDto> mapper = Mapping.from(Outer.class)
        .to(OuterDto.class)
        .omitOtherSourceProperties()
        .restructure(OuterDto::getMid)
        .applying(midConfig -> midConfig.restructure(MidDto::getLeaf)
            .implicitly())
        .useMapper(moneyMapper)
        .mapper();

    Outer outer = new Outer();
    outer.setOuterName("outer");
    outer.setMidName("mid");
    Money price = new Money();
    price.setAmount(new BigDecimal("5.00"));
    outer.setPrice(price);

    OuterDto dto = mapper.map(outer);

    assertThat(dto.getMid()
        .getLeaf()
        .getPrice()
        .getAmount()).isEqualByComparingTo("5.00");
  }
}
