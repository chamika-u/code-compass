package com.hackathon.mcp.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Service for IBM WatsonX integration
 */
@ApplicationScoped
public class WatsonXService {
    
    private static final Logger logger = LoggerFactory.getLogger(WatsonXService.class);
    
    @Inject
    @ConfigProperty(name = "watsonx.api.url")
    private String apiUrl;
    
    @Inject
    @ConfigProperty(name = "watsonx.api.key")
    private String apiKey;
    
    @Inject
    @ConfigProperty(name = "watsonx.project.id")
    private String projectId;
    
    @Inject
    @ConfigProperty(name = "watsonx.model.id", defaultValue = "ibm/granite-13b-chat-v2")
    private String modelId;
    
    @Inject
    @ConfigProperty(name = "watsonx.max.tokens", defaultValue = "2000")
    private int maxTokens;
    
    @Inject
    @ConfigProperty(name = "watsonx.temperature", defaultValue = "0.7")
    private double temperature;
    
    @Inject
    @ConfigProperty(name = "watsonx.timeout.seconds", defaultValue = "60")
    private int timeoutSeconds;
    
    private final HttpClient httpClient;

    public WatsonXService() {
        this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }

    /**
     * Generate text using WatsonX
     * 
     * @param prompt The prompt to send to the model
     * @return Generated text response
     */
    public String generate(String prompt) {
        logger.info("Generating text with WatsonX");
        logger.debug("Prompt length: {} characters", prompt.length());
        
        try {
            String requestBody = buildRequestBody(prompt);
            HttpRequest request = buildRequest(requestBody);
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            validateResponse(response);
            
            String generatedText = extractGeneratedText(response.body());
            logger.info("Successfully generated text, length: {} characters", generatedText.length());
            
            return generatedText;
            
        } catch (Exception e) {
            logger.error("Error generating text with WatsonX", e);
            throw new RuntimeException("Failed to generate text: " + e.getMessage(), e);
        }
    }

    /**
     * Generate structured JSON using WatsonX
     * 
     * @param prompt The prompt to send to the model
     * @param schema Expected JSON schema description
     * @return Generated JSON string
     */
    public String generateJson(String prompt, String schema) {
        logger.info("Generating JSON with WatsonX");
        
        String enhancedPrompt = String.format(
            "%s\n\nPlease respond with valid JSON following this schema:\n%s\n\nJSON Response:",
            prompt,
            schema
        );
        
        return generate(enhancedPrompt);
    }

    /**
     * Build request body for WatsonX API
     */
    private String buildRequestBody(String prompt) {
        return String.format("""
            {
              "model_id": "%s",
              "input": "%s",
              "parameters": {
                "max_new_tokens": %d,
                "temperature": %.2f,
                "top_p": 0.9,
                "top_k": 50,
                "repetition_penalty": 1.1
              },
              "project_id": "%s"
            }
            """,
            modelId,
            escapeJson(prompt),
            maxTokens,
            temperature,
            projectId
        );
    }

    /**
     * Build HTTP request for WatsonX API
     */
    private HttpRequest buildRequest(String body) {
        String endpoint = apiUrl + "/ml/v1/text/generation?version=2023-05-29";
        
        return HttpRequest.newBuilder()
            .uri(URI.create(endpoint))
            .timeout(Duration.ofSeconds(timeoutSeconds))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
    }

    /**
     * Validate HTTP response
     */
    private void validateResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        
        if (statusCode >= 200 && statusCode < 300) {
            return;
        }
        
        String errorMessage = switch (statusCode) {
            case 400 -> "Bad request - invalid parameters";
            case 401 -> "Unauthorized - invalid API key";
            case 403 -> "Forbidden - insufficient permissions";
            case 404 -> "Not found - invalid endpoint or model";
            case 429 -> "Rate limit exceeded";
            case 500, 502, 503 -> "WatsonX API server error";
            default -> "HTTP error: " + statusCode;
        };
        
        logger.error("WatsonX API error: {} - {}", statusCode, errorMessage);
        logger.error("Response body: {}", response.body());
        throw new RuntimeException(errorMessage);
    }

    /**
     * Extract generated text from WatsonX response
     */
    private String extractGeneratedText(String responseBody) {
        try {
            // Simple JSON parsing - in production, use proper JSON library
            // Expected format: {"results":[{"generated_text":"..."}]}
            
            int textStart = responseBody.indexOf("\"generated_text\":\"");
            if (textStart == -1) {
                throw new RuntimeException("Could not find generated_text in response");
            }
            
            textStart += "\"generated_text\":\"".length();
            int textEnd = responseBody.indexOf("\"", textStart);
            
            if (textEnd == -1) {
                throw new RuntimeException("Malformed response - could not find end of generated_text");
            }
            
            String generatedText = responseBody.substring(textStart, textEnd);
            return unescapeJson(generatedText);
            
        } catch (Exception e) {
            logger.error("Error extracting generated text from response", e);
            throw new RuntimeException("Failed to parse WatsonX response: " + e.getMessage(), e);
        }
    }

    /**
     * Escape string for JSON
     */
    private String escapeJson(String text) {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    /**
     * Unescape JSON string
     */
    private String unescapeJson(String text) {
        return text
            .replace("\\\"", "\"")
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
            .replace("\\\\", "\\");
    }

    /**
     * Test connection to WatsonX
     */
    public boolean testConnection() {
        try {
            logger.info("Testing WatsonX connection");
            String response = generate("Hello, this is a test.");
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            logger.error("WatsonX connection test failed", e);
            return false;
        }
    }
}

// Made with Bob
