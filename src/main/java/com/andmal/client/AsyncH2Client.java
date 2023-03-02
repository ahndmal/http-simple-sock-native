package com.andmal.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class AsyncH2Client {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(executorService);
            try (AsynchronousSocketChannel client = AsynchronousSocketChannel.open(group)) {
                Future<Void> connect = client.connect(new InetSocketAddress("tunetheweb.com", 80));
                connect.get();

                String PRI = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n";
                String linkHeader = "'Link','</assets/css/common.css>;rel=preload'";
                String req2 = """
                        send HEADERS frame <length=53, flags=0x25, stream_id=13>
                        ; END_STREAM | END_HEADERS | PRIORITY
                        (padlen=0, dep_stream_id=11, weight=16, exclusive=0)
                        ; Open new stream
                        :method: GET
                        :path: /performance/
                        :scheme: https
                        :authority: www.tunetheweb.com
                        accept: */*
                        accept-encoding: gzip, deflate
                        user-agent: Java
                        """;

//             ; Open new stream
//             :method: GET
//             :path: /
//             :scheme: https
//             :authority: www.facebook.com
//             accept: */*
//             accept-encoding: gzip, deflate
//             user-agent: nghttp2/1.28.0

                String settings = """
                        send SETTINGS frame <length=12, flags=0x00, stream_id=0>
                        (niv=2)
                        [SETTINGS_MAX_CONCURRENT_STREAMS(0x03):100]
                        [SETTINGS_INITIAL_WINDOW_SIZE(0x04):65535]
                        """;

//                String settingsEx1 = """
//                        SETTINGS_HEADER_TABLE_SIZE (0x1)
//                        SETTINGS_ENABLE_PUSH (0x2)
//                        SETTINGS_MAX_CONCURRENT_STREAMS (0x3)
//                        SETTINGS_INITIAL_WINDOW_SIZE (0x4)
//                        SETTINGS_MAX_FRAME_SIZE (0x5)
//                        SETTINGS_MAX_HEADER_LIST_SIZE (0x6)
//                        SETTINGS_ACCEPT_CACHE_DIGEST (0x7)a
//                        SETTINGS_ENABLE_CONNECT_PROTOCOL (0x8)b
//                        """;

                String priorityFrame = """
                        [ 0.107] send PRIORITY frame <length=5, flags=0x00, stream_id=3>
                             (dep_stream_id=0, weight=201, exclusive=0)
                        [ 0.107] send PRIORITY frame <length=5, flags=0x00, stream_id=5>
                              (dep_stream_id=0, weight=101, exclusive=0)
                        [ 0.107] send PRIORITY frame <length=5, flags=0x00, stream_id=7>
                               (dep_stream_id=0, weight=1, exclusive=0)
                        [ 0.107] send PRIORITY frame <length=5, flags=0x00, stream_id=9>
                               (dep_stream_id=7, weight=1, exclusive=0)
                        [ 0.107] send PRIORITY frame <length=5, flags=0x00, stream_id=11>
                              (dep_stream_id=3, weight=1, exclusive=0)
                        """;

                String headers = """
                        send HEADERS frame <length=53, flags=0x25, stream_id=13>
                        ; END_STREAM | END_HEADERS | PRIORITY
                        (padlen=0, dep_stream_id=11, weight=16, exclusive=0)
                        ; Open new stream
                        :method: GET
                        :path: /performance/
                        :scheme: https
                        :authority: www.tunetheweb.com
                        accept: */*
                        accept-encoding: gzip, deflate
                        user-agent: nghttp2/1.43.0
                        """;

                ByteBuffer byteBuffer = ByteBuffer.allocate(PRI.length() + settings.length());
                byteBuffer.put(PRI.getBytes());
//                byteBuffer.put(settings.getBytes());

//                Future<Integer> written = client.write(byteBuffer);

                client.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        System.out.println("[CLIENT] written data: " + new String(attachment.array()));
                        attachment.clear();
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        System.err.println("[CLIENT] Failed to read from client: " + exc);
                        exc.printStackTrace();
                    }
                });


                Thread.sleep(2000);

                byteBuffer.clear();
                byteBuffer.put(settings.getBytes());
                client.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        System.out.println("[CLIENT] written data: " + new String(attachment.array()));
                        attachment.clear();
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        System.err.println("[CLIENT] Failed to read from client: " + exc);
                        exc.printStackTrace();
                    }
                });

                // read

                ByteBuffer readData = ByteBuffer.allocate(200);
                client.read(readData, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        attachment.flip();
                        if (result != 1) {
                            onMessageReceived(client, byteBuffer);
                        }
                        attachment.clear();
                        client.read(attachment, attachment, this);
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        System.err.println("[SERVER] Failed to read from client: " + exc);
                        exc.printStackTrace();
                    }
                });

            }

        }
    }

    private static void onMessageReceived(AsynchronousSocketChannel client, ByteBuffer buffer) {
        System.out.println("[SERVER] Received message from server: " + client);
        System.out.println("[SERVER] Buffer: " + buffer);
        System.out.println("[SERVER] Buffer: " + new String(buffer.array()));
    }
}
