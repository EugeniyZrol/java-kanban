package server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected void sendText(HttpExchange h, String text) throws IOException {
        sendResponse(h, 200, text);
    }

    protected void sendCreated(HttpExchange h, String text) throws IOException {
        sendResponse(h, 201, text);
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        sendResponse(h, 404, text);
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        sendResponse(h, 406, text);
    }

    protected void sendResponse(HttpExchange h, int statusCode, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;" + DEFAULT_CHARSET);
        h.sendResponseHeaders(statusCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected String readRequestBody(HttpExchange e) throws IOException {
        return new String(e.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }

    protected Integer getIdFromPath (String[] pathParts){
        if (pathParts.length > 2) {
            return Integer.parseInt(pathParts[2]);
        } return null;
    }
}