package com.remondis.remap.records;

/**
 * Record with a nested complex type for hierarchical mapping tests.
 */
public record PersonWithAddressRecord(String name, AddressRecord address) {
}
