package com.example.unswpolicieschatgpt;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unswpolicieschatgpt.chatgptapi.ChatGPTClient;
import com.theokanning.openai.service.OpenAiService;

import java.util.concurrent.ExecutionException;


public class ChatBotActivity extends AppCompatActivity {

    private ChatGPTClient chatGPTClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);





        //Code testing the ChatGPT API library.
        String token = "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";

        chatGPTClient = new ChatGPTClient(token);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    chatGPTClient.sendPrompt("Tell me about butterflies.");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

}
