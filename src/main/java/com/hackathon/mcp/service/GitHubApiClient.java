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
import java.util.concurrent.CompletableFuture;

/**
 * Client for GitHub REST API
 */
@ApplicationScoped
public class GitHubApiClient {
    
    private static final Logger logger = LoggerFactory.getLogger(GitHubApiClient.class);
    
    @Inject
    @ConfigProperty(name = "github.api.base.url", defaultValue = "https://api.github.com")
    private String baseUrl;
    
    @Inject
    @ConfigProperty(name = "github.api.timeout.seconds", defaultValue = "30")
    private int timeoutSeconds;
    
    private final HttpClient httpClient;

    public GitHubApiClient() {
        this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }

    /**
     * Get repository information
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @return JSON response as string
     */
    public String getRepository(String owner, String repo) {
        String url = String.format("%s/repos/%s/%s", baseUrl, owner, repo);
        logger.info("Fetching repository: {}/{}", owner, repo);
        
        try {
            HttpRequest request = buildRequest(url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            validateResponse(response);
            return response.body();
            
        } catch (Exception e) {
            logger.error("Error fetching repository: {}/{}", owner, repo, e);
            throw new RuntimeException("Failed to fetch repository: " + e.getMessage(), e);
        }
    }

    /**
     * Get repository contents (file or directory)
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param path Path to file or directory
     * @return JSON response as string
     */
    public String getContents(String owner, String repo, String path) {
        String url = String.format("%s/repos/%s/%s/contents/%s", baseUrl, owner, repo, path);
        logger.info("Fetching contents: {}/{}/{}", owner, repo, path);
        
        try {
            HttpRequest request = buildRequest(url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            validateResponse(response);
            return response.body();
            
        } catch (Exception e) {
            logger.error("Error fetching contents: {}/{}/{}", owner, repo, path, e);
            throw new RuntimeException("Failed to fetch contents: " + e.getMessage(), e);
        }
    }

    /**
     * Get repository tree (recursive file structure)
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param sha Branch or commit SHA (default: main)
     * @return JSON response as string
     */
    public String getTree(String owner, String repo, String sha) {
        String url = String.format("%s/repos/%s/%s/git/trees/%s?recursive=1", baseUrl, owner, repo, sha);
        logger.info("Fetching tree: {}/{} at {}", owner, repo, sha);
        
        try {
            HttpRequest request = buildRequest(url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            validateResponse(response);
            return response.body();
            
        } catch (Exception e) {
            logger.error("Error fetching tree: {}/{}", owner, repo, e);
            throw new RuntimeException("Failed to fetch tree: " + e.getMessage(), e);
        }
    }

    /**
     * Get README content
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @return JSON response as string
     */
    public String getReadme(String owner, String repo) {
        String url = String.format("%s/repos/%s/%s/readme", baseUrl, owner, repo);
        logger.info("Fetching README: {}/{}", owner, repo);
        
        try {
            HttpRequest request = buildRequest(url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            validateResponse(response);
            return response.body();
            
        } catch (Exception e) {
            logger.error("Error fetching README: {}/{}", owner, repo, e);
            throw new RuntimeException("Failed to fetch README: " + e.getMessage(), e);
        }
    }

    /**
     * Get file content by path
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param path File path
     * @return File content as string
     */
    public String getFileContent(String owner, String repo, String path) {
        logger.info("Fetching file content: {}/{}/{}", owner, repo, path);
        
        try {
            String contentsJson = getContents(owner, repo, path);
            // Parse JSON and decode base64 content
            // This is simplified - in production, use proper JSON parsing
            return contentsJson;
            
        } catch (Exception e) {
            logger.error("Error fetching file content: {}/{}/{}", owner, repo, path, e);
            throw new RuntimeException("Failed to fetch file content: " + e.getMessage(), e);
        }
    }

    /**
     * Get repository languages
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @return JSON response as string
     */
    public String getLanguages(String owner, String repo) {
        String url = String.format("%s/repos/%s/%s/languages", baseUrl, owner, repo);
        logger.info("Fetching languages: {}/{}", owner, repo);
        
        try {
            HttpRequest request = buildRequest(url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            validateResponse(response);
            return response.body();
            
        } catch (Exception e) {
            logger.error("Error fetching languages: {}/{}", owner, repo, e);
            throw new RuntimeException("Failed to fetch languages: " + e.getMessage(), e);
        }
    }

    /**
     * Build HTTP request with common headers
     */
    private HttpRequest buildRequest(String url) {
        return HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(timeoutSeconds))
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .header("User-Agent", "MCP-GitHub-Onboarding-Server")
            .GET()
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
            case 404 -> "Resource not found";
            case 403 -> "API rate limit exceeded or forbidden";
            case 401 -> "Unauthorized - invalid credentials";
            case 500, 502, 503 -> "GitHub API server error";
            default -> "HTTP error: " + statusCode;
        };
        
        logger.error("GitHub API error: {} - {}", statusCode, errorMessage);
        throw new RuntimeException(errorMessage);
    }
}

// Made with Bob
