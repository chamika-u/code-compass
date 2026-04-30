package com.hackathon.mcp.protocol;

import com.hackathon.mcp.model.MCPRequest;
import com.hackathon.mcp.model.MCPResponse;
import com.hackathon.mcp.model.MCPResponse.MCPError;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles MCP protocol communication via stdio
 */
public class MCPProtocolHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(MCPProtocolHandler.class);
    private final Jsonb jsonb;
    private final MCPRequestRouter requestRouter;
    private final ExecutorService executorService;
    private volatile boolean running = false;

    public MCPProtocolHandler(MCPRequestRouter requestRouter) {
        this.jsonb = JsonbBuilder.create();
        this.requestRouter = requestRouter;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Start listening for MCP requests on stdin
     */
    public void start() {
        if (running) {
            logger.warn("MCP Protocol Handler is already running");
            return;
        }
        
        running = true;
        executorService.submit(this::processStdio);
        logger.info("MCP Protocol Handler started");
    }

    /**
     * Stop the protocol handler
     */
    public void stop() {
        running = false;
        executorService.shutdown();
        logger.info("MCP Protocol Handler stopped");
    }

    /**
     * Process stdio communication
     */
    private void processStdio() {
        try (Scanner scanner = new Scanner(System.in);
             PrintWriter writer = new PrintWriter(System.out, true)) {
            
            logger.info("Listening for MCP requests on stdin...");
            
            while (running && scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                
                if (line.isEmpty()) {
                    continue;
                }
                
                logger.debug("Received request: {}", line);
                
                try {
                    MCPRequest request = jsonb.fromJson(line, MCPRequest.class);
                    MCPResponse response = handleRequest(request);
                    String responseJson = jsonb.toJson(response);
                    
                    writer.println(responseJson);
                    writer.flush();
                    
                    logger.debug("Sent response: {}", responseJson);
                    
                } catch (Exception e) {
                    logger.error("Error processing request: {}", line, e);
                    MCPResponse errorResponse = new MCPResponse(
                        null,
                        new MCPError(-32700, "Parse error: " + e.getMessage())
                    );
                    writer.println(jsonb.toJson(errorResponse));
                    writer.flush();
                }
            }
            
        } catch (Exception e) {
            logger.error("Fatal error in stdio processing", e);
        }
    }

    /**
     * Handle an MCP request and generate a response
     */
    private MCPResponse handleRequest(MCPRequest request) {
        try {
            logger.info("Handling request: method={}, id={}", request.getMethod(), request.getId());
            
            Object result = requestRouter.route(request);
            return new MCPResponse(request.getId(), result);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: {}", e.getMessage());
            return new MCPResponse(
                request.getId(),
                new MCPError(-32601, "Method not found: " + request.getMethod())
            );
            
        } catch (Exception e) {
            logger.error("Error handling request", e);
            return new MCPResponse(
                request.getId(),
                new MCPError(-32603, "Internal error: " + e.getMessage())
            );
        }
    }

    /**
     * Send a notification (no response expected)
     */
    public void sendNotification(String method, Object params) {
        try {
            MCPRequest notification = new MCPRequest(null, method, 
                params instanceof java.util.Map ? (java.util.Map<String, Object>) params : null);
            String json = jsonb.toJson(notification);
            
            System.out.println(json);
            System.out.flush();
            
            logger.debug("Sent notification: {}", json);
            
        } catch (Exception e) {
            logger.error("Error sending notification", e);
        }
    }
}

// Made with Bob
