package io.github.tmanabe.attribute;

import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

import java.util.List;

public class ShapeAttributeImpl extends TypedAttributeImpl<List<Integer>> implements ShapeAttribute {
    @Override
    public String getKey() {
        return "Shape";
    }

    @Override
    public void copyTo(AttributeImpl target) {
        if (target instanceof ShapeAttributeImpl) {
            ((ShapeAttributeImpl) target).set(get());
        }
    }
}
