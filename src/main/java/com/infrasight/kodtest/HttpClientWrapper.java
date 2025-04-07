package com.infrasight.kodtest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

/**
 * A wrapper around Java's HttpClient to handle HTTP requests and responses.
 * It includes retry logic for handling rate limiting (HTTP 429) responses.
 */
public class HttpClientWrapper {
    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public HttpClientWrapper() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public <T> Optional<T> sendRequest(String url, String method, Map<String, String> headers, Object body, TypeReference<T> responseType) {
        int maxRetries = 6;
        int retryDelay = 1000;

        for (int i = 0; i < maxRetries; i++) {
            try {
                HttpRequest.Builder reqBuilder = HttpRequest.newBuilder().uri(URI.create(url));
                headers.forEach(reqBuilder::header);

                if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
                    String reqBody = objectMapper.writeValueAsString(body);
                    reqBuilder.method(method, HttpRequest.BodyPublishers.ofString(reqBody));
                } else {
                    reqBuilder.method(method, HttpRequest.BodyPublishers.noBody());
                }

                HttpRequest request = reqBuilder.build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 429) { 
                    Thread.sleep(retryDelay);
                    retryDelay += 1000;
                    continue;
                }

                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    return Optional.ofNullable(objectMapper.readValue(response.body(), responseType));
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }

        System.err.println("Max retries reached. Request failed permanently.");
        return Optional.empty();
    }
}