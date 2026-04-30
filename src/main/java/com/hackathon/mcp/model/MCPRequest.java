package com.hackathon.mcp.model;

import jakarta.json.bind.annotation.JsonbProperty;
import java.util.Map;

/**
 * Represents an MCP JSON-RPC request
 */
public class MCPRequest {
    
    @JsonbProperty("jsonrpc")
    private String jsonrpc = "2.0";
    
    private String id;
    private String method;
    private Map<String, Object> params;

    public MCPRequest() {
    }

    public MCPRequest(String id, String method, Map<String, Object> params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "MCPRequest{" +
                "jsonrpc='" + jsonrpc + '\'' +
                ", id='" + id + '\'' +
                ", method='" + method + '\'' +
                ", params=" + params +
                '}';
    }
}

// Made with Bob
