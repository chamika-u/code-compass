# 🚀 Repository Documentation Generator

An AI-powered web application that automatically generates comprehensive developer onboarding guides for any GitHub repository using IBM WatsonX AI and MCP (Model Context Protocol) servers.

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Node](https://img.shields.io/badge/node-%3E%3D16.0.0-brightgreen.svg)
![WatsonX](https://img.shields.io/badge/AI-WatsonX-purple.svg)

## ✨ Features

- 🤖 **AI-Powered Analysis** - Uses IBM WatsonX AI to intelligently analyze repositories
- ⚡ **Fast Generation** - Creates comprehensive guides in seconds
- 🎨 **Beautiful UI** - Modern, responsive web interface
- 📚 **Complete Documentation** - Includes setup, architecture, testing, and contribution guidelines
- 🔄 **Real-time Progress** - Live updates during documentation generation
- 💾 **Download Support** - Export generated documentation as Markdown files
- 🌐 **GitHub Integration** - Fetches repository metadata directly from GitHub API
- 🛠️ **MCP Architecture** - Built on Model Context Protocol for extensibility

## 🎯 What It Generates

The tool creates comprehensive onboarding guides that include:

- **Project Overview** - Description, purpose, and key information
- **Repository Metadata** - Stars, forks, languages, license, and statistics
- **Getting Started** - Prerequisites, installation steps, and quick start
- **Project Structure** - Directory organization and file distribution
- **Technology Stack** - Languages, frameworks, and tools used
- **Development Workflow** - Best practices and coding standards
- **Key Features** - Main functionality and capabilities
- **Testing Strategy** - How to run and write tests
- **Contributing Guidelines** - Step-by-step contribution process
- **Resources** - Links, documentation, and community channels

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Node.js** v16.0.0 or higher
- **npm** v8.0.0 or higher
- **Git** (for cloning the repository)

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/repo-documentation-generator.git
cd repo-documentation-generator
```

### 2. Install Dependencies

```bash
npm install
```

### 3. Configure Environment Variables

Copy the example environment file and configure your API keys:

```bash
cp .env.example .env
```

Edit `.env` and add your credentials:

```env
# Server Configuration
PORT=3000
NODE_ENV=development

# WatsonX AI Configuration
WATSONX_API_KEY=your_watsonx_api_key_here
WATSONX_PROJECT_ID=your_project_id_here
WATSONX_URL=https://us-south.ml.cloud.ibm.com

# GitHub API Configuration (Optional)
GITHUB_TOKEN=your_github_token_here
```

#### Getting API Keys:

**WatsonX API Key:**
1. Go to [IBM Cloud](https://cloud.ibm.com/)
2. Create an account or sign in
3. Navigate to Watson Machine Learning service
4. Create a new instance
5. Generate API key from service credentials
6. Create a project and copy the project ID

**GitHub Token (Optional but Recommended):**
1. Go to [GitHub Settings > Tokens](https://github.com/settings/tokens)
2. Click "Generate new token (classic)"
3. Select scopes: `public_repo` (for public repositories)
4. Generate and copy the token

> **Note:** The application works without API keys but uses template-based generation instead of AI-powered analysis.

### 4. Start the Server

```bash
# Production mode
npm start

# Development mode (with auto-reload)
npm run dev
```

### 5. Open in Browser

Navigate to: **http://localhost:3000**

## 🎮 How to Use

1. **Open the Application** - Go to http://localhost:3000 in your browser
2. **Enter Repository URL** - Paste any GitHub repository URL (e.g., `https://github.com/expressjs/express`)
3. **Click Generate** - The AI will analyze the repository and generate documentation
4. **View Results** - Review the generated onboarding guide in the preview
5. **Download** - Click "Download Markdown" to save the documentation

### Example Repositories to Try:

- `https://github.com/expressjs/express` - Express.js web framework
- `https://github.com/facebook/react` - React JavaScript library
- `https://github.com/nodejs/node` - Node.js runtime
- `https://github.com/microsoft/vscode` - Visual Studio Code

## 🏗️ Project Structure

```
repo-documentation-generator/
├── public/                 # Frontend files
│   └── index.html         # Main UI (HTML + CSS + JavaScript)
├── docs/                  # Generated documentation examples
│   ├── EXPRESS_JS_ONBOARDING_GUIDE.md
│   └── HELA_BOJUN_ONBOARDING_GUIDE.md
├── server.js              # Express.js backend server
├── package.json           # Node.js dependencies
├── .env.example          # Environment variables template
├── .gitignore            # Git ignore rules
└── README.md             # This file
```

## 🔧 API Endpoints

### `GET /`
Serves the main web interface

### `GET /api/health`
Health check endpoint

**Response:**
```json
{
  "status": "healthy",
  "timestamp": "2026-04-30T23:00:00.000Z",
  "watsonxConfigured": true
}
```

### `POST /api/generate-documentation`
Generates documentation for a GitHub repository

**Request:**
```json
{
  "repoUrl": "https://github.com/owner/repo"
}
```

**Response:**
```json
{
  "success": true,
  "repoName": "repo",
  "markdown": "# Repository Documentation...",
  "metadata": {
    "name": "repo",
    "description": "Repository description",
    "stars": 1000,
    "language": "JavaScript"
  }
}
```

## 🤖 How It Works

### Architecture Flow:

1. **User Input** → User pastes GitHub repository URL in the web UI
2. **Metadata Fetch** → Server fetches repository data from GitHub API
3. **Structure Analysis** → Analyzes file structure, languages, and patterns
4. **AI Generation** → WatsonX AI generates comprehensive documentation
5. **Response** → Markdown documentation returned to frontend
6. **Display** → UI renders the documentation with preview and download options

### Technology Stack:

**Frontend:**
- HTML5, CSS3, JavaScript (Vanilla)
- Responsive design with modern UI/UX
- Real-time progress indicators

**Backend:**
- Node.js with Express.js
- Axios for HTTP requests
- CORS enabled for API access

**AI & APIs:**
- IBM WatsonX AI (Granite models)
- GitHub REST API v3
- Model Context Protocol (MCP)

## 🧪 Testing

Currently, the application includes manual testing. To test:

1. Start the server: `npm start`
2. Open http://localhost:3000
3. Try generating documentation for various repositories
4. Verify the output quality and completeness

## 🚢 Deployment

### Deploy to Heroku

```bash
# Login to Heroku
heroku login

# Create app
heroku create your-app-name

# Set environment variables
heroku config:set WATSONX_API_KEY=your_key
heroku config:set WATSONX_PROJECT_ID=your_project_id
heroku config:set GITHUB_TOKEN=your_token

# Deploy
git push heroku main

# Open app
heroku open
```

### Deploy to Vercel

```bash
# Install Vercel CLI
npm i -g vercel

# Deploy
vercel --prod
```

### Deploy to AWS/DigitalOcean/Google Cloud

1. Set up a Node.js environment
2. Clone the repository
3. Install dependencies: `npm install`
4. Set environment variables
5. Start with PM2: `pm2 start server.js`
6. Configure reverse proxy (Nginx/Apache)

## 🔒 Security Considerations

- **API Keys** - Never commit `.env` file to version control
- **Rate Limiting** - GitHub API has rate limits (60 requests/hour without token, 5000 with token)
- **Input Validation** - Server validates all repository URLs
- **CORS** - Configured for security
- **HTTPS** - Use HTTPS in production

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Fork the Repository**
2. **Create a Feature Branch** (`git checkout -b feature/amazing-feature`)
3. **Commit Changes** (`git commit -m 'Add amazing feature'`)
4. **Push to Branch** (`git push origin feature/amazing-feature`)
5. **Open a Pull Request**

### Development Guidelines:

- Follow existing code style
- Add comments for complex logic
- Test thoroughly before submitting PR
- Update documentation as needed

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- **IBM WatsonX** - For providing powerful AI capabilities
- **GitHub** - For the comprehensive API
- **Express.js** - For the robust web framework
- **Open Source Community** - For inspiration and support

## 📞 Support

### Getting Help:

- **Issues** - Report bugs or request features via GitHub Issues
- **Documentation** - Check this README and code comments
- **Community** - Join discussions in GitHub Discussions

### Common Issues:

**Issue: "Failed to generate documentation"**
- Check your WatsonX API key is valid
- Verify the repository URL is correct
- Ensure you have internet connectivity

**Issue: "GitHub rate limit exceeded"**
- Add a GitHub personal access token to `.env`
- Wait for rate limit to reset (1 hour)

**Issue: "Port already in use"**
- Change PORT in `.env` file
- Kill process using the port: `lsof -ti:3000 | xargs kill` (Mac/Linux)

## 🗺️ Roadmap

### Planned Features:

- [ ] Support for private repositories
- [ ] Multiple AI model options
- [ ] Customizable documentation templates
- [ ] Batch processing for multiple repositories
- [ ] API key management UI
- [ ] Documentation versioning
- [ ] Export to PDF/HTML formats
- [ ] Integration with CI/CD pipelines
- [ ] Multi-language support
- [ ] Advanced analytics and insights

## 📊 Statistics

- **Lines of Code:** ~1,200
- **Dependencies:** 4 production, 1 development
- **Supported Languages:** All GitHub-supported languages
- **Average Generation Time:** 3-5 seconds

---

## 🎉 Get Started Now!

```bash
git clone https://github.com/your-username/repo-documentation-generator.git
cd repo-documentation-generator
npm install
cp .env.example .env
# Add your API keys to .env
npm start
# Open http://localhost:3000
```

**Happy Documenting! 📚✨**

---

*Built with ❤️ using IBM WatsonX AI, Node.js, and Modern Web Technologies*