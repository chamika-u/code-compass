const axios = require('axios');
require('dotenv').config();

console.log('='.repeat(70));
console.log('🎉 FINAL WATSONX TEST - ibm-granite/granite-4.0-h-small');
console.log('='.repeat(70));

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
    console.log('  Project ID:', process.env.WATSONX_PROJECT_ID);
    console.log('  URL:', process.env.WATSONX_URL);
    console.log('  Model: ibm-granite/granite-4.0-h-small');
    
    try {
        console.log('\n🔑 Step 1: Getting IAM Token...');
        const iamToken = await getIAMToken(process.env.WATSONX_API_KEY);
        console.log('✅ IAM token obtained successfully');
        
        console.log('\n🤖 Step 2: Calling WatsonX AI...');
        
        const endpoint = `${process.env.WATSONX_URL}/ml/v1/text/generation?version=2023-05-29`;
        
        const testPrompt = `Generate a brief welcome message for a GitHub repository documentation guide. 
Keep it professional and encouraging for new developers.`;
        
        const response = await axios.post(
            endpoint,
            {
                input: testPrompt,
                model_id: 'ibm-granite/granite-4.0-h-small',
                project_id: process.env.WATSONX_PROJECT_ID,
                parameters: {
                    max_new_tokens: 300,
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
                timeout: 60000
            }
        );
        
        console.log('\n' + '='.repeat(70));
        console.log('🎉🎉🎉 SUCCESS! WATSONX AI IS WORKING! 🎉🎉🎉');
        console.log('='.repeat(70));
        
        console.log('\n📊 Response Details:');
        console.log('  Status:', response.status);
        console.log('  Model:', response.data.model_id);
        console.log('  Created:', response.data.created_at);
        
        if (response.data.results && response.data.results[0]) {
            const generatedText = response.data.results[0].generated_text;
            console.log('\n📝 AI Generated Text:');
            console.log('─'.repeat(70));
            console.log(generatedText);
            console.log('─'.repeat(70));
            console.log(`\n  Characters: ${generatedText.length}`);
            console.log(`  Tokens used: ${response.data.results[0].generated_token_count || 'N/A'}`);
        }
        
        console.log('\n' + '='.repeat(70));
        console.log('✅ WATSONX AI INTEGRATION: FULLY OPERATIONAL');
        console.log('='.repeat(70));
        console.log('\n🚀 Your application is now powered by IBM WatsonX AI!');
        console.log('🌐 Visit http://localhost:3000 to generate AI-powered documentation!');
        console.log('\n💡 The system will now use AI instead of templates for documentation generation.');
        
    } catch (error) {
        console.error('\n❌ Error:', error.message);
        if (error.response) {
            console.error('  Status:', error.response.status);
            console.error('  Details:', JSON.stringify(error.response.data, null, 2));
        }
        console.log('\n' + '='.repeat(70));
        console.log('❌ Test Failed');
        console.log('='.repeat(70));
    }
}

testWatsonX().catch(err => {
    console.error('Unexpected error:', err);
    process.exit(1);
});

// Made with Bob
