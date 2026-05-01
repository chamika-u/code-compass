# WatsonX WML Instance Setup Guide

## 🚨 Current Issue

**Error**: "project_id is not associated with a WML instance"

**What this means**: Your WatsonX project exists, but it's not connected to a Watson Machine Learning (WML) service instance, which is required to use the AI models.

---

## 🔍 Why WML Won't Create

There are several common reasons why WML instance creation might fail:

### 1. **Account Limitations**

**Lite/Free Tier Restrictions:**
- IBM Cloud Lite accounts have limitations
- May only allow one WML instance per account
- Some regions may not support Lite tier WML

**Check your account:**
```
1. Go to: https://cloud.ibm.com/account
2. Check: Account Type (Lite, Pay-As-You-Go, Subscription)
3. View: Resource limits and quotas
```

### 2. **Regional Availability**

**WML is not available in all regions:**
- ✅ Supported: `us-south`, `eu-de`, `eu-gb`, `jp-tok`
- ❌ Limited: Some regions don't support all features

**Your current region**: `eu-de` (Frankfurt) - Should be supported

### 3. **Existing Instance Limit**

**You may already have a WML instance:**
```
1. Go to: https://cloud.ibm.com/resources
2. Filter by: "Watson Machine Learning"
3. Check if instance already exists
```

### 4. **Billing/Payment Issues**

**Common causes:**
- Credit card not verified
- Payment method expired
- Billing account suspended
- Free tier quota exceeded

### 5. **Service Catalog Issues**

**WML might not be visible:**
- Service temporarily unavailable
- Region-specific catalog issues
- Account permissions problem

---

## ✅ Solutions & Workarounds

### Solution 1: Use Existing WML Instance

If you already have a WML instance:

1. **Find your WML instance:**
   ```
   https://cloud.ibm.com/resources
   → Filter: "Watson Machine Learning"
   ```

2. **Get the instance details:**
   - Click on the instance
   - Copy the **Instance ID** or **GUID**

3. **Associate with your project:**
   ```
   https://dataplatform.cloud.ibm.com/projects
   → Open your project
   → Manage → Services
   → Associate existing service
   → Select your WML instance
   ```

### Solution 2: Create WML Instance via CLI

Sometimes the UI fails but CLI works:

```bash
# Install IBM Cloud CLI
# Download from: https://cloud.ibm.com/docs/cli

# Login
ibmcloud login

# Target your region
ibmcloud target -r eu-de

# Create WML instance
ibmcloud resource service-instance-create my-wml-instance \
  pm-20 lite eu-de

# List instances to verify
ibmcloud resource service-instances
```

### Solution 3: Create New Project with WML

Instead of adding WML to existing project, create new project:

1. **Go to WatsonX Projects:**
   ```
   https://dataplatform.cloud.ibm.com/projects
   ```

2. **Create New Project:**
   - Click "New project"
   - Choose "Create an empty project"
   - Name: "Documentation Generator"
   - **Important**: Check "Associate a Watson Machine Learning service"

3. **During creation:**
   - Select existing WML instance, OR
   - Create new WML instance (if available)

4. **Get new Project ID:**
   - Open project
   - Settings → General
   - Copy Project ID
   - Update `.env` file

### Solution 4: Use Different Region

If `eu-de` has issues, try another region:

1. **Create project in different region:**
   - `us-south` (Dallas) - Most stable
   - `eu-gb` (London)
   - `jp-tok` (Tokyo)

2. **Update `.env` file:**
   ```
   WATSONX_URL=https://us-south.ml.cloud.ibm.com
   ```

### Solution 5: Upgrade Account

If on Lite tier with limitations:

1. **Upgrade to Pay-As-You-Go:**
   ```
   https://cloud.ibm.com/account/settings
   → Upgrade account
   ```

2. **Benefits:**
   - No resource limits
   - All regions available
   - Better support
   - More WML instances

---

## 🔧 Step-by-Step: Complete Setup

### Method A: Fresh Start (Recommended)

1. **Delete old project** (if problematic):
   ```
   https://dataplatform.cloud.ibm.com/projects
   → Select project → Delete
   ```

2. **Create WML instance first:**
   ```
   https://cloud.ibm.com/catalog/services/watson-machine-learning
   → Select Lite plan
   → Region: us-south (recommended)
   → Create
   ```

3. **Create new WatsonX project:**
   ```
   https://dataplatform.cloud.ibm.com/projects
   → New project
   → Associate the WML instance you just created
   ```

4. **Get credentials:**
   - Project ID from project settings
   - API key from IBM Cloud (already have)
   - Update `.env` file

### Method B: Fix Existing Project

1. **Check WML instances:**
   ```
   https://cloud.ibm.com/resources
   → Filter: "Watson Machine Learning"
   ```

2. **If WML exists:**
   ```
   → Go to your WatsonX project
   → Manage → Services
   → Associate existing service
   → Select WML instance
   ```

3. **If no WML exists:**
   ```
   → Create WML instance first (see Method A, step 2)
   → Then associate with project
   ```

---

## 🧪 Verification Steps

After setup, verify everything works:

### 1. Check WML Association

```bash
# Run the test script
node test-watsonx-iam.js
```

**Expected output:**
```
✅ Successfully obtained IAM token!
✅ SUCCESS! WatsonX API is working!
📝 Generated Text: "..."
```

### 2. Test in Application

1. Open: http://localhost:3000
2. Enter any GitHub repo URL
3. Click "Generate Documentation"
4. Check terminal for:
   ```
   Getting IAM token for WatsonX...
   IAM token obtained successfully
   Generated documentation (X characters)
   ```

### 3. Check Logs

Look for these messages (not errors):
- ✅ "Getting IAM token for WatsonX..."
- ✅ "IAM token obtained successfully"
- ✅ "Generated documentation"
- ❌ "Error calling WatsonX" (should not appear)

---

## 📋 Troubleshooting Checklist

- [ ] IBM Cloud account is active
- [ ] Payment method is valid (if not Lite)
- [ ] WML service is available in your region
- [ ] WML instance exists in your account
- [ ] WML instance is associated with project
- [ ] Project ID is correct UUID format
- [ ] API key is valid and not expired
- [ ] IAM token generation works
- [ ] No billing/quota issues

---

## 🆘 Common Error Messages

### Error: "no_associated_service_instance_error"
**Cause**: Project not linked to WML instance
**Fix**: Associate WML instance with project (see Solution 1)

### Error: "container_not_found"
**Cause**: Invalid project ID
**Fix**: Get correct project ID from project settings

### Error: "authentication_token_not_valid"
**Cause**: API key expired or invalid
**Fix**: Generate new API key from IBM Cloud

### Error: "insufficient_permissions"
**Cause**: API key doesn't have access to project
**Fix**: Ensure API key has Editor/Admin role on project

### Error: "rate_limit_exceeded"
**Cause**: Too many API calls
**Fix**: Wait a few minutes, or upgrade account

---

## 💡 Alternative: Use Template Generation

While fixing WML setup, the system works with template generation:

**Current behavior:**
```javascript
// server.js automatically falls back
try {
    // Try WatsonX AI
    return await generateWithWatsonX(...);
} catch (error) {
    // Falls back to template
    return generateTemplateDocumentation(...);
}
```

**Template generation provides:**
- ✅ Fast generation (5-10 seconds)
- ✅ Good quality documentation
- ✅ All standard sections included
- ❌ Not AI-customized
- ❌ Less context-aware

---

## 📞 Getting Help

### IBM Cloud Support

1. **Free tier**: Community forums
   - https://community.ibm.com/community/user/watsonai/communities

2. **Paid accounts**: Open support ticket
   - https://cloud.ibm.com/unifiedsupport/cases/add

### Documentation

- **WatsonX**: https://www.ibm.com/docs/en/watsonx-as-a-service
- **WML**: https://cloud.ibm.com/docs/watson-machine-learning
- **Projects**: https://dataplatform.cloud.ibm.com/docs/content/wsj/getting-started/projects.html

### Check Status

- **IBM Cloud Status**: https://cloud.ibm.com/status
- **WatsonX Status**: Check for service disruptions

---

## 🎯 Quick Fix Summary

**If you just want it working NOW:**

1. Create new WML instance: https://cloud.ibm.com/catalog/services/watson-machine-learning
2. Create new WatsonX project with WML: https://dataplatform.cloud.ibm.com/projects
3. Copy new Project ID
4. Update `.env`: `WATSONX_PROJECT_ID=new-project-id-here`
5. Restart server: `npm start`
6. Test: `node test-watsonx-iam.js`

**Total time**: 5-10 minutes

---

**Last Updated**: 2026-04-30
**Status**: WML association required for AI generation