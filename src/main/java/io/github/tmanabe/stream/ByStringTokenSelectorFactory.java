package io.github.tmanabe.stream;

import org.apache.lucene.analysis.TokenFilterFactory;
import org.apache.lucene.analysis.TokenStream;

import java.util.Map;

public class ByStringTokenSelectorFactory extends TokenFilterFactory {
    String string;

    public ByStringTokenSelectorFactory(Map<String, String> args) {
        super(args);
        string = args.remove("string");
    }

    @Override
    public ByStringTokenSelector create(TokenStream input) {
        return new ByStringTokenSelector(input, string);
    }
}
