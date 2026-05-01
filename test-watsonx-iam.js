const axios = require('axios');
require('dotenv').config();

console.log('='.repeat(60));
console.log('WatsonX IAM Token Authentication Test');
console.log('='.repeat(60));

async function getIAMToken(apiKey) {
    console.log('\n🔑 Step 1: Getting IAM Token from IBM Cloud...\n');
    
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
        
        console.log('✅ Successfully obtained IAM token!');
        console.log('  Token type:', response.data.token_type);
        console.log('  Expires in:', response.data.expires_in, 'seconds');
        console.log('  Access token length:', response.data.access_token.length);
        
        return response.data.access_token;
        
    } catch (error) {
        console.error('❌ Failed to get IAM token');
        if (error.response) {
            console.error('  Status:', error.response.status);
            console.error('  Error:', JSON.stringify(error.response.data, null, 2));
        } else {
            console.error('  Error:', error.message);
        }
        throw error;
    }
}

async function testWatsonXWithIAM() {
    const apiKey = process.env.WATSONX_API_KEY;
    
    if (!apiKey) {
        console.error('❌ WATSONX_API_KEY not found in .env file');
        return;
    }
    
    console.log('📋 Configuration:');
    console.log('  API Key:', apiKey.substring(0, 10) + '...');
    console.log('  Project ID:', process.env.WATSONX_PROJECT_ID);
    console.log('  URL:', process.env.WATSONX_URL);
    
    try {
        // Step 1: Get IAM token
        const iamToken = await getIAMToken(apiKey);
        
        // Step 2: Test WatsonX with IAM token
        console.log('\n🤖 Step 2: Testing WatsonX API with IAM token...\n');
        
        const endpoint = `${process.env.WATSONX_URL}/ml/v1/text/generation?version=2023-05-29`;
        
        const response = await axios.post(
            endpoint,
            {
                input: "Hello! Please respond with a brief greeting.",
                model_id: 'ibm/granite-13b-chat-v2',
                project_id: process.env.WATSONX_PROJECT_ID,
                parameters: {
                    max_new_tokens: 100,
                    temperature: 0.7,
                    top_p: 0.9,
                    top_k: 50
                }
            },
            {
                headers: {
                    'Authorization': `Bearer ${iamToken}`,
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                timeout: 30000
            }
        );
        
        console.log('✅ SUCCESS! WatsonX API is working with IAM token!\n');
        console.log('Response Status:', response.status);
        
        if (response.data.results && response.data.results[0]) {
            console.log('\n📝 Generated Text:');
            console.log('  "' + response.data.results[0].generated_text + '"');
        }
        
        console.log('\n' + '='.repeat(60));
        console.log('✅ WatsonX Integration: WORKING');
        console.log('='.repeat(60));
        console.log('\n💡 Solution: You need to get IAM token first!');
        console.log('   The API key must be exchanged for an IAM token.');
        
    } catch (error) {
        console.error('\n❌ Error:', error.message);
        if (error.response) {
            console.error('Status:', error.response.status);
            console.error('Details:', JSON.stringify(error.response.data, null, 2));
        }
        
        console.log('\n' + '='.repeat(60));
        console.log('❌ WatsonX Integration: FAILED');
        console.log('='.repeat(60));
    }
}

testWatsonXWithIAM().catch(err => {
    console.error('Unexpected error:', err);
    process.exit(1);
});

// Made with Bob
