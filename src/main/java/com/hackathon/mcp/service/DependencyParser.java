package com.hackathon.mcp.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for dependency files from various package managers
 */
@ApplicationScoped
public class DependencyParser {
    
    private static final Logger logger = LoggerFactory.getLogger(DependencyParser.class);
    private final Jsonb jsonb = JsonbBuilder.create();

    /**
     * Check if a file is a dependency file
     */
    public boolean isDependencyFile(String path) {
        String fileName = path.toLowerCase();
        return fileName.equals("package.json") ||
               fileName.equals("pom.xml") ||
               fileName.equals("build.gradle") ||
               fileName.equals("requirements.txt") ||
               fileName.equals("pipfile") ||
               fileName.equals("cargo.toml") ||
               fileName.equals("go.mod") ||
               fileName.equals("composer.json") ||
               fileName.equals("gemfile");
    }

    /**
     * Parse dependency file based on type
     */
    public Map<String, Object> parse(String path, String content) {
        String fileName = path.toLowerCase();
        
        logger.debug("Parsing dependency file: {}", path);
        
        try {
            if (fileName.endsWith("package.json")) {
                return parsePackageJson(content);
            } else if (fileName.endsWith("pom.xml")) {
                return parsePomXml(content);
            } else if (fileName.endsWith("requirements.txt")) {
                return parseRequirementsTxt(content);
            } else if (fileName.endsWith("build.gradle")) {
                return parseBuildGradle(content);
            } else if (fileName.endsWith("cargo.toml")) {
                return parseCargoToml(content);
            } else if (fileName.endsWith("go.mod")) {
                return parseGoMod(content);
            } else if (fileName.endsWith("composer.json")) {
                return parseComposerJson(content);
            } else if (fileName.endsWith("gemfile")) {
                return parseGemfile(content);
            }
            
            return Map.of("message", "Unsupported dependency file type");
            
        } catch (Exception e) {
            logger.error("Error parsing dependency file: {}", path, e);
            return Map.of("error", "Failed to parse: " + e.getMessage());
        }
    }

    /**
     * Parse package.json (Node.js/npm)
     */
    private Map<String, Object> parsePackageJson(String content) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> packageJson = jsonb.fromJson(content, Map.class);
            
            Map<String, Object> result = new HashMap<>();
            result.put("package_manager", "npm");
            result.put("name", packageJson.get("name"));
            result.put("version", packageJson.get("version"));
            
            @SuppressWarnings("unchecked")
            Map<String, String> dependencies = (Map<String, String>) packageJson.get("dependencies");
            @SuppressWarnings("unchecked")
            Map<String, String> devDependencies = (Map<String, String>) packageJson.get("devDependencies");
            
            result.put("runtime_dependencies", dependencies != null ? dependencies : Map.of());
            result.put("development_dependencies", devDependencies != null ? devDependencies : Map.of());
            result.put("total_dependencies", 
                (dependencies != null ? dependencies.size() : 0) + 
                (devDependencies != null ? devDependencies.size() : 0));
            
            return result;
            
        } catch (Exception e) {
            logger.error("Error parsing package.json", e);
            return Map.of("error", "Invalid package.json format");
        }
    }

    /**
     * Parse pom.xml (Maven/Java)
     */
    private Map<String, Object> parsePomXml(String content) {
        Map<String, Object> result = new HashMap<>();
        result.put("package_manager", "maven");
        
        List<String> dependencies = new ArrayList<>();
        
        // Simple regex-based parsing (in production, use proper XML parser)
        Pattern depPattern = Pattern.compile("<dependency>.*?<groupId>(.*?)</groupId>.*?<artifactId>(.*?)</artifactId>.*?<version>(.*?)</version>.*?</dependency>", Pattern.DOTALL);
        Matcher matcher = depPattern.matcher(content);
        
        while (matcher.find()) {
            String groupId = matcher.group(1);
            String artifactId = matcher.group(2);
            String version = matcher.group(3);
            dependencies.add(String.format("%s:%s:%s", groupId, artifactId, version));
        }
        
        result.put("dependencies", dependencies);
        result.put("total_dependencies", dependencies.size());
        
        return result;
    }

    /**
     * Parse requirements.txt (Python/pip)
     */
    private Map<String, Object> parseRequirementsTxt(String content) {
        Map<String, Object> result = new HashMap<>();
        result.put("package_manager", "pip");
        
        List<String> dependencies = new ArrayList<>();
        String[] lines = content.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#")) {
                dependencies.add(line);
            }
        }
        
        result.put("dependencies", dependencies);
        result.put("total_dependencies", dependencies.size());
        
        return result;
    }

    /**
     * Parse build.gradle (Gradle/Java)
     */
    private Map<String, Object> parseBuildGradle(String content) {
        Map<String, Object> result = new HashMap<>();
        result.put("package_manager", "gradle");
        
        List<String> dependencies = new ArrayList<>();
        
        // Simple regex-based parsing
        Pattern depPattern = Pattern.compile("(implementation|api|compile|testImplementation)\\s+['\"]([^'\"]+)['\"]");
        Matcher matcher = depPattern.matcher(content);
        
        while (matcher.find()) {
            String type = matcher.group(1);
            String dep = matcher.group(2);
            dependencies.add(String.format("%s: %s", type, dep));
        }
        
        result.put("dependencies", dependencies);
        result.put("total_dependencies", dependencies.size());
        
        return result;
    }

    /**
     * Parse Cargo.toml (Rust)
     */
    private Map<String, Object> parseCargoToml(String content) {
        Map<String, Object> result = new HashMap<>();
        result.put("package_manager", "cargo");
        
        List<String> dependencies = new ArrayList<>();
        
        // Simple parsing for [dependencies] section
        boolean inDepsSection = false;
        String[] lines = content.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.equals("[dependencies]")) {
                inDepsSection = true;
                continue;
            }
            if (line.startsWith("[") && !line.equals("[dependencies]")) {
                inDepsSection = false;
            }
            if (inDepsSection && !line.isEmpty() && !line.startsWith("#")) {
                dependencies.add(line);
            }
        }
        
        result.put("dependencies", dependencies);
        result.put("total_dependencies", dependencies.size());
        
        return result;
    }

    /**
     * Parse go.mod (Go)
     */
    private Map<String, Object> parseGoMod(String content) {
        Map<String, Object> result = new HashMap<>();
        result.put("package_manager", "go");
        
        List<String> dependencies = new ArrayList<>();
        
        Pattern depPattern = Pattern.compile("require\\s+([^\\s]+)\\s+([^\\s]+)");
        Matcher matcher = depPattern.matcher(content);
        
        while (matcher.find()) {
            String module = matcher.group(1);
            String version = matcher.group(2);
            dependencies.add(String.format("%s %s", module, version));
        }
        
        result.put("dependencies", dependencies);
        result.put("total_dependencies", dependencies.size());
        
        return result;
    }

    /**
     * Parse composer.json (PHP)
     */
    private Map<String, Object> parseComposerJson(String content) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> composerJson = jsonb.fromJson(content, Map.class);
            
            Map<String, Object> result = new HashMap<>();
            result.put("package_manager", "composer");
            result.put("name", composerJson.get("name"));
            
            @SuppressWarnings("unchecked")
            Map<String, String> require = (Map<String, String>) composerJson.get("require");
            @SuppressWarnings("unchecked")
            Map<String, String> requireDev = (Map<String, String>) composerJson.get("require-dev");
            
            result.put("runtime_dependencies", require != null ? require : Map.of());
            result.put("development_dependencies", requireDev != null ? requireDev : Map.of());
            result.put("total_dependencies", 
                (require != null ? require.size() : 0) + 
                (requireDev != null ? requireDev.size() : 0));
            
            return result;
            
        } catch (Exception e) {
            logger.error("Error parsing composer.json", e);
            return Map.of("error", "Invalid composer.json format");
        }
    }

    /**
     * Parse Gemfile (Ruby)
     */
    private Map<String, Object> parseGemfile(String content) {
        Map<String, Object> result = new HashMap<>();
        result.put("package_manager", "bundler");
        
        List<String> dependencies = new ArrayList<>();
        
        Pattern gemPattern = Pattern.compile("gem\\s+['\"]([^'\"]+)['\"](?:,\\s*['\"]([^'\"]+)['\"])?");
        Matcher matcher = gemPattern.matcher(content);
        
        while (matcher.find()) {
            String gem = matcher.group(1);
            String version = matcher.group(2);
            dependencies.add(version != null ? String.format("%s %s", gem, version) : gem);
        }
        
        result.put("dependencies", dependencies);
        result.put("total_dependencies", dependencies.size());
        
        return result;
    }
}

// Made with Bob
