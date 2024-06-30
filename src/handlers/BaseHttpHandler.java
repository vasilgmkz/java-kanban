package handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected void sendText(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(StandardCharsets.UTF_8));
        }
        exchange.close();
    }
}
