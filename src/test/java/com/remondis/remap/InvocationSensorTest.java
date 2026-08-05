package com.remondis.remap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.Semaphore;

import org.junit.jupiter.api.Test;

class InvocationSensorTest {

  @Test
  void shouldCacheThreadSafe() {

    Semaphore s1 = new Semaphore(1);
    s1.acquireUninterruptibly();

    Semaphore s2 = new Semaphore(1);
    s2.acquireUninterruptibly();

    Thread t1 = new Thread(new Runnable() {

      @Override
      public void run() {
        InvocationSensor<DummyDto> invocationSensor = new InvocationSensor<>(DummyDto.class);
        DummyDto sensor = invocationSensor.getSensor();
        sensor.getString();
        s2.release();
        s1.acquireUninterruptibly();
      }
    });
    t1.start();

    // Hier warte bis t1 mindestens getString() aufgerufen hast
    s2.acquireUninterruptibly();
    InvocationSensor<DummyDto> invocationSensor = new InvocationSensor<>(DummyDto.class);
    List<String> trackedPropertyNames = invocationSensor.getTrackedPropertyNames();
    assertTrue(trackedPropertyNames.isEmpty());
    s1.release();
  }

  @Test
  void shouldCache() {
    InvocationSensor<DummyDto> first = new InvocationSensor<>(DummyDto.class);
    InvocationSensor<DummyDto> second = new InvocationSensor<>(DummyDto.class);
    // The proxy is created once per type and cached, so all sensors of a type share the same proxy instance.
    assertSame(first.getSensor(), second.getSensor());
  }

  @Test
  void shouldTrackInvocations() {
    InvocationSensor<DummyDto> invocationSensor = new InvocationSensor<>(DummyDto.class);
    DummyDto sensor = invocationSensor.getSensor();

    sensor.getString();
    List<String> trackedPropertyNames = invocationSensor.getTrackedPropertyNames();
    assertEquals(1, trackedPropertyNames.size());
    assertTrue(trackedPropertyNames.contains("string"));

    sensor.getAnotherString();
    trackedPropertyNames = invocationSensor.getTrackedPropertyNames();
    assertEquals(1, trackedPropertyNames.size());
    assertTrue(trackedPropertyNames.contains("anotherString"));

    sensor.getString();
    sensor.getAnotherString();
    trackedPropertyNames = invocationSensor.getTrackedPropertyNames();
    assertEquals(2, trackedPropertyNames.size());
    assertTrue(trackedPropertyNames.contains("string"));
    assertTrue(trackedPropertyNames.contains("anotherString"));
  }

}
