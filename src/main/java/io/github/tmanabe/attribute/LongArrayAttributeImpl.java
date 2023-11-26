package io.github.tmanabe.attribute;

import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class LongArrayAttributeImpl extends TypedAttributeImpl<long[]> implements LongArrayAttribute {
    @Override
    public void reflectWith(AttributeReflector reflector) {
        reflector.reflect(this.getClass(), "long[]", get());
    }

    @Override
    public void copyTo(AttributeImpl target) {
        if (target instanceof LongArrayAttributeImpl) {
            ((LongArrayAttributeImpl) target).set(get());
        }
    }
}
