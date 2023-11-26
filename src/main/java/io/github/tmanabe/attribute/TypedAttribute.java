package io.github.tmanabe.attribute;

import org.apache.lucene.util.Attribute;

public interface TypedAttribute<T> extends Attribute {
  T get();

  void set(T t);

  String getKey();
}
