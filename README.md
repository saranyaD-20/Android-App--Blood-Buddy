🩸 BloodBuddy – AI-Powered Health Recommendation App

BloodBuddy is a mobile health recommendation application designed to provide personalized food recommendations for users based on selected health conditions. It combines a mobile interface, a backend API, a database, and an AI-powered chatbot to make health-related information more accessible.

The application aims to help users explore food recommendations and general health information in one place.

🎯 Problem Statement

People managing specific health conditions may find it difficult to identify food options that suit their individual needs. Finding relevant information across different sources can also be time-consuming.

BloodBuddy aims to simplify access to condition-specific food recommendations through a mobile application with an integrated AI chatbot.

✨ Key Features

- Personalized Food Recommendations:Displays food suggestions based on the selected health condition.
- Multiple Health Conditions: Supports five health conditions configured in the application.
- AI-Powered Chatbot:Provides conversational access to general health and food-related information using Ollama.
- Mobile User Interface: Provides a dedicated Android application interface.
- Database Integration:Uses MySQL to store and retrieve application data.
- Backend API: Uses PHP to handle communication between the mobile app and the database.
- Centralized Information:Brings food recommendations and chatbot functionality into one application.

🛠️ Technology Stack

Frontend – Mobile Application
- Java
- Android Studio
- XML layouts (if used in the project)

Backend
- Core PHP
- REST-style HTTP endpoints (if implemented)

Database
- MySQL

Development Environment
- XAMPP
- phpMyAdmin

 AI Integration
- Ollama
- AI chatbot integration

Testing and Design
- Postman for API testing
- Figma for UI/UX design

🏗️ System Architecture

BloodBuddy follows a mobile client–server architecture.

1. User Interface: The user interacts with the Android application.
2. Request Handling: The Android app sends requests to the PHP backend.
3. Backend Processing: PHP processes requests and communicates with MySQL when database access is needed.
4. Database: MySQL stores and retrieves the application's configured data.
5. AI Chatbot: The application communicates with the configured Ollama model to generate chatbot responses.
6. Response Display:The app displays the returned recommendations or chatbot responses to the user.

```text
Android Application
   (Java / Android Studio)
           |
           | HTTP Requests
           v
      Core PHP Backend
        /          \
       v            v
   MySQL DB      Ollama AI
       \            /
        v          v
       Android Application
       Displays Results
```

*The diagram is a conceptual overview. The actual request flow depends on how the chatbot and database are connected in the implementation.*

🔄 How It Works

1. The user opens BloodBuddy on an Android device.
2. The user selects a supported health condition.
3. The application retrieves or displays relevant food recommendations.
4. The user can interact with the AI chatbot for general food-related questions.
5. The backend processes the relevant requests and returns responses.
6. The application displays the information in a mobile-friendly format.

🚀 Installation and Setup

Prerequisites

Install the following tools:

- Android Studio
- Java Development Kit compatible with the project
- XAMPP
- MySQL and phpMyAdmin
- Ollama, if running the chatbot locally
- Git

1. Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd BloodBuddy
```

Replace the repository URL with your actual GitHub repository URL.

2. Set Up the Backend

1. Copy the PHP backend folder into the XAMPP `htdocs` directory.
2. Start Apache and MySQL from the XAMPP Control Panel.
3. Open phpMyAdmin in your browser.
4. Create the database required by the application.
5. Import the project's SQL file if one is included.
6. Configure the database connection settings in the PHP backend.

3. Configure the Android Application

1. Open the project in Android Studio.
2. Allow Gradle to synchronize the project dependencies.
3. Update the backend API base URL in the application configuration.
4. Ensure the Android app can reach the PHP backend.

For an Android emulator, the host machine's localhost is commonly accessed using `10.0.2.2` instead of `localhost`. For a physical device, use the computer's reachable local IP address and ensure both devices can communicate over the network.

 4. Configure the AI Chatbot

1. Install Ollama if it is used as a locally hosted model runtime.
2. Download the model configured by your application.
3. Start Ollama and verify that the configured model is available.
4. Update the chatbot endpoint and model configuration to match your implementation.

Do not expose private API credentials or sensitive configuration values in the repository.

5. Run the Application

1. Start Apache and MySQL in XAMPP.
2. Start Ollama if required by the chatbot.
3. Run the project from Android Studio using an emulator or a connected Android device.
4. Test the recommendation and chatbot features.

🧪 Testing

The following tests can be used to validate the application:

- Verify that supported health conditions can be selected.
- Check that the appropriate food recommendations are displayed.
- Test backend API responses using Postman.
- Verify database connectivity and data retrieval.
- Check chatbot responses when Ollama is available.
- Test behavior when the backend, database, or AI service is unavailable.
- Verify the mobile interface on different screen sizes.

🔐 Security and Responsible Use

- Keep database credentials and private configuration values out of public repositories.
- Validate user input on the backend.
- Restrict database access to authorized application operations.
- Handle backend and AI-service errors safely.
- Treat AI chatbot responses as general informational content, not as medical diagnosis or personalized medical advice.
- Encourage users to consult qualified healthcare professionals for condition-specific dietary decisions.

🔮 Future Improvements

- User authentication and personalized profiles
- Expanded food and nutrition information
- More detailed dietary preferences and filters
- Improved chatbot response validation
- Multilingual support
- Food recommendation history
- Enhanced accessibility and UI/UX
- Automated backend testing and deployment

👩‍💻 Author

Saranya D

B.Tech – Artificial Intelligence and Data Science

GitHub: https://github.com/saranyaD-20

---

*BloodBuddy is an educational health-information project. Food recommendations and chatbot responses should not replace professional medical advice.*
