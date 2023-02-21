package ru.netology;

import java.util.List;

public class Request {



    private String method;
    private final String path;
    private final String protocolVersion;
    private final List<String> headers;
    private final byte[] body;

    public Request(String method, String path, String protocolVersion, List<String> headers, byte[] body) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }


    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}
