const axios = require('axios');
require('dotenv').config();

console.log('='.repeat(70));
console.log('🎉 WATSONX DEPLOYMENT TEST');
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

async function testDeployment() {
    console.log('\n📋 Configuration:');
    console.log('  Deployment ID: 019de0f4-4798-769e-af21-90d136794712');
    console.log('  URL:', process.env.WATSONX_URL);
    
    try {
        console.log('\n🔑 Getting IAM Token...');
        const iamToken = await getIAMToken(process.env.WATSONX_API_KEY);
        console.log('✅ IAM token obtained');
        
        console.log('\n🤖 Calling WatsonX Deployment...');
        
        const deploymentId = '019de0f4-4798-769e-af21-90d136794712';
        const endpoint = `${process.env.WATSONX_URL}/ml/v1/deployments/${deploymentId}/text/generation?version=2023-05-29`;
        
        console.log('  Endpoint:', endpoint);
        
        const response = await axios.post(
            endpoint,
            {
                input: "Generate a brief welcome message for a developer onboarding guide.",
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
                timeout: 60000
            }
        );
        
        console.log('\n' + '='.repeat(70));
        console.log('🎉🎉🎉 SUCCESS! WATSONX AI IS WORKING! 🎉🎉🎉');
        console.log('='.repeat(70));
        
        console.log('\n📊 Response:');
        console.log('  Status:', response.status);
        
        if (response.data.results && response.data.results[0]) {
            const text = response.data.results[0].generated_text;
            console.log('\n📝 AI Generated Text:');
            console.log('─'.repeat(70));
            console.log(text);
            console.log('─'.repeat(70));
        }
        
        console.log('\n' + '='.repeat(70));
        console.log('✅ WATSONX AI: FULLY OPERATIONAL!');
        console.log('='.repeat(70));
        console.log('\n🚀 Your application is now powered by IBM WatsonX AI!');
        console.log('🌐 Visit http://localhost:3000 to try it!');
        
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

testDeployment().catch(console.error);

// Made with Bob
