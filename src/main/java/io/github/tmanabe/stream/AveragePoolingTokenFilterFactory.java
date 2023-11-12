package io.github.tmanabe.stream;

import io.github.tmanabe.PythonProcess;
import io.github.tmanabe.Safetensors;
import io.github.tmanabe.SafetensorsBuilder;
import org.apache.lucene.analysis.TokenFilterFactory;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Map;

public class AveragePoolingTokenFilterFactory extends TokenFilterFactory {
    private final PythonProcess pythonProcess;

    public AveragePoolingTokenFilterFactory(Map<String, String> args) throws IOException {
        super(args);
        PythonProcess.HealthChecker healthChecker = (pythonProcess) -> {
            SafetensorsBuilder safetensorsBuilder = new SafetensorsBuilder();
            safetensorsBuilder.add("last_hidden_state", Arrays.asList(1, 2, 2), new float[]{-1f, 0f, 1f, 2f});
            pythonProcess.write(safetensorsBuilder);
            Safetensors safetensors = pythonProcess.read();
            {
                FloatBuffer lastHiddenState = safetensors.getFloatBuffer("last_hidden_state");
                assert lastHiddenState.limit() == 2;
                assert lastHiddenState.get(0) == 0f;
                assert lastHiddenState.get(1) == 1f;
            }
        };
        pythonProcess = new PythonProcess("average_pooling_filter.py", healthChecker);
    }

    @Override
    public PythonTokenFilter create(TokenStream input) {
        return new PythonTokenFilter(input, pythonProcess);
    }
}
