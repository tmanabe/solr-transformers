package io.github.tmanabe.stream;

import io.github.tmanabe.TensorSummarizer;
import io.github.tmanabe.attribute.IntegerListAttribute;
import io.github.tmanabe.attribute.StringAttribute;
import io.github.tmanabe.demo2.FloatArrayAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

public class ReflectableVectorNormalizerTokenFilter extends TokenFilter {
    private final FloatArrayAttribute floatArrayAttribute = addAttribute(FloatArrayAttribute.class);

    // For debugging
    private final StringAttribute stringAttribute = addAttribute(StringAttribute.class);
    private final IntegerListAttribute integerListAttribute = addAttribute(IntegerListAttribute.class);
    private final CharTermAttribute charTermAttribute = addAttribute(CharTermAttribute.class);

    public ReflectableVectorNormalizerTokenFilter(TokenStream input) {
        super(input);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            float[] floatArray = floatArrayAttribute.getFloatArray();
            if (null == floatArray) {
                return true;
            }
            float norm = 0.0f;
            for (float f : floatArray) {
                norm += f * f;
            }
            if (0 < norm) {
                norm = (float) Math.sqrt(norm);
                for (int i = 0; i < floatArray.length; ++i) {
                    floatArray[i] /= norm;
                }
            }
            new TensorSummarizer(charTermAttribute, stringAttribute.get(), integerListAttribute.get()).append(floatArray);
            return true;
        } else {
            return false;
        }
    }
}
