package io.github.tmanabe.stream;

import io.github.tmanabe.TensorSummarizer;
import io.github.tmanabe.attribute.FloatBufferAttribute;
import io.github.tmanabe.attribute.ShapeAttribute;
import io.github.tmanabe.attribute.NameAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.nio.FloatBuffer;

public class ReflectableVectorNormalizerTokenFilter extends TokenFilter {
    private final FloatBufferAttribute floatBufferAttribute = addAttribute(FloatBufferAttribute.class);

    // For debugging
    private final CharTermAttribute charTermAttribute = addAttribute(CharTermAttribute.class);
    private final NameAttribute nameAttribute = addAttribute(NameAttribute.class);
    private final ShapeAttribute shapeAttribute = addAttribute(ShapeAttribute.class);

    public ReflectableVectorNormalizerTokenFilter(TokenStream input) {
        super(input);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            FloatBuffer floatBuffer = floatBufferAttribute.get();
            if (null == floatBufferAttribute.get()) {
                return true;
            }
            float norm = 0.0f;
            for (int i = 0; i < floatBuffer.limit(); ++i) {
                norm += floatBuffer.get(i) * floatBuffer.get(i);
            }
            if (0 < norm) {
                norm = (float) Math.sqrt(norm);
                for (int i = 0; i < floatBuffer.limit(); ++i) {
                    floatBuffer.put(i, floatBuffer.get(i) / norm);
                }
            }
            new TensorSummarizer(charTermAttribute, nameAttribute, shapeAttribute).append(floatBufferAttribute);
            return true;
        } else {
            return false;
        }
    }
}
