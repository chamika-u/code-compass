package com.hackathon.mcp.tools;

import com.hackathon.mcp.model.RepositoryMetadata;
import com.hackathon.mcp.model.ToolDefinition;
import com.hackathon.mcp.service.GitHubApiClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * MCP Tool to fetch GitHub repository metadata
 */
@ApplicationScoped
public class FetchRepoMetadataTool implements MCPTool {
    
    private static final Logger logger = LoggerFactory.getLogger(FetchRepoMetadataTool.class);
    
    @Inject
    private GitHubApiClient githubClient;
    
    private final Jsonb jsonb = JsonbBuilder.create();

    @Override
    public ToolDefinition getDefinition() {
        return new ToolDefinition(
            "fetch_repo_metadata",
            "Fetch metadata for a GitHub repository including name, description, stars, language, and topics",
            Map.of(
                "type", "object",
                "properties", Map.of(
                    "repository_url", Map.of(
                        "type", "string",
                        "description", "GitHub repository URL (e.g., https://github.com/owner/repo)"
                    )
                ),
                "required", new String[]{"repository_url"}
            )
        );
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        logger.info("Executing fetch_repo_metadata tool");
        
        // Validate arguments
        if (!arguments.containsKey("repository_url")) {
            throw new IllegalArgumentException("Missing required argument: repository_url");
        }
        
        String repoUrl = (String) arguments.get("repository_url");
        logger.debug("Repository URL: {}", repoUrl);
        
        // Parse owner and repo from URL
        String[] parts = parseRepositoryUrl(repoUrl);
        String owner = parts[0];
        String repo = parts[1];
        
        try {
            // Fetch repository data from GitHub
            String repoJson = githubClient.getRepository(owner, repo);
            RepositoryMetadata metadata = jsonb.fromJson(repoJson, RepositoryMetadata.class);
            
            // Fetch languages
            String languagesJson = githubClient.getLanguages(owner, repo);
            
            // Build response using HashMap (Map.of() has a 10 entry limit)
            Map<String, Object> result = new HashMap<>();
            result.put("name", metadata.getName());
            result.put("full_name", metadata.getFullName());
            result.put("description", metadata.getDescription() != null ? metadata.getDescription() : "No description available");
            result.put("url", metadata.getHtmlUrl());
            result.put("language", metadata.getLanguage() != null ? metadata.getLanguage() : "Not specified");
            result.put("stars", metadata.getStars());
            result.put("forks", metadata.getForks());
            result.put("open_issues", metadata.getOpenIssues());
            result.put("default_branch", metadata.getDefaultBranch());
            result.put("topics", metadata.getTopics() != null ? metadata.getTopics() : new String[0]);
            result.put("created_at", metadata.getCreatedAt());
            result.put("updated_at", metadata.getUpdatedAt());
            result.put("languages", languagesJson);
            
            String resultJson = jsonb.toJson(result);
            logger.info("Successfully fetched metadata for {}/{}", owner, repo);
            
            return resultJson;
            
        } catch (Exception e) {
            logger.error("Error fetching repository metadata", e);
            throw new RuntimeException("Failed to fetch repository metadata: " + e.getMessage(), e);
        }
    }

    /**
     * Parse repository URL to extract owner and repo name
     * Supports formats:
     * - https://github.com/owner/repo
     * - github.com/owner/repo
     * - owner/repo
     */
    private String[] parseRepositoryUrl(String url) {
        String cleaned = url.trim()
            .replace("https://", "")
            .replace("http://", "")
            .replace("github.com/", "")
            .replaceAll("\\.git$", "");
        
        String[] parts = cleaned.split("/");
        
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid repository URL format. Expected: owner/repo");
        }
        
        return new String[]{parts[0], parts[1]};
    }
}

// Made with Bob
