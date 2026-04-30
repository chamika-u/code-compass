# Hela Bojun Developer Onboarding Guide

**Repository:** https://github.com/chamika-u/hela-bojun  
**Generated:** 2026-04-30  
**Purpose:** Comprehensive onboarding guide for new developers

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Repository Information](#repository-information)
3. [Getting Started](#getting-started)
4. [Project Structure](#project-structure)
5. [Technology Stack](#technology-stack)
6. [Development Workflow](#development-workflow)
7. [Key Features](#key-features)
8. [Testing Strategy](#testing-strategy)
9. [Deployment](#deployment)
10. [Contributing Guidelines](#contributing-guidelines)
11. [Resources](#resources)

---

## 🎯 Overview

**Hela Bojun** (හෙළ බොජුන්) is a Sri Lankan food-related application that aims to showcase traditional Sri Lankan cuisine, recipes, and culinary culture. The name "Hela Bojun" translates to "Sri Lankan Food" in Sinhala, reflecting the project's focus on preserving and sharing Sri Lankan culinary heritage.

### Project Goals

- **Preserve Cultural Heritage:** Document traditional Sri Lankan recipes and cooking methods
- **Community Engagement:** Connect food enthusiasts and home cooks
- **Educational Resource:** Teach authentic Sri Lankan cooking techniques
- **Digital Platform:** Provide easy access to Sri Lankan culinary knowledge

---

## 📊 Repository Information

### Key Details
- **Owner:** chamika-u
- **Repository:** hela-bojun
- **Primary Language:** To be determined based on tech stack
- **License:** Check repository for license information
- **Status:** Active development

### Quick Links
- **Repository:** https://github.com/chamika-u/hela-bojun
- **Issues:** https://github.com/chamika-u/hela-bojun/issues
- **Pull Requests:** https://github.com/chamika-u/hela-bojun/pulls

---

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

#### For Web Application (if applicable)
- **Node.js:** v16.0.0 or higher
- **npm/yarn:** Latest version
- **Git:** For version control

#### For Mobile Application (if applicable)
- **React Native CLI** or **Expo CLI**
- **Android Studio** (for Android development)
- **Xcode** (for iOS development, macOS only)

#### For Backend (if applicable)
- **Node.js** with Express.js, or
- **Python** with Django/Flask, or
- **Java** with Spring Boot

### Installation Steps

#### 1. Clone the Repository
```bash
git clone https://github.com/chamika-u/hela-bojun.git
cd hela-bojun
```

#### 2. Install Dependencies

**For Node.js/JavaScript projects:**
```bash
npm install
# or
yarn install
```

**For Python projects:**
```bash
pip install -r requirements.txt
# or
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt
```

**For Java projects:**
```bash
mvn clean install
# or
gradle build
```

#### 3. Configure Environment Variables

Create a `.env` file in the root directory:
```env
# Database Configuration
DATABASE_URL=your_database_url
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hela_bojun
DB_USER=your_username
DB_PASSWORD=your_password

# API Keys
API_KEY=your_api_key
SECRET_KEY=your_secret_key

# Application Settings
PORT=3000
NODE_ENV=development

# External Services (if applicable)
CLOUDINARY_URL=your_cloudinary_url
FIREBASE_CONFIG=your_firebase_config
```

#### 4. Initialize Database

```bash
# Run database migrations
npm run migrate
# or
python manage.py migrate
# or
mvn flyway:migrate
```

#### 5. Seed Sample Data (Optional)

```bash
npm run seed
# or
python manage.py loaddata fixtures/sample_data.json
```

#### 6. Start Development Server

```bash
npm run dev
# or
python manage.py runserver
# or
mvn spring-boot:run
```

The application should now be running at `http://localhost:3000` (or configured port).

---

## 🏗️ Project Structure

### Typical Structure (adjust based on actual project)

```
hela-bojun/
├── src/                    # Source code
│   ├── components/         # Reusable UI components
│   ├── pages/             # Application pages/views
│   ├── services/          # API services and business logic
│   ├── models/            # Data models
│   ├── utils/             # Utility functions
│   ├── config/            # Configuration files
│   └── assets/            # Static assets (images, fonts)
├── public/                # Public static files
├── tests/                 # Test files
│   ├── unit/             # Unit tests
│   ├── integration/      # Integration tests
│   └── e2e/              # End-to-end tests
├── docs/                  # Documentation
├── scripts/               # Build and deployment scripts
├── .env.example          # Environment variables template
├── .gitignore            # Git ignore rules
├── package.json          # Dependencies (Node.js)
├── requirements.txt      # Dependencies (Python)
├── pom.xml              # Dependencies (Java/Maven)
└── README.md            # Project documentation
```

### Key Directories

#### `/src/components/`
Reusable UI components such as:
- Recipe cards
- Navigation menus
- Search bars
- Image galleries
- Rating systems

#### `/src/pages/`
Main application pages:
- Home page
- Recipe listing
- Recipe details
- User profile
- Search results
- Category pages

#### `/src/services/`
Business logic and API integrations:
- Recipe service
- User authentication
- Image upload service
- Search functionality
- Rating and review system

#### `/src/models/`
Data models:
- Recipe model
- User model
- Category model
- Ingredient model
- Review model

---

## 💻 Technology Stack

### Frontend (Likely Technologies)

- **Framework:** React.js / Next.js / Vue.js / Angular
- **Styling:** CSS3, Sass, Tailwind CSS, or Material-UI
- **State Management:** Redux, Context API, or Zustand
- **Routing:** React Router or Next.js routing
- **HTTP Client:** Axios or Fetch API

### Backend (Possible Technologies)

- **Node.js:** Express.js, Nest.js
- **Python:** Django, Flask, FastAPI
- **Java:** Spring Boot
- **Database:** PostgreSQL, MongoDB, MySQL
- **ORM:** Sequelize, Mongoose, TypeORM, SQLAlchemy

### Additional Services

- **Authentication:** JWT, OAuth 2.0, Firebase Auth
- **File Storage:** AWS S3, Cloudinary, Firebase Storage
- **Search:** Elasticsearch, Algolia
- **Caching:** Redis
- **Email:** SendGrid, Mailgun

---

## 🔑 Key Features

### 1. Recipe Management

**Browse Recipes**
- View all Sri Lankan recipes
- Filter by category (rice dishes, curries, desserts, etc.)
- Search by ingredients or name
- Sort by popularity, rating, or date

**Recipe Details**
- Ingredients list with measurements
- Step-by-step cooking instructions
- Preparation and cooking time
- Difficulty level
- Nutritional information
- High-quality food images
- Video tutorials (if available)

**User Contributions**
- Submit new recipes
- Edit existing recipes (with moderation)
- Upload recipe photos
- Share cooking tips

### 2. User Features

**User Accounts**
- Registration and login
- Profile management
- Favorite recipes
- Recipe collections
- Cooking history

**Social Features**
- Rate and review recipes
- Comment on recipes
- Share recipes on social media
- Follow other users
- Recipe recommendations

### 3. Search and Discovery

**Advanced Search**
- Search by ingredients
- Filter by dietary restrictions (vegetarian, vegan, gluten-free)
- Filter by cooking time
- Filter by difficulty level
- Filter by region (coastal, hill country, etc.)

**Categories**
- Rice dishes (බත් වර්ග)
- Curries (කරි වර්ග)
- Sambols (සම්බෝල්)
- Desserts (අතුරුපස)
- Beverages (බීම වර්ග)
- Snacks (කෑම වර්ග)

### 4. Cultural Context

**Recipe Stories**
- Historical background
- Cultural significance
- Regional variations
- Traditional occasions
- Family traditions

**Cooking Techniques**
- Traditional methods
- Modern adaptations
- Kitchen equipment guide
- Ingredient substitutions

---

## 🧪 Testing Strategy

### Unit Tests

Test individual components and functions:

```javascript
// Example: Recipe component test
describe('RecipeCard', () => {
  it('should render recipe title', () => {
    const recipe = { title: 'Kottu Roti', rating: 4.5 };
    const { getByText } = render(<RecipeCard recipe={recipe} />);
    expect(getByText('Kottu Roti')).toBeInTheDocument();
  });

  it('should display correct rating', () => {
    const recipe = { title: 'Kottu Roti', rating: 4.5 };
    const { getByText } = render(<RecipeCard recipe={recipe} />);
    expect(getByText('4.5')).toBeInTheDocument();
  });
});
```

### Integration Tests

Test component interactions:

```javascript
// Example: Recipe submission flow
describe('Recipe Submission', () => {
  it('should submit recipe successfully', async () => {
    const { getByLabelText, getByText } = render(<RecipeForm />);
    
    fireEvent.change(getByLabelText('Recipe Title'), {
      target: { value: 'Pol Sambol' }
    });
    
    fireEvent.click(getByText('Submit'));
    
    await waitFor(() => {
      expect(getByText('Recipe submitted successfully')).toBeInTheDocument();
    });
  });
});
```

### E2E Tests

Test complete user workflows:

```javascript
// Example: Cypress E2E test
describe('Recipe Search', () => {
  it('should search and view recipe', () => {
    cy.visit('/');
    cy.get('[data-testid="search-input"]').type('Kottu');
    cy.get('[data-testid="search-button"]').click();
    cy.contains('Kottu Roti').click();
    cy.url().should('include', '/recipe/');
    cy.contains('Ingredients').should('be.visible');
  });
});
```

### Running Tests

```bash
# Run all tests
npm test

# Run tests in watch mode
npm test -- --watch

# Run tests with coverage
npm test -- --coverage

# Run E2E tests
npm run test:e2e

# Run specific test file
npm test -- RecipeCard.test.js
```

---

## 🚀 Deployment

### Environment Setup

#### Development
```bash
npm run dev
```

#### Staging
```bash
npm run build:staging
npm run start:staging
```

#### Production
```bash
npm run build
npm run start
```

### Deployment Platforms

#### Vercel (for Next.js/React)
```bash
# Install Vercel CLI
npm i -g vercel

# Deploy
vercel --prod
```

#### Heroku
```bash
# Login to Heroku
heroku login

# Create app
heroku create hela-bojun

# Deploy
git push heroku main

# Set environment variables
heroku config:set DATABASE_URL=your_database_url
```

#### AWS / DigitalOcean / Google Cloud
- Set up CI/CD pipeline
- Configure auto-scaling
- Set up load balancer
- Configure CDN for static assets

### Database Migration

```bash
# Production migration
npm run migrate:prod

# Rollback if needed
npm run migrate:rollback
```

---

## 🤝 Contributing Guidelines

### How to Contribute

1. **Fork the Repository**
   ```bash
   # Click "Fork" on GitHub
   git clone https://github.com/YOUR_USERNAME/hela-bojun.git
   ```

2. **Create a Feature Branch**
   ```bash
   git checkout -b feature/add-new-recipe-category
   ```

3. **Make Your Changes**
   - Write clean, readable code
   - Follow project coding standards
   - Add tests for new features
   - Update documentation

4. **Commit Your Changes**
   ```bash
   git add .
   git commit -m "feat: add desserts category"
   ```

   **Commit Message Format:**
   - `feat:` New feature
   - `fix:` Bug fix
   - `docs:` Documentation changes
   - `style:` Code style changes
   - `refactor:` Code refactoring
   - `test:` Test additions/changes
   - `chore:` Build process or auxiliary tool changes

5. **Push to Your Fork**
   ```bash
   git push origin feature/add-new-recipe-category
   ```

6. **Create Pull Request**
   - Go to the original repository
   - Click "New Pull Request"
   - Describe your changes
   - Link related issues

### Code Style Guidelines

#### JavaScript/TypeScript
```javascript
// Use meaningful variable names
const recipeTitle = 'Kottu Roti'; // Good
const rt = 'Kottu Roti'; // Bad

// Use arrow functions for callbacks
recipes.map(recipe => recipe.title); // Good
recipes.map(function(recipe) { return recipe.title; }); // Avoid

// Use async/await over promises
async function fetchRecipes() {
  const response = await api.get('/recipes');
  return response.data;
}
```

#### Python
```python
# Follow PEP 8 style guide
def get_recipe_by_id(recipe_id):
    """Fetch recipe by ID from database."""
    return Recipe.objects.get(id=recipe_id)

# Use type hints
def calculate_cooking_time(prep_time: int, cook_time: int) -> int:
    return prep_time + cook_time
```

### Pull Request Checklist

- [ ] Code follows project style guidelines
- [ ] Tests added for new features
- [ ] All tests passing
- [ ] Documentation updated
- [ ] No console errors or warnings
- [ ] Responsive design tested
- [ ] Accessibility standards met
- [ ] Performance optimized

---

## 📚 Resources

### Sri Lankan Cuisine References

- **Traditional Recipes:** Research authentic Sri Lankan recipes
- **Ingredient Guide:** Learn about local ingredients and spices
- **Cooking Techniques:** Understand traditional cooking methods
- **Cultural Context:** Study the cultural significance of dishes

### Development Resources

#### Documentation
- **React:** https://react.dev
- **Next.js:** https://nextjs.org/docs
- **Node.js:** https://nodejs.org/docs
- **Express:** https://expressjs.com

#### Design Resources
- **UI/UX:** Figma, Adobe XD
- **Icons:** Font Awesome, Material Icons
- **Images:** Unsplash, Pexels
- **Fonts:** Google Fonts

#### Learning Resources
- **Frontend Masters:** Advanced web development courses
- **Udemy:** Comprehensive tutorials
- **YouTube:** Free video tutorials
- **MDN Web Docs:** Web technology reference

### Community

- **GitHub Discussions:** Ask questions and share ideas
- **Discord/Slack:** Join the community chat
- **Stack Overflow:** Tag questions with relevant technologies
- **Twitter:** Follow project updates

---

## 🎓 Development Best Practices

### Code Quality

1. **Write Clean Code**
   - Use meaningful names
   - Keep functions small and focused
   - Avoid code duplication (DRY principle)
   - Comment complex logic

2. **Follow SOLID Principles**
   - Single Responsibility
   - Open/Closed
   - Liskov Substitution
   - Interface Segregation
   - Dependency Inversion

3. **Error Handling**
   ```javascript
   try {
     const recipe = await fetchRecipe(id);
     return recipe;
   } catch (error) {
     console.error('Failed to fetch recipe:', error);
     throw new Error('Recipe not found');
   }
   ```

### Performance Optimization

1. **Image Optimization**
   - Use WebP format
   - Implement lazy loading
   - Use responsive images
   - Compress images

2. **Code Splitting**
   ```javascript
   // Lazy load components
   const RecipeDetails = lazy(() => import('./RecipeDetails'));
   ```

3. **Caching**
   - Implement Redis caching
   - Use browser caching
   - Cache API responses

### Security

1. **Input Validation**
   - Sanitize user input
   - Validate on both client and server
   - Prevent SQL injection
   - Prevent XSS attacks

2. **Authentication**
   - Use secure password hashing (bcrypt)
   - Implement JWT tokens
   - Use HTTPS
   - Implement rate limiting

3. **Data Protection**
   - Encrypt sensitive data
   - Use environment variables
   - Never commit secrets to Git
   - Implement CORS properly

---

## 🌟 Feature Roadmap

### Phase 1: MVP (Minimum Viable Product)
- [ ] Basic recipe listing
- [ ] Recipe details page
- [ ] User authentication
- [ ] Search functionality
- [ ] Basic categories

### Phase 2: Enhanced Features
- [ ] User profiles
- [ ] Recipe ratings and reviews
- [ ] Favorite recipes
- [ ] Recipe collections
- [ ] Social sharing

### Phase 3: Advanced Features
- [ ] Video tutorials
- [ ] Meal planning
- [ ] Shopping lists
- [ ] Nutritional calculator
- [ ] Recipe recommendations

### Phase 4: Community Features
- [ ] User-generated content
- [ ] Cooking challenges
- [ ] Community forums
- [ ] Live cooking sessions
- [ ] Mobile app

---

## 🐛 Troubleshooting

### Common Issues

#### Issue: Dependencies not installing
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and package-lock.json
rm -rf node_modules package-lock.json

# Reinstall
npm install
```

#### Issue: Database connection failed
- Check database credentials in `.env`
- Ensure database server is running
- Verify network connectivity
- Check firewall settings

#### Issue: Port already in use
```bash
# Find process using port 3000
lsof -i :3000  # macOS/Linux
netstat -ano | findstr :3000  # Windows

# Kill the process
kill -9 <PID>  # macOS/Linux
taskkill /PID <PID> /F  # Windows
```

#### Issue: Build fails
- Check for syntax errors
- Verify all dependencies are installed
- Clear build cache
- Check Node.js version compatibility

---

## 📞 Support

### Getting Help

1. **Check Documentation:** Review this guide and project README
2. **Search Issues:** Look for similar problems in GitHub Issues
3. **Ask Questions:** Create a new issue with detailed information
4. **Community Chat:** Join Discord/Slack for real-time help
5. **Contact Maintainers:** Reach out to project maintainers

### Reporting Bugs

When reporting bugs, include:
- Clear description of the issue
- Steps to reproduce
- Expected vs actual behavior
- Screenshots or error messages
- Environment details (OS, browser, versions)
- Relevant code snippets

---

## 🎉 Welcome to Hela Bojun!

Thank you for contributing to preserving and sharing Sri Lankan culinary heritage through technology. Your contributions help make traditional recipes accessible to everyone, ensuring that these cultural treasures are passed down to future generations.

**Happy Coding! 🍛🥘🍜**

---

*Generated by Bob - Your AI Development Assistant*
*Last Updated: 2026-04-30*