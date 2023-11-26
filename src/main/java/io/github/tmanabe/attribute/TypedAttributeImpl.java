package io.github.tmanabe.attribute;

import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public abstract class TypedAttributeImpl<T> extends AttributeImpl implements TypedAttribute<T> {
  private T t = null;

  @Override
  public T get() {
    return t;
  }

  @Override
  public void set(T t) {
    this.t = t;
  }

  @Override
  public void clear() {
    t = null;
  }
}
