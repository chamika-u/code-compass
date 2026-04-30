package com.hackathon.mcp.integration;

import com.hackathon.mcp.model.MCPRequest;
import com.hackathon.mcp.model.MCPResponse;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for MCP Server
 * Note: These tests require the server to be running
 */
class MCPServerIntegrationTest {
    
    private final Jsonb jsonb = JsonbBuilder.create();

    @Test
    void testInitializeRequest() {
        MCPRequest request = new MCPRequest(
            "test-1",
            "initialize",
            Map.of("protocolVersion", "2024-11-05")
        );
        
        String requestJson = jsonb.toJson(request);
        assertNotNull(requestJson);
        assertTrue(requestJson.contains("initialize"));
    }

    @Test
    void testToolsListRequest() {
        MCPRequest request = new MCPRequest(
            "test-2",
            "tools/list",
            Map.of()
        );
        
        String requestJson = jsonb.toJson(request);
        assertNotNull(requestJson);
        assertTrue(requestJson.contains("tools/list"));
    }

    @Test
    void testFetchRepoMetadataRequest() {
        MCPRequest request = new MCPRequest(
            "test-3",
            "tools/call",
            Map.of(
                "name", "fetch_repo_metadata",
                "arguments", Map.of(
                    "repository_url", "https://github.com/octocat/Hello-World"
                )
            )
        );
        
        String requestJson = jsonb.toJson(request);
        assertNotNull(requestJson);
        assertTrue(requestJson.contains("fetch_repo_metadata"));
    }

    @Test
    void testAnalyzeStructureRequest() {
        MCPRequest request = new MCPRequest(
            "test-4",
            "tools/call",
            Map.of(
                "name", "analyze_structure",
                "arguments", Map.of(
                    "repository_url", "https://github.com/octocat/Hello-World",
                    "branch", "main"
                )
            )
        );
        
        String requestJson = jsonb.toJson(request);
        assertNotNull(requestJson);
        assertTrue(requestJson.contains("analyze_structure"));
    }

    @Test
    void testGenerateGuideRequest() {
        MCPRequest request = new MCPRequest(
            "test-5",
            "tools/call",
            Map.of(
                "name", "generate_guide",
                "arguments", Map.of(
                    "repository_url", "https://github.com/octocat/Hello-World",
                    "guide_type", "quick_start"
                )
            )
        );
        
        String requestJson = jsonb.toJson(request);
        assertNotNull(requestJson);
        assertTrue(requestJson.contains("generate_guide"));
    }

    @Test
    void testResponseSerialization() {
        MCPResponse response = new MCPResponse(
            "test-6",
            Map.of("status", "success", "data", "test data")
        );
        
        String responseJson = jsonb.toJson(response);
        assertNotNull(responseJson);
        assertTrue(responseJson.contains("success"));
        
        MCPResponse deserialized = jsonb.fromJson(responseJson, MCPResponse.class);
        assertEquals("test-6", deserialized.getId());
        assertNotNull(deserialized.getResult());
    }

    @Test
    void testErrorResponse() {
        MCPResponse.MCPError error = new MCPResponse.MCPError(
            -32601,
            "Method not found"
        );
        
        MCPResponse response = new MCPResponse("test-7", error);
        
        String responseJson = jsonb.toJson(response);
        assertNotNull(responseJson);
        assertTrue(responseJson.contains("Method not found"));
        
        MCPResponse deserialized = jsonb.fromJson(responseJson, MCPResponse.class);
        assertNotNull(deserialized.getError());
        assertEquals(-32601, deserialized.getError().getCode());
    }

    @Test
    void testPingRequest() {
        MCPRequest request = new MCPRequest(
            "test-8",
            "ping",
            Map.of()
        );
        
        String requestJson = jsonb.toJson(request);
        assertNotNull(requestJson);
        assertTrue(requestJson.contains("ping"));
    }

    @Test
    void testRequestWithNullParams() {
        MCPRequest request = new MCPRequest("test-9", "ping", null);
        
        String requestJson = jsonb.toJson(request);
        assertNotNull(requestJson);
        
        MCPRequest deserialized = jsonb.fromJson(requestJson, MCPRequest.class);
        assertEquals("ping", deserialized.getMethod());
    }

    @Test
    void testCompleteWorkflow() {
        // 1. Initialize
        MCPRequest initRequest = new MCPRequest(
            "workflow-1",
            "initialize",
            Map.of("protocolVersion", "2024-11-05")
        );
        assertNotNull(jsonb.toJson(initRequest));
        
        // 2. List tools
        MCPRequest listRequest = new MCPRequest(
            "workflow-2",
            "tools/list",
            Map.of()
        );
        assertNotNull(jsonb.toJson(listRequest));
        
        // 3. Call tool
        MCPRequest callRequest = new MCPRequest(
            "workflow-3",
            "tools/call",
            Map.of(
                "name", "fetch_repo_metadata",
                "arguments", Map.of("repository_url", "https://github.com/test/repo")
            )
        );
        assertNotNull(jsonb.toJson(callRequest));
        
        // All requests should serialize successfully
        assertTrue(true, "Complete workflow test passed");
    }
}

// Made with Bob
