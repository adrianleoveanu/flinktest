package util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class SocketConnection implements Closeable {

    private final String host;
    private final int port;
    private final Object lock;
    private volatile Socket socket;
    private volatile BufferedReader reader;
    private volatile PrintStream writer;

    public SocketConnection(String host, int port) {
        this.host = host;
        this.port = port;
        this.lock = new Object();
        this.socket = null;
        this.reader = null;
        this.writer = null;
    }

    private void connect() throws IOException {
        this.socket = new Socket(this.host, this.port);
        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.writer = new PrintStream(this.socket.getOutputStream());
    }

    private void ensureConnected() throws IOException {
        // only acquire lock if null
        if (this.socket == null) {
            synchronized (this.lock) {
                // recheck if socket is still null
                if (this.socket == null) {
                    connect();
                }
            }
        }
    }

    public BufferedReader getReader() throws IOException {
        ensureConnected();
        return this.reader;
    }

    public PrintStream getWriter() throws IOException {
        ensureConnected();
        return this.writer;
    }

    @Override
    public void close() throws IOException {
        if (this.socket != null) {
            synchronized (this.lock) {
                if (this.socket != null) {
                    this.reader.close();
                    this.reader = null;

                    this.writer.close();
                    this.writer = null;

                    this.socket.close();
                    this.socket = null;
                }
            }
        }
    }
}