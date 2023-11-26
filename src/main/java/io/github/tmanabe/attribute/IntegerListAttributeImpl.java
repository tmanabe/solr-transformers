package io.github.tmanabe.attribute;

import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

import java.util.List;

public class IntegerListAttributeImpl extends TypedAttributeImpl<List<Integer>> implements IntegerListAttribute {
    @Override
    public void reflectWith(AttributeReflector reflector) {
        reflector.reflect(this.getClass(), "List<Integer>", get());
    }

    @Override
    public void copyTo(AttributeImpl target) {
        if (target instanceof IntegerListAttributeImpl) {
            ((IntegerListAttributeImpl) target).set(get());
        }
    }
}
