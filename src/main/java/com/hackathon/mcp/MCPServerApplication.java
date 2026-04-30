package com.hackathon.mcp;

import com.hackathon.mcp.protocol.MCPProtocolHandler;
import com.hackathon.mcp.protocol.MCPRequestRouter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import io.openliberty.microprofile.server.spi.ServerStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main MCP Server Application
 * Initializes and starts the MCP protocol handler
 */
@ApplicationScoped
public class MCPServerApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(MCPServerApplication.class);
    
    @Inject
    private MCPRequestRouter requestRouter;
    
    private MCPProtocolHandler protocolHandler;

    /**
     * Start the MCP server when Liberty server starts
     */
    public void onServerStart(@Observes ServerStartedEvent event) {
        logger.info("=================================================");
        logger.info("Starting MCP GitHub Onboarding Server");
        logger.info("=================================================");
        
        try {
            // Initialize protocol handler
            protocolHandler = new MCPProtocolHandler(requestRouter);
            protocolHandler.start();
            
            logger.info("MCP Server started successfully");
            logger.info("Ready to accept MCP requests via stdio");
            
        } catch (Exception e) {
            logger.error("Failed to start MCP Server", e);
            throw new RuntimeException("MCP Server startup failed", e);
        }
    }

    /**
     * Stop the MCP server on shutdown
     */
    public void shutdown() {
        logger.info("Shutting down MCP Server");
        
        if (protocolHandler != null) {
            protocolHandler.stop();
        }
        
        logger.info("MCP Server stopped");
    }
}

// Made with Bob
