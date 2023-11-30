package io.github.tmanabe.stream;

import io.github.tmanabe.PythonProcess;
import io.github.tmanabe.SafetensorsBuilder;
import io.github.tmanabe.SafetensorsViewer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.TokenFilterFactory;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Map;

public class VectorNormalizerTokenFilterFactory extends TokenFilterFactory {
    private final PythonProcess pythonProcess;

    public VectorNormalizerTokenFilterFactory(Map<String, String> args) throws IOException {
        super(args);
        PythonProcess.HealthChecker healthChecker = (pythonProcess) -> {
            SafetensorsBuilder safetensorsBuilder = new SafetensorsBuilder();
            safetensorsBuilder.add("last_hidden_state", Arrays.asList(1, 2), new float[]{-3f, 4f});
            pythonProcess.write(safetensorsBuilder);
            SafetensorsViewer safetensorsViewer = pythonProcess.read();
            {
                FloatBuffer lastHiddenState = safetensorsViewer.getFloatBuffer("last_hidden_state");
                assert lastHiddenState.limit() == 2;
                assert lastHiddenState.get(0) == -0.6f;
                assert lastHiddenState.get(1) == 0.8f;
            }
        };
        pythonProcess = new PythonProcess("vector_normalizer.py", healthChecker);
    }

    @Override
    public PythonTokenFilter create(TokenStream input) {
        return new PythonTokenFilter(input, pythonProcess);
    }
}
