package com.remondis.extern.usecase.metamodel;

import static com.remondis.remap.MappingModel.nameEqualsPredicate;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Predicate;

import com.remondis.remap.MappedResult;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingModel;
import com.remondis.remap.MappingOperation;
import org.junit.jupiter.api.Test;

class MetaModelFeatureTest {

  @Test
  void shouldGetSourceFieldByFieldSelector() {
    Mapper<Source, Destination> mapper = getMapper();
    MappingModel<Source, Destination> model = mapper.getMappingModel();
    MappingModel<Source, Destination>.TransformationSearchResult result = model.findMappingBySource(Source::getNested);

    assertResult(result);
    assertMultiResult(result);

    result = model.findMapping(Source::getNested, Destination::getNested);

    assertResult(result);
    assertSingleResult(result);
  }

  @Test
  void shouldPerformObjectTransformation() {
    Mapper<Source, Destination> mapper = getMapper();

    Predicate<String> destPredicate = nameEqualsPredicate("nested");

    MappingModel<Source, Destination> model = mapper.getMappingModel();
    MappingModel<Source, Destination>.TransformationSearchResult mappingModel = model
        .findMappingByDestination(destPredicate);

    assertResult(mappingModel);
    assertSingleResult(mappingModel);
    assertValueMapping(mappingModel);

    NestedSource source = new NestedSource("string");
    MappedResult mappedResult = mappingModel.performValueTransformation(source);

    assertMappingHasValue(mappedResult);

    NestedDestination nestedDestination = (NestedDestination) mappedResult.getValue();

    NestedDestination expectedDest = new NestedDestination("string");
    assertEquals(expectedDest.getStringRenamed(), nestedDestination.getStringRenamed());
  }

  @Test
  void shouldSetTransformation() {
    Mapper<Source, Destination> mapper = getMapper();

    Predicate<String> destPredicate = nameEqualsPredicate("doesNotExistInSource");

    MappingModel<Source, Destination> model = mapper.getMappingModel();
    MappingModel<Source, Destination>.TransformationSearchResult mappingModel = model
        .findMappingByDestination(destPredicate);

    assertResult(mappingModel);
    assertSingleResult(mappingModel);
    assertObjectMapping(mappingModel);

    Source source = new Source();
    source.setString("string");
    MappedResult mappedResult = mappingModel.performValueTransformation(source);
    assertMappingValue(mappedResult, "string");
  }

  @Test
  void shouldGetOmitInDest() {
    Mapper<Source, Destination> mapper = getMapper();

    Predicate<String> destPredicate = nameEqualsPredicate("omitInDestination");

    MappingModel<Source, Destination> model = mapper.getMappingModel();
    MappingModel<Source, Destination>.TransformationSearchResult mappingModel = model
        .findMappingByDestination(destPredicate);

    assertResult(mappingModel);
    assertSingleResult(mappingModel);
    assertValueMapping(mappingModel);

    MappedResult mappedResult = mappingModel.performValueTransformation("testString");
    assertMappingSkipped(mappedResult);
  }

  @Test
  void shouldGetOmitInSource() {
    Mapper<Source, Destination> mapper = getMapper();

    Predicate<String> sourcePredicate = nameEqualsPredicate("omitInSource");

    MappingModel<Source, Destination> model = mapper.getMappingModel();
    MappingModel<Source, Destination>.TransformationSearchResult mappingModel = model
        .findMappingBySource(sourcePredicate);

    assertResult(mappingModel);
    assertSingleResult(mappingModel);
    assertValueMapping(mappingModel);

    MappedResult mappedResult = mappingModel.performValueTransformation("testString");
    assertMappingSkipped(mappedResult);
  }

  @Test
  void shouldGetBySource_multiMatch() {
    Mapper<Source, Destination> mapper = getMapper();

    Predicate<String> sourcePredicate = nameEqualsPredicate("string");

    MappingModel<Source, Destination> model = mapper.getMappingModel();
    MappingModel<Source, Destination>.TransformationSearchResult mappingModel = model
        .findMappingBySource(sourcePredicate);

    assertResult(mappingModel);
    assertMultiResult(mappingModel);

    assertThatThrownBy(() -> mappingModel.performValueTransformation("testString"))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldGetByDest_singleMatch() {
    Mapper<Source, Destination> mapper = getMapper();

    Predicate<String> destPredicate = nameEqualsPredicate("stringRename");

    MappingModel<Source, Destination> model = mapper.getMappingModel();
    MappingModel<Source, Destination>.TransformationSearchResult mappingModel = model
        .findMappingByDestination(destPredicate);

    assertResult(mappingModel);
    assertSingleResult(mappingModel);
    assertValueMapping(mappingModel);

    MappedResult mappedResult = mappingModel.performValueTransformation("testString");

    assertMappingValue(mappedResult, "testString");
  }

  @Test
  void shouldGetBySourceAndDest_singleMatch() {
    Mapper<Source, Destination> mapper = getMapper();

    Predicate<String> sourcePredicate = nameEqualsPredicate("string");
    Predicate<String> destPredicate = nameEqualsPredicate("stringRename");

    MappingModel<Source, Destination> model = mapper.getMappingModel();
    MappingModel<Source, Destination>.TransformationSearchResult mappingModel = model.findMapping(sourcePredicate,
        destPredicate);

    assertResult(mappingModel);
    assertSingleResult(mappingModel);
    assertValueMapping(mappingModel);

    MappedResult mappedResult = mappingModel.performValueTransformation("testString");

    assertMappingValue(mappedResult, "testString");
  }

  private Mapper<Source, Destination> getMapper() {
    Mapper<NestedSource, NestedDestination> nestedMapper = Mapping.from(NestedSource.class)
        .to(NestedDestination.class)
        .reassign(NestedSource::getString)
        .to(NestedDestination::getStringRenamed)
        .mapper();
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .useMapper(nestedMapper)
        .omitInSource(Source::getOmitInSource)
        .omitInDestination(Destination::getOmitInDestination)
        .reassign(Source::getString)
        .to(Destination::getStringRename)
        .replace(Source::getString, Destination::getStringLength)
        .with(String::length)
        .reassign(Source::getNested)
        .to(Destination::getNested)
        .replace(Source::getNested, Destination::getFlattened)
        .with(NestedSource::getString)
        .set(Destination::getDoesNotExistInSource)
        .with(Source::getString)
        .mapper();
    return mapper;
  }

  private void assertValueMapping(MappingModel<Source, Destination>.TransformationSearchResult mappingModel) {
    assertTrue(mappingModel.isValueTransformation());
    assertFalse(mappingModel.isObjectTransformation());
  }

  private void assertObjectMapping(MappingModel<Source, Destination>.TransformationSearchResult mappingModel) {
    assertTrue(mappingModel.isObjectTransformation());
    assertFalse(mappingModel.isValueTransformation());
  }

  private void assertMappingSkipped(MappedResult mappedResult) {
    assertEquals(MappingOperation.SKIP, mappedResult.getOperation());
  }

  private void assertSingleResult(MappingModel<Source, Destination>.TransformationSearchResult mappingModel) {
    assertTrue(mappingModel.hasSingleResult(), "Mapping model should have a single result!");
    assertFalse(mappingModel.hasMultipleResults(), "Mapping model should not have multiple results!");
  }

  private void assertMappingValue(MappedResult mappedResult, Object expectedValue) {
    assertMappingHasValue(mappedResult);
    assertEquals(expectedValue, mappedResult.getValue());
  }

  private void assertMappingHasValue(MappedResult mappedResult) {
    assertTrue(mappedResult.hasValue(), "Mapping result should have a value!");
  }

  private void assertResult(MappingModel<Source, Destination>.TransformationSearchResult mappingModel) {
    assertTrue(mappingModel.hasResult(), "Mapping model should have a result!");
  }

  private void assertMultiResult(MappingModel<Source, Destination>.TransformationSearchResult mappingModel) {
    assertFalse(mappingModel.hasSingleResult(), "Mapping model should have multiple results!");
  }

}
