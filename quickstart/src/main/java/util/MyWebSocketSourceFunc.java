package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.myorg.quickstart.FromWebsocketToFile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;


public class MyWebSocketSourceFunc extends RichSourceFunction<String> {
    private boolean running = true;
    transient AsyncHttpClient client;
    transient BoundRequestBuilder boundRequestBuilder;
    transient WebSocketUpgradeHandler.Builder webSocketListener;


    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        WebSocketUpgradeHandler webSocketUpgradeHandler = webSocketListener.addWebSocketListener(
                new WebSocketListener() {

                    private final ObjectMapper myMapper = new ObjectMapper();

                    @Override
                    public void onOpen(WebSocket webSocket) {
                        webSocket.sendTextFrame("helloooooooooooooooooooooo");
                    }

                    @Override
                    public void onClose(WebSocket webSocket, int i, String s) {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onTextFrame(String payload, boolean finalFragment, int rsv) {
                        if (payload != null) {
                            try {
                                FromWebsocketToFile.MESSAGES.put(payload);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }).build();
        boundRequestBuilder.execute(webSocketUpgradeHandler).get();

        while (running) {
            ctx.collect(FromWebsocketToFile.MESSAGES.take());
        }
        running = false;
    }

    @Override
    public void cancel() {
        running = false;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        client = Dsl.asyncHttpClient();
        boundRequestBuilder = client.prepareGet("wss://echo.websocket.org");
        webSocketListener = new WebSocketUpgradeHandler.Builder();
    }

    private String hash(String input) {
        if (input == null) {
            return "-- NULL --";
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            return "--NOT CALCULATED--";
        }
    }
}
