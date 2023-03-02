package com.andmal.client;

import java.io.*;
import java.net.Socket;

public class Http1Client {
    public static void main(String[] args) {
        h2Req();
//        simpleClient();
    }

    private static void simpleClient() {
        try {
            try (Socket socket = new Socket("facebook.com", 80)) {
                try (var writer = new PrintWriter(socket.getOutputStream())) {
                    writer.print("GET / HTTP/1.1\n");
                    writer.print("Host: facebook.com\r\n");
                    writer.print("\r\n");
                    writer.flush();

                    socket.shutdownOutput();

                    String outStr;
                    try (var bufRead = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                        while ((outStr = bufRead.readLine()) != null) {
                            System.out.println(outStr);
                        }
                        socket.shutdownInput();
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void h2Req() {
        try {
            try (Socket client = new Socket("tunetheweb.com", 80)) {
                System.out.println(">> sending request");
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
                try (PrintWriter writer = new PrintWriter(client.getOutputStream())) {
//                    writer.print(PRI);
                    writer.print(settings);
//                    writer.print(headers);
                    writer.flush();

//                    client.shutdownOutput();

                    String data;
                    try (var reader = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                        while ((data = reader.readLine()) != null) {
                            System.out.println(data);
                        }
                    }
                    client.shutdownInput();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}