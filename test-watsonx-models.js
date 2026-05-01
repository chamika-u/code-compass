const axios = require('axios');
require('dotenv').config();

console.log('='.repeat(60));
console.log('WatsonX Available Models Test');
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

async function testModel(iamToken, modelId) {
    try {
        const endpoint = `${process.env.WATSONX_URL}/ml/v1/text/generation?version=2023-05-29`;
        
        const response = await axios.post(
            endpoint,
            {
                input: "Hello! Please respond with a brief greeting.",
                model_id: modelId,
                project_id: process.env.WATSONX_PROJECT_ID,
                parameters: {
                    max_new_tokens: 50,
                    temperature: 0.7
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
        
        console.log(`✅ ${modelId}: WORKING`);
        if (response.data.results && response.data.results[0]) {
            console.log(`   Response: "${response.data.results[0].generated_text.substring(0, 100)}..."`);
        }
        return true;
        
    } catch (error) {
        console.log(`❌ ${modelId}: ${error.response?.status || 'ERROR'} - ${error.response?.data?.errors?.[0]?.message || error.message}`);
        return false;
    }
}

async function testAllModels() {
    console.log('\n📋 Configuration:');
    console.log('  Project ID:', process.env.WATSONX_PROJECT_ID);
    console.log('  URL:', process.env.WATSONX_URL);
    
    try {
        console.log('\n🔑 Getting IAM token...');
        const iamToken = await getIAMToken(process.env.WATSONX_API_KEY);
        console.log('✅ IAM token obtained\n');
        
        console.log('🧪 Testing available models:\n');
        
        // Common WatsonX models to test
        const modelsToTest = [
            'ibm/granite-13b-chat-v2',
            'ibm/granite-13b-instruct-v2',
            'meta-llama/llama-2-70b-chat',
            'meta-llama/llama-2-13b-chat',
            'google/flan-t5-xxl',
            'google/flan-ul2',
            'ibm/mpt-7b-instruct2',
            'bigscience/mt0-xxl',
            'eleutherai/gpt-neox-20b'
        ];
        
        let workingModel = null;
        
        for (const model of modelsToTest) {
            const works = await testModel(iamToken, model);
            if (works && !workingModel) {
                workingModel = model;
            }
            await new Promise(resolve => setTimeout(resolve, 1000)); // Wait 1s between tests
        }
        
        console.log('\n' + '='.repeat(60));
        if (workingModel) {
            console.log('✅ FOUND WORKING MODEL!');
            console.log(`   Model: ${workingModel}`);
            console.log('\n💡 Update your .env and server.js to use this model:');
            console.log(`   model_id: '${workingModel}'`);
        } else {
            console.log('❌ No working models found');
            console.log('   This might be due to:');
            console.log('   1. Project not properly configured');
            console.log('   2. No models available in your plan');
            console.log('   3. Region-specific model availability');
        }
        console.log('='.repeat(60));
        
    } catch (error) {
        console.error('\n❌ Error:', error.message);
    }
}

testAllModels().catch(err => {
    console.error('Unexpected error:', err);
    process.exit(1);
});

// Made with Bob
