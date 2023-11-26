package io.github.tmanabe.stream;

import io.github.tmanabe.attribute.NameAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;

public class ByNameTokenSelector extends TokenFilter {
    private final String name;
    private final NameAttribute nameAttribute = addAttribute(NameAttribute.class);

    public ByNameTokenSelector(TokenStream input, String name) {
        super(input);
        this.name = name;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        while (input.incrementToken()) {
            if (nameAttribute.get().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
