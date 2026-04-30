
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
