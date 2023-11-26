package io.github.tmanabe.attribute;

import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

import java.nio.FloatBuffer;

public class FloatBufferAttributeImpl extends TypedAttributeImpl<FloatBuffer> implements FloatBufferAttribute {
    @Override
    public String getKey() {
        return "FloatBuffer";
    }

    @Override
    public void copyTo(AttributeImpl target) {
        if (target instanceof FloatBufferAttributeImpl) {
            ((FloatBufferAttributeImpl) target).set(get());
        }
    }
}
