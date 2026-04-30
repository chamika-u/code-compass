package com.hackathon.mcp.tools;

import com.hackathon.mcp.model.ToolDefinition;
import com.hackathon.mcp.service.GitHubApiClient;
import com.hackathon.mcp.service.DependencyParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * MCP Tool to analyze GitHub repository structure
 */
@ApplicationScoped
public class AnalyzeStructureTool implements MCPTool {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyzeStructureTool.class);
    
    @Inject
    private GitHubApiClient githubClient;
    
    @Inject
    private DependencyParser dependencyParser;
    
    private final Jsonb jsonb = JsonbBuilder.create();

    @Override
    public ToolDefinition getDefinition() {
        return new ToolDefinition(
            "analyze_structure",
            "Analyze GitHub repository structure including file tree, README content, and dependencies from package files",
            Map.of(
                "type", "object",
                "properties", Map.of(
                    "repository_url", Map.of(
                        "type", "string",
                        "description", "GitHub repository URL (e.g., https://github.com/owner/repo)"
                    ),
                    "branch", Map.of(
                        "type", "string",
                        "description", "Branch name (optional, defaults to main/master)"
                    )
                ),
                "required", new String[]{"repository_url"}
            )
        );
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        logger.info("Executing analyze_structure tool");
        
        // Validate arguments
        if (!arguments.containsKey("repository_url")) {
            throw new IllegalArgumentException("Missing required argument: repository_url");
        }
        
        String repoUrl = (String) arguments.get("repository_url");
        String branch = (String) arguments.getOrDefault("branch", "main");
        logger.debug("Repository URL: {}, Branch: {}", repoUrl, branch);
        
        // Parse owner and repo from URL
        String[] parts = parseRepositoryUrl(repoUrl);
        String owner = parts[0];
        String repo = parts[1];
        
        try {
            // Fetch repository tree
            String treeJson = fetchTree(owner, repo, branch);
            Map<String, Object> tree = jsonb.fromJson(treeJson, Map.class);
            
            // Fetch README
            String readmeContent = fetchReadme(owner, repo);
            
            // Parse dependencies
            Map<String, Object> dependencies = parseDependencies(owner, repo, tree);
            
            // Build file structure summary
            Map<String, Object> fileStructure = buildFileStructure(tree);
            
            // Build response
            Map<String, Object> result = Map.of(
                "file_tree", fileStructure,
                "readme", Map.of(
                    "content", readmeContent,
                    "summary", "README content retrieved successfully"
                ),
                "dependencies", dependencies,
                "analysis_timestamp", new Date().toString()
            );
            
            String resultJson = jsonb.toJson(result);
            logger.info("Successfully analyzed structure for {}/{}", owner, repo);
            
            return resultJson;
            
        } catch (Exception e) {
            logger.error("Error analyzing repository structure", e);
            throw new RuntimeException("Failed to analyze repository structure: " + e.getMessage(), e);
        }
    }

    /**
     * Fetch repository tree with fallback for branch name
     */
    private String fetchTree(String owner, String repo, String branch) {
        try {
            return githubClient.getTree(owner, repo, branch);
        } catch (Exception e) {
            logger.warn("Failed to fetch tree for branch '{}', trying 'master'", branch);
            try {
                return githubClient.getTree(owner, repo, "master");
            } catch (Exception e2) {
                logger.error("Failed to fetch tree for both 'main' and 'master' branches");
                throw new RuntimeException("Could not fetch repository tree", e2);
            }
        }
    }

    /**
     * Fetch README content
     */
    private String fetchReadme(String owner, String repo) {
        try {
            String readmeJson = githubClient.getReadme(owner, repo);
            Map<String, Object> readme = jsonb.fromJson(readmeJson, Map.class);
            
            // Decode base64 content
            String content = (String) readme.get("content");
            if (content != null) {
                return new String(Base64.getDecoder().decode(content.replaceAll("\\s", "")));
            }
            return "README not available";
            
        } catch (Exception e) {
            logger.warn("Could not fetch README: {}", e.getMessage());
            return "README not available";
        }
    }

    /**
     * Parse dependencies from package files
     */
    private Map<String, Object> parseDependencies(String owner, String repo, Map<String, Object> tree) {
        Map<String, Object> allDependencies = new HashMap<>();
        
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> treeItems = (List<Map<String, Object>>) tree.get("tree");
            
            if (treeItems != null) {
                for (Map<String, Object> item : treeItems) {
                    String path = (String) item.get("path");
                    String type = (String) item.get("type");
                    
                    if ("blob".equals(type) && dependencyParser.isDependencyFile(path)) {
                        try {
                            String fileContent = fetchFileContent(owner, repo, path);
                            Map<String, Object> deps = dependencyParser.parse(path, fileContent);
                            allDependencies.putAll(deps);
                        } catch (Exception e) {
                            logger.warn("Could not parse dependency file: {}", path, e);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error parsing dependencies", e);
        }
        
        return allDependencies.isEmpty() ? Map.of("message", "No dependency files found") : allDependencies;
    }

    /**
     * Fetch and decode file content
     */
    private String fetchFileContent(String owner, String repo, String path) {
        try {
            String contentJson = githubClient.getContents(owner, repo, path);
            Map<String, Object> content = jsonb.fromJson(contentJson, Map.class);
            
            String encodedContent = (String) content.get("content");
            if (encodedContent != null) {
                return new String(Base64.getDecoder().decode(encodedContent.replaceAll("\\s", "")));
            }
            return "";
            
        } catch (Exception e) {
            logger.error("Error fetching file content: {}", path, e);
            throw new RuntimeException("Failed to fetch file content", e);
        }
    }

    /**
     * Build file structure summary
     */
    private Map<String, Object> buildFileStructure(Map<String, Object> tree) {
        Map<String, Object> structure = new HashMap<>();
        
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> treeItems = (List<Map<String, Object>>) tree.get("tree");
            
            if (treeItems != null) {
                List<String> directories = new ArrayList<>();
                List<String> files = new ArrayList<>();
                Map<String, Integer> fileTypes = new HashMap<>();
                
                for (Map<String, Object> item : treeItems) {
                    String path = (String) item.get("path");
                    String type = (String) item.get("type");
                    
                    if ("tree".equals(type)) {
                        directories.add(path);
                    } else if ("blob".equals(type)) {
                        files.add(path);
                        
                        // Count file types
                        String extension = getFileExtension(path);
                        fileTypes.put(extension, fileTypes.getOrDefault(extension, 0) + 1);
                    }
                }
                
                structure.put("total_files", files.size());
                structure.put("total_directories", directories.size());
                structure.put("file_types", fileTypes);
                structure.put("key_directories", getKeyDirectories(directories));
                structure.put("sample_files", files.subList(0, Math.min(20, files.size())));
            }
            
        } catch (Exception e) {
            logger.error("Error building file structure", e);
        }
        
        return structure;
    }

    /**
     * Get file extension
     */
    private String getFileExtension(String path) {
        int lastDot = path.lastIndexOf('.');
        if (lastDot > 0 && lastDot < path.length() - 1) {
            return path.substring(lastDot + 1);
        }
        return "no-extension";
    }

    /**
     * Identify key directories
     */
    private List<String> getKeyDirectories(List<String> directories) {
        List<String> keyDirs = new ArrayList<>();
        String[] importantDirs = {"src", "lib", "test", "tests", "docs", "config", "public", "dist", "build"};
        
        for (String dir : directories) {
            for (String important : importantDirs) {
                if (dir.startsWith(important + "/") || dir.equals(important)) {
                    keyDirs.add(dir);
                    break;
                }
            }
        }
        
        return keyDirs;
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
