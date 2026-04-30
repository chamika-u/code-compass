package com.hackathon.mcp.protocol;

import com.hackathon.mcp.model.MCPRequest;
import com.hackathon.mcp.model.ToolDefinition;
import com.hackathon.mcp.tools.MCPTool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Routes MCP requests to appropriate handlers
 */
@ApplicationScoped
public class MCPRequestRouter {
    
    private static final Logger logger = LoggerFactory.getLogger(MCPRequestRouter.class);
    
    @Inject
    private MCPServerInfo serverInfo;
    
    @Inject
    private MCPToolRegistry toolRegistry;

    /**
     * Route an MCP request to the appropriate handler
     */
    public Object route(MCPRequest request) {
        String method = request.getMethod();
        
        logger.debug("Routing request: method={}", method);
        
        return switch (method) {
            case "initialize" -> handleInitialize(request);
            case "tools/list" -> handleToolsList();
            case "tools/call" -> handleToolCall(request);
            case "ping" -> handlePing();
            default -> throw new IllegalArgumentException("Unknown method: " + method);
        };
    }

    /**
     * Handle initialize request
     */
    private Map<String, Object> handleInitialize(MCPRequest request) {
        logger.info("Handling initialize request");
        
        Map<String, Object> result = new HashMap<>();
        result.put("protocolVersion", serverInfo.getProtocolVersion());
        result.put("serverInfo", Map.of(
            "name", serverInfo.getName(),
            "version", serverInfo.getVersion()
        ));
        result.put("capabilities", Map.of(
            "tools", Map.of("listChanged", false)
        ));
        
        return result;
    }

    /**
     * Handle tools/list request
     */
    private Map<String, Object> handleToolsList() {
        logger.info("Handling tools/list request");
        
        List<MCPTool> tools = toolRegistry.getAllTools();
        List<ToolDefinition> toolDefinitions = tools.stream()
            .map(MCPTool::getDefinition)
            .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("tools", toolDefinitions);
        
        return result;
    }

    /**
     * Handle tools/call request
     */
    private Map<String, Object> handleToolCall(MCPRequest request) {
        Map<String, Object> params = request.getParams();
        
        if (params == null || !params.containsKey("name")) {
            throw new IllegalArgumentException("Missing 'name' parameter in tools/call");
        }
        
        String toolName = (String) params.get("name");
        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");
        
        logger.info("Handling tools/call request: tool={}", toolName);
        
        MCPTool tool = toolRegistry.getTool(toolName);
        if (tool == null) {
            throw new IllegalArgumentException("Tool not found: " + toolName);
        }
        
        Object result = tool.execute(arguments != null ? arguments : new HashMap<>());
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", List.of(Map.of(
            "type", "text",
            "text", result
        )));
        
        return response;
    }

    /**
     * Handle ping request
     */
    private Map<String, Object> handlePing() {
        logger.debug("Handling ping request");
        return Map.of("status", "ok");
    }
}

// Made with Bob
