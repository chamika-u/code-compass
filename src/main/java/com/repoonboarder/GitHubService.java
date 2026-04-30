package com.repoonboarder;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class GitHubService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final String GITHUB_API_BASE = "https://api.github.com";

    public GitHubService() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetches and returns the raw JSON from GitHub repository API
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @return Raw JSON response as String
     * @throws IOException If request fails
     * @throws InterruptedException If request is interrupted
     */
    public String getRepoSummary(String owner, String repo) throws IOException, InterruptedException {
        String url = String.format("%s/repos/%s/%s", GITHUB_API_BASE, owner, repo);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "GitHubService")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("GitHub API returned status code: " + response.statusCode());
        }
        
        return response.body();
    }

    /**
     * Fetches the file tree from GitHub repository and returns file paths as newline-separated string
     * Limited to 150 lines
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @return Newline-separated file paths (max 150 lines)
     * @throws IOException If request fails
     * @throws InterruptedException If request is interrupted
     */
    public String getFileTree(String owner, String repo) throws IOException, InterruptedException {
        String url = String.format("%s/repos/%s/%s/git/trees/HEAD?recursive=1", GITHUB_API_BASE, owner, repo);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "GitHubService")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("GitHub API returned status code: " + response.statusCode());
        }
        
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode tree = root.get("tree");
        
        if (tree == null || !tree.isArray()) {
            return "";
        }
        
        return tree.findValuesAsText("path").stream()
                .limit(150)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Reads a file from GitHub repository, decodes base64 content and returns it
     * Limited to 3000 characters
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param path File path in repository
     * @return Decoded file content (max 3000 characters)
     * @throws IOException If request fails
     * @throws InterruptedException If request is interrupted
     */
    public String readFile(String owner, String repo, String path) throws IOException, InterruptedException {
        String url = String.format("%s/repos/%s/%s/contents/%s", GITHUB_API_BASE, owner, repo, path);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "GitHubService")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("GitHub API returned status code: " + response.statusCode());
        }
        
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode contentNode = root.get("content");
        
        if (contentNode == null) {
            throw new IOException("No content field found in response");
        }
        
        // Remove newlines from base64 content and decode
        String base64Content = contentNode.asText().replaceAll("\\s+", "");
        byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
        String decodedContent = new String(decodedBytes, StandardCharsets.UTF_8);
        
        // Limit to 3000 characters
        if (decodedContent.length() > 3000) {
            return decodedContent.substring(0, 3000);
        }
        
        return decodedContent;
    }
}

// Made with Bob
