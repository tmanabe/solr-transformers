package io.github.tmanabe.stream;

import io.github.tmanabe.PythonProcess;
import io.github.tmanabe.SafetensorsBuilder;
import io.github.tmanabe.SafetensorsViewer;
import io.github.tmanabe.TensorSummarizer;
import io.github.tmanabe.attribute.FloatBufferAttribute;
import io.github.tmanabe.attribute.ShapeAttribute;
import io.github.tmanabe.attribute.LongBufferAttribute;
import io.github.tmanabe.attribute.NameAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.LongBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

public class PythonTokenFilter extends TokenFilter {
    private final PythonProcess pythonProcess;
    private final NameAttribute nameAttribute = addAttribute(NameAttribute.class);
    private final ShapeAttribute shapeAttribute = addAttribute(ShapeAttribute.class);
    private final LongBufferAttribute longBufferAttribute = addAttribute(LongBufferAttribute.class);
    private final FloatBufferAttribute floatBufferAttribute = addAttribute(FloatBufferAttribute.class);
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
                LongBuffer longBuffer = longBufferAttribute.get();
                if (null != longBuffer) {
                    safetensorsBuilder.add(nameAttribute.get(), shapeAttribute.get(), longBuffer);
                }
                FloatBuffer floatBuffer = floatBufferAttribute.get();
                if (null != floatBuffer) {
                    safetensorsBuilder.add(nameAttribute.get(), shapeAttribute.get(), floatBuffer);
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
            nameAttribute.set(tensorName);
            shapeAttribute.set(safetensorsViewer.getHeader().get(tensorName).getShape());
            floatBufferAttribute.set(safetensorsViewer.getFloatBuffer(tensorName));
            new TensorSummarizer(charTermAttribute, nameAttribute, shapeAttribute).append(floatBufferAttribute);
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
