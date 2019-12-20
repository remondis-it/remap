package com.remondis.remap.deepcopy;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.remondis.remap.DeepCopy;
import com.remondis.remap.Mapper;

public class DeepCopyTest {

  private B b1;
  private B b2;
  private B b3;

  private A a;

  @Before
  public void setup() {
    this.b1 = new B(new BigDecimal(1L));
    this.b2 = new B(new BigDecimal(2L));
    this.b3 = new B(new BigDecimal(3L));

    Set<List<Map<NonJavaBean, B>>> map = new HashSet<>();
    List<Map<NonJavaBean, B>> list = new LinkedList<>();
    Map<NonJavaBean, B> innerMap = new Hashtable<>();
    map.add(list);
    list.add(innerMap);
    innerMap.put(new NonJavaBean("innerReadOnly"), new B(new BigDecimal(9999L)));

    this.a = new A(1, 101L, "string", new NonJavaBean("readOnlyString"), asList(b2, b3), map);
  }

  @Test
  public void shouldCreateDeepCopy_copyNonJavaBeanByFunction() {
    Mapper<A, A> mapper = DeepCopy.of(A.class)
        .copyWith(NonJavaBean.class, njb -> new NonJavaBean(njb.getReadOnly()))
        .getMapper();
    A deepCopyOfA = mapper.map(a);
    assertNotSame(a, deepCopyOfA);

    List<B> list = a.getBs();
    List<B> copyList = deepCopyOfA.getBs();
    assertNotSame(list, copyList);
    for (int i = 0; i < list.size(); i++) {
      B originalB = list.get(i);
      B deepCopyB = copyList.get(i);
      assertEquals(originalB, deepCopyB);
      assertNotSame(originalB, deepCopyB);
    }

    assertEquals(a.getNonJavaBean(), deepCopyOfA.getNonJavaBean());
    assertNotSame(a.getNonJavaBean(), deepCopyOfA.getNonJavaBean());
    assertSame(a.getPrimitive(), deepCopyOfA.getPrimitive());
    assertSame(a.getString(), deepCopyOfA.getString());
    assertSame(a.getWrapper(), deepCopyOfA.getWrapper());

    Set<List<Map<NonJavaBean, B>>> set = a.getMap();
    Set<List<Map<NonJavaBean, B>>> deepCopySet = deepCopyOfA.getMap();
    assertNotSame(set, deepCopySet);

    List<Map<NonJavaBean, B>> firstList = set.iterator()
        .next();
    List<Map<NonJavaBean, B>> firstListDeepCopy = deepCopySet.iterator()
        .next();
    assertEquals(firstList, firstListDeepCopy);
    assertNotSame(firstList, firstListDeepCopy);

    Map<NonJavaBean, B> map = firstList.iterator()
        .next();
    Map<NonJavaBean, B> deepCopyMap = firstListDeepCopy.iterator()
        .next();

    Entry<NonJavaBean, B> originalEntry = map.entrySet()
        .iterator()
        .next();

    Entry<NonJavaBean, B> deepCopyEntry = deepCopyMap.entrySet()
        .iterator()
        .next();

    assertEquals(originalEntry.getKey(), deepCopyEntry.getKey());
    assertNotSame(originalEntry.getKey(), deepCopyEntry.getKey());

    assertEquals(originalEntry.getValue(), deepCopyEntry.getValue());
    assertNotSame(originalEntry.getValue(), deepCopyEntry.getValue());

  }

  @Test
  public void shouldCreateDeepCopy_copyReferenceOfNonJavaBean() {
    Mapper<A, A> mapper = DeepCopy.of(A.class)
        .copyReference(NonJavaBean.class)
        .getMapper();
    A deepCopyOfA = mapper.map(a);
    assertNotSame(a, deepCopyOfA);

    List<B> list = a.getBs();
    List<B> copyList = deepCopyOfA.getBs();
    assertNotSame(list, copyList);
    for (int i = 0; i < list.size(); i++) {
      B originalB = list.get(i);
      B deepCopyB = copyList.get(i);
      assertEquals(originalB, deepCopyB);
      assertNotSame(originalB, deepCopyB);
    }

    assertSame(a.getNonJavaBean(), deepCopyOfA.getNonJavaBean());
    assertSame(a.getPrimitive(), deepCopyOfA.getPrimitive());
    assertSame(a.getString(), deepCopyOfA.getString());
    assertSame(a.getWrapper(), deepCopyOfA.getWrapper());

    Set<List<Map<NonJavaBean, B>>> set = a.getMap();
    Set<List<Map<NonJavaBean, B>>> deepCopySet = deepCopyOfA.getMap();
    assertNotSame(set, deepCopySet);

    List<Map<NonJavaBean, B>> firstList = set.iterator()
        .next();
    List<Map<NonJavaBean, B>> firstListDeepCopy = deepCopySet.iterator()
        .next();
    assertEquals(firstList, firstListDeepCopy);
    assertNotSame(firstList, firstListDeepCopy);

    Map<NonJavaBean, B> map = firstList.iterator()
        .next();
    Map<NonJavaBean, B> deepCopyMap = firstListDeepCopy.iterator()
        .next();

    Entry<NonJavaBean, B> originalEntry = map.entrySet()
        .iterator()
        .next();

    Entry<NonJavaBean, B> deepCopyEntry = deepCopyMap.entrySet()
        .iterator()
        .next();

    assertSame(originalEntry.getKey(), deepCopyEntry.getKey());

    assertEquals(originalEntry.getValue(), deepCopyEntry.getValue());
    assertNotSame(originalEntry.getValue(), deepCopyEntry.getValue());

  }

}
