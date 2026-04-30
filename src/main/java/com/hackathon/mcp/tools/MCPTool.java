package com.hackathon.mcp.tools;

import com.hackathon.mcp.model.ToolDefinition;
import java.util.Map;

/**
 * Base interface for MCP tools
 */
public interface MCPTool {
    
    /**
     * Get the tool definition (name, description, input schema)
     */
    ToolDefinition getDefinition();
    
    /**
     * Execute the tool with given arguments
     * 
     * @param arguments Tool arguments
     * @return Tool execution result
     */
    Object execute(Map<String, Object> arguments);
}

// Made with Bob
