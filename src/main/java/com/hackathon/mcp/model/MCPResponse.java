package com.hackathon.mcp.model;

import jakarta.json.bind.annotation.JsonbProperty;

/**
 * Represents an MCP JSON-RPC response
 */
public class MCPResponse {
    
    @JsonbProperty("jsonrpc")
    private String jsonrpc = "2.0";
    
    private String id;
    private Object result;
    private MCPError error;

    public MCPResponse() {
    }

    public MCPResponse(String id, Object result) {
        this.id = id;
        this.result = result;
    }

    public MCPResponse(String id, MCPError error) {
        this.id = id;
        this.error = error;
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

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public MCPError getError() {
        return error;
    }

    public void setError(MCPError error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "MCPResponse{" +
                "jsonrpc='" + jsonrpc + '\'' +
                ", id='" + id + '\'' +
                ", result=" + result +
                ", error=" + error +
                '}';
    }

    /**
     * Represents an MCP error object
     */
    public static class MCPError {
        private int code;
        private String message;
        private Object data;

        public MCPError() {
        }

        public MCPError(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public MCPError(int code, String message, Object data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "MCPError{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
}

// Made with Bob
