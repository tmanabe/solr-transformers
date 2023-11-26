package io.github.tmanabe.stream;

import io.github.tmanabe.SafetensorsViewer;
import io.github.tmanabe.TensorSummarizer;
import io.github.tmanabe.attribute.LongBufferAttribute;
import io.github.tmanabe.PythonProcess;
import io.github.tmanabe.attribute.NameAttribute;
import io.github.tmanabe.attribute.ShapeAttribute;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

public class DistilBertJapaneseTokenizer extends Tokenizer {
    private final PythonProcess pythonProcess;
    private final NameAttribute nameAttribute = addAttribute(NameAttribute.class);
    private final ShapeAttribute shapeAttribute = addAttribute(ShapeAttribute.class);
    private final LongBufferAttribute longBufferAttribute = addAttribute(LongBufferAttribute.class);
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
            nameAttribute.set(tensorName);
            shapeAttribute.set(safetensorsViewer.getHeader().get(tensorName).getShape());
            longBufferAttribute.set(safetensorsViewer.getLongBuffer(tensorName));
            new TensorSummarizer(charTermAttribute, nameAttribute, shapeAttribute).append(longBufferAttribute);
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
