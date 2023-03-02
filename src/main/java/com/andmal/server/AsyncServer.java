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
    public static void init() {
        final String HOST = "localhost";
        final int PORT = 8080;
        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(executorService);
            try (AsynchronousServerSocketChannel asyncServer = AsynchronousServerSocketChannel.open(group)) {
                asyncServer.bind(new InetSocketAddress(HOST, PORT));
                System.out.println("[SERVER] Listening on " + HOST + ":" + PORT);

                while (true) {
                    Future<AsynchronousSocketChannel> future = asyncServer.accept();
                    AsynchronousSocketChannel client = future.get();
                    System.out.println("[SERVER] Accepted connection from " + client.getRemoteAddress());

                    ByteBuffer byteBuffer = ByteBuffer.allocate(200);
                    StringBuilder data = new StringBuilder();
                    data.append("HTTP/1.1 200 OK\r\n");
                    data.append("Content-Type: text/html\n");
                    data.append("Host: localhost");
                    data.append("\r\n\r\n");
                    char[] html = "<!doctype html><html><head><title>Java HTTP</title></head><body>Test...</body></html>".toCharArray();
                    data.append(html);
                    System.out.println(data.toString());

                    byteBuffer.put(data.toString().getBytes());
                    client.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            attachment.clear();
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            System.err.println("[SERVER] Failed to read from client: " + exc);
                            exc.printStackTrace();
                        }
                    });

//                    client.close();
//                    asyncServer.close();

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
        System.out.println("[SERVER] Received message from server: " + client);
        System.out.println("[SERVER] Buffer: " + buffer);
        System.out.println("[SERVER] Buffer: " + new String(buffer.array()));
    }
}
