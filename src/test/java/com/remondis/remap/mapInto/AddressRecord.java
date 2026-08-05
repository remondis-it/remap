package com.remondis.remap.mapInto;

/**
 * A record destination element type for testing mapInto collection key matching with records. The
 * {@code houseNumber} component is not part of the source type and must be preserved from the matched destination
 * element.
 */
public record AddressRecord(String street, String city, Integer houseNumber) {
}
