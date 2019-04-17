package com.remondis.remap;

import static com.remondis.remap.Properties.asString;

import java.beans.PropertyDescriptor;
import java.util.Optional;

import com.remondis.propertypath.api.Get;
import com.remondis.propertypath.api.Getter;
import com.remondis.propertypath.api.PropertyPath;

/**
 * The reassign operation maps a field to another field while the field names may differ. A reassign operation is only
 * allowed on fields of the same type.
 *
 * @param <RS> The type of the source field.
 * @param <RD> The type of the destination field.
 * @author schuettec
 */
public class PropertyPathTransformation<RS, RD> extends Transformation {

  private static final String PROPERTY_PATH_MSG = "Replacing %s\n           with %s\n"
      + "           using property path: %s";
  private Get<RS, RD, ?> propertyPath;

  PropertyPathTransformation(Mapping<?, ?> mapping, PropertyDescriptor sourceProperty,
      PropertyDescriptor destinationProperty, PropertyPath<RD, RS, ?> propertyPath) {
    super(mapping, sourceProperty, destinationProperty);
    this.propertyPath = createGetter(sourceProperty, propertyPath);
  }

  @SuppressWarnings("unchecked")
  private Get<RS, RD, ?> createGetter(PropertyDescriptor sourceProperty, PropertyPath<RD, RS, ?> propertyPath) {
    try {
      return Getter.newFor((Class<RS>) sourceProperty.getPropertyType())
          .evaluate(propertyPath);
    } catch (Exception e) {
      // TODO: This is a known API bug in property path that will be fixed with the next release.
      throw new RuntimeException(
          "This will never happen, if this happens please file a bug report on https://github.com/remondis-it/remap/issues");
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void performTransformation(PropertyDescriptor sourceProperty, Object source,
      PropertyDescriptor destinationProperty, Object destination) throws MappingException {
    Object sourceValue = readOrFail(sourceProperty, source);

    if (sourceValue == null) {
      // Skip if source value is null. Property paths are null-friendly.
      return;
    }

    try {
      Optional<RD> optional = propertyPath.from((RS) sourceValue);
      if (optional.isPresent()) {
        RD destinationValue = optional.get();
        writeOrFail(destinationProperty, destination, destinationValue);
      }
    } catch (Exception e) {
      throw new MappingException(
          String.format("The property path for mapping %s to %s evaluating %s failed with an exception.",
              asString(sourceProperty), asString(destinationProperty), propertyPath.toString()),
          e);
    }
  }

  @Override
  protected void validateTransformation() throws MappingException {
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((propertyPath == null) ? 0 : propertyPath.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PropertyPathTransformation other = (PropertyPathTransformation) obj;
    if (propertyPath == null) {
      if (other.propertyPath != null) {
        return false;
      }
    } else if (!propertyPath.equals(other.propertyPath)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString(boolean detailed) {
    return String.format(PROPERTY_PATH_MSG, asString(sourceProperty, detailed), asString(destinationProperty, detailed),
        propertyPath.toString(detailed));
  }
}
