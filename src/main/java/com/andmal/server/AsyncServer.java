package com.andmal.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncServer {

    public static void main(String[] args) {
        init();
    }

    public static void init() {
        final String HOST = "localhost";
        final int PORT = 8080;

        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {

            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(executorService);

            try (AsynchronousServerSocketChannel asyncServer = AsynchronousServerSocketChannel.open(group)) {

                asyncServer.bind(new InetSocketAddress(HOST, PORT));
                System.out.printf("[SERVER] Listening on %s : %d", HOST, PORT);

                while (true) {
                    Future<AsynchronousSocketChannel> future = asyncServer.accept();

                    AsynchronousSocketChannel client = future.get();

                    System.out.printf("[SERVER] Accepted connection from %s",client.getRemoteAddress());

                    String data = """
                            HTTP/1.1 200 OK
                            Date: Mon, 27 Jul 2009 12:28:53 GMT
                            Server: Java
                            Accept-Ranges: bytes
                            Vary: Accept-Encoding
                            Content-Type: text/plain
                            
                            Hello World! My content includes a trailing CRLF.
                            """;

                    System.out.println(data);

                    ByteBuffer byteBuffer = ByteBuffer.wrap(data.getBytes());

//                    byteBuffer.put(data.getBytes());

                    client.write(byteBuffer);

//                    client.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
//                        @Override
//                        public void completed(Integer result, ByteBuffer attachment) {
//                            attachment.clear();
//                        }
//
//                        @Override
//                        public void failed(Throwable exc, ByteBuffer attachment) {
//                            System.err.println("[SERVER] Failed to read from client: " + exc);
//                            exc.printStackTrace();
//                        }
//                    });

                    byteBuffer.clear();
                    client.shutdownInput();
                    client.shutdownOutput();

                    client.close();

//                    client.read(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
//                        @Override
//                        public void completed(Integer result, ByteBuffer attachment) {
//                            attachment.flip();
//                            if (result != 1) {
//                                onMessageReceived(client, byteBuffer);
//                            }
//                            attachment.clear();
//                            client.read(attachment, attachment, this);
//                        }
//
//                        @Override
//                        public void failed(Throwable exc, ByteBuffer attachment) {
//                            System.err.println("[SERVER] Failed to read from client: " + exc);
//                            exc.printStackTrace();
//                        }
//                    });
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void onMessageReceived(AsynchronousSocketChannel client, ByteBuffer buffer) {
        System.out.printf("[SERVER] Received message from server: %s", client);
        System.out.printf("[SERVER] Buffer: %s", buffer);
        System.out.printf("[SERVER] Buffer: %s", new String(buffer.array()));
    }
}
