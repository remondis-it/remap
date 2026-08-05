package com.remondis.remap.records;

/**
 * Record with an extra field not in the source, for omitInDestination tests.
 */
public record PersonWithExtraRecord(String name, int age, String extra) {
}
