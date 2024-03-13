package com.remondis.remap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.Semaphore;

import org.junit.Before;
import org.junit.Test;

public class InvocationSensorTest {

  @Before
  public void setup() {
    InvocationSensor.interceptionHandlerCache.clear();
  }

  @Test
  public void shouldCacheThreadSafe() {

    Semaphore s1 = new Semaphore(1);
    s1.acquireUninterruptibly();

    Semaphore s2 = new Semaphore(1);
    s2.acquireUninterruptibly();

    InterceptionHandler<?> interceptionHandler = InvocationSensor.interceptionHandlerCache.get(DummyDto.class);

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
  public void shouldCache() {
    assertTrue(InvocationSensor.interceptionHandlerCache.isEmpty());
    InvocationSensor<DummyDto> invocationSensor = new InvocationSensor<>(DummyDto.class);
    assertFalse(InvocationSensor.interceptionHandlerCache.isEmpty());
    assertTrue(InvocationSensor.interceptionHandlerCache.containsKey(DummyDto.class));
    assertNotNull(InvocationSensor.interceptionHandlerCache.get(DummyDto.class));

    InterceptionHandler<?> interceptionHandler = InvocationSensor.interceptionHandlerCache.get(DummyDto.class);

    DummyDto sensor = invocationSensor.getSensor();
    sensor.getString();

    List<String> trackedPropertyNames = interceptionHandler.getTrackedPropertyNames();
    assertEquals(1, trackedPropertyNames.size());
    assertTrue(trackedPropertyNames.contains("string"));

    sensor.getAnotherString();
    trackedPropertyNames = interceptionHandler.getTrackedPropertyNames();
    assertEquals(1, trackedPropertyNames.size());
    assertTrue(trackedPropertyNames.contains("anotherString"));

    sensor.getString();
    sensor.getAnotherString();
    trackedPropertyNames = interceptionHandler.getTrackedPropertyNames();
    assertEquals(2, trackedPropertyNames.size());
    assertTrue(trackedPropertyNames.contains("string"));
    assertTrue(trackedPropertyNames.contains("anotherString"));

  }

}
