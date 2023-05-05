package com.remondis.remap.utils.mapover;

import static com.remondis.remap.utils.property.ChangeType.ALL;

import java.util.Collection;
import java.util.function.Function;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.CollectionChangeFunction;
import com.remondis.remap.utils.property.match.MatchMethod;
import com.remondis.remap.utils.property.visitor.MapOverCollectionVisitor;

public class MapOverCollectionReferenceBuilder<M extends MapOverWithReference<R, T>, R, T, TT>
    extends MapOverCollectionBuilder<M, R, T, TT> {

  public MapOverCollectionReferenceBuilder(M mapOver, Function<T, Collection<TT>> propertyExtractor,
      MatchMethod<TT> matchMethod) {
    super(mapOver, propertyExtractor, matchMethod);
  }

  public <ID> M byReference(Function<TT, ID> idExtractor) {
    return byReference(idExtractor, ALL);
  }

  public <ID> M byReference(Function<TT, ID> idExtractor, ChangeType changeType) {
    mapOver.getWalker()
        .addProperty(propertyExtractor, new MapOverCollectionVisitor<>(matchMethod, changeType,
            new CollectionChangeFunction<>(mapOver.getEntityManager(), idExtractor)));
    return mapOver;
  }
}
