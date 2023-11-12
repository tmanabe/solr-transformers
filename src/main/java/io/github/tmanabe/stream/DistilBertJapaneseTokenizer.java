package io.github.tmanabe.stream;

import io.github.tmanabe.attribute.LongArrayAttribute;
import io.github.tmanabe.PythonProcess;
import io.github.tmanabe.Safetensors;
import io.github.tmanabe.attribute.StringAttribute;
import io.github.tmanabe.attribute.IntegerListAttribute;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.AttributeFactory;

import java.io.IOException;
import java.nio.LongBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

public class DistilBertJapaneseTokenizer extends Tokenizer {
    private final PythonProcess pythonProcess;
    private final StringAttribute stringAttribute = addAttribute(StringAttribute.class);
    private final IntegerListAttribute integerListAttribute = addAttribute(IntegerListAttribute.class);
    private final LongArrayAttribute longArrayAttribute = addAttribute(LongArrayAttribute.class);

    private Safetensors safetensors = null;
    private Queue<String> tensorNamesToOutput = null;
    private long[] longs = null;


    public DistilBertJapaneseTokenizer(AttributeFactory factory, PythonProcess pythonProcess) {
        super(factory);
        this.pythonProcess = pythonProcess;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (null == safetensors) {
            synchronized (pythonProcess) {
                pythonProcess.write(IOUtils.toString(input));
                safetensors = pythonProcess.read();
            }
        }
        if (null == tensorNamesToOutput) {
            tensorNamesToOutput = new ArrayDeque<>(safetensors.getHeader().keySet());
        }
        if (tensorNamesToOutput.isEmpty()) {
            return false;
        } else {
            String tensorName = tensorNamesToOutput.poll();
            clearAttributes();
            stringAttribute.set(tensorName);
            integerListAttribute.set(safetensors.getHeader().get(tensorName).getShape());
            {
                LongBuffer longBuffer = safetensors.getLongBuffer(tensorName);
                if (null == longs || longBuffer.limit() != longs.length) {
                    longs = new long[longBuffer.limit()];
                }
                longBuffer.get(longs);
                longArrayAttribute.set(longs);
            }
            return true;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        this.safetensors = null;
        this.tensorNamesToOutput = null;
    }
}
