package com.repoonboarder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.openliberty.mcp.server.Tool;
import java.io.IOException;

/**
 * MCP Tool for generating repository onboarding guides
 */
@ApplicationScoped
public class RepoTools {

    @Inject
    private GitHubService gitHubService;

    @Inject
    private WatsonXService watsonXService;

    /**
     * Fetches repository context from GitHub and uses IBM WatsonX AI to generate a structured developer onboarding guide.
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @return JSON string containing the onboarding guide
     * @throws IOException If GitHub or WatsonX API requests fail
     * @throws InterruptedException If requests are interrupted
     */
    @Tool(description = "Fetches repository context from GitHub and uses IBM WatsonX AI to generate a structured developer onboarding guide.")
    public String generateOnboardingGuide(String owner, String repo) throws IOException, InterruptedException {
        // Fetch repository summary
        String repoSummary = gitHubService.getRepoSummary(owner, repo);
        
        // Fetch file tree
        String fileTree = gitHubService.getFileTree(owner, repo);
        
        // Fetch README.md
        String readme = "";
        try {
            readme = gitHubService.readFile(owner, repo, "README.md");
        } catch (IOException e) {
            // README.md might not exist, continue without it
            readme = "No README.md found";
        }
        
        // Fetch package.json
        String packageJson = "";
        try {
            packageJson = gitHubService.readFile(owner, repo, "package.json");
        } catch (IOException e) {
            // package.json might not exist, continue without it
            packageJson = "No package.json found";
        }
        
        // Concatenate all context data
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("=== Repository Summary ===\n");
        contextBuilder.append(repoSummary);
        contextBuilder.append("\n\n=== File Tree ===\n");
        contextBuilder.append(fileTree);
        contextBuilder.append("\n\n=== README.md ===\n");
        contextBuilder.append(readme);
        contextBuilder.append("\n\n=== package.json ===\n");
        contextBuilder.append(packageJson);
        
        String context = contextBuilder.toString();
        
        // Generate guide using WatsonX
        String guide = watsonXService.generateGuide(context);
        
        return guide;
    }
}

// Made with Bob