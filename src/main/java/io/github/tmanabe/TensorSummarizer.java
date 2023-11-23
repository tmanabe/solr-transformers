package io.github.tmanabe;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.util.Arrays;
import java.util.List;

public class TensorSummarizer {
    private final CharTermAttribute charTermAttribute;

    public TensorSummarizer(CharTermAttribute charTermAttribute, String tensorName, List<Integer> shape) {
        charTermAttribute.setEmpty();
        charTermAttribute.append("tensorName=");
        charTermAttribute.append(tensorName);
        charTermAttribute.append(", shape=");
        if (null == shape) {
            charTermAttribute.append((String) null);
        } else {
            charTermAttribute.append(shape.toString());
        }
        this.charTermAttribute = charTermAttribute;
    }

    public void append(long[] longs) {
        charTermAttribute.append(", longs=");
        if (longs.length < 8) {
            charTermAttribute.append(Arrays.toString(longs));
        } else {
            charTermAttribute.append("[");
            for (int i = 0; i < 3; ++i) {
                charTermAttribute.append(Long.toString(longs[i]));
                charTermAttribute.append(", ");
            }
            charTermAttribute.append("...");
            for (int i = longs.length - 3; i < longs.length; ++i) {
                charTermAttribute.append(", ");
                charTermAttribute.append(Long.toString(longs[i]));
            }
            charTermAttribute.append("]");
        }
    }

    public void append(float[] floats) {
        charTermAttribute.append(", floats=");
        if (floats.length < 4) {
            charTermAttribute.append(Arrays.toString(floats));
        } else {
            charTermAttribute.append("[");
            charTermAttribute.append(Float.toString(floats[0]));
            charTermAttribute.append(", ..., ");
            charTermAttribute.append(Float.toString(floats[floats.length - 1]));
            charTermAttribute.append("]");
        }
    }
}
