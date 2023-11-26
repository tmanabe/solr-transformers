package io.github.tmanabe.stream;

import org.apache.lucene.analysis.TokenFilterFactory;
import org.apache.lucene.analysis.TokenStream;

import java.util.Map;

public class ByNameTokenSelectorFactory extends TokenFilterFactory {
    String name;

    public ByNameTokenSelectorFactory(Map<String, String> args) {
        super(args);
        name = args.remove("select");
    }

    @Override
    public ByNameTokenSelector create(TokenStream input) {
        return new ByNameTokenSelector(input, name);
    }
}
