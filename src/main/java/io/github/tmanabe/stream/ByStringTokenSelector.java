package io.github.tmanabe.stream;

import io.github.tmanabe.attribute.StringAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;

public class ByStringTokenSelector extends TokenFilter {
    private final String string;
    private final StringAttribute stringAttribute = addAttribute(StringAttribute.class);

    public ByStringTokenSelector(TokenStream input, String string) {
        super(input);
        this.string = string;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        while (input.incrementToken()) {
            if (stringAttribute.get().equals(string)) {
                return true;
            }
        }
        return false;
    }
}
