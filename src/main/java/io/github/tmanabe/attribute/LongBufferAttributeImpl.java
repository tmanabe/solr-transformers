package io.github.tmanabe.attribute;

import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

import java.nio.LongBuffer;

public class LongBufferAttributeImpl extends TypedAttributeImpl<LongBuffer> implements LongBufferAttribute {
    @Override
    public String getKey() {
        return "LongBuffer";
    }

    @Override
    public void copyTo(AttributeImpl target) {
        if (target instanceof LongBufferAttributeImpl) {
            ((LongBufferAttributeImpl) target).set(get());
        }
    }
}
