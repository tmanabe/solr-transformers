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

public class LineDistilBertTokenFilterFactory extends TokenFilterFactory {
    private final PythonProcess pythonProcess;

    public LineDistilBertTokenFilterFactory(Map<String, String> args) throws IOException {
        super(args);
        PythonProcess.HealthChecker healthChecker = (pythonProcess) -> {
            SafetensorsBuilder safetensorsBuilder = new SafetensorsBuilder();
            safetensorsBuilder.add("input_ids", Arrays.asList(1, 5), new long[]{2L, 295L, 2607L, 3029L, 3L});
            safetensorsBuilder.add("attention_mask", Arrays.asList(1, 5), new long[]{1, 1, 1, 1, 1});
            pythonProcess.write(safetensorsBuilder);
            Safetensors safetensors = pythonProcess.read();
            {
                FloatBuffer lastHiddenState = safetensors.getFloatBuffer("last_hidden_state");
                assert lastHiddenState.limit() == 5 * 768;
                assert Float.toString(lastHiddenState.get(0)).startsWith("0.442");
                assert Float.toString(lastHiddenState.get(5 * 768 - 1)).startsWith("0.096");
            }
        };
        pythonProcess = new PythonProcess("line_distilbert_filter.py", healthChecker);
    }

    @Override
    public PythonTokenFilter create(TokenStream input) {
        return new PythonTokenFilter(input, pythonProcess);
    }
}
