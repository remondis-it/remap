package com.remondis.extern.usecase.metamodel;

import static com.remondis.remap.MappingModel.nameEqualsPredicate;

import org.junit.Test;

import com.remondis.remap.FieldTransformation;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingModel;

public class MetaModelFeatureTest {

  @Test
  public void shouldGetMetaModel() {

    Mapper<NestedSource, NestedDestination> nestedMapper = Mapping.from(NestedSource.class)
        .to(NestedDestination.class)
        .reassign(NestedSource::getString)
        .to(NestedDestination::getStringRenamed)
        .mapper();
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .useMapper(nestedMapper)
        .reassign(Source::getString)
        .to(Destination::getStringRename)
        .replace(Source::getString, Destination::getStringLength)
        .with(String::length)
        .omitInSource(Source::getZahl)
        .reassign(Source::getNested)
        .to(Destination::getNested)
        .replace(Source::getNested, Destination::getFlattened)
        .with(NestedSource::getString)
        .set(Destination::getDoesNotExistInSource)
        .with(Source::getString)
        .mapper();

    MappingModel<Source, Destination> model = mapper.getMappingModel();
    FieldTransformation reassign = model.getFieldTransformation(nameEqualsPredicate("string"),
        nameEqualsPredicate("stringRename"));

    // assertEquals("testString", reassign.performTransformation("testString", null));
  }

}
