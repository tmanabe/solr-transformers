package io.github.tmanabe;

import io.github.tmanabe.attribute.FloatBufferAttribute;
import io.github.tmanabe.attribute.LongBufferAttribute;
import io.github.tmanabe.attribute.ShapeAttribute;
import io.github.tmanabe.attribute.NameAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.nio.FloatBuffer;
import java.nio.LongBuffer;

public class TensorSummarizer {
    private final CharTermAttribute charTermAttribute;

    public TensorSummarizer(CharTermAttribute charTermAttribute, NameAttribute nameAttribute, ShapeAttribute shapeAttribute) {
        charTermAttribute.setEmpty();
        charTermAttribute.append(nameAttribute.getKey());
        charTermAttribute.append("=");
        charTermAttribute.append(nameAttribute.get());
        charTermAttribute.append(", ");
        charTermAttribute.append(shapeAttribute.getKey());
        charTermAttribute.append("=");
        if (null == shapeAttribute.get()) {
            charTermAttribute.append((String) null);
        } else {
            charTermAttribute.append(shapeAttribute.get().toString());
        }
        this.charTermAttribute = charTermAttribute;
    }

    public void append(LongBufferAttribute longBufferAttribute) {
        charTermAttribute.append(", ");
        charTermAttribute.append(longBufferAttribute.getKey());
        charTermAttribute.append("=");
        LongBuffer longBuffer = longBufferAttribute.get();
        if (null == longBuffer) {
            charTermAttribute.append((String) null);
            return;
        }
        charTermAttribute.append("[");
        if (longBuffer.limit() < 8) {
            for (int i = 0; i < longBuffer.limit(); ++i) {
                if (0 < i) charTermAttribute.append(", ");
                charTermAttribute.append(Long.toString(longBuffer.get(i)));
            }
        } else {
            for (int i = 0; i < 3; ++i) {
                charTermAttribute.append(Long.toString(longBuffer.get(i)));
                charTermAttribute.append(", ");
            }
            charTermAttribute.append("...");
            for (int i = longBuffer.limit() - 3; i < longBuffer.limit(); ++i) {
                charTermAttribute.append(", ");
                charTermAttribute.append(Long.toString(longBuffer.get(i)));
            }
        }
        charTermAttribute.append("]");
    }

    public void append(FloatBufferAttribute floatBufferAttribute) {
        charTermAttribute.append(", ");
        charTermAttribute.append(floatBufferAttribute.getKey());
        charTermAttribute.append("=");
        FloatBuffer floatBuffer = floatBufferAttribute.get();
        if (null == floatBuffer) {
            charTermAttribute.append((String) null);
            return;
        }
        charTermAttribute.append("[");
        if (floatBuffer.limit() < 4) {
            for (int i = 0; i < floatBuffer.limit(); ++i) {
                if (0 < i) charTermAttribute.append(", ");
                charTermAttribute.append(Float.toString(floatBuffer.get(i)));
            }
        } else {
            charTermAttribute.append(Float.toString(floatBuffer.get(0)));
            charTermAttribute.append(", ..., ");
            charTermAttribute.append(Float.toString(floatBuffer.get(floatBuffer.limit() - 1)));
        }
        charTermAttribute.append("]");
    }
}
