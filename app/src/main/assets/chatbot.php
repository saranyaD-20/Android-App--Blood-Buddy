<?php
// Set headers to ensure proper JSON response
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Set time limit for long-running requests
set_time_limit(300); // 5 minutes

// Get the raw POST data
$rawData = file_get_contents("php://input");
$data = json_decode($rawData, true);

// Check if we have a message
if (!isset($data['message']) || empty($data['message'])) {
    echo json_encode([
        "error" => "No message provided"
    ]);
    exit();
}

$userMessage = $data['message'];

// System prompt for the AI
$systemPrompt = "You are Blood Buddy, a helpful virtual assistant that recommends meals (breakfast, lunch, dinner, snacks) for people with blood-related conditions like anemia, hypertension, diabetes, pregnancy, or menstruation. Always add: This is not medical advice.";

// Check if Ollama is running
$ch = curl_init("http://localhost:11434/api/tags");
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_TIMEOUT, 5);
$ollamaCheck = curl_exec($ch);
$ollamaCheckError = curl_error($ch);
$ollamaCheckHttpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

if ($ollamaCheckError || $ollamaCheckHttpCode !== 200) {
    // If Ollama is not running, use a fallback response
    $fallbackResponse = getFallbackResponse($userMessage);
    echo json_encode([
        "reply" => $fallbackResponse
    ]);
    exit();
}

// Get available models
$models = json_decode($ollamaCheck, true);
$availableModels = [];

if (isset($models['models'])) {
    foreach ($models['models'] as $model) {
        $availableModels[] = $model['name'];
    }
}

// Define model preferences in order of preference (smaller models first)
$modelPreferences = [
    'tinyllama:latest',  // Smallest model (1B parameters)
    'phi:latest',        // Small model (3B parameters)
    'llama2:latest',     // Medium model (7B parameters)
    'mistral:latest'     // Larger model (7B parameters but might need more memory)
];

// Find the first available model from our preferences
$selectedModel = null;
foreach ($modelPreferences as $preferredModel) {
    if (in_array($preferredModel, $availableModels)) {
        $selectedModel = $preferredModel;
        break;
    }
}

if (!$selectedModel) {
    // If no models are available, use a fallback response
    $fallbackResponse = getFallbackResponse($userMessage);
    echo json_encode([
        "reply" => $fallbackResponse
    ]);
    exit();
}

// Prepare data for Ollama API
$ollamaData = [
    "model" => $selectedModel,
    "prompt" => $systemPrompt . "\nUser: $userMessage\nAssistant:",
    "stream" => false
];

// Initialize cURL session
$ch = curl_init("http://localhost:11434/api/generate");

// Set cURL options
curl_setopt_array($ch, [
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_POST => true,
    CURLOPT_HTTPHEADER => ['Content-Type: application/json'],
    CURLOPT_POSTFIELDS => json_encode($ollamaData),
    CURLOPT_TIMEOUT => 60 // 60 seconds timeout
]);

// Execute cURL request
$response = curl_exec($ch);
$curlError = curl_error($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);

// Close cURL session
curl_close($ch);

// Check for cURL errors
if ($curlError) {
    // If there's a cURL error, use a fallback response
    $fallbackResponse = getFallbackResponse($userMessage);
    echo json_encode([
        "reply" => $fallbackResponse
    ]);
    exit();
}

// Check HTTP response code
if ($httpCode !== 200) {
    // For 500 errors, provide more detailed debugging information
    if ($httpCode === 500) {
        $errorResponse = json_decode($response, true);
        $errorMessage = isset($errorResponse['error']) ? $errorResponse['error'] : "Unknown error";
        
        // Check if it's a memory issue
        if (strpos($errorMessage, "memory") !== false) {
            // If there's a memory issue, use a fallback response
            $fallbackResponse = getFallbackResponse($userMessage);
            echo json_encode([
                "reply" => $fallbackResponse
            ]);
        } else {
            // For other 500 errors, use a fallback response
            $fallbackResponse = getFallbackResponse($userMessage);
            echo json_encode([
                "reply" => $fallbackResponse
            ]);
        }
    } else {
        // For other HTTP errors, use a fallback response
        $fallbackResponse = getFallbackResponse($userMessage);
        echo json_encode([
            "reply" => $fallbackResponse
        ]);
    }
    exit();
}

// Check if we got a response
if (!$response) {
    // If there's no response, use a fallback response
    $fallbackResponse = getFallbackResponse($userMessage);
    echo json_encode([
        "reply" => $fallbackResponse
    ]);
    exit();
}

// Parse the Ollama response
$result = json_decode($response, true);

// Check if we have a valid response
if (!isset($result["response"])) {
    // If there's no valid response, use a fallback response
    $fallbackResponse = getFallbackResponse($userMessage);
    echo json_encode([
        "reply" => $fallbackResponse
    ]);
    exit();
}

// Return the response
echo json_encode([
    "reply" => $result["response"]
]);

// Function to get a fallback response when Ollama is not available
function getFallbackResponse($userMessage) {
    // Convert user message to lowercase for easier matching
    $message = strtolower($userMessage);
    
    // Check for greetings
    if (preg_match('/^(hi|hello|hey|greetings|sup|yo|hi bro|hello there)/i', $message)) {
        return "Hello! I'm Blood Buddy, your virtual assistant for blood-related health advice. How can I help you today? I can recommend meals for conditions like anemia, hypertension, diabetes, pregnancy, or menstruation.";
    }
    
    // Check for blood-related conditions
    if (strpos($message, 'anemia') !== false) {
        return "For anemia, I recommend iron-rich foods like lean red meat, leafy greens, beans, and fortified cereals. For breakfast, try oatmeal with berries and nuts. For lunch, a spinach salad with grilled chicken would be good. Remember, this is not medical advice.";
    }
    
    if (strpos($message, 'diabetes') !== false) {
        return "For diabetes, focus on low glycemic index foods. For breakfast, try Greek yogurt with berries and nuts. For lunch, a quinoa bowl with vegetables and lean protein would be good. Remember, this is not medical advice.";
    }
    
    if (strpos($message, 'hypertension') !== false || strpos($message, 'blood pressure') !== false) {
        return "For hypertension, focus on low-sodium foods. For breakfast, try whole grain toast with avocado. For lunch, a salad with grilled fish would be good. Remember, this is not medical advice.";
    }
    
    if (strpos($message, 'pregnancy') !== false) {
        return "During pregnancy, focus on nutrient-dense foods. For breakfast, try whole grain cereal with milk and fruit. For lunch, a sandwich with lean protein and vegetables would be good. Remember, this is not medical advice.";
    }
    
    if (strpos($message, 'menstruation') !== false || strpos($message, 'period') !== false) {
        return "During menstruation, focus on iron-rich foods. For breakfast, try fortified cereal with milk and berries. For lunch, a bean and vegetable soup would be good. Remember, this is not medical advice.";
    }
    
    // Check for thank you messages
    if (preg_match('/^(thanks|thank you|thx|ty)/i', $message)) {
        return "You're welcome! Let me know if you need any more help with meal recommendations for blood-related conditions.";
    }
    
    // Check for goodbye messages
    if (preg_match('/^(bye|goodbye|see you|farewell)/i', $message)) {
        return "Goodbye! Take care and remember to maintain a healthy diet for your blood-related condition.";
    }
    
    // Default response for unrecognized messages
    return "I'm Blood Buddy, your virtual assistant for blood-related health advice. I can recommend meals for conditions like anemia, hypertension, diabetes, pregnancy, or menstruation. What would you like to know about?";
}
?> 