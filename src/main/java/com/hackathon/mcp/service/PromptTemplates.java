package com.hackathon.mcp.service;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Prompt templates for WatsonX guide generation
 */
@ApplicationScoped
public class PromptTemplates {

    /**
     * Generate onboarding guide prompt
     */
    public String generateOnboardingGuidePrompt(
            String repoMetadata,
            String structure,
            String readmeContent,
            String dependencies) {
        
        return String.format("""
            You are an expert software developer creating a comprehensive onboarding guide for a GitHub repository.
            
            Based on the following repository information, generate a detailed, structured onboarding guide in JSON format.
            
            REPOSITORY METADATA:
            %s
            
            REPOSITORY STRUCTURE:
            %s
            
            README CONTENT:
            %s
            
            DEPENDENCIES:
            %s
            
            Generate a comprehensive onboarding guide that includes:
            1. Project overview and purpose
            2. Technology stack analysis
            3. Architecture overview
            4. Setup instructions (prerequisites, installation steps)
            5. Key files and directories explanation
            6. Development workflow
            7. Common tasks and commands
            8. Contribution guidelines
            9. Troubleshooting tips
            
            Respond with ONLY valid JSON in this exact structure (no markdown, no code blocks):
            {
              "project_overview": {
                "name": "string",
                "description": "string",
                "purpose": "string",
                "target_audience": "string"
              },
              "technology_stack": {
                "primary_language": "string",
                "frameworks": ["string"],
                "tools": ["string"],
                "package_manager": "string"
              },
              "architecture": {
                "pattern": "string",
                "key_components": ["string"],
                "data_flow": "string"
              },
              "setup_instructions": {
                "prerequisites": ["string"],
                "installation_steps": ["string"],
                "configuration": ["string"],
                "verification": "string"
              },
              "key_files": [
                {
                  "path": "string",
                  "purpose": "string",
                  "importance": "high|medium|low"
                }
              ],
              "development_workflow": {
                "getting_started": ["string"],
                "common_commands": {
                  "build": "string",
                  "test": "string",
                  "run": "string",
                  "deploy": "string"
                },
                "best_practices": ["string"]
              },
              "contribution_guidelines": {
                "how_to_contribute": ["string"],
                "code_style": "string",
                "testing_requirements": "string",
                "pull_request_process": ["string"]
              },
              "troubleshooting": [
                {
                  "issue": "string",
                  "solution": "string"
                }
              ],
              "additional_resources": {
                "documentation": ["string"],
                "tutorials": ["string"],
                "community": ["string"]
              }
            }
            """,
            repoMetadata,
            structure,
            truncate(readmeContent, 2000),
            dependencies
        );
    }

    /**
     * Generate quick start guide prompt
     */
    public String generateQuickStartPrompt(String repoMetadata, String readmeContent) {
        return String.format("""
            Create a concise quick-start guide for this repository.
            
            REPOSITORY: %s
            README: %s
            
            Provide a brief JSON response with:
            - 3-5 key setup steps
            - Essential commands to get started
            - First task a new developer should try
            
            JSON format:
            {
              "quick_start": {
                "setup_steps": ["string"],
                "essential_commands": ["string"],
                "first_task": "string"
              }
            }
            """,
            repoMetadata,
            truncate(readmeContent, 1000)
        );
    }

    /**
     * Generate architecture analysis prompt
     */
    public String generateArchitecturePrompt(String structure, String dependencies) {
        return String.format("""
            Analyze the architecture of this codebase based on its structure and dependencies.
            
            STRUCTURE: %s
            DEPENDENCIES: %s
            
            Provide architectural insights in JSON:
            {
              "architecture_analysis": {
                "pattern": "string (e.g., MVC, microservices, monolithic)",
                "layers": ["string"],
                "design_principles": ["string"],
                "scalability_notes": "string"
              }
            }
            """,
            structure,
            dependencies
        );
    }

    /**
     * Generate setup instructions prompt
     */
    public String generateSetupInstructionsPrompt(
            String repoMetadata,
            String dependencies,
            String readmeContent) {
        
        return String.format("""
            Generate detailed setup instructions for this repository.
            
            REPOSITORY: %s
            DEPENDENCIES: %s
            README: %s
            
            Create step-by-step setup instructions in JSON:
            {
              "setup": {
                "prerequisites": {
                  "system_requirements": ["string"],
                  "software": ["string"],
                  "accounts": ["string"]
                },
                "installation": {
                  "steps": ["string"],
                  "commands": ["string"]
                },
                "configuration": {
                  "environment_variables": ["string"],
                  "config_files": ["string"]
                },
                "verification": {
                  "test_commands": ["string"],
                  "expected_output": "string"
                }
              }
            }
            """,
            repoMetadata,
            dependencies,
            truncate(readmeContent, 1500)
        );
    }

    /**
     * Truncate text to maximum length
     */
    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "... (truncated)";
    }

    /**
     * Escape special characters for JSON
     */
    private String escapeForJson(String text) {
        if (text == null) {
            return "";
        }
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}

// Made with Bob
