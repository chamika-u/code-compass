# GitHub Personal Access Token Setup Guide

## Why You Need a Token

To access **private repositories** and increase API rate limits, you need a GitHub Personal Access Token (PAT).

### Benefits:
- ✅ Access private repositories
- ✅ Increased rate limit: 5,000 requests/hour (vs 60 without token)
- ✅ Access to all your repositories

---

## Step-by-Step Guide

### 1. Go to GitHub Token Settings

Open this URL in your browser:
```
https://github.com/settings/tokens
```

Or navigate manually:
1. Click your profile picture (top right)
2. Go to **Settings**
3. Scroll down to **Developer settings** (left sidebar)
4. Click **Personal access tokens** → **Tokens (classic)**

### 2. Generate New Token

1. Click **"Generate new token"** → **"Generate new token (classic)"**
2. Give it a descriptive name (e.g., "Code Compass Documentation Generator")
3. Set expiration (recommended: 90 days or custom)

### 3. Select Required Permissions

For this application, you need these scopes:

**Required:**
- ✅ `repo` - Full control of private repositories
  - This includes: `repo:status`, `repo_deployment`, `public_repo`, `repo:invite`, `security_events`

**Optional (for enhanced features):**
- ✅ `read:org` - Read organization data
- ✅ `read:user` - Read user profile data

### 4. Generate and Copy Token

1. Scroll down and click **"Generate token"**
2. **IMPORTANT:** Copy the token immediately (starts with `ghp_`)
3. You won't be able to see it again!

Example token format: `ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`

### 5. Add Token to Your Application

#### Option A: Update .env file

1. Open the `.env` file in your project root
2. Find the line with `# GITHUB_TOKEN=`
3. Replace it with:
   ```
   GITHUB_TOKEN=ghp_your_actual_token_here
   ```

#### Option B: Use the command below

```bash
# Windows PowerShell
(Get-Content .env) -replace '# GITHUB_TOKEN=', 'GITHUB_TOKEN=ghp_your_actual_token_here' | Set-Content .env
```

### 6. Restart the Server

After adding the token:

1. Stop the current server (Ctrl+C in terminal)
2. Restart with: `npm start`
3. You should see: `🔑 GitHub token configured: true`

---

## Security Best Practices

### ⚠️ IMPORTANT Security Notes:

1. **Never commit tokens to Git**
   - The `.env` file is already in `.gitignore`
   - Never share your token publicly

2. **Token Expiration**
   - Set reasonable expiration dates
   - Rotate tokens regularly

3. **Minimal Permissions**
   - Only grant necessary scopes
   - For public repos only, you can use fewer permissions

4. **Revoke Compromised Tokens**
   - If exposed, immediately revoke at: https://github.com/settings/tokens
   - Generate a new one

---

## Troubleshooting

### Token Not Working (401 Error)

**Possible causes:**
1. Token expired
2. Token revoked
3. Insufficient permissions
4. Token not properly set in `.env`

**Solution:**
1. Verify token in `.env` file (no spaces, no quotes)
2. Check token hasn't expired: https://github.com/settings/tokens
3. Regenerate if needed

### Still Getting Rate Limited

- Verify token is loaded: Check server startup message
- Ensure token has `repo` scope
- Check remaining rate limit:
  ```bash
  curl -H "Authorization: token YOUR_TOKEN" https://api.github.com/rate_limit
  ```

---

## Quick Setup Commands

### Complete Setup (Windows PowerShell)

```powershell
# 1. Open .env file
notepad .env

# 2. Add your token (replace with your actual token)
# GITHUB_TOKEN=ghp_your_actual_token_here

# 3. Restart server
npm start
```

---

## Testing Your Token

After setup, test with a private repository:

1. Go to http://localhost:3000
2. Enter a private repository URL
3. Click "Generate Documentation"
4. Should work without 401 errors!

---

## Additional Resources

- [GitHub PAT Documentation](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)
- [GitHub API Rate Limits](https://docs.github.com/en/rest/overview/resources-in-the-rest-api#rate-limiting)
- [Token Security Best Practices](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/token-expiration-and-revocation)

---

**Need Help?** Check the server logs for specific error messages or consult the GitHub API documentation.