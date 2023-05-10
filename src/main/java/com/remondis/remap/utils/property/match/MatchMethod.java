package com.remondis.remap.utils.property.match;

import lombok.Getter;

import java.util.function.BiPredicate;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@Getter
public class MatchMethod<TT> {

  private final Function<TT, Object> matchPropertyExtractor;
  private final BiPredicate<TT, TT> matchFunction;

  public MatchMethod(Function<TT, Object> matchPropertyExtractor) {
    requireNonNull(matchPropertyExtractor, "matchPropertyExtractor must not be null!");
    this.matchFunction = null;
    this.matchPropertyExtractor = matchPropertyExtractor;
  }

  public MatchMethod(BiPredicate<TT, TT> matchFunction) {
    requireNonNull(matchFunction, "matchFunction must not be null!");
    this.matchFunction = matchFunction;
    this.matchPropertyExtractor = null;
  }
}
