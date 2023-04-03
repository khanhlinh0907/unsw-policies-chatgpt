package com.example.unswpolicieschatgpt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class ChatbotConversationActivity extends AppCompatActivity {

    private Button mBackButton;
    private Button mSendButton;

    private EditText mInputField;

    private RecyclerView mConvoRv;

    private ChatbotConversationAdapter adapter;

    private ArrayList<ConversationMessage> mConversation = new ArrayList<>();;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot_conversation);
        setTitle("Chatbot");

        //Get the handle to RecyclerView
        mConvoRv = findViewById(R.id.conversationRV);

        //Instantiate a Linear RecyclerView Layout Manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mConvoRv.setLayoutManager(layoutManager);

        adapter = new ChatbotConversationAdapter(mConversation);
        //Create an Adapter instance with an ArrayList of Policy objects
        mConvoRv.setAdapter(adapter);



        mBackButton = findViewById(R.id.backButton);
        mSendButton = findViewById(R.id.sendButton);
        mInputField = findViewById(R.id.message_input_field);

        //Handles back button - returns user to chabotActivity.

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatBotActivity.class);
                startActivity(intent);
            }
        });



        //Default message that starts every conversation.


        mConversation.add(new ConversationMessage("BOT", "Hello there. " +
                "I'm a UNSWer chatbot. How can I help you today?"));
        adapter.notifyDataSetChanged();


        //Handling user messages and Send Button.
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mInputField.getText().equals("")) {
                    String userInput = mInputField.getText().toString();

                    mConversation.add(new ConversationMessage("USER", userInput));
                    adapter.notifyDataSetChanged();

                    //Clears input text field.
                    mInputField.setText("");

                }

                else {
                    System.out.println("ChatbotConversationActivity: No Text Found.");
                }

            }
        });

    }
}