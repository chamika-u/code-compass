const axios = require('axios');
require('dotenv').config();

console.log('='.repeat(60));
console.log('WatsonX API Integration Test');
console.log('='.repeat(60));

async function testWatsonX() {
    console.log('\n📋 Configuration Check:');
    console.log('  WATSONX_URL:', process.env.WATSONX_URL || '❌ Not set');
    console.log('  WATSONX_PROJECT_ID:', process.env.WATSONX_PROJECT_ID || '❌ Not set');
    console.log('  WATSONX_API_KEY:', process.env.WATSONX_API_KEY ? '✅ Set (length: ' + process.env.WATSONX_API_KEY.length + ')' : '❌ Not set');
    
    if (!process.env.WATSONX_API_KEY || !process.env.WATSONX_PROJECT_ID || !process.env.WATSONX_URL) {
        console.error('\n❌ Missing required environment variables!');
        console.log('\nPlease ensure .env file contains:');
        console.log('  WATSONX_API_KEY=your_api_key');
        console.log('  WATSONX_PROJECT_ID=your_project_id');
        console.log('  WATSONX_URL=https://eu-de.ml.cloud.ibm.com');
        return;
    }

    console.log('\n🔄 Testing WatsonX API connection...\n');

    try {
        const endpoint = `${process.env.WATSONX_URL}/ml/v1/text/generation?version=2023-05-29`;
        console.log('  Endpoint:', endpoint);
        
        const requestBody = {
            input: "Hello! Please respond with a brief greeting.",
            model_id: 'ibm/granite-13b-chat-v2',
            project_id: process.env.WATSONX_PROJECT_ID,
            parameters: {
                max_new_tokens: 100,
                temperature: 0.7,
                top_p: 0.9,
                top_k: 50
            }
        };

        console.log('  Model:', requestBody.model_id);
        console.log('  Project ID:', requestBody.project_id);
        console.log('  Sending request...\n');

        const response = await axios.post(
            endpoint,
            requestBody,
            {
                headers: {
                    'Authorization': `Bearer ${process.env.WATSONX_API_KEY}`,
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                timeout: 30000 // 30 second timeout
            }
        );
        
        console.log('✅ SUCCESS! WatsonX API is working!\n');
        console.log('Response Status:', response.status);
        console.log('Response Data:', JSON.stringify(response.data, null, 2));
        
        if (response.data.results && response.data.results[0]) {
            console.log('\n📝 Generated Text:');
            console.log('  "' + response.data.results[0].generated_text + '"');
        }

        console.log('\n' + '='.repeat(60));
        console.log('✅ WatsonX Integration: WORKING');
        console.log('='.repeat(60));
        
    } catch (error) {
        console.error('❌ FAILED! WatsonX API error:\n');
        
        if (error.response) {
            console.error('  Status Code:', error.response.status);
            console.error('  Status Text:', error.response.statusText);
            console.error('  Error Details:', JSON.stringify(error.response.data, null, 2));
            
            if (error.response.status === 401) {
                console.error('\n⚠️  Authentication Error (401 Unauthorized)');
                console.error('  Possible causes:');
                console.error('    1. API key is expired');
                console.error('    2. API key is invalid');
                console.error('    3. API key doesn\'t have access to this project');
                console.error('\n  Solution:');
                console.error('    1. Go to: https://cloud.ibm.com/');
                console.error('    2. Navigate to: Resource list → AI / Machine Learning → WatsonX');
                console.error('    3. Generate a new API key');
                console.error('    4. Update WATSONX_API_KEY in .env file');
            } else if (error.response.status === 404) {
                console.error('\n⚠️  Not Found Error (404)');
                console.error('  Possible causes:');
                console.error('    1. Invalid project ID');
                console.error('    2. Invalid model ID');
                console.error('    3. Wrong WatsonX URL region');
            } else if (error.response.status === 429) {
                console.error('\n⚠️  Rate Limit Error (429)');
                console.error('  You have exceeded the API rate limit.');
                console.error('  Please wait a few minutes and try again.');
            }
        } else if (error.request) {
            console.error('  No response received from server');
            console.error('  Error:', error.message);
            console.error('\n  Possible causes:');
            console.error('    1. Network connectivity issues');
            console.error('    2. Invalid WATSONX_URL');
            console.error('    3. Firewall blocking the request');
        } else {
            console.error('  Error:', error.message);
        }

        console.log('\n' + '='.repeat(60));
        console.log('❌ WatsonX Integration: NOT WORKING');
        console.log('='.repeat(60));
        console.log('\nThe system will fall back to template generation.');
    }
}

// Run the test
testWatsonX().catch(err => {
    console.error('Unexpected error:', err);
    process.exit(1);
});

// Made with Bob
