package io.github.tmanabe.attribute;

import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class NameAttributeImpl extends TypedAttributeImpl<String> implements NameAttribute {
    @Override
    public String getKey() {
        return "Name";
    }

    @Override
    public void copyTo(AttributeImpl target) {
        if (target instanceof NameAttributeImpl) {
            ((NameAttributeImpl) target).set(get());
        }
    }
}
