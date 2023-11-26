package io.github.tmanabe.stream;

import io.github.tmanabe.PythonProcess;
import io.github.tmanabe.SafetensorsBuilder;
import io.github.tmanabe.SafetensorsViewer;
import io.github.tmanabe.TensorSummarizer;
import io.github.tmanabe.attribute.FloatArrayAttribute;
import io.github.tmanabe.attribute.IntegerListAttribute;
import io.github.tmanabe.attribute.LongArrayAttribute;
import io.github.tmanabe.attribute.StringAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class PythonTokenFilter extends TokenFilter {
    private final PythonProcess pythonProcess;
    private final StringAttribute stringAttribute = addAttribute(StringAttribute.class);
    private final IntegerListAttribute integerListAttribute = addAttribute(IntegerListAttribute.class);
    private final LongArrayAttribute longArrayAttribute = addAttribute(LongArrayAttribute.class);
    private final FloatArrayAttribute floatArrayAttribute = addAttribute(FloatArrayAttribute.class);
    private final CharTermAttribute charTermAttribute = addAttribute(CharTermAttribute.class);  // For debugging

    private SafetensorsViewer safetensorsViewer = null;
    private Queue<String> tensorNamesToOutput = null;

    public PythonTokenFilter(TokenStream input, PythonProcess pythonProcess) {
        super(input);
        this.pythonProcess = pythonProcess;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (null == safetensorsViewer) {
            SafetensorsBuilder safetensorsBuilder = new SafetensorsBuilder();
            while (input.incrementToken()) {
                long[] longArray = longArrayAttribute.get();
                if (null != longArray) {
                    safetensorsBuilder.add(stringAttribute.get(), integerListAttribute.get(), longArray);
                }
                float[] floatArray = floatArrayAttribute.get();
                if (null != floatArray) {
                    safetensorsBuilder.add(stringAttribute.get(), integerListAttribute.get(), floatArray);
                }
            }
            synchronized (pythonProcess) {
                pythonProcess.write(safetensorsBuilder);
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
                FloatBuffer floatBuffer = safetensorsViewer.getFloatBuffer(tensorName);
                float[] floats = new float[floatBuffer.limit()];
                floatBuffer.get(floats);
                floatArrayAttribute.set(floats);
            }
            new TensorSummarizer(charTermAttribute, tensorName, shape).append(floatArrayAttribute.get());
            return true;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        safetensorsViewer = null;
        tensorNamesToOutput = null;
    }
}
