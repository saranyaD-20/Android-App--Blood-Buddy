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

$message = $data['message'];

// Simple response based on keywords
$response = "I'm sorry, I don't understand that yet.";
$messageLower = strtolower($message);

if (strpos($messageLower, "hello") !== false || strpos($messageLower, "hi") !== false) {
    $response = "Hello! How can I help you with your health today?";
} else if (strpos($messageLower, "anemia") !== false) {
    $response = "Anemia is a condition where your blood doesn't have enough healthy red blood cells. Would you like information about diet recommendations for anemia?";
} else if (strpos($messageLower, "diabetes") !== false) {
    $response = "Diabetes is a chronic condition that affects how your body turns food into energy. Would you like information about diet recommendations for diabetes?";
} else if (strpos($messageLower, "hypertension") !== false || strpos($messageLower, "blood pressure") !== false) {
    $response = "Hypertension, or high blood pressure, is a common condition that can lead to serious health problems. Would you like information about diet recommendations for hypertension?";
} else if (strpos($messageLower, "menstruation") !== false || strpos($messageLower, "period") !== false) {
    $response = "Menstruation is a natural process that occurs in the female body. Would you like information about diet recommendations during menstruation?";
} else if (strpos($messageLower, "pregnancy") !== false) {
    $response = "Pregnancy is a special time when proper nutrition is crucial for both mother and baby. Would you like information about diet recommendations during pregnancy?";
} else if (strpos($messageLower, "thank") !== false) {
    $response = "You're welcome! Is there anything else I can help you with?";
} else if (strpos($messageLower, "bye") !== false || strpos($messageLower, "goodbye") !== false) {
    $response = "Goodbye! Take care of your health!";
}

// Return the response
echo json_encode([
    "reply" => $response
]);
?> 