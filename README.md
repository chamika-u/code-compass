# MCP GitHub Onboarding Server

An agentic Model Context Protocol (MCP) server that generates comprehensive onboarding guides for GitHub repositories using IBM WatsonX Granite AI.

![Java](https://img.shields.io/badge/Java-17-orange)
![Open Liberty](https://img.shields.io/badge/Open%20Liberty-23.0-blue)
![MCP](https://img.shields.io/badge/MCP-2024--11--05-green)
![WatsonX](https://img.shields.io/badge/IBM%20WatsonX-Granite-purple)

## 🎯 Overview

This hackathon project provides an intelligent MCP server that:
- Fetches GitHub repository metadata via REST API
- Analyzes repository structure and dependencies
- Generates AI-powered onboarding guides using IBM WatsonX Granite
- Supports multiple guide types (comprehensive, quick-start, architecture, setup)

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    MCP Client                           │
│              (Claude Desktop, etc.)                     │
└────────────────────┬────────────────────────────────────┘
                     │ stdio (JSON-RPC 2.0)
┌────────────────────▼────────────────────────────────────┐
│              Open Liberty Server                        │
│  ┌─────────────────────────────────────────────────┐   │
│  │         MCP Protocol Handler                    │   │
│  │  - Request routing                              │   │
│  │  - Tool registry                                │   │
│  │  - Response formatting                          │   │
│  └─────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────┐   │
│  │              MCP Tools                          │   │
│  │  ├─ fetch_repo_metadata                        │   │
│  │  ├─ analyze_structure                          │   │
│  │  └─ generate_guide                             │   │
│  └─────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────┐   │
│  │            Service Layer                        │   │
│  │  ├─ GitHubApiClient (Java HttpClient)         │   │
│  │  ├─ WatsonXService (IBM SDK)                  │   │
│  │  ├─ DependencyParser                          │   │
│  │  └─ PromptTemplates                           │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                     │                    │
         ┌───────────▼──────────┐  ┌─────▼──────────┐
         │   GitHub REST API    │  │  IBM WatsonX   │
         │                      │  │    Granite     │
         └──────────────────────┘  └────────────────┘
```

## ✨ Features

### 🔧 Three Powerful Tools

1. **fetch_repo_metadata** - Retrieve comprehensive repository information
   - Name, description, stars, language
   - Topics, creation/update dates
   - Language breakdown

2. **analyze_structure** - Deep repository analysis
   - File tree with statistics
   - README content extraction
   - Dependency parsing (npm, Maven, pip, etc.)
   - Key directory identification

3. **generate_guide** - AI-powered guide generation
   - Comprehensive onboarding guides
   - Quick-start guides
   - Architecture analysis
   - Setup instructions

### 🎨 Supported Package Managers

- **npm** (package.json)
- **Maven** (pom.xml)
- **Gradle** (build.gradle)
- **pip** (requirements.txt)
- **Cargo** (Cargo.toml)
- **Go** (go.mod)
- **Composer** (composer.json)
- **Bundler** (Gemfile)

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- IBM WatsonX API credentials
- Git

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/mcp-github-onboarding.git
cd mcp-github-onboarding
```

2. **Configure environment variables**

Create a `.env` file or set environment variables:

```bash
export WATSONX_API_URL=https://us-south.ml.cloud.ibm.com
export WATSONX_API_KEY=your_api_key_here
export WATSONX_PROJECT_ID=your_project_id_here
```

3. **Build the project**
```bash
mvn clean install
```

4. **Run the server**
```bash
mvn liberty:run
```

The server will start and listen for MCP requests on stdin/stdout.

## 📖 Usage

### Configuration

Edit `src/main/resources/application.properties`:

```properties
# GitHub API Configuration
github.api.base.url=https://api.github.com
github.api.timeout.seconds=30

# WatsonX Configuration
watsonx.api.url=${WATSONX_API_URL}
watsonx.api.key=${WATSONX_API_KEY}
watsonx.project.id=${WATSONX_PROJECT_ID}
watsonx.model.id=ibm/granite-13b-chat-v2
watsonx.max.tokens=2000
watsonx.temperature=0.7

# MCP Server Configuration
mcp.server.name=github-onboarding-server
mcp.server.version=1.0.0
mcp.protocol.version=2024-11-05
```

### MCP Client Configuration

Add to your MCP client configuration (e.g., Claude Desktop):

```json
{
  "mcpServers": {
    "github-onboarding": {
      "command": "java",
      "args": [
        "-jar",
        "target/mcp-github-onboarding.war"
      ],
      "env": {
        "WATSONX_API_URL": "https://us-south.ml.cloud.ibm.com",
        "WATSONX_API_KEY": "your_api_key",
        "WATSONX_PROJECT_ID": "your_project_id"
      }
    }
  }
}
```

### Example Requests

#### 1. Fetch Repository Metadata

```json
{
  "jsonrpc": "2.0",
  "id": "1",
  "method": "tools/call",
  "params": {
    "name": "fetch_repo_metadata",
    "arguments": {
      "repository_url": "https://github.com/facebook/react"
    }
  }
}
```

#### 2. Analyze Repository Structure

```json
{
  "jsonrpc": "2.0",
  "id": "2",
  "method": "tools/call",
  "params": {
    "name": "analyze_structure",
    "arguments": {
      "repository_url": "https://github.com/expressjs/express",
      "branch": "master"
    }
  }
}
```

#### 3. Generate Onboarding Guide

```json
{
  "jsonrpc": "2.0",
  "id": "3",
  "method": "tools/call",
  "params": {
    "name": "generate_guide",
    "arguments": {
      "repository_url": "https://github.com/vercel/next.js",
      "guide_type": "comprehensive"
    }
  }
}
```

## 🧪 Testing

### Run Unit Tests

```bash
mvn test
```

### Run Integration Tests

```bash
mvn verify
```

### Test Coverage

```bash
mvn jacoco:report
```

View coverage report at `target/site/jacoco/index.html`

## 📁 Project Structure

```
mcp-github-onboarding/
├── src/
│   ├── main/
│   │   ├── java/com/hackathon/mcp/
│   │   │   ├── MCPServerApplication.java
│   │   │   ├── model/
│   │   │   │   ├── MCPRequest.java
│   │   │   │   ├── MCPResponse.java
│   │   │   │   ├── ToolDefinition.java
│   │   │   │   └── RepositoryMetadata.java
│   │   │   ├── protocol/
│   │   │   │   ├── MCPProtocolHandler.java
│   │   │   │   ├── MCPRequestRouter.java
│   │   │   │   ├── MCPServerInfo.java
│   │   │   │   └── MCPToolRegistry.java
│   │   │   ├── service/
│   │   │   │   ├── GitHubApiClient.java
│   │   │   │   ├── WatsonXService.java
│   │   │   │   ├── DependencyParser.java
│   │   │   │   └── PromptTemplates.java
│   │   │   └── tools/
│   │   │       ├── MCPTool.java
│   │   │       ├── FetchRepoMetadataTool.java
│   │   │       ├── AnalyzeStructureTool.java
│   │   │       └── GenerateGuideTool.java
│   │   ├── liberty/config/
│   │   │   └── server.xml
│   │   └── resources/
│   │       ├── application.properties
│   │       └── logback.xml
│   └── test/
│       └── java/com/hackathon/mcp/
│           ├── service/
│           │   └── DependencyParserTest.java
│           ├── protocol/
│           │   └── MCPRequestRouterTest.java
│           └── integration/
│               └── MCPServerIntegrationTest.java
├── docs/
│   └── API_DOCUMENTATION.md
├── pom.xml
└── README.md
```

## 🔍 API Documentation

See [docs/API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md) for detailed API documentation.

## 🛠️ Development

### Build Commands

```bash
# Clean build
mvn clean install

# Run in dev mode
mvn liberty:dev

# Package WAR
mvn package

# Run tests
mvn test

# Generate documentation
mvn javadoc:javadoc
```

### Logging

Logs are written to:
- Console: `INFO` level
- File: `logs/mcp-server.log` (all levels)
- Error file: `logs/mcp-server-error.log` (errors only)

Configure logging in `src/main/resources/logback.xml`

## 🐛 Troubleshooting

### Common Issues

**Issue: WatsonX API authentication fails**
```
Solution: Verify WATSONX_API_KEY and WATSONX_PROJECT_ID are set correctly
```

**Issue: GitHub API rate limit exceeded**
```
Solution: Add GitHub personal access token for higher rate limits
```

**Issue: Port 9080 already in use**
```
Solution: Change port in server.xml or stop conflicting service
```

**Issue: JSON parsing errors**
```
Solution: Ensure repository URL format is correct (owner/repo or full URL)
```

## 📊 Performance

- **GitHub API**: ~500ms per request
- **WatsonX Generation**: 5-15 seconds (depends on guide complexity)
- **Memory**: ~512MB heap recommended
- **Concurrent Requests**: Supports multiple simultaneous tool calls

## 🔐 Security

- API keys stored in environment variables
- No credentials in source code
- HTTPS for all external API calls
- Input validation on all tool parameters

## 🤝 Contributing

This is a hackathon project. Contributions welcome!

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

MIT License - see LICENSE file for details

## 🙏 Acknowledgments

- **Open Liberty** - Java application server
- **IBM WatsonX** - AI/ML platform
- **GitHub REST API** - Repository data
- **Model Context Protocol** - MCP specification

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/mcp-github-onboarding/issues)
- **Documentation**: [docs/](docs/)
- **Email**: your.email@example.com

## 🎉 Hackathon Info

**Event**: [Hackathon Name]  
**Date**: April 2026  
**Team**: [Your Team Name]  
**Category**: AI/Developer Tools

---

Built with ❤️ using Open Liberty, Java, and IBM WatsonX Granite