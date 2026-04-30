# Express.js Developer Onboarding Guide

**Repository:** https://github.com/expressjs/express  
**Generated:** 2026-04-30  
**Purpose:** Comprehensive onboarding guide for new developers

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Repository Metadata](#repository-metadata)
3. [Getting Started](#getting-started)
4. [Architecture & Design](#architecture--design)
5. [Development Workflow](#development-workflow)
6. [Testing Strategy](#testing-strategy)
7. [Contributing Guidelines](#contributing-guidelines)
8. [Key Concepts](#key-concepts)
9. [Resources](#resources)

---

## 🎯 Overview

Express.js is a minimal and flexible Node.js web application framework that provides a robust set of features for web and mobile applications. It's one of the most popular Node.js frameworks, known for its simplicity, performance, and extensive middleware ecosystem.

### What is Express?

- **Fast, unopinionated, minimalist web framework** for Node.js
- **Middleware-based architecture** for handling HTTP requests
- **Routing system** for defining application endpoints
- **Template engine support** for dynamic HTML generation
- **Robust API** for building RESTful services

---

## 📊 Repository Metadata

### Key Statistics
- **Language:** JavaScript (Node.js)
- **License:** MIT
- **Stars:** 65,000+ ⭐
- **Forks:** 15,000+
- **Active Contributors:** 300+
- **First Release:** 2010
- **Current Version:** 4.x (stable), 5.x (beta)

### Repository Structure
```
express/
├── lib/              # Core Express source code
├── test/             # Test suite
├── examples/         # Example applications
├── benchmarks/       # Performance benchmarks
├── History.md        # Changelog
├── index.js          # Main entry point
├── package.json      # Dependencies and scripts
└── README.md         # Project documentation
```

---

## 🚀 Getting Started

### Prerequisites

- **Node.js:** v14.0.0 or higher
- **npm:** v6.0.0 or higher (comes with Node.js)
- **Git:** For cloning the repository

### Installation

#### 1. Clone the Repository
```bash
git clone https://github.com/expressjs/express.git
cd express
```

#### 2. Install Dependencies
```bash
npm install
```

#### 3. Run Tests
```bash
npm test
```

#### 4. Run Examples
```bash
cd examples/hello-world
node index.js
```

### Quick Start - Using Express in Your Project

```bash
# Create a new project
mkdir my-express-app
cd my-express-app
npm init -y

# Install Express
npm install express

# Create app.js
cat > app.js << 'EOF'
const express = require('express');
const app = express();
const port = 3000;

app.get('/', (req, res) => {
  res.send('Hello World!');
});

app.listen(port, () => {
  console.log(`App listening at http://localhost:${port}`);
});
EOF

# Run the application
node app.js
```

---

## 🏗️ Architecture & Design

### Core Components

#### 1. **Application (`lib/application.js`)**
The main Express application object that handles:
- Routing configuration
- Middleware registration
- Settings management
- HTTP server creation

```javascript
const express = require('express');
const app = express(); // Creates an Express application instance
```

#### 2. **Router (`lib/router/`)**
Modular routing system that:
- Matches HTTP requests to handlers
- Supports route parameters and wildcards
- Enables route grouping and nesting
- Provides middleware chaining

```javascript
const router = express.Router();
router.get('/users/:id', (req, res) => {
  res.json({ id: req.params.id });
});
```

#### 3. **Request (`lib/request.js`)**
Enhanced HTTP request object with:
- Query parameter parsing
- Body parsing (via middleware)
- Cookie handling
- Content negotiation

#### 4. **Response (`lib/response.js`)**
Enhanced HTTP response object with:
- JSON/HTML/file sending
- Status code helpers
- Header management
- Redirect functionality

#### 5. **Middleware (`lib/middleware/`)**
Functions that process requests:
- Execute in sequence
- Can modify request/response
- Control flow with `next()`
- Handle errors

### Design Patterns

#### Middleware Pattern
```javascript
// Middleware function signature
function middleware(req, res, next) {
  // Do something with req/res
  next(); // Pass control to next middleware
}

// Error-handling middleware
function errorHandler(err, req, res, next) {
  res.status(500).send('Something broke!');
}
```

#### Chain of Responsibility
Middleware functions form a chain where each can:
- Process the request
- Pass to the next middleware
- End the request-response cycle
- Handle errors

#### Factory Pattern
Express uses factories to create application instances:
```javascript
const express = require('express');
const app = express(); // Factory function
```

---

## 💻 Development Workflow

### Project Structure Best Practices

```
my-express-app/
├── src/
│   ├── controllers/    # Request handlers
│   ├── models/         # Data models
│   ├── routes/         # Route definitions
│   ├── middleware/     # Custom middleware
│   ├── services/       # Business logic
│   └── utils/          # Helper functions
├── tests/
│   ├── unit/           # Unit tests
│   ├── integration/    # Integration tests
│   └── e2e/            # End-to-end tests
├── config/             # Configuration files
├── public/             # Static assets
├── views/              # Template files
├── .env                # Environment variables
├── .gitignore
├── package.json
└── README.md
```

### Development Commands

```bash
# Install dependencies
npm install

# Run in development mode (with auto-reload)
npm run dev

# Run tests
npm test

# Run tests with coverage
npm run test:coverage

# Lint code
npm run lint

# Format code
npm run format

# Build for production
npm run build

# Start production server
npm start
```

### Environment Configuration

Create a `.env` file:
```env
NODE_ENV=development
PORT=3000
DATABASE_URL=mongodb://localhost:27017/myapp
JWT_SECRET=your-secret-key
LOG_LEVEL=debug
```

Load with `dotenv`:
```javascript
require('dotenv').config();

const port = process.env.PORT || 3000;
const dbUrl = process.env.DATABASE_URL;
```

---

## 🧪 Testing Strategy

### Test Structure

Express uses **Mocha** and **Supertest** for testing:

```javascript
const request = require('supertest');
const express = require('express');
const app = express();

app.get('/user', (req, res) => {
  res.status(200).json({ name: 'john' });
});

describe('GET /user', () => {
  it('responds with json', (done) => {
    request(app)
      .get('/user')
      .expect('Content-Type', /json/)
      .expect(200)
      .end((err, res) => {
        if (err) return done(err);
        done();
      });
  });
});
```

### Running Tests

```bash
# Run all tests
npm test

# Run specific test file
npm test -- test/app.router.js

# Run with coverage
npm run test-cov

# Run tests in watch mode
npm test -- --watch
```

### Test Categories

1. **Unit Tests** - Test individual functions/modules
2. **Integration Tests** - Test component interactions
3. **Acceptance Tests** - Test complete request/response cycles
4. **Performance Tests** - Benchmark critical paths

---

## 🤝 Contributing Guidelines

### Before Contributing

1. **Read the Contributing Guide:** Check `Contributing.md`
2. **Check Existing Issues:** Avoid duplicates
3. **Discuss Major Changes:** Open an issue first
4. **Follow Code Style:** Use ESLint configuration

### Contribution Workflow

#### 1. Fork and Clone
```bash
# Fork on GitHub, then:
git clone https://github.com/YOUR_USERNAME/express.git
cd express
git remote add upstream https://github.com/expressjs/express.git
```

#### 2. Create a Branch
```bash
git checkout -b feature/my-new-feature
```

#### 3. Make Changes
- Write code following style guidelines
- Add tests for new functionality
- Update documentation

#### 4. Run Tests
```bash
npm test
npm run lint
```

#### 5. Commit Changes
```bash
git add .
git commit -m "feat: add new feature"
```

Follow **Conventional Commits**:
- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation
- `test:` Tests
- `refactor:` Code refactoring

#### 6. Push and Create PR
```bash
git push origin feature/my-new-feature
```

Then create a Pull Request on GitHub.

### Code Style

- **Indentation:** 2 spaces
- **Semicolons:** Required
- **Quotes:** Single quotes
- **Line Length:** 80 characters max
- **Naming:** camelCase for variables, PascalCase for classes

---

## 🔑 Key Concepts

### 1. Middleware

Middleware functions have access to request, response, and next middleware:

```javascript
// Application-level middleware
app.use((req, res, next) => {
  console.log('Time:', Date.now());
  next();
});

// Router-level middleware
router.use((req, res, next) => {
  console.log('Request URL:', req.originalUrl);
  next();
});

// Error-handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).send('Something broke!');
});
```

### 2. Routing

Define routes for different HTTP methods:

```javascript
// Basic routing
app.get('/', (req, res) => res.send('GET request'));
app.post('/', (req, res) => res.send('POST request'));
app.put('/user', (req, res) => res.send('PUT request'));
app.delete('/user', (req, res) => res.send('DELETE request'));

// Route parameters
app.get('/users/:userId/books/:bookId', (req, res) => {
  res.send(req.params);
});

// Route handlers (multiple callbacks)
app.get('/example', 
  (req, res, next) => {
    console.log('First handler');
    next();
  },
  (req, res) => {
    res.send('Second handler');
  }
);
```

### 3. Request Object

Access request data:

```javascript
app.get('/user/:id', (req, res) => {
  // Route parameters
  const userId = req.params.id;
  
  // Query parameters
  const page = req.query.page;
  
  // Request body (requires body-parser)
  const data = req.body;
  
  // Headers
  const userAgent = req.get('User-Agent');
  
  // Cookies (requires cookie-parser)
  const token = req.cookies.token;
  
  res.json({ userId, page, data });
});
```

### 4. Response Object

Send responses:

```javascript
app.get('/api/data', (req, res) => {
  // Send JSON
  res.json({ message: 'Success', data: [] });
  
  // Send status
  res.status(404).send('Not Found');
  
  // Redirect
  res.redirect('/new-url');
  
  // Send file
  res.sendFile('/path/to/file.pdf');
  
  // Set headers
  res.set('Content-Type', 'text/html');
  res.send('<h1>Hello</h1>');
});
```

### 5. Error Handling

Handle errors gracefully:

```javascript
// Async error handling
app.get('/async', async (req, res, next) => {
  try {
    const data = await fetchData();
    res.json(data);
  } catch (err) {
    next(err); // Pass to error handler
  }
});

// Error middleware (must have 4 parameters)
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(err.status || 500).json({
    error: {
      message: err.message,
      status: err.status
    }
  });
});
```

---

## 📚 Resources

### Official Documentation
- **Website:** https://expressjs.com
- **API Reference:** https://expressjs.com/en/4x/api.html
- **Guide:** https://expressjs.com/en/guide/routing.html

### Learning Resources
- **Express.js Tutorial:** https://developer.mozilla.org/en-US/docs/Learn/Server-side/Express_Nodejs
- **Node.js Best Practices:** https://github.com/goldbergyoni/nodebestpractices
- **Express Security:** https://expressjs.com/en/advanced/best-practice-security.html

### Community
- **GitHub Issues:** https://github.com/expressjs/express/issues
- **Stack Overflow:** Tag `express`
- **Gitter Chat:** https://gitter.im/expressjs/express
- **Twitter:** @expressjs

### Related Projects
- **Body Parser:** Parse request bodies
- **Cookie Parser:** Parse cookies
- **Morgan:** HTTP request logger
- **Helmet:** Security middleware
- **CORS:** Enable CORS
- **Compression:** Gzip compression

### Example Applications
```bash
# Explore examples in the repository
cd express/examples

# Available examples:
# - hello-world: Basic app
# - auth: Authentication
# - content-negotiation: Content types
# - cookies: Cookie handling
# - downloads: File downloads
# - error-pages: Error handling
# - mvc: MVC pattern
# - multi-router: Multiple routers
# - static-files: Serving static files
```

---

## 🎓 Next Steps

### For New Contributors
1. ✅ Set up development environment
2. ✅ Run tests successfully
3. ✅ Read through core source files
4. ✅ Pick a "good first issue" from GitHub
5. ✅ Submit your first PR

### For Users
1. ✅ Build a simple REST API
2. ✅ Add authentication middleware
3. ✅ Implement error handling
4. ✅ Add logging and monitoring
5. ✅ Deploy to production

### Advanced Topics
- **Performance Optimization:** Caching, clustering
- **Security Hardening:** Helmet, rate limiting, input validation
- **Microservices:** Service communication, API gateways
- **GraphQL Integration:** Apollo Server with Express
- **WebSocket Support:** Socket.io integration

---

## 📝 Summary

Express.js is a powerful, flexible framework that follows these principles:

- **Minimalist:** Core is small, extend with middleware
- **Unopinionated:** Freedom in architecture choices
- **Middleware-based:** Composable request processing
- **Well-documented:** Extensive guides and examples
- **Battle-tested:** Used by millions of applications

**Welcome to the Express.js community! Happy coding! 🚀**

---

*Generated by Bob - Your AI Development Assistant*