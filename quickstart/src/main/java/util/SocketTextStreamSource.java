package util;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.myorg.quickstart.FromFileToStream;

public class SocketTextStreamSource implements SourceFunction<String> {

    private volatile boolean running;

    public SocketTextStreamSource() {
        this.running = true;
    }

    @Override
    public void run(SourceContext<String> context) throws Exception {
        try (SocketConnection conn = FromFileToStream.CONNECTION) {
            String line;

            while (this.running && (line = conn.getReader().readLine()) != null) {
                context.collect(line);
            }
        }
    }

    @Override
    public void cancel() {
        this.running = false;
    }
}
