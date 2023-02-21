package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
  public static void main(String[] args) throws IOException {

    final var server = new Server();

    // добавление хендлеров (обработчиков)
    server.addHandler("GET", "/messages", new Handler() {
      @Override
      public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
        var response = "Hello fro GET /message";
        responseStream.write(response.getBytes(), 0, response.length());
      }
    });
    server.addHandler("POST", "/messages", new Handler() {
      @Override
      public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
        var response = "Hello fro POST /message";
        responseStream.write(response.getBytes(), 0, response.length());
      }
    });

  server.listen(9999);
  }
}


