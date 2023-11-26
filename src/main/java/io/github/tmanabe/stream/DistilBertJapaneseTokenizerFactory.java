package io.github.tmanabe.stream;

import io.github.tmanabe.PythonProcess;
import io.github.tmanabe.SafetensorsViewer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

import java.io.IOException;
import java.nio.LongBuffer;
import java.util.Map;

public class DistilBertJapaneseTokenizerFactory extends TokenizerFactory {
    private final PythonProcess pythonProcess;

    public DistilBertJapaneseTokenizerFactory(Map<String, String> args) throws IOException {
        super(args);
        PythonProcess.HealthChecker healthChecker = (pythonProcess) -> {
            pythonProcess.write("ダミー入力");
            SafetensorsViewer safetensorsViewer = pythonProcess.read();
            {
                LongBuffer inputIDs = safetensorsViewer.getLongBuffer("input_ids");
                assert inputIDs.limit() == 5;
                assert inputIDs.get(0) == 2L;
                assert inputIDs.get(1) == 295L;
                assert inputIDs.get(2) == 2607L;
                assert inputIDs.get(3) == 3029L;
                assert inputIDs.get(4) == 3L;
            }
            {
                LongBuffer attentionMask = safetensorsViewer.getLongBuffer("attention_mask");
                assert attentionMask.limit() == 5;
                for (int i = 0; i < 5; ++i) assert 1L == attentionMask.get(i);
            }
        };
        pythonProcess = new PythonProcess("distilbert_japanese_tokenizer.py", healthChecker);
    }

    @Override
    public Tokenizer create(AttributeFactory factory) {
        return new DistilBertJapaneseTokenizer(factory, pythonProcess);
    }
}
