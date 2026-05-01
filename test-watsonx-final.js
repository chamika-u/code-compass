const axios = require('axios');
require('dotenv').config();

console.log('='.repeat(60));
console.log('WatsonX Final Test - granite-4-h-small');
console.log('='.repeat(60));

async function getIAMToken(apiKey) {
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
}

async function testWatsonX() {
    console.log('\n📋 Configuration:');
    console.log('  API Key:', process.env.WATSONX_API_KEY.substring(0, 10) + '...');
    console.log('  Project ID:', process.env.WATSONX_PROJECT_ID);
    console.log('  URL:', process.env.WATSONX_URL);
    console.log('  Model: granite-4-h-small');
    
    try {
        console.log('\n🔑 Step 1: Getting IAM Token...');
        const iamToken = await getIAMToken(process.env.WATSONX_API_KEY);
        console.log('✅ IAM token obtained successfully\n');
        
        console.log('🤖 Step 2: Testing WatsonX with granite-4-h-small...');
        
        const endpoint = `${process.env.WATSONX_URL}/ml/v1/text/generation?version=2023-05-29`;
        
        const response = await axios.post(
            endpoint,
            {
                input: "Generate a brief welcome message for a developer onboarding guide.",
                model_id: 'granite-4-h-small',
                project_id: process.env.WATSONX_PROJECT_ID,
                parameters: {
                    max_new_tokens: 200,
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
        
        console.log('✅ SUCCESS! WatsonX AI is working!\n');
        console.log('Response Status:', response.status);
        console.log('Model Used:', response.data.model_id);
        
        if (response.data.results && response.data.results[0]) {
            console.log('\n📝 Generated Text:');
            console.log('─'.repeat(60));
            console.log(response.data.results[0].generated_text);
            console.log('─'.repeat(60));
        }
        
        console.log('\n' + '='.repeat(60));
        console.log('🎉 WatsonX AI Integration: FULLY WORKING!');
        console.log('='.repeat(60));
        console.log('\n✅ The system is now ready to generate AI-powered documentation!');
        console.log('✅ Visit http://localhost:3000 to try it out!');
        
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

testWatsonX().catch(err => {
    console.error('Unexpected error:', err);
    process.exit(1);
});

// Made with Bob
