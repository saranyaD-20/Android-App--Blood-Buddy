<?php
// Set headers to allow cross-origin requests and specify JSON content type
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// Get POST data
$user_id = isset($_POST['user_id']) ? $_POST['user_id'] : '';
$message = isset($_POST['message']) ? $_POST['message'] : '';

// Validate input
if (empty($user_id) || empty($message)) {
    echo json_encode([
        "success" => false,
        "message" => "Missing required parameters"
    ]);
    exit;
}

// Simple response logic based on keywords
$response = "I'm sorry, I don't understand that yet.";
$message_lower = strtolower($message);

if (strpos($message_lower, "hello") !== false || strpos($message_lower, "hi") !== false) {
    $response = "Hello! How can I help you with your health today?";
} else if (strpos($message_lower, "anemia") !== false) {
    $response = "Anemia is a condition where your blood doesn't have enough healthy red blood cells. Would you like information about diet recommendations for anemia?";
} else if (strpos($message_lower, "diabetes") !== false) {
    $response = "Diabetes is a chronic condition that affects how your body turns food into energy. Would you like information about diet recommendations for diabetes?";
} else if (strpos($message_lower, "hypertension") !== false || strpos($message_lower, "blood pressure") !== false) {
    $response = "Hypertension, or high blood pressure, is a common condition that can lead to serious health problems. Would you like information about diet recommendations for hypertension?";
} else if (strpos($message_lower, "menstruation") !== false || strpos($message_lower, "period") !== false) {
    $response = "Menstruation is a natural process that occurs in the female body. Would you like information about diet recommendations during menstruation?";
} else if (strpos($message_lower, "pregnancy") !== false) {
    $response = "Pregnancy is a special time when proper nutrition is crucial for both mother and baby. Would you like information about diet recommendations during pregnancy?";
} else if (strpos($message_lower, "thank") !== false) {
    $response = "You're welcome! Is there anything else I can help you with?";
} else if (strpos($message_lower, "bye") !== false || strpos($message_lower, "goodbye") !== false) {
    $response = "Goodbye! Take care of your health!";
}

// Return JSON response
echo json_encode([
    "success" => true,
    "response" => $response
]);
?> 