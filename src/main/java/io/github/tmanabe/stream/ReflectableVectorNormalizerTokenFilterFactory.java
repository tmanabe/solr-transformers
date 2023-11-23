package io.github.tmanabe.stream;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.TokenFilterFactory;

import java.util.Map;

public class ReflectableVectorNormalizerTokenFilterFactory extends TokenFilterFactory {
    public ReflectableVectorNormalizerTokenFilterFactory(Map<String, String> args) {
        super(args);
    }

    @Override
    public ReflectableVectorNormalizerTokenFilter create(TokenStream input) {
        return new ReflectableVectorNormalizerTokenFilter(input);
    }
}
