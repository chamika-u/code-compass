package com.hackathon.mcp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DependencyParser
 */
class DependencyParserTest {
    
    private DependencyParser parser;

    @BeforeEach
    void setUp() {
        parser = new DependencyParser();
    }

    @Test
    void testIsDependencyFile() {
        assertTrue(parser.isDependencyFile("package.json"));
        assertTrue(parser.isDependencyFile("pom.xml"));
        assertTrue(parser.isDependencyFile("requirements.txt"));
        assertTrue(parser.isDependencyFile("build.gradle"));
        assertFalse(parser.isDependencyFile("README.md"));
        assertFalse(parser.isDependencyFile("index.js"));
    }

    @Test
    void testParsePackageJson() {
        String packageJson = """
            {
              "name": "test-project",
              "version": "1.0.0",
              "dependencies": {
                "express": "^4.18.0",
                "lodash": "^4.17.21"
              },
              "devDependencies": {
                "jest": "^29.0.0"
              }
            }
            """;
        
        Map<String, Object> result = parser.parse("package.json", packageJson);
        
        assertEquals("npm", result.get("package_manager"));
        assertEquals("test-project", result.get("name"));
        assertEquals("1.0.0", result.get("version"));
        assertEquals(3, result.get("total_dependencies"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> deps = (Map<String, String>) result.get("runtime_dependencies");
        assertTrue(deps.containsKey("express"));
        assertTrue(deps.containsKey("lodash"));
    }

    @Test
    void testParseRequirementsTxt() {
        String requirements = """
            flask==2.3.0
            requests>=2.28.0
            pytest
            # This is a comment
            django==4.2.0
            """;
        
        Map<String, Object> result = parser.parse("requirements.txt", requirements);
        
        assertEquals("pip", result.get("package_manager"));
        assertEquals(4, result.get("total_dependencies"));
    }

    @Test
    void testParsePomXml() {
        String pom = """
            <project>
              <dependencies>
                <dependency>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-starter-web</artifactId>
                  <version>3.0.0</version>
                </dependency>
                <dependency>
                  <groupId>junit</groupId>
                  <artifactId>junit</artifactId>
                  <version>4.13.2</version>
                </dependency>
              </dependencies>
            </project>
            """;
        
        Map<String, Object> result = parser.parse("pom.xml", pom);
        
        assertEquals("maven", result.get("package_manager"));
        assertEquals(2, result.get("total_dependencies"));
    }

    @Test
    void testParseInvalidJson() {
        String invalidJson = "{ invalid json }";
        
        Map<String, Object> result = parser.parse("package.json", invalidJson);
        
        assertTrue(result.containsKey("error"));
    }

    @Test
    void testParseUnsupportedFile() {
        Map<String, Object> result = parser.parse("unknown.txt", "content");
        
        assertTrue(result.containsKey("message"));
        assertEquals("Unsupported dependency file type", result.get("message"));
    }
}

// Made with Bob
