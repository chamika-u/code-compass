const express = require('express');
const cors = require('cors');
const axios = require('axios');
const path = require('path');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.static('public'));

// WatsonX Configuration
const WATSONX_API_KEY = process.env.WATSONX_API_KEY;
const WATSONX_PROJECT_ID = process.env.WATSONX_PROJECT_ID;
const WATSONX_URL = process.env.WATSONX_URL || 'https://us-south.ml.cloud.ibm.com';

// GitHub API Configuration
const GITHUB_TOKEN = process.env.GITHUB_TOKEN;

/**
 * Fetch repository metadata from GitHub
 */
async function fetchRepoMetadata(repoUrl, customToken = null) {
    try {
        // Extract owner and repo from URL
        const match = repoUrl.match(/github\.com\/([^\/]+)\/([^\/]+)/);
        if (!match) {
            throw new Error('Invalid GitHub URL');
        }

        const [, owner, repo] = match;
        const cleanRepo = repo.replace('.git', '');

        // Use custom token if provided, otherwise fall back to env token
        const token = customToken || GITHUB_TOKEN;
        const headers = token ? { Authorization: `token ${token}` } : {};
        
        const [repoData, languagesData, readmeData] = await Promise.all([
            axios.get(`https://api.github.com/repos/${owner}/${cleanRepo}`, { headers }),
            axios.get(`https://api.github.com/repos/${owner}/${cleanRepo}/languages`, { headers }),
            axios.get(`https://api.github.com/repos/${owner}/${cleanRepo}/readme`, { headers })
                .catch(() => ({ data: { content: '' } }))
        ]);

        // Decode README content
        let readmeContent = '';
        if (readmeData.data.content) {
            readmeContent = Buffer.from(readmeData.data.content, 'base64').toString('utf-8');
        }

        return {
            name: repoData.data.name,
            fullName: repoData.data.full_name,
            description: repoData.data.description || 'No description provided',
            stars: repoData.data.stargazers_count,
            forks: repoData.data.forks_count,
            language: repoData.data.language,
            languages: Object.keys(languagesData.data),
            topics: repoData.data.topics || [],
            license: repoData.data.license?.name || 'No license',
            createdAt: repoData.data.created_at,
            updatedAt: repoData.data.updated_at,
            homepage: repoData.data.homepage,
            readme: readmeContent.substring(0, 5000), // Limit README size
            owner: owner,
            repo: cleanRepo
        };
    } catch (error) {
        console.error('Error fetching repo metadata:', error.message);
        throw new Error('Failed to fetch repository metadata');
    }
}

/**
 * Analyze repository structure
 */
async function analyzeRepoStructure(owner, repo, customToken = null) {
    try {
        const token = customToken || GITHUB_TOKEN;
        const headers = token ? { Authorization: `token ${token}` } : {};
        
        // Fetch repository tree
        const treeData = await axios.get(
            `https://api.github.com/repos/${owner}/${repo}/git/trees/main?recursive=1`,
            { headers }
        ).catch(() => 
            axios.get(`https://api.github.com/repos/${owner}/${repo}/git/trees/master?recursive=1`, { headers })
        );

        const files = treeData.data.tree || [];
        
        // Analyze file structure
        const structure = {
            totalFiles: files.filter(f => f.type === 'blob').length,
            directories: files.filter(f => f.type === 'tree').map(f => f.path),
            fileTypes: {},
            hasTests: false,
            hasDocs: false,
            hasCI: false,
            packageManager: 'unknown'
        };

        files.forEach(file => {
            if (file.type === 'blob') {
                const ext = path.extname(file.path);
                structure.fileTypes[ext] = (structure.fileTypes[ext] || 0) + 1;

                // Check for common patterns
                if (file.path.includes('test') || file.path.includes('spec')) {
                    structure.hasTests = true;
                }
                if (file.path.includes('doc') || file.path.includes('README')) {
                    structure.hasDocs = true;
                }
                if (file.path.includes('.github') || file.path.includes('.gitlab')) {
                    structure.hasCI = true;
                }
                if (file.path === 'package.json') {
                    structure.packageManager = 'npm';
                } else if (file.path === 'pom.xml') {
                    structure.packageManager = 'maven';
                } else if (file.path === 'requirements.txt' || file.path === 'setup.py') {
                    structure.packageManager = 'pip';
                }
            }
        });

        return structure;
    } catch (error) {
        console.error('Error analyzing repo structure:', error.message);
        return {
            totalFiles: 0,
            directories: [],
            fileTypes: {},
            hasTests: false,
            hasDocs: false,
            hasCI: false,
            packageManager: 'unknown'
        };
    }
}

/**
 * Get IAM token from IBM Cloud
 */
async function getIAMToken(apiKey) {
    try {
        const response = await axios.post(
            'https://iam.cloud.ibm.com/identity/token',
            new URLSearchParams({
                'grant_type': 'urn:ibm:params:oauth:grant-type:apikey',
                'apikey': apiKey
            }),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Accept': 'application/json'
                }
            }
        );
        return response.data.access_token;
    } catch (error) {
        console.error('Error getting IAM token:', error.message);
        throw error;
    }
}

/**
 * Generate documentation using WatsonX AI
 */
async function generateWithWatsonX(metadata, structure) {
    try {
        // Prepare prompt for WatsonX
        const prompt = `Generate a comprehensive developer onboarding guide for the following GitHub repository:

Repository: ${metadata.fullName}
Description: ${metadata.description}
Primary Language: ${metadata.language}
Languages: ${metadata.languages.join(', ')}
Stars: ${metadata.stars}
Topics: ${metadata.topics.join(', ')}

Project Structure:
- Total Files: ${structure.totalFiles}
- Has Tests: ${structure.hasTests}
- Has Documentation: ${structure.hasDocs}
- Has CI/CD: ${structure.hasCI}
- Package Manager: ${structure.packageManager}

README Preview:
${metadata.readme}

Please generate a detailed developer onboarding guide in Markdown format that includes:
1. Project Overview
2. Getting Started (Prerequisites, Installation)
3. Project Structure
4. Technology Stack
5. Development Workflow
6. Key Features
7. Testing Strategy
8. Contributing Guidelines
9. Resources

Make it comprehensive, well-structured, and developer-friendly.`;

        // Call WatsonX API
        if (!WATSONX_API_KEY) {
            console.warn('WatsonX API key not configured, using template generation');
            return generateTemplateDocumentation(metadata, structure);
        }

        // Get IAM token first
        console.log('Getting IAM token for WatsonX...');
        const iamToken = await getIAMToken(WATSONX_API_KEY);
        console.log('IAM token obtained successfully');

        // Use text generation with deployment space
        const response = await axios.post(
            `${WATSONX_URL}/ml/v1/text/generation?version=2023-05-29`,
            {
                input: prompt,
                model_id: 'ibm/granite-3-8b-instruct',
                project_id: WATSONX_PROJECT_ID,
                parameters: {
                    decoding_method: "greedy",
                    max_new_tokens: 4000,
                    min_new_tokens: 0,
                    stop_sequences: [],
                    repetition_penalty: 1
                }
            },
            {
                headers: {
                    'Authorization': `Bearer ${iamToken}`,
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            }
        );

        return response.data.results[0].generated_text;
    } catch (error) {
        console.error('Error calling WatsonX:', error.message);
        if (error.response) {
            console.error('WatsonX API response:', error.response.status, error.response.data);
        }
        console.log('Falling back to template generation');
        return generateTemplateDocumentation(metadata, structure);
    }
}

/**
 * Generate template documentation (fallback)
 */
function generateTemplateDocumentation(metadata, structure) {
    const doc = `# ${metadata.name} Developer Onboarding Guide

**Repository:** https://github.com/${metadata.fullName}  
**Generated:** ${new Date().toISOString().split('T')[0]}  
**Purpose:** Comprehensive onboarding guide for new developers

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Repository Metadata](#repository-metadata)
3. [Getting Started](#getting-started)
4. [Project Structure](#project-structure)
5. [Technology Stack](#technology-stack)
6. [Development Workflow](#development-workflow)
7. [Testing Strategy](#testing-strategy)
8. [Contributing Guidelines](#contributing-guidelines)
9. [Resources](#resources)

---

## 🎯 Overview

${metadata.description}

### Key Information
- **Primary Language:** ${metadata.language}
- **Stars:** ${metadata.stars} ⭐
- **Forks:** ${metadata.forks}
- **License:** ${metadata.license}
- **Topics:** ${metadata.topics.join(', ') || 'None'}

---

## 📊 Repository Metadata

### Statistics
- **Created:** ${new Date(metadata.createdAt).toLocaleDateString()}
- **Last Updated:** ${new Date(metadata.updatedAt).toLocaleDateString()}
- **Total Files:** ${structure.totalFiles}
- **Languages:** ${metadata.languages.join(', ')}

### Project Health
- ✅ Tests: ${structure.hasTests ? 'Yes' : 'No'}
- ✅ Documentation: ${structure.hasDocs ? 'Yes' : 'No'}
- ✅ CI/CD: ${structure.hasCI ? 'Yes' : 'No'}

---

## 🚀 Getting Started

### Prerequisites

${getPrerequisites(metadata.language, structure.packageManager)}

### Installation

#### 1. Clone the Repository
\`\`\`bash
git clone https://github.com/${metadata.fullName}.git
cd ${metadata.name}
\`\`\`

#### 2. Install Dependencies
${getInstallCommands(structure.packageManager)}

#### 3. Run the Application
${getRunCommands(metadata.language, structure.packageManager)}

---

## 🏗️ Project Structure

${getProjectStructure(metadata.language, structure)}

---

## 💻 Technology Stack

### Primary Technologies
- **Language:** ${metadata.language}
- **Package Manager:** ${structure.packageManager}

### File Distribution
${Object.entries(structure.fileTypes)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 10)
    .map(([ext, count]) => `- ${ext || 'no extension'}: ${count} files`)
    .join('\n')}

---

## 🔑 Key Features

${getKeyFeatures(metadata)}

---

## 🧪 Testing Strategy

${structure.hasTests ? `
This project includes automated tests. Run them using:

\`\`\`bash
${getTestCommands(metadata.language, structure.packageManager)}
\`\`\`
` : `
No automated tests found in the repository. Consider adding tests to improve code quality.
`}

---

## 🤝 Contributing Guidelines

### How to Contribute

1. **Fork the Repository**
   \`\`\`bash
   git clone https://github.com/YOUR_USERNAME/${metadata.name}.git
   \`\`\`

2. **Create a Feature Branch**
   \`\`\`bash
   git checkout -b feature/your-feature-name
   \`\`\`

3. **Make Your Changes**
   - Write clean, readable code
   - Follow project coding standards
   - Add tests for new features
   - Update documentation

4. **Commit Your Changes**
   \`\`\`bash
   git add .
   git commit -m "feat: add your feature"
   \`\`\`

5. **Push and Create PR**
   \`\`\`bash
   git push origin feature/your-feature-name
   \`\`\`

---

## 📚 Resources

### Official Links
- **Repository:** https://github.com/${metadata.fullName}
- **Issues:** https://github.com/${metadata.fullName}/issues
- **Pull Requests:** https://github.com/${metadata.fullName}/pulls
${metadata.homepage ? `- **Homepage:** ${metadata.homepage}` : ''}

### Community
- **GitHub Discussions:** Engage with the community
- **Issue Tracker:** Report bugs and request features

---

## 🎓 Next Steps

### For New Contributors
1. ✅ Set up development environment
2. ✅ Read through the codebase
3. ✅ Pick a "good first issue"
4. ✅ Submit your first PR

### For Users
1. ✅ Follow installation instructions
2. ✅ Explore the features
3. ✅ Report any issues
4. ✅ Share feedback

---

**Welcome to ${metadata.name}! Happy coding! 🚀**

---

*Generated by Repository Documentation Generator*
*Powered by WatsonX AI & MCP Servers*
`;

    return doc;
}

// Helper functions
function getPrerequisites(language, packageManager) {
    const prereqs = {
        'JavaScript': '- **Node.js:** v16.0.0 or higher\n- **npm:** Latest version',
        'TypeScript': '- **Node.js:** v16.0.0 or higher\n- **npm:** Latest version\n- **TypeScript:** Latest version',
        'Python': '- **Python:** 3.8 or higher\n- **pip:** Latest version',
        'Java': '- **Java JDK:** 11 or higher\n- **Maven/Gradle:** Latest version',
        'Go': '- **Go:** 1.18 or higher',
        'Rust': '- **Rust:** Latest stable version\n- **Cargo:** Included with Rust',
        'Ruby': '- **Ruby:** 2.7 or higher\n- **Bundler:** Latest version'
    };
    return prereqs[language] || '- Check repository README for specific requirements';
}

function getInstallCommands(packageManager) {
    const commands = {
        'npm': '```bash\nnpm install\n```',
        'maven': '```bash\nmvn clean install\n```',
        'pip': '```bash\npip install -r requirements.txt\n```',
        'gradle': '```bash\ngradle build\n```'
    };
    return commands[packageManager] || '```bash\n# Check repository README for installation instructions\n```';
}

function getRunCommands(language, packageManager) {
    if (packageManager === 'npm') {
        return '```bash\nnpm start\n# or\nnpm run dev\n```';
    } else if (packageManager === 'maven') {
        return '```bash\nmvn spring-boot:run\n```';
    } else if (packageManager === 'pip') {
        return '```bash\npython main.py\n# or\npython manage.py runserver\n```';
    }
    return '```bash\n# Check repository README for run instructions\n```';
}

function getProjectStructure(language, structure) {
    return `The project contains ${structure.totalFiles} files organized across multiple directories.

Key directories may include:
${structure.directories.slice(0, 10).map(dir => `- \`${dir}/\``).join('\n') || '- Check repository for structure details'}`;
}

function getKeyFeatures(metadata) {
    if (metadata.topics.length > 0) {
        return metadata.topics.map(topic => `- **${topic}**`).join('\n');
    }
    return '- Check repository README for feature details';
}

function getTestCommands(language, packageManager) {
    if (packageManager === 'npm') {
        return 'npm test';
    } else if (packageManager === 'maven') {
        return 'mvn test';
    } else if (packageManager === 'pip') {
        return 'pytest\n# or\npython -m unittest';
    }
    return '# Check repository for test commands';
}

// API Routes

/**
 * Health check endpoint
 */
app.get('/api/health', (req, res) => {
    res.json({ 
        status: 'healthy',
        timestamp: new Date().toISOString(),
        watsonxConfigured: !!WATSONX_API_KEY
    });
});

/**
 * Generate documentation endpoint
 */
app.post('/api/generate-documentation', async (req, res) => {
    try {
        const { repoUrl, githubToken } = req.body;

        if (!repoUrl) {
            return res.status(400).json({ error: 'Repository URL is required' });
        }

        console.log(`Generating documentation for: ${repoUrl}`);
        if (githubToken) {
            console.log('Using custom GitHub token from request');
        }

        // Step 1: Fetch repository metadata
        const metadata = await fetchRepoMetadata(repoUrl, githubToken);
        console.log(`Fetched metadata for ${metadata.fullName}`);

        // Step 2: Analyze repository structure
        const structure = await analyzeRepoStructure(metadata.owner, metadata.repo, githubToken);
        console.log(`Analyzed structure: ${structure.totalFiles} files`);

        // Step 3: Generate documentation with WatsonX
        const markdown = await generateWithWatsonX(metadata, structure);
        console.log(`Generated documentation (${markdown.length} characters)`);

        res.json({
            success: true,
            repoName: metadata.name,
            markdown: markdown,
            metadata: {
                name: metadata.name,
                description: metadata.description,
                stars: metadata.stars,
                language: metadata.language
            }
        });

    } catch (error) {
        console.error('Error generating documentation:', error);
        res.status(500).json({
            error: 'Failed to generate documentation',
            message: error.message
        });
    }
});

// Serve frontend
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// Start server
app.listen(PORT, () => {
    console.log(`🚀 Repository Documentation Generator running on http://localhost:${PORT}`);
    console.log(`📊 WatsonX configured: ${!!WATSONX_API_KEY}`);
    console.log(`🔑 GitHub token configured: ${!!GITHUB_TOKEN}`);
});

module.exports = app;

// Made with Bob
