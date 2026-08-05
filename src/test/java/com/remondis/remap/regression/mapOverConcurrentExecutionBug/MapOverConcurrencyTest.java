package com.remondis.remap.regression.mapOverConcurrentExecutionBug;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

import com.remondis.remap.utils.mapOver.MapOver;
import com.remondis.remap.utils.propertywalker.PropertyAccess;
import com.remondis.remap.utils.propertywalker.VisitorFunction;

/**
 * Reproduces a thread-confinement bug in {@code BiRecursivePropertyWalker.execute()}: property visitors of a single
 * {@link MapOver#mapOver(Object, Object)} call were dispatched via {@code Collection.parallelStream()}, so a
 * synchronous call mapping a handful of bean properties could silently run user-supplied getters/setters on
 * {@code ForkJoinPool.commonPool()} threads instead of the calling thread. {@link MapOver}'s own documentation
 * targets partial updates of <em>attached JPA entities</em>, whose persistence context is normally confined to a
 * single thread - running their accessors from arbitrary pool threads risks violating that confinement.
 * <p>
 * A {@link CyclicBarrier} with as many parties as there are properties only trips if all property visitors are
 * actually executing at the same time on different threads. A correct, sequential implementation can never trip
 * such a barrier, because only one visitor at a time ever calls {@code await()} on the (single) calling thread.
 */
class MapOverConcurrencyTest {

  @Test
  void shouldNotRunPropertyVisitorsConcurrently() {
    CyclicBarrier barrier = new CyclicBarrier(2);

    MapOver<Bean, Bean> mapOver = MapOver.create(Bean.class)
        .addPropertyAction(Bean::getP0, Bean::setP0, barrierAwaitingVisitor(barrier))
        .addPropertyAction(Bean::getP1, Bean::setP1, barrierAwaitingVisitor(barrier))
        .build();

    Bean source = new Bean();
    source.setP0(10);
    source.setP1(20);
    Bean target = new Bean();

    assertThatThrownBy(() -> mapOver.mapOver(source, target)).hasCauseInstanceOf(TimeoutException.class);
  }

  @Test
  void shouldStillMapAllPropertiesSequentially() {
    MapOver<Bean, Bean> mapOver = MapOver.create(Bean.class)
        .mapProperty(Bean::getP0, Bean::setP0)
        .mapProperty(Bean::getP1, Bean::setP1)
        .build();

    Bean source = new Bean();
    source.setP0(10);
    source.setP1(20);
    Bean target = new Bean();

    mapOver.mapOver(source, target);

    assertThat(target.getP0()).isEqualTo(10);
    assertThat(target.getP1()).isEqualTo(20);
  }

  private static <T, P> VisitorFunction<T, P> barrierAwaitingVisitor(CyclicBarrier barrier) {
    return (PropertyAccess<T, P> access) -> {
      try {
        barrier.await(1, TimeUnit.SECONDS);
      } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
        throw new RuntimeException("Property visitors did not run concurrently.", e);
      }
      access.targetProperty()
          .set(access.sourceProperty()
              .get());
    };
  }
}
