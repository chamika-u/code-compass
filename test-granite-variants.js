const axios = require('axios');
require('dotenv').config();

async function getIAMToken(apiKey) {
    const response = await axios.post(
        'https://iam.cloud.ibm.com/identity/token',
        new URLSearchParams({
            'grant_type': 'urn:ibm:params:oauth:grant-type:apikey',
            'apikey': apiKey
        }),
        { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }
    );
    return response.data.access_token;
}

async function testModel(iamToken, modelId) {
    try {
        const response = await axios.post(
            `${process.env.WATSONX_URL}/ml/v1/text/generation?version=2023-05-29`,
            {
                input: "Hello",
                model_id: modelId,
                project_id: process.env.WATSONX_PROJECT_ID,
                parameters: { max_new_tokens: 50 }
            },
            {
                headers: {
                    'Authorization': `Bearer ${iamToken}`,
                    'Content-Type': 'application/json'
                },
                timeout: 10000
            }
        );
        console.log(`✅ ${modelId}: WORKS!`);
        return true;
    } catch (error) {
        console.log(`❌ ${modelId}: ${error.response?.data?.errors?.[0]?.code || 'error'}`);
        return false;
    }
}

async function main() {
    console.log('Testing Granite model variants...\n');
    const iamToken = await getIAMToken(process.env.WATSONX_API_KEY);
    
    const variants = [
        'granite-4-h-small',
        'ibm/granite-4-h-small',
        'granite-3-8b-instruct',
        'ibm/granite-3-8b-instruct',
        'granite-3.0-8b-instruct',
        'ibm/granite-3.0-8b-instruct',
        'granite-20b-multilingual',
        'ibm/granite-20b-multilingual',
        'granite-13b-chat-v2',
        'ibm/granite-13b-chat-v2',
        'granite-13b-instruct-v2',
        'ibm/granite-13b-instruct-v2'
    ];
    
    for (const model of variants) {
        await testModel(iamToken, model);
        await new Promise(r => setTimeout(r, 500));
    }
}

main().catch(console.error);

// Made with Bob
