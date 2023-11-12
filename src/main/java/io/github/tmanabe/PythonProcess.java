package io.github.tmanabe;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Consumer;

public class PythonProcess {
    @FunctionalInterface
    public interface HealthChecker {
        void check(PythonProcess pythonProcess) throws IOException;
    }

    private static final String PYTHON = "/Users/owner/.pyenv/versions/3.9.16/bin/python";  // Hard coding for security

    private final DataOutputStream dataOutputStream;
    private final DataInputStream dataInputStream;

    public PythonProcess(String resourceName, HealthChecker healthChecker) throws IOException {
        URL url = this.getClass().getResource(resourceName);

        if (null == url) throw new IOException("Resource not found: " + resourceName);

        File source = new File(url.getFile());
        File target = Files.createTempFile(resourceName, null).toFile();
        try {
            try (FileReader fileReader = new FileReader(source);
                 BufferedReader bufferedReader = new BufferedReader(fileReader);
                 PrintWriter printWriter = new PrintWriter(target)) {
                String line = bufferedReader.readLine();
                while (null != line) {
                    printWriter.println(line);
                    line = bufferedReader.readLine();
                }
            }
            Process process = new ProcessBuilder(PYTHON, target.getAbsolutePath()).start();
            this.dataOutputStream = new DataOutputStream(process.getOutputStream());
            this.dataInputStream = new DataInputStream(process.getInputStream());
            healthChecker.check(this);
        } finally {
            boolean ignore = target.delete();
        }
    }

    private void writeContentLength(long l) throws IOException {
        byte[] littleEndianLong = new byte[Long.BYTES];
        ByteBuffer.wrap(littleEndianLong).order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().put(l);
        dataOutputStream.write(littleEndianLong);
    }

    public void write(String string) throws IOException {  // To tokenize
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        writeContentLength(bytes.length);
        dataOutputStream.write(bytes);
        dataOutputStream.flush();
    }

    public void write(SafetensorsBuilder builder) throws IOException {
        writeContentLength(builder.contentLength());
        builder.build().save(dataOutputStream);
        dataOutputStream.flush();
    }

    public Safetensors read() throws IOException {
        dataInputStream.readLong();  // Skip unused content-length
        return Safetensors.load(dataInputStream);
    }
}
