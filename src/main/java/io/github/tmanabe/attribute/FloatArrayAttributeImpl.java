package io.github.tmanabe.attribute;

import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class FloatArrayAttributeImpl extends TypedAttributeImpl<float[]> implements FloatArrayAttribute {
    @Override
    public void reflectWith(AttributeReflector reflector) {
        reflector.reflect(this.getClass(), "float[]", get());
    }

    @Override
    public void copyTo(AttributeImpl target) {
        if (target instanceof FloatArrayAttributeImpl) {
            ((FloatArrayAttributeImpl) target).set(get());
        }
    }
}
