# solr-transformers

## Requirements

### Java Packages

- [safetensors-java](https://github.com/tmanabe/safetensors-java)

### Python Packages

- torch
- transformers
- (For [LINE's DistilBERT](https://huggingface.co/line-corporation/line-distilbert-base-japanese))
  - sentencepiece
  - fugashi
  - unidic-lite

## Example

### Schema

```xml
  <fieldType name="float_array" class="io.github.tmanabe.schema.FloatArrayField">
    <analyzer>
      <tokenizer class="io.github.tmanabe.stream.DistilBertJapaneseTokenizerFactory"/>
      <filter class="io.github.tmanabe.stream.LineDistilBertTokenFilterFactory"/>
      <filter class="io.github.tmanabe.stream.ByNameTokenSelectorFactory" select="last_hidden_state"/>
      <filter class="io.github.tmanabe.stream.AveragePoolingTokenFilterFactory"/>
      <filter class="io.github.tmanabe.stream.VectorNormalizerTokenFilterFactory"/>
    </analyzer>
  </fieldType>
```

### Analysis Screen

![example](example.png)
