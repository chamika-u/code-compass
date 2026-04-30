package com.hackathon.mcp.tools;

import com.hackathon.mcp.model.ToolDefinition;
import com.hackathon.mcp.service.GitHubApiClient;
import com.hackathon.mcp.service.WatsonXService;
import com.hackathon.mcp.service.PromptTemplates;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * MCP Tool to generate comprehensive onboarding guide using WatsonX
 */
@ApplicationScoped
public class GenerateGuideTool implements MCPTool {
    
    private static final Logger logger = LoggerFactory.getLogger(GenerateGuideTool.class);
    
    @Inject
    private GitHubApiClient githubClient;
    
    @Inject
    private WatsonXService watsonxService;
    
    @Inject
    private PromptTemplates promptTemplates;
    
    @Inject
    private FetchRepoMetadataTool metadataTool;
    
    @Inject
    private AnalyzeStructureTool structureTool;
    
    private final Jsonb jsonb = JsonbBuilder.create();

    @Override
    public ToolDefinition getDefinition() {
        return new ToolDefinition(
            "generate_guide",
            "Generate a comprehensive onboarding guide for a GitHub repository using AI. Combines repository metadata, structure analysis, and WatsonX to create detailed documentation.",
            Map.of(
                "type", "object",
                "properties", Map.of(
                    "repository_url", Map.of(
                        "type", "string",
                        "description", "GitHub repository URL (e.g., https://github.com/owner/repo)"
                    ),
                    "guide_type", Map.of(
                        "type", "string",
                        "description", "Type of guide to generate: 'comprehensive', 'quick_start', 'architecture', or 'setup'",
                        "enum", new String[]{"comprehensive", "quick_start", "architecture", "setup"}
                    )
                ),
                "required", new String[]{"repository_url"}
            )
        );
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        logger.info("Executing generate_guide tool");
        
        // Validate arguments
        if (!arguments.containsKey("repository_url")) {
            throw new IllegalArgumentException("Missing required argument: repository_url");
        }
        
        String repoUrl = (String) arguments.get("repository_url");
        String guideType = (String) arguments.getOrDefault("guide_type", "comprehensive");
        
        logger.info("Generating {} guide for repository: {}", guideType, repoUrl);
        
        try {
            // Step 1: Fetch repository metadata
            logger.info("Step 1/4: Fetching repository metadata...");
            String metadataJson = (String) metadataTool.execute(Map.of("repository_url", repoUrl));
            
            // Step 2: Analyze repository structure
            logger.info("Step 2/4: Analyzing repository structure...");
            String structureJson = (String) structureTool.execute(Map.of("repository_url", repoUrl));
            
            // Step 3: Parse the collected data
            logger.info("Step 3/4: Processing collected data...");
            @SuppressWarnings("unchecked")
            Map<String, Object> metadata = jsonb.fromJson(metadataJson, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> structure = jsonb.fromJson(structureJson, Map.class);
            
            // Extract key information
            @SuppressWarnings("unchecked")
            Map<String, Object> readme = (Map<String, Object>) structure.get("readme");
            String readmeContent = readme != null ? (String) readme.get("content") : "No README available";
            
            String dependenciesJson = jsonb.toJson(structure.get("dependencies"));
            
            // Step 4: Generate guide using WatsonX
            logger.info("Step 4/4: Generating guide with WatsonX AI...");
            String prompt = buildPrompt(guideType, metadataJson, structureJson, readmeContent, dependenciesJson);
            String generatedGuide = watsonxService.generate(prompt);
            
            // Build final response
            Map<String, Object> result = Map.of(
                "guide_type", guideType,
                "repository", repoUrl,
                "generated_guide", cleanJsonResponse(generatedGuide),
                "metadata_summary", buildMetadataSummary(metadata),
                "generation_timestamp", new java.util.Date().toString(),
                "status", "success"
            );
            
            String resultJson = jsonb.toJson(result);
            logger.info("Successfully generated {} guide for {}", guideType, repoUrl);
            
            return resultJson;
            
        } catch (Exception e) {
            logger.error("Error generating guide", e);
            
            // Return error response
            Map<String, Object> errorResult = Map.of(
                "status", "error",
                "error_message", e.getMessage(),
                "repository", repoUrl,
                "guide_type", guideType
            );
            
            return jsonb.toJson(errorResult);
        }
    }

    /**
     * Build prompt based on guide type
     */
    private String buildPrompt(String guideType, String metadata, String structure, 
                               String readme, String dependencies) {
        
        return switch (guideType.toLowerCase()) {
            case "quick_start" -> promptTemplates.generateQuickStartPrompt(metadata, readme);
            case "architecture" -> promptTemplates.generateArchitecturePrompt(structure, dependencies);
            case "setup" -> promptTemplates.generateSetupInstructionsPrompt(metadata, dependencies, readme);
            default -> promptTemplates.generateOnboardingGuidePrompt(metadata, structure, readme, dependencies);
        };
    }

    /**
     * Build metadata summary for response
     */
    private Map<String, Object> buildMetadataSummary(Map<String, Object> metadata) {
        return Map.of(
            "name", metadata.getOrDefault("name", "Unknown"),
            "language", metadata.getOrDefault("language", "Unknown"),
            "stars", metadata.getOrDefault("stars", 0),
            "description", metadata.getOrDefault("description", "No description")
        );
    }

    /**
     * Clean JSON response from WatsonX
     * Removes markdown code blocks and extra formatting
     */
    private String cleanJsonResponse(String response) {
        if (response == null) {
            return "{}";
        }
        
        // Remove markdown code blocks
        String cleaned = response
            .replaceAll("```json\\s*", "")
            .replaceAll("```\\s*", "")
            .trim();
        
        // Find JSON object boundaries
        int jsonStart = cleaned.indexOf('{');
        int jsonEnd = cleaned.lastIndexOf('}');
        
        if (jsonStart >= 0 && jsonEnd > jsonStart) {
            cleaned = cleaned.substring(jsonStart, jsonEnd + 1);
        }
        
        // Validate it's valid JSON
        try {
            jsonb.fromJson(cleaned, Map.class);
            return cleaned;
        } catch (Exception e) {
            logger.warn("Generated content is not valid JSON, returning as text");
            return jsonb.toJson(Map.of("content", response));
        }
    }

    /**
     * Parse repository URL to extract owner and repo name
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
