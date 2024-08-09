package util;

import javax.websocket.*;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebsocketClient extends RichSinkFunction<String> {
    final WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();

    @OnOpen
    public void open(Session session) {
        //Open websocket connexion
        try {
            Session webSocketSession = webSocketContainer.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session webSocketSession, EndpointConfig config) {
                    // session.addMessageHandler( ... );
                }
            }, URI.create("ws://some.uri"));
        } catch (DeploymentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnMessage
    void message(String msg) {
        //Send message in websocket

    }

}

