package com.infrasight.kodtest;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
/**
 *  Service class for handling authentication
 */
public class AuthenticationService {
    private final HttpClientWrapper httpClient;

    public AuthenticationService(HttpClientWrapper httpClient) {
        this.httpClient = httpClient;
    }
   
    public String authenticate(String authUrl, String username, String password) {
        Map<String, String> requestBody = Map.of("user", username, "password", password);

        Optional<JsonNode> response = httpClient.sendRequest(
            authUrl,
            "POST",
            Map.of("Content-Type", "application/json", "Accept", "application/json"),
            requestBody,
            new TypeReference<>() {}
        );

        if (response.isPresent() && response.get().has("token")) {
            return "Bearer " + response.get().get("token").asText();
        } else {
            throw new RuntimeException("Authentication failed.");
        }
    }
}