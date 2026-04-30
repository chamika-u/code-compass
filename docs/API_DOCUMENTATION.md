# MCP GitHub Onboarding Server - API Documentation

## Overview

The MCP GitHub Onboarding Server is an agentic Model Context Protocol (MCP) server that generates comprehensive onboarding guides for GitHub repositories using IBM WatsonX Granite AI.

## Architecture

```
┌─────────────┐
│ MCP Client  │
└──────┬──────┘
       │ stdio (JSON-RPC)
┌──────▼──────────────────────┐
│  MCP Protocol Handler       │
│  (Open Liberty Server)      │
├─────────────────────────────┤
│  Tool Registry              │
│  ├─ fetch_repo_metadata     │
│  ├─ analyze_structure       │
│  └─ generate_guide          │
├─────────────────────────────┤
│  Services                   │
│  ├─ GitHub API Client       │
│  ├─ WatsonX Service         │
│  └─ Dependency Parser       │
└─────────────────────────────┘
```

## MCP Protocol Methods

### 1. Initialize

Initialize the MCP server connection.

**Request:**
```json
{
  "jsonrpc": "2.0",
  "id": "1",
  "method": "initialize",
  "params": {
    "protocolVersion": "2024-11-05"
  }
}
```

**Response:**
```json
{
  "jsonrpc": "2.0",
  "id": "1",
  "result": {
    "protocolVersion": "2024-11-05",
    "serverInfo": {
      "name": "github-onboarding-server",
      "version": "1.0.0"
    },
    "capabilities": {
      "tools": {
        "listChanged": false
      }
    }
  }
}
```

### 2. List Tools

Get available tools.

**Request:**
```json
{
  "jsonrpc": "2.0",
  "id": "2",
  "method": "tools/list",
  "params": {}
}
```

**Response:**
```json
{
  "jsonrpc": "2.0",
  "id": "2",
  "result": {
    "tools": [
      {
        "name": "fetch_repo_metadata",
        "description": "Fetch metadata for a GitHub repository",
        "inputSchema": {
          "type": "object",
          "properties": {
            "repository_url": {
              "type": "string",
              "description": "GitHub repository URL"
            }
          },
          "required": ["repository_url"]
        }
      },
      {
        "name": "analyze_structure",
        "description": "Analyze repository structure",
        "inputSchema": { ... }
      },
      {
        "name": "generate_guide",
        "description": "Generate onboarding guide",
        "inputSchema": { ... }
      }
    ]
  }
}
```

### 3. Call Tool

Execute a tool.

**Request:**
```json
{
  "jsonrpc": "2.0",
  "id": "3",
  "method": "tools/call",
  "params": {
    "name": "fetch_repo_metadata",
    "arguments": {
      "repository_url": "https://github.com/owner/repo"
    }
  }
}
```

**Response:**
```json
{
  "jsonrpc": "2.0",
  "id": "3",
  "result": {
    "content": [
      {
        "type": "text",
        "text": "{\"name\":\"repo\",\"stars\":100,...}"
      }
    ]
  }
}
```

## Available Tools

### Tool 1: fetch_repo_metadata

Fetches comprehensive metadata about a GitHub repository.

**Parameters:**
- `repository_url` (string, required): GitHub repository URL
  - Format: `https://github.com/owner/repo` or `owner/repo`

**Returns:**
```json
{
  "name": "repository-name",
  "full_name": "owner/repository-name",
  "description": "Repository description",
  "url": "https://github.com/owner/repo",
  "language": "JavaScript",
  "stars": 1234,
  "forks": 56,
  "open_issues": 12,
  "default_branch": "main",
  "topics": ["topic1", "topic2"],
  "created_at": "2023-01-01T00:00:00Z",
  "updated_at": "2024-04-30T00:00:00Z",
  "languages": "{\"JavaScript\":75,\"TypeScript\":25}"
}
```

**Example:**
```json
{
  "name": "fetch_repo_metadata",
  "arguments": {
    "repository_url": "https://github.com/facebook/react"
  }
}
```

### Tool 2: analyze_structure

Analyzes repository structure including file tree, README, and dependencies.

**Parameters:**
- `repository_url` (string, required): GitHub repository URL
- `branch` (string, optional): Branch name (default: "main")

**Returns:**
```json
{
  "file_tree": {
    "total_files": 150,
    "total_directories": 25,
    "file_types": {
      "js": 45,
      "json": 10,
      "md": 5
    },
    "key_directories": ["src", "test", "docs"],
    "sample_files": ["src/index.js", "package.json", ...]
  },
  "readme": {
    "content": "# Project Title\n\n...",
    "summary": "README content retrieved successfully"
  },
  "dependencies": {
    "package_manager": "npm",
    "runtime_dependencies": {
      "express": "^4.18.0",
      "lodash": "^4.17.21"
    },
    "development_dependencies": {
      "jest": "^29.0.0"
    },
    "total_dependencies": 3
  },
  "analysis_timestamp": "Wed Apr 30 22:00:00 IST 2026"
}
```

**Example:**
```json
{
  "name": "analyze_structure",
  "arguments": {
    "repository_url": "https://github.com/expressjs/express",
    "branch": "master"
  }
}
```

### Tool 3: generate_guide

Generates a comprehensive onboarding guide using WatsonX AI.

**Parameters:**
- `repository_url` (string, required): GitHub repository URL
- `guide_type` (string, optional): Type of guide to generate
  - Options: `"comprehensive"`, `"quick_start"`, `"architecture"`, `"setup"`
  - Default: `"comprehensive"`

**Returns:**
```json
{
  "guide_type": "comprehensive",
  "repository": "https://github.com/owner/repo",
  "generated_guide": {
    "project_overview": {
      "name": "Project Name",
      "description": "...",
      "purpose": "...",
      "target_audience": "..."
    },
    "technology_stack": {
      "primary_language": "JavaScript",
      "frameworks": ["Express", "React"],
      "tools": ["npm", "webpack"],
      "package_manager": "npm"
    },
    "architecture": {
      "pattern": "MVC",
      "key_components": ["Controllers", "Models", "Views"],
      "data_flow": "..."
    },
    "setup_instructions": {
      "prerequisites": ["Node.js 18+", "npm 9+"],
      "installation_steps": ["Clone repo", "npm install", "npm start"],
      "configuration": ["Set environment variables"],
      "verification": "Run npm test"
    },
    "key_files": [
      {
        "path": "src/index.js",
        "purpose": "Application entry point",
        "importance": "high"
      }
    ],
    "development_workflow": {
      "getting_started": ["..."],
      "common_commands": {
        "build": "npm run build",
        "test": "npm test",
        "run": "npm start",
        "deploy": "npm run deploy"
      },
      "best_practices": ["..."]
    },
    "contribution_guidelines": {
      "how_to_contribute": ["..."],
      "code_style": "ESLint + Prettier",
      "testing_requirements": "Jest unit tests",
      "pull_request_process": ["..."]
    },
    "troubleshooting": [
      {
        "issue": "Port already in use",
        "solution": "Change PORT in .env"
      }
    ],
    "additional_resources": {
      "documentation": ["..."],
      "tutorials": ["..."],
      "community": ["..."]
    }
  },
  "metadata_summary": {
    "name": "repo-name",
    "language": "JavaScript",
    "stars": 1234,
    "description": "..."
  },
  "generation_timestamp": "Wed Apr 30 22:00:00 IST 2026",
  "status": "success"
}
```

**Example:**
```json
{
  "name": "generate_guide",
  "arguments": {
    "repository_url": "https://github.com/vercel/next.js",
    "guide_type": "quick_start"
  }
}
```

## Error Handling

### Error Response Format

```json
{
  "jsonrpc": "2.0",
  "id": "request-id",
  "error": {
    "code": -32601,
    "message": "Method not found",
    "data": {
      "details": "Additional error information"
    }
  }
}
```

### Error Codes

| Code | Message | Description |
|------|---------|-------------|
| -32700 | Parse error | Invalid JSON |
| -32600 | Invalid Request | Invalid request object |
| -32601 | Method not found | Method does not exist |
| -32602 | Invalid params | Invalid method parameters |
| -32603 | Internal error | Server internal error |

## Rate Limits

- **GitHub API**: 60 requests/hour (unauthenticated), 5000/hour (authenticated)
- **WatsonX API**: Based on your IBM Cloud plan

## Best Practices

1. **Repository URLs**: Use full GitHub URLs or `owner/repo` format
2. **Branch Names**: Specify branch if not using default (main/master)
3. **Guide Types**: Choose appropriate guide type for your use case
4. **Error Handling**: Always check response status and handle errors
5. **Caching**: Consider caching results for frequently accessed repositories

## Examples

### Complete Workflow

```javascript
// 1. Initialize
{
  "jsonrpc": "2.0",
  "id": "1",
  "method": "initialize",
  "params": {"protocolVersion": "2024-11-05"}
}

// 2. List available tools
{
  "jsonrpc": "2.0",
  "id": "2",
  "method": "tools/list",
  "params": {}
}

// 3. Fetch repository metadata
{
  "jsonrpc": "2.0",
  "id": "3",
  "method": "tools/call",
  "params": {
    "name": "fetch_repo_metadata",
    "arguments": {
      "repository_url": "https://github.com/microsoft/vscode"
    }
  }
}

// 4. Analyze structure
{
  "jsonrpc": "2.0",
  "id": "4",
  "method": "tools/call",
  "params": {
    "name": "analyze_structure",
    "arguments": {
      "repository_url": "https://github.com/microsoft/vscode"
    }
  }
}

// 5. Generate comprehensive guide
{
  "jsonrpc": "2.0",
  "id": "5",
  "method": "tools/call",
  "params": {
    "name": "generate_guide",
    "arguments": {
      "repository_url": "https://github.com/microsoft/vscode",
      "guide_type": "comprehensive"
    }
  }
}
```

## Support

For issues and questions:
- GitHub Issues: [Project Repository]
- Documentation: [docs/]
- Email: support@example.com