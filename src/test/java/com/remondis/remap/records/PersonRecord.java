package com.remondis.remap.records;

/**
 * Record with same field names as {@link PersonPojo} for implicit mapping tests.
 */
public record PersonRecord(String name, int age, String email, boolean active) {
}
