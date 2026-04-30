package com.hackathon.mcp.protocol;

import com.hackathon.mcp.model.MCPRequest;
import com.hackathon.mcp.model.ToolDefinition;
import com.hackathon.mcp.tools.MCPTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MCPRequestRouter
 */
class MCPRequestRouterTest {
    
    private MCPRequestRouter router;
    
    @Mock
    private MCPServerInfo serverInfo;
    
    @Mock
    private MCPToolRegistry toolRegistry;
    
    @Mock
    private MCPTool mockTool;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        router = new MCPRequestRouter();
        
        // Use reflection to inject mocks (in production, use proper DI)
        try {
            var serverInfoField = MCPRequestRouter.class.getDeclaredField("serverInfo");
            serverInfoField.setAccessible(true);
            serverInfoField.set(router, serverInfo);
            
            var toolRegistryField = MCPRequestRouter.class.getDeclaredField("toolRegistry");
            toolRegistryField.setAccessible(true);
            toolRegistryField.set(router, toolRegistry);
        } catch (Exception e) {
            fail("Failed to inject mocks: " + e.getMessage());
        }
    }

    @Test
    void testHandleInitialize() {
        when(serverInfo.getProtocolVersion()).thenReturn("2024-11-05");
        when(serverInfo.getName()).thenReturn("test-server");
        when(serverInfo.getVersion()).thenReturn("1.0.0");
        
        MCPRequest request = new MCPRequest("1", "initialize", Map.of());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) router.route(request);
        
        assertNotNull(result);
        assertEquals("2024-11-05", result.get("protocolVersion"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> serverInfoMap = (Map<String, String>) result.get("serverInfo");
        assertEquals("test-server", serverInfoMap.get("name"));
        assertEquals("1.0.0", serverInfoMap.get("version"));
    }

    @Test
    void testHandleToolsList() {
        ToolDefinition toolDef = new ToolDefinition(
            "test_tool",
            "Test tool description",
            Map.of("type", "object")
        );
        
        when(mockTool.getDefinition()).thenReturn(toolDef);
        when(toolRegistry.getAllTools()).thenReturn(List.of(mockTool));
        
        MCPRequest request = new MCPRequest("2", "tools/list", Map.of());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) router.route(request);
        
        assertNotNull(result);
        assertTrue(result.containsKey("tools"));
        
        @SuppressWarnings("unchecked")
        List<ToolDefinition> tools = (List<ToolDefinition>) result.get("tools");
        assertEquals(1, tools.size());
        assertEquals("test_tool", tools.get(0).getName());
    }

    @Test
    void testHandleToolCall() {
        when(mockTool.execute(any())).thenReturn("Tool execution result");
        when(toolRegistry.getTool("test_tool")).thenReturn(mockTool);
        
        MCPRequest request = new MCPRequest(
            "3",
            "tools/call",
            Map.of(
                "name", "test_tool",
                "arguments", Map.of("arg1", "value1")
            )
        );
        
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) router.route(request);
        
        assertNotNull(result);
        assertTrue(result.containsKey("content"));
        
        verify(mockTool, times(1)).execute(any());
    }

    @Test
    void testHandleToolCallMissingName() {
        MCPRequest request = new MCPRequest(
            "4",
            "tools/call",
            Map.of("arguments", Map.of())
        );
        
        assertThrows(IllegalArgumentException.class, () -> router.route(request));
    }

    @Test
    void testHandleToolCallToolNotFound() {
        when(toolRegistry.getTool("nonexistent_tool")).thenReturn(null);
        
        MCPRequest request = new MCPRequest(
            "5",
            "tools/call",
            Map.of("name", "nonexistent_tool")
        );
        
        assertThrows(IllegalArgumentException.class, () -> router.route(request));
    }

    @Test
    void testHandlePing() {
        MCPRequest request = new MCPRequest("6", "ping", Map.of());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) router.route(request);
        
        assertNotNull(result);
        assertEquals("ok", result.get("status"));
    }

    @Test
    void testHandleUnknownMethod() {
        MCPRequest request = new MCPRequest("7", "unknown_method", Map.of());
        
        assertThrows(IllegalArgumentException.class, () -> router.route(request));
    }
}

// Made with Bob
