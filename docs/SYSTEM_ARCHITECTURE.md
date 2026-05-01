# System Architecture & Verification Guide

## 🏗️ Architecture Overview

This project implements a **dual-architecture system** that combines:
1. **Express.js Server** (Node.js) - Web UI and API
2. **MCP Server** (Java/Liberty) - Model Context Protocol implementation
3. **WatsonX AI** - IBM's AI for intelligent documentation generation

---

## 📊 System Components

### 1. Express.js Web Server (Node.js)

**Location**: [`server.js`](../server.js)

**Purpose**: 
- Provides web UI at http://localhost:3000
- Handles HTTP API requests
- Integrates with GitHub API
- Calls WatsonX AI for documentation generation

**Key Functions**:
```javascript
// Fetch repository metadata from GitHub
fetchRepoMetadata(repoUrl, customToken)

// Analyze repository structure
analyzeRepoStructure(owner, repo, customToken)

// Generate documentation using WatsonX AI
generateWithWatsonX(metadata, structure)
```

**WatsonX Integration** (Lines 149-216):
```javascript
const response = await axios.post(
    `${WATSONX_URL}/ml/v1/text/generation?version=2023-05-29`,
    {
        input: prompt,
        model_id: 'ibm/granite-13b-chat-v2',
        project_id: WATSONX_PROJECT_ID,
        parameters: {
            max_new_tokens: 4000,
            temperature: 0.7,
            top_p: 0.9,
            top_k: 50
        }
    },
    {
        headers: {
            'Authorization': `Bearer ${WATSONX_API_KEY}`,
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    }
);
```

### 2. MCP Server (Java/Liberty)

**Location**: [`src/main/java/com/hackathon/mcp/`](../src/main/java/com/hackathon/mcp/)

**Purpose**:
- Implements Model Context Protocol (MCP)
- Provides tools for repository analysis
- Integrates with WatsonX AI via Java SDK
- Communicates via stdio (standard input/output)

**Key Components**:

#### MCPServerApplication
**File**: [`MCPServerApplication.java`](../src/main/java/com/hackathon/mcp/MCPServerApplication.java)
- Main entry point for MCP server
- Initializes protocol handler
- Starts stdio communication

#### WatsonXService
**File**: [`WatsonXService.java`](../src/main/java/com/hackathon/mcp/service/WatsonXService.java)
- Java implementation of WatsonX integration
- Uses HTTP client for API calls
- Handles authentication and error handling

**WatsonX Integration** (Lines 66-87):
```java
public String generate(String prompt) {
    String requestBody = buildRequestBody(prompt);
    HttpRequest request = buildRequest(requestBody);
    
    HttpResponse<String> response = httpClient.send(request, 
        HttpResponse.BodyHandlers.ofString());
    
    validateResponse(response);
    return extractGeneratedText(response.body());
}
```

#### MCP Tools
**Location**: [`src/main/java/com/hackathon/mcp/tools/`](../src/main/java/com/hackathon/mcp/tools/)

1. **FetchRepoMetadataTool** - Fetches GitHub repository metadata
2. **AnalyzeStructureTool** - Analyzes repository structure
3. **GenerateGuideTool** - Generates onboarding guides using WatsonX

### 3. WatsonX AI Integration

**Configuration**: [`.env`](../.env)
```
WATSONX_API_KEY=your_api_key_here
WATSONX_PROJECT_ID=your_project_id_here
WATSONX_URL=https://eu-de.ml.cloud.ibm.com
```

**Model Used**: `ibm/granite-13b-chat-v2`

**API Endpoint**: 
```
POST {WATSONX_URL}/ml/v1/text/generation?version=2023-05-29
```

---

## 🔍 Verification Steps

### Step 1: Verify Express.js Server is Running

```bash
# Check if server is running
curl http://localhost:3000/api/health

# Expected output:
{
  "status": "healthy",
  "timestamp": "2026-04-30T23:00:00.000Z",
  "watsonxConfigured": true
}
```

### Step 2: Verify WatsonX Configuration

**Check Environment Variables**:
```bash
# Windows PowerShell
Get-Content .env | Select-String "WATSONX"

# Expected output:
WATSONX_API_KEY=53Z-h-3FdUj_iDALM-6iz-WAb690VFhtcSYxdlHWEbjh
WATSONX_PROJECT_ID=98534793b23e461a88ff1913c846f518
WATSONX_URL=https://eu-de.ml.cloud.ibm.com
```

**Check Server Logs**:
```
🚀 Repository Documentation Generator running on http://localhost:3000
📊 WatsonX configured: true
🔑 GitHub token configured: false
```

### Step 3: Verify WatsonX API Call

**Monitor Terminal Output** when generating documentation:

```
Generating documentation for: https://github.com/user/repo
Fetched metadata for user/repo
Analyzed structure: X files
Generated documentation (Y characters)  ← Success!
```

**OR if WatsonX fails**:
```
Error calling WatsonX: Request failed with status code 401
Falling back to template generation  ← Using fallback
```

### Step 4: Test WatsonX API Key

**Create a test script** (`test-watsonx.js`):
```javascript
const axios = require('axios');
require('dotenv').config();

async function testWatsonX() {
    try {
        const response = await axios.post(
            `${process.env.WATSONX_URL}/ml/v1/text/generation?version=2023-05-29`,
            {
                input: "Hello, this is a test.",
                model_id: 'ibm/granite-13b-chat-v2',
                project_id: process.env.WATSONX_PROJECT_ID,
                parameters: {
                    max_new_tokens: 100,
                    temperature: 0.7
                }
            },
            {
                headers: {
                    'Authorization': `Bearer ${process.env.WATSONX_API_KEY}`,
                    'Content-Type': 'application/json'
                }
            }
        );
        
        console.log('✅ WatsonX API is working!');
        console.log('Response:', response.data);
    } catch (error) {
        console.error('❌ WatsonX API error:', error.response?.status, error.message);
        console.error('Details:', error.response?.data);
    }
}

testWatsonX();
```

**Run test**:
```bash
node test-watsonx.js
```

### Step 5: Verify MCP Server (Java)

**Check if MCP server is built**:
```bash
# Check for compiled classes
dir src\main\java\com\hackathon\mcp\*.class

# Or check Maven build
mvn clean package
```

**Check Liberty server configuration**:
```bash
# View server.xml
type src\main\liberty\config\server.xml
```

**Start MCP Server** (if not running):
```bash
mvn liberty:run
```

**Expected output**:
```
=================================================
Starting MCP GitHub Onboarding Server
=================================================
MCP Server started successfully
Ready to accept MCP requests via stdio
```

---

## 🔧 Current System Status

### What's Working ✅

1. **Express.js Server**: Running on port 3000
2. **GitHub API Integration**: Fetching repository metadata
3. **Repository Analysis**: Analyzing file structure
4. **Template Generation**: Fallback documentation generation
5. **Custom Token Support**: Accepting tokens via UI

### What Needs Attention ⚠️

1. **WatsonX API Key**: Currently returning 401 (Unauthorized)
   - The API key in `.env` is expired or invalid
   - Need to regenerate from IBM Cloud

2. **MCP Server**: Java server exists but may not be actively used by Express.js
   - Express.js is currently standalone
   - MCP server provides alternative implementation

---

## 🔄 How the System Actually Works

### Current Flow (Express.js + WatsonX)

```
User Request (Web UI)
    ↓
Express.js Server (server.js)
    ↓
GitHub API (Fetch Metadata)
    ↓
Repository Analysis (Analyze Structure)
    ↓
WatsonX AI (Generate Documentation)
    ↓ (if WatsonX fails)
Template Generation (Fallback)
    ↓
Return Markdown Documentation
```

### Alternative Flow (MCP Server)

```
MCP Client Request
    ↓
MCP Protocol Handler (Java)
    ↓
MCP Tool Router
    ↓
Tool Execution (FetchRepoMetadataTool, etc.)
    ↓
WatsonX Service (Java)
    ↓
Return MCP Response
```

---

## 🛠️ Fixing WatsonX Integration

### Issue: 401 Unauthorized Error

**Cause**: WatsonX API key is expired or invalid

**Solution**:

1. **Get New API Key from IBM Cloud**:
   - Go to: https://cloud.ibm.com/
   - Navigate to: Resource list → AI / Machine Learning → WatsonX
   - Generate new API key

2. **Update `.env` file**:
   ```
   WATSONX_API_KEY=your_new_api_key_here
   WATSONX_PROJECT_ID=your_project_id_here
   WATSONX_URL=https://eu-de.ml.cloud.ibm.com
   ```

3. **Restart Server**:
   ```bash
   # Stop current server (Ctrl+C)
   npm start
   ```

4. **Verify**:
   - Generate documentation for any repo
   - Check terminal output for "Generated documentation" (not "Falling back")

---

## 📈 Performance Metrics

### With WatsonX AI ✅
- **Quality**: High-quality, context-aware documentation
- **Customization**: Tailored to repository specifics
- **Intelligence**: Understands code patterns and best practices
- **Time**: ~15-30 seconds per repository

### With Template Fallback ⚠️
- **Quality**: Good, but generic
- **Customization**: Limited to repository metadata
- **Intelligence**: Rule-based, not AI-powered
- **Time**: ~5-10 seconds per repository

---

## 🎯 Verification Checklist

Use this checklist to verify the system is working correctly:

- [ ] Express.js server running on port 3000
- [ ] Can access web UI at http://localhost:3000
- [ ] Health endpoint returns `watsonxConfigured: true`
- [ ] Can fetch public repository metadata
- [ ] Can use custom GitHub token for private repos
- [ ] WatsonX API key is valid (no 401 errors)
- [ ] Documentation generation completes successfully
- [ ] Generated documentation is AI-powered (not template)
- [ ] Can download markdown files
- [ ] MCP server compiles successfully (optional)

---

## 📚 Additional Resources

- **WatsonX Documentation**: https://www.ibm.com/docs/en/watsonx-as-a-service
- **MCP Protocol**: https://modelcontextprotocol.io/
- **GitHub API**: https://docs.github.com/en/rest
- **Express.js**: https://expressjs.com/

---

## 🔐 Security Notes

1. **API Keys**: Never commit API keys to version control
2. **Tokens**: User tokens are never stored on server
3. **HTTPS**: Use HTTPS in production
4. **Rate Limiting**: Implement rate limiting for production use

---

**Last Updated**: 2026-04-30
**System Version**: 1.0.0