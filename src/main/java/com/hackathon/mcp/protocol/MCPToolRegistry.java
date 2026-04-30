package com.hackathon.mcp.protocol;

import com.hackathon.mcp.tools.MCPTool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for MCP tools
 */
@ApplicationScoped
public class MCPToolRegistry {
    
    private static final Logger logger = LoggerFactory.getLogger(MCPToolRegistry.class);
    
    @Inject
    private Instance<MCPTool> tools;
    
    private final Map<String, MCPTool> toolMap = new HashMap<>();

    @PostConstruct
    public void initialize() {
        logger.info("Initializing MCP Tool Registry");
        
        for (MCPTool tool : tools) {
            String toolName = tool.getDefinition().getName();
            toolMap.put(toolName, tool);
            logger.info("Registered tool: {}", toolName);
        }
        
        logger.info("Tool registry initialized with {} tools", toolMap.size());
    }

    /**
     * Get a tool by name
     */
    public MCPTool getTool(String name) {
        return toolMap.get(name);
    }

    /**
     * Get all registered tools
     */
    public List<MCPTool> getAllTools() {
        return new ArrayList<>(toolMap.values());
    }

    /**
     * Check if a tool exists
     */
    public boolean hasTool(String name) {
        return toolMap.containsKey(name);
    }
}

// Made with Bob
