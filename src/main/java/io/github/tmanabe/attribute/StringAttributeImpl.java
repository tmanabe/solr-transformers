package io.github.tmanabe.attribute;

import org.apache.lucene.util.AttributeReflector;

public class StringAttributeImpl extends TypedAttributeImpl<String> implements StringAttribute {
    @Override
    public void reflectWith(AttributeReflector reflector) {
        reflector.reflect(this.getClass(), "String", get());
    }
}
