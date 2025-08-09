package com.simats.bloodbuddy;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatbotActivity extends AppCompatActivity {
    private static final String TAG = "ChatbotActivity";
    private static final String CHATBOT_URL = IPv4Connection.getBaseUrl()+"chatbot.php";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private ImageView btnSend;
    private ImageView btnBack;
    private ImageView btnClearChat;
    private OkHttpClient client;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        editTextMessage = findViewById(R.id.editTextMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        btnClearChat = findViewById(R.id.btnClearChat);

        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        btnBack.setOnClickListener(v -> finish());

        btnClearChat.setOnClickListener(v -> {
            messageList.clear();
            chatAdapter.notifyDataSetChanged();
            addMessage(new ChatMessage("Hello! I'm Blood Buddy, your virtual assistant. How can I help you today?", false));
        });

        addMessage(new ChatMessage("Hello! I'm Blood Buddy, your virtual assistant for blood-related health advice. How can I help you today?", false));

        btnSend.setOnClickListener(v -> {
            String message = editTextMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                addMessage(new ChatMessage(message, true));
                editTextMessage.setText("");
                sendMessageToServer(message);
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMessage(ChatMessage message) {
        runOnUiThread(() -> {
            messageList.add(message);
            chatAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.smoothScrollToPosition(messageList.size() - 1);
        }); 
    }

    private void sendMessageToServer(String message) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("message", message);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON body", e);
            runOnUiThread(() -> {
                Toast.makeText(this, "Error preparing message", Toast.LENGTH_SHORT).show();
                addMessage(new ChatMessage("Sorry, something went wrong. Please try again.", false));
            });
            return;
        }

        RequestBody requestBody = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(CHATBOT_URL)
                .post(requestBody)
                .build();

        addMessage(new ChatMessage("Thinking...", false));

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    removeThinkingMessage();
                    Toast.makeText(ChatbotActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    addMessage(new ChatMessage("Sorry, I couldn't connect to the server.", false));
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body() != null ? response.body().string() : "";
                runOnUiThread(() -> {
                    removeThinkingMessage();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        JSONObject responseObject = json.getJSONObject("response");
                        String botResponse = responseObject.getString("text");
                        addMessage(new ChatMessage(botResponse, false));

                        // Check for and display recommendation if available
                        if (responseObject.has("recommendation")) {
                            JSONObject recommendation = responseObject.getJSONObject("recommendation");
                            String mealName = recommendation.getString("meal");
                            JSONArray ingredients = recommendation.getJSONArray("ingredients");
                            String explanation = recommendation.getString("explanation");

                            StringBuilder formattedRecommendation = new StringBuilder();
                            formattedRecommendation.append("Recommended Meal: ").append(mealName).append("\n\nIngredients:\n");
                            for (int i = 0; i < ingredients.length(); i++) {
                                formattedRecommendation.append("• ").append(ingredients.getString(i)).append("\n");
                            }
                            formattedRecommendation.append("\nReason: ").append(explanation);

                            addMessage(new ChatMessage(formattedRecommendation.toString(), false));
                        }

                    } catch (JSONException e) {
                        Toast.makeText(ChatbotActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        addMessage(new ChatMessage("Sorry, I couldn't understand the response.", false));
                    }
                });
            }
        });
    }

    private void removeThinkingMessage() {
        if (!messageList.isEmpty() && messageList.get(messageList.size() - 1).getMessage().equals("Thinking...")) {
            messageList.remove(messageList.size() - 1);
            chatAdapter.notifyItemRemoved(messageList.size());
        }
    }
}
