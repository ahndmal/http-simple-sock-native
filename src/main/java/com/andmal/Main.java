package com.andmal;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try {
            try (ServerSocket serverSocket = new ServerSocket(8080)) {
                System.out.println(">>> Server started on port 8080");

                while(true) {
                    Socket accept = serverSocket.accept();

                    System.out.println("> client connected");

                    InputStream inputStream = accept.getInputStream();
                    OutputStream outputStream = accept.getOutputStream();

                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));

                    writer.write("HTTP/1.1 200 OK\n");
                    writer.write("Content-Type: text/html\r\n\n");
                    writer.write("<html>" +
                            "<head><title>Java</title></head>" +
                            "<body>" +
                            "<h2>Java Server</h2>" +
                            "<div>Test</div>" +
                            "</body>" +
                            "</html>");

                    System.out.println("request ended");
                    writer.close();
                    inputStream.close();
                    outputStream.close();
                    accept.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}