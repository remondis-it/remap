package com.remondis.remap.utils.mapover;

import static com.remondis.remap.utils.property.ChangeType.ALL;

import java.util.Collection;
import java.util.function.Function;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.CollectionChangeFunction;
import com.remondis.remap.utils.property.match.MatchMethod;
import com.remondis.remap.utils.property.visitor.MapOverCollectionVisitor;
import com.remondis.remap.utils.property.visitor.VisitorFunction;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapOverCollectionBuilder<M extends MapOverCommon<R, T>, R, T, TT> {
  protected final M mapOver;
  protected final Function<T, Collection<TT>> propertyExtractor;
  protected final MatchMethod<TT> matchMethod;

  public M byOverwrite() {
    return byOverwrite(ALL);
  }

  public M byOverwrite(ChangeType changeType) {
    return byAction(new MapOverCollectionVisitor<>(matchMethod, changeType, new CollectionChangeFunction<>()));
  }

  public M byMapper(MapOver<TT, TT> mapper) {
    return byMapper(mapper, ALL);
  }

  public M byMapper(MapOver<TT, TT> mapper, ChangeType changeType) {
    return byAction(new MapOverCollectionVisitor<>(matchMethod, changeType, new CollectionChangeFunction<>(mapper)));
  }

  public M byAction(VisitorFunction<T, Collection<TT>> action) {
    mapOver.getWalker()
        .addProperty(propertyExtractor, propertyExtractor, action); // TODO reafactor dupl
    return mapOver;
  }
}
