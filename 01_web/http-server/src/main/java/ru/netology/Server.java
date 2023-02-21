package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server {


    final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    public Server() throws IOException {
    }

    private final ConcurrentHashMap <String, ConcurrentHashMap<String, Handler>> handlers = new ConcurrentHashMap<>();

    public void listen (int port) {
            try {
                ServerSocket server = new ServerSocket(port);
                System.out.println("Server started");
                while (true) {

                    Socket socket = server.accept();
                    System.out.println("Client connected");
                    handlingServer(socket);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        

    public void handlingServer(Socket socket) {

                new Thread(() -> {
                    try (final var out = new BufferedOutputStream(socket.getOutputStream());
                         final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                        while (true) {

                            // read only request line for simplicity
                            // must be in form GET /path HTTP/1.1
                            final var requestLine = in.readLine();
                            final var parts = requestLine.split(" ");

                            if (parts.length != 3) {
                                // just close socket
                                continue;
                            }

                            var request = new Request(parts[0], parts[1], parts[2], null, null);

                            if (!handlers.containsKey(request.getMethod())) {
                                out.write((
                                        "HTTP/1.1 404 Not Found\r\n" +
                                                "Content-Length: 0\r\n" +
                                                "Connection: close\r\n" +
                                                "\r\n"
                                ).getBytes());
                                out.flush();
                            }

                            final var path = parts[1];
                            if (!validPaths.contains(path)) {
                                out.write((
                                        "HTTP/1.1 404 Not Found\r\n" +
                                                "Content-Length: 0\r\n" +
                                                "Connection: close\r\n" +
                                                "\r\n"
                                ).getBytes());
                                out.flush();
                                continue;
                            }

                            final var filePath = Path.of(".", "public", path);
                            final var mimeType = Files.probeContentType(filePath);

                            // special case for classic
                            if (path.equals("/classic.html")) {
                                final var template = Files.readString(filePath);
                                final var content = template.replace(
                                        "{time}",
                                        LocalDateTime.now().toString()
                                ).getBytes();
                                out.write((
                                        "HTTP/1.1 200 OK\r\n" +
                                                "Content-Type: " + mimeType + "\r\n" +
                                                "Content-Length: " + content.length + "\r\n" +
                                                "Connection: close\r\n" +
                                                "\r\n"
                                ).getBytes());
                                out.write(content);
                                out.flush();
                                continue;
                            }

                            final var length = Files.size(filePath);
                            out.write((
                                    "HTTP/1.1 200 OK\r\n" +
                                            "Content-Type: " + mimeType + "\r\n" +
                                            "Content-Length: " + length + "\r\n" +
                                            "Connection: close\r\n" +
                                            "\r\n"
                            ).getBytes());
                            Files.copy(filePath, out);
                            out.flush();
                        }
                     } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
        }


    public void addHandler(String method, String path, ru.netology.Handler handler) {
        if (!handlers.containsKey(method)) {
            handlers.put(method, new ConcurrentHashMap<>());
        }

        handlers.get(method).put(path, handler);
    }
}








