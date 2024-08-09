package util;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.myorg.quickstart.FromFileToStream;

public class SocketTextStreamSink extends RichSinkFunction<String> {

    private transient SocketConnection connection;

    @Override
    public void open(Configuration parameters) throws Exception {
        this.connection = FromFileToStream.CONNECTION;
    }

    @Override
    public void invoke(String value, Context context) throws Exception {
        this.connection.getWriter().println(value);
        this.connection.getWriter().flush();
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }
}
