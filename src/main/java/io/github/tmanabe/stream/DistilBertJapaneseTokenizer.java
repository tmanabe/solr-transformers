package io.github.tmanabe.stream;

import io.github.tmanabe.SafetensorsViewer;
import io.github.tmanabe.TensorSummarizer;
import io.github.tmanabe.attribute.LongArrayAttribute;
import io.github.tmanabe.PythonProcess;
import io.github.tmanabe.attribute.StringAttribute;
import io.github.tmanabe.attribute.IntegerListAttribute;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;

import java.io.IOException;
import java.nio.LongBuffer;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class DistilBertJapaneseTokenizer extends Tokenizer {
    private final PythonProcess pythonProcess;
    private final StringAttribute stringAttribute = addAttribute(StringAttribute.class);
    private final IntegerListAttribute integerListAttribute = addAttribute(IntegerListAttribute.class);
    private final LongArrayAttribute longArrayAttribute = addAttribute(LongArrayAttribute.class);
    private final CharTermAttribute charTermAttribute = addAttribute(CharTermAttribute.class);  // For debugging

    private SafetensorsViewer safetensorsViewer = null;
    private Queue<String> tensorNamesToOutput = null;

    public DistilBertJapaneseTokenizer(AttributeFactory factory, PythonProcess pythonProcess) {
        super(factory);
        this.pythonProcess = pythonProcess;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (null == safetensorsViewer) {
            synchronized (pythonProcess) {
                pythonProcess.write(IOUtils.toString(input));
                safetensorsViewer = pythonProcess.read();
            }
        }
        if (null == tensorNamesToOutput) {
            tensorNamesToOutput = new ArrayDeque<>(safetensorsViewer.getHeader().keySet());
        }
        if (tensorNamesToOutput.isEmpty()) {
            return false;
        } else {
            String tensorName = tensorNamesToOutput.poll();
            clearAttributes();
            stringAttribute.set(tensorName);
            List<Integer> shape = safetensorsViewer.getHeader().get(tensorName).getShape();
            integerListAttribute.set(shape);
            {
                LongBuffer longBuffer = safetensorsViewer.getLongBuffer(tensorName);
                long[] longs = new long[longBuffer.limit()];
                longBuffer.get(longs);
                longArrayAttribute.set(longs);
            }
            new TensorSummarizer(charTermAttribute, tensorName, shape).append(longArrayAttribute.get());
            return true;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        this.safetensorsViewer = null;
        this.tensorNamesToOutput = null;
    }
}
