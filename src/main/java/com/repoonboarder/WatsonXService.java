package com.repoonboarder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Service for IBM WatsonX integration
 */
@ApplicationScoped
public class WatsonXService {

    @Inject
    @ConfigProperty(name = "watsonx.api.key")
    private String apiKey;

    @Inject
    @ConfigProperty(name = "watsonx.project.id")
    private String projectId;

    @Inject
    @ConfigProperty(name = "watsonx.url")
    private String watsonxUrl;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WatsonXService() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Gets IAM token from IBM Cloud
     * 
     * @return Access token
     * @throws IOException If request fails
     * @throws InterruptedException If request is interrupted
     */
    public String getIamToken() throws IOException, InterruptedException {
        String url = "https://iam.cloud.ibm.com/identity/token";
        
        String formData = "grant_type=" + URLEncoder.encode("urn:ibm:params:oauth:grant-type:apikey", StandardCharsets.UTF_8)
                + "&apikey=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("IAM token request failed with status code: " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode accessToken = root.get("access_token");

        if (accessToken == null) {
            throw new IOException("No access_token found in IAM response");
        }

        return accessToken.asText();
    }

    /**
     * Generates an onboarding guide using WatsonX
     * 
     * @param repoContext Repository context information
     * @return JSON content from WatsonX response
     * @throws IOException If request fails
     * @throws InterruptedException If request is interrupted
     */
    public String generateGuide(String repoContext) throws IOException, InterruptedException {
        String accessToken = getIamToken();
        
        String systemPrompt = "You are a senior engineer. Given the repo context, produce a structured onboarding guide. "
                + "Respond ONLY with a valid JSON object containing exactly these keys: "
                + "architectureOverview (string), keyFiles (array of objects with file and purpose), "
                + "gotchas (array of strings), firstTask (string).";

        String requestBody = buildChatRequestBody(systemPrompt, repoContext);
        
        String url = watsonxUrl + "/ml/v1/text/chat?version=2024-05-31";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("WatsonX API returned status code: " + response.statusCode() 
                    + ", body: " + response.body());
        }

        return extractContent(response.body());
    }

    /**
     * Builds the chat request body for WatsonX API
     */
    private String buildChatRequestBody(String systemPrompt, String userMessage) {
        try {
            String escapedSystemPrompt = objectMapper.writeValueAsString(systemPrompt);
            String escapedUserMessage = objectMapper.writeValueAsString(userMessage);
            
            // Remove surrounding quotes added by writeValueAsString
            escapedSystemPrompt = escapedSystemPrompt.substring(1, escapedSystemPrompt.length() - 1);
            escapedUserMessage = escapedUserMessage.substring(1, escapedUserMessage.length() - 1);

            return String.format("""
                {
                  "model_id": "ibm/granite-3-8b-instruct",
                  "messages": [
                    {
                      "role": "system",
                      "content": "%s"
                    },
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ],
                  "project_id": "%s"
                }
                """, escapedSystemPrompt, escapedUserMessage, projectId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build request body", e);
        }
    }

    /**
     * Extracts the content field from WatsonX JSON response
     */
    private String extractContent(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        
        // Navigate to choices[0].message.content
        JsonNode choices = root.get("choices");
        if (choices == null || !choices.isArray() || choices.size() == 0) {
            throw new IOException("No choices found in WatsonX response");
        }

        JsonNode firstChoice = choices.get(0);
        JsonNode message = firstChoice.get("message");
        if (message == null) {
            throw new IOException("No message found in WatsonX response");
        }

        JsonNode content = message.get("content");
        if (content == null) {
            throw new IOException("No content found in WatsonX response");
        }

        return content.asText();
    }
}

// Made with Bob