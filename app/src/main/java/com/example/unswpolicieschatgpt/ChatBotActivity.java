package com.example.unswpolicieschatgpt;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.unswpolicieschatgpt.chatgptapi.ChatGPTClient;
import com.example.unswpolicieschatgpt.database.Policy;
import com.example.unswpolicieschatgpt.database.PolicyDao;
import com.example.unswpolicieschatgpt.database.PolicyDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theokanning.openai.embedding.Embedding;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ChatBotActivity extends AppCompatActivity {

    // Messages RV list
    RecyclerView mRecyclerView;
    private List<ChatBotMessage> messageList = new ArrayList<>();
    private ChatBotAdapter adapter;
    private Button newConvButton;

    BottomNavigationView bottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        //Get the ActionBar instance
        ActionBar actionBar = getSupportActionBar();

        //actionBar.setTitle("UNSW PolicyPilot Chatbot");

        //Set the title and center align it
        TextView titleTextView = new TextView(this);
        titleTextView.setText("UNSW PolicyPilot Chatbot");
        titleTextView.setTextSize(24);
        titleTextView.setTextColor(getResources().getColor(R.color.black));
        titleTextView.setGravity(Gravity.CENTER);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(titleTextView);

        mRecyclerView = findViewById(R.id.rvListChatBot);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

        getData();
        adapter = new ChatBotAdapter(messageList);
        mRecyclerView.setAdapter(adapter);

        //Change colour of top action bar
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.unsw_yellow)));

        // Change text colour of top action bar
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>"));

        newConvButton = findViewById(R.id.newConversationButton);

        newConvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatbotConversationActivity.class);
                startActivity(intent);
            }
        });

        bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.chatbot);
        //Perform item selected listener
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //Check which item is selected
                switch (item.getItemId()) {
                    case R.id.chatbot:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });


        /**
         * Connect Firebase Database with Android App
         */
        FirebaseApp.initializeApp(this);
        FirebaseDatabase mFirebaseDatabase;
        mFirebaseDatabase = FirebaseDatabase.getInstance("https://unswpolicychatbot-default-rtdb.asia-southeast1.firebasedatabase.app/");

    }

    /*
    getData - hard coded data for chatbot message list
     */
    private void getData(){
        messageList.add(new ChatBotMessage("Assessments Design", "Here is the Design Procedure", "13:08", "19/04/23"));
        messageList.add(new ChatBotMessage("Special Consideration", "Please refer to this link", "10:45", "19/04/23"));
        messageList.add(new ChatBotMessage("Marks and Grades", "This should be the mark distribution", "09:46", "19/03/23"));
        messageList.add(new ChatBotMessage("Assessment principles", "Thank you for your inquiry", "06:35", "19/04/23"));
        messageList.add(new ChatBotMessage("Academic Progression", "There are two pre-requisite courses", "15:34", "19/04/23"));
        messageList.add(new ChatBotMessage("Enrolment and withdrawal", "Kindly refer to this key dates link", "12:57", "19/04/23"));
        messageList.add(new ChatBotMessage("Recognition of prior learning", "Credit transfer is not granted", "15:15", "19/04/23"));
        messageList.add(new ChatBotMessage("Exam delivery mode", "Please refer to exams tab on Moodle", "17:22", "19/04/23"));
        messageList.add(new ChatBotMessage("Class and room location ", "See school refers to Quad2082", "12:09", "19/04/23"));
    }


}
