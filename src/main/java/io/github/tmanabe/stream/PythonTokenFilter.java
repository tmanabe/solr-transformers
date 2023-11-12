package io.github.tmanabe.stream;

import io.github.tmanabe.PythonProcess;
import io.github.tmanabe.Safetensors;
import io.github.tmanabe.SafetensorsBuilder;
import io.github.tmanabe.attribute.IntegerListAttribute;
import io.github.tmanabe.attribute.LongArrayAttribute;
import io.github.tmanabe.attribute.StringAttribute;
import io.github.tmanabe.demo2.FloatArrayAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

public class PythonTokenFilter extends TokenFilter {
    private final PythonProcess pythonProcess;
    private final StringAttribute stringAttribute = addAttribute(StringAttribute.class);
    private final IntegerListAttribute integerListAttribute = addAttribute(IntegerListAttribute.class);
    private final LongArrayAttribute longArrayAttribute = addAttribute(LongArrayAttribute.class);
    private final FloatArrayAttribute floatArrayAttribute = addAttribute(FloatArrayAttribute.class);

    private Safetensors safetensors = null;
    private Queue<String> tensorNamesToOutput = null;
    private float[] floats = null;

    public PythonTokenFilter(TokenStream input, PythonProcess pythonProcess) {
        super(input);
        this.pythonProcess = pythonProcess;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (null == safetensors) {
            SafetensorsBuilder safetensorsBuilder = new SafetensorsBuilder();
            while (input.incrementToken()) {
                long[] longArray = longArrayAttribute.get();
                if (null != longArray) {
                    safetensorsBuilder.add(stringAttribute.get(), integerListAttribute.get(), longArray);
                }
                float[] floatArray = floatArrayAttribute.getFloatArray();
                if (null != floatArray) {
                    safetensorsBuilder.add(stringAttribute.get(), integerListAttribute.get(), floatArray);
                }
            }
            synchronized (pythonProcess) {
                pythonProcess.write(safetensorsBuilder);
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
                FloatBuffer floatBuffer = safetensors.getFloatBuffer(tensorName);
                if (null == floats || floatBuffer.limit() != floats.length) {
                    floats = new float[floatBuffer.limit()];
                }
                floatBuffer.get(floats);
                floatArrayAttribute.setFloatArray(floats);
            }
            return true;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        safetensors = null;
        tensorNamesToOutput = null;
    }
}
