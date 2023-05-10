package com.remondis.remap.utils.property.walker;

import com.remondis.remap.utils.property.visitor.VisitorFunction;
import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @param <T> The bean type.
 * @param <P> The property type.
 */
@RequiredArgsConstructor
public class BiPropertyVisitor<T, P> {

  private final Class<T> type;
  private final Function<T, P> propertyExtractorSource;
  private final Function<T, P> propertyExtractorTarget;
  private final BiConsumer<T, P> propertyWriterSource;
  private final BiConsumer<T, P> propertyWriterTarget;

  private final VisitorFunction<T, P> visitorFunction;

  public BiPropertyVisitor(Class<T> type, Function<T, P> propertyExtractor, VisitorFunction<T, P> visitorFunction) {
    this.type = type;
    this.propertyExtractorSource = propertyExtractor;
    this.propertyExtractorTarget = propertyExtractor;
    this.propertyWriterSource = null;
    this.propertyWriterTarget = null;
    this.visitorFunction = visitorFunction;
  }

  public BiPropertyVisitor(Class<T> type, Function<T, P> propertyExtractorSource,
      Function<T, P> propertyExtractorTarget, VisitorFunction<T, P> visitorFunction) {
    this.type = type;
    this.propertyExtractorSource = propertyExtractorSource;
    this.propertyExtractorTarget = propertyExtractorTarget;
    this.propertyWriterSource = null;
    this.propertyWriterTarget = null;
    this.visitorFunction = visitorFunction;
  }

  public BiPropertyVisitor(Class<T> type, Function<T, P> propertyExtractor, BiConsumer<T, P> propertyWriter,
      VisitorFunction<T, P> visitorFunction) {
    this.type = type;
    this.propertyExtractorSource = propertyExtractor;
    this.propertyExtractorTarget = propertyExtractor;
    this.propertyWriterSource = propertyWriter;
    this.propertyWriterTarget = propertyWriter;
    this.visitorFunction = visitorFunction;
  }

  protected void execute(T source, T target) {
    PropertyAccess<T, P> propertyAccess = new PropertyAccess<>(source, target, propertyExtractorSource,
        propertyExtractorTarget, propertyWriterSource, propertyWriterTarget);
    visitorFunction.consume(propertyAccess);
  }

}
