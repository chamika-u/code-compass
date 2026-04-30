package com.hackathon.mcp.protocol;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Holds MCP server information
 */
@ApplicationScoped
public class MCPServerInfo {
    
    @Inject
    @ConfigProperty(name = "mcp.server.name", defaultValue = "github-onboarding-server")
    private String name;
    
    @Inject
    @ConfigProperty(name = "mcp.server.version", defaultValue = "1.0.0")
    private String version;
    
    @Inject
    @ConfigProperty(name = "mcp.protocol.version", defaultValue = "2024-11-05")
    private String protocolVersion;

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}

// Made with Bob
