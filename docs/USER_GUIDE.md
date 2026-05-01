# Repository Documentation Generator - User Guide

## 🚀 Quick Start

The Repository Documentation Generator creates comprehensive developer onboarding guides for any GitHub repository using AI-powered analysis.

---

## 📖 Table of Contents

1. [Basic Usage](#basic-usage)
2. [Using GitHub Tokens](#using-github-tokens)
3. [Private Repositories](#private-repositories)
4. [Features](#features)
5. [Troubleshooting](#troubleshooting)
6. [FAQ](#faq)

---

## Basic Usage

### For Public Repositories

1. **Open the Application**
   - Navigate to http://localhost:3000 in your browser

2. **Enter Repository URL**
   - Paste any public GitHub repository URL
   - Example: `https://github.com/facebook/react`

3. **Generate Documentation**
   - Click "Generate Documentation" button
   - Wait for AI analysis (typically 10-30 seconds)

4. **Download Results**
   - Review the generated documentation
   - Click "Download Markdown" to save the guide

### Example Public Repositories to Try

- **Express.js**: `https://github.com/expressjs/express`
- **React**: `https://github.com/facebook/react`
- **Node.js**: `https://github.com/nodejs/node`
- **Vue.js**: `https://github.com/vuejs/vue`

---

## Using GitHub Tokens

### Why Use a Token?

**Without Token (Anonymous):**
- ❌ Cannot access private repositories
- ❌ Limited to 60 requests per hour
- ❌ May encounter rate limiting

**With Token:**
- ✅ Access to private repositories
- ✅ 5,000 requests per hour
- ✅ No rate limiting issues
- ✅ Access to all your repositories

### How to Use a Token in the UI

#### Step 1: Get Your GitHub Token

1. Go to GitHub Settings: https://github.com/settings/tokens
2. Click **"Generate new token"** → **"Generate new token (classic)"**
3. Give it a descriptive name (e.g., "Documentation Generator")
4. Set expiration (recommended: 90 days)
5. Select permissions:
   - ✅ **repo** - Full control of private repositories
6. Click **"Generate token"**
7. **Copy the token immediately** (starts with `ghp_`)

#### Step 2: Use Token in the Application

1. Open http://localhost:3000
2. Enter your repository URL in the first field
3. **Paste your GitHub token** in the second field (labeled "GitHub Token")
4. Click "Generate Documentation"

#### Step 3: Security Features

- **Show/Hide Button**: Click the eye icon to toggle token visibility
- **Privacy**: Your token is only used for this request
- **Not Stored**: Tokens are never saved on the server
- **Secure**: Transmitted over HTTPS only

---

## Private Repositories

### Accessing Your Private Repos

To generate documentation for private repositories:

1. **Generate a GitHub Token** (see above)
   - Must have `repo` scope selected
   - This grants access to your private repositories

2. **Enter Private Repo URL**
   ```
   https://github.com/your-username/private-repo
   ```

3. **Provide Your Token**
   - Paste token in the "GitHub Token" field
   - Token must belong to a user with access to the repo

4. **Generate Documentation**
   - Click "Generate Documentation"
   - The system will use your token to access the private repo

### Organization Repositories

For organization private repositories:

- Your token must have access to the organization
- You must be a member of the organization
- The `repo` scope includes organization repositories

---

## Features

### What Gets Generated?

The AI-powered documentation includes:

1. **📋 Project Overview**
   - Repository description
   - Key statistics (stars, forks, language)
   - License information

2. **🚀 Getting Started**
   - Prerequisites
   - Installation instructions
   - Running the application

3. **🏗️ Project Structure**
   - Directory organization
   - File distribution
   - Key components

4. **💻 Technology Stack**
   - Primary languages
   - Frameworks and libraries
   - Package managers

5. **🔑 Key Features**
   - Main functionality
   - Notable capabilities

6. **🧪 Testing Strategy**
   - Test commands
   - Testing frameworks

7. **🤝 Contributing Guidelines**
   - How to contribute
   - Development workflow
   - Code standards

8. **📚 Resources**
   - Official links
   - Documentation
   - Community resources

### AI-Powered Analysis

- **WatsonX AI Integration**: Uses IBM's WatsonX AI for intelligent analysis
- **Context-Aware**: Understands project structure and patterns
- **Language-Specific**: Tailored to the repository's primary language
- **Comprehensive**: Covers all aspects of developer onboarding

---

## Troubleshooting

### Common Issues

#### 1. "Failed to generate documentation" Error

**Possible Causes:**
- Invalid repository URL
- Repository doesn't exist
- Private repository without token
- Invalid or expired token

**Solutions:**
- Verify the repository URL is correct
- Check if repository is private (requires token)
- Generate a new token if expired
- Ensure token has `repo` scope

#### 2. Rate Limit Exceeded

**Error Message:** "API rate limit exceeded"

**Solutions:**
- Wait for rate limit to reset (1 hour for anonymous)
- Use a GitHub token (increases limit to 5,000/hour)
- Check remaining rate limit at: https://api.github.com/rate_limit

#### 3. Token Not Working (401 Error)

**Possible Causes:**
- Token expired
- Token revoked
- Insufficient permissions
- Token not properly copied

**Solutions:**
1. Verify token hasn't expired: https://github.com/settings/tokens
2. Check token has `repo` scope
3. Regenerate token if needed
4. Ensure no extra spaces when pasting

#### 4. Private Repository Access Denied

**Error Message:** "404 Not Found" or "Failed to fetch repository metadata"

**Solutions:**
- Verify you have access to the repository
- Ensure token belongs to a user with repository access
- Check token has `repo` scope (not just `public_repo`)
- For organization repos, verify organization membership

---

## FAQ

### General Questions

**Q: Is my GitHub token stored on the server?**
A: No, tokens are only used for the current request and are never stored.

**Q: Can I use this for private repositories?**
A: Yes, provide a GitHub token with `repo` scope.

**Q: How long does generation take?**
A: Typically 10-30 seconds depending on repository size.

**Q: What format is the output?**
A: Markdown (.md) format, ready for GitHub or documentation sites.

**Q: Can I edit the generated documentation?**
A: Yes, download the markdown file and edit it as needed.

### Token Questions

**Q: How do I create a GitHub token?**
A: See the [GitHub Token Setup Guide](GITHUB_TOKEN_SETUP.md) for detailed instructions.

**Q: What permissions does my token need?**
A: For private repos: `repo` scope. For public repos only: `public_repo` is sufficient.

**Q: How long should I set token expiration?**
A: 90 days is recommended. You can always regenerate when it expires.

**Q: Can I use the same token for multiple requests?**
A: Yes, you can reuse the same token for all your requests.

**Q: What if my token is compromised?**
A: Immediately revoke it at https://github.com/settings/tokens and generate a new one.

### Technical Questions

**Q: Does this work with GitLab or Bitbucket?**
A: Currently only GitHub repositories are supported.

**Q: Can I generate documentation for multiple repos at once?**
A: No, generate one repository at a time.

**Q: What languages are supported?**
A: All programming languages on GitHub are supported.

**Q: Does it analyze the actual code?**
A: Yes, it analyzes repository structure, files, and README content.

---

## Best Practices

### For Best Results

1. **Use Tokens for Private Repos**
   - Always provide a token for private repositories
   - Prevents authentication errors

2. **Keep Tokens Secure**
   - Never share tokens publicly
   - Revoke tokens you no longer need
   - Use reasonable expiration dates

3. **Review Generated Documentation**
   - AI-generated content may need minor adjustments
   - Add project-specific details as needed
   - Update with team-specific guidelines

4. **Regular Updates**
   - Regenerate documentation when project changes significantly
   - Keep onboarding guides current

---

## Security & Privacy

### Your Data

- **Repository URLs**: Only used to fetch public metadata
- **GitHub Tokens**: Used only for the current request, never stored
- **Generated Content**: Not stored on our servers
- **Privacy**: All processing happens in real-time

### Token Security

- Tokens are transmitted securely
- Never logged or stored
- Used only for GitHub API calls
- Discarded after request completes

---

## Support

### Need Help?

1. **Check Troubleshooting Section**: Most common issues are covered above
2. **Review Token Setup Guide**: See [GITHUB_TOKEN_SETUP.md](GITHUB_TOKEN_SETUP.md)
3. **Check Server Logs**: Look for specific error messages in the terminal
4. **GitHub API Status**: Check https://www.githubstatus.com/

### Useful Links

- **GitHub Token Settings**: https://github.com/settings/tokens
- **GitHub API Documentation**: https://docs.github.com/en/rest
- **Rate Limit Info**: https://docs.github.com/en/rest/overview/resources-in-the-rest-api#rate-limiting

---

## Tips & Tricks

### Productivity Tips

1. **Bookmark Frequently Used Repos**
   - Save common repository URLs
   - Quick access to team projects

2. **Token Management**
   - Use a password manager for tokens
   - Set calendar reminders for expiration
   - Name tokens descriptively

3. **Documentation Workflow**
   - Generate initial documentation
   - Review and customize
   - Add to repository as ONBOARDING.md
   - Update periodically

4. **Team Usage**
   - Share generated guides with new team members
   - Customize for your team's workflow
   - Include in onboarding process

---

## Examples

### Example 1: Public Repository

```
Repository URL: https://github.com/expressjs/express
GitHub Token: (leave empty)
Click: Generate Documentation
Result: Complete onboarding guide for Express.js
```

### Example 2: Private Repository

```
Repository URL: https://github.com/mycompany/private-api
GitHub Token: ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
Click: Generate Documentation
Result: Complete onboarding guide with full access
```

### Example 3: Organization Repository

```
Repository URL: https://github.com/myorg/internal-tools
GitHub Token: ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
(Token must have org access)
Click: Generate Documentation
Result: Complete onboarding guide for org repo
```

---

**Happy Documenting! 🚀**

*For technical setup details, see [GITHUB_TOKEN_SETUP.md](GITHUB_TOKEN_SETUP.md)*