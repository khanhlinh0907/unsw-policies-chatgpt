package com.example.unswpolicieschatgpt;

import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;

import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;

import com.example.unswpolicieschatgpt.database.Policy;
import com.example.unswpolicieschatgpt.database.PolicyDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import java.util.List;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;

    Button btnViewPolicies;

    Button btnGoToChatBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        PolicyDatabase.UploadTask task = new PolicyDatabase.UploadTask(this);
        task.execute();

        //Change colour of top action bar
        //Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.unsw_yellow)));

        //Bottom Navigation View
        bottomNav = findViewById(R.id.bottomNavigationView);

        //Buttons
        btnViewPolicies = findViewById(R.id.btn_view_policies);
        btnGoToChatBot = findViewById(R.id.btn_go_to_chatbot);

        //View policies or guidelines
        btnViewPolicies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        btnGoToChatBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChatBotActivity.class);
                startActivity(intent);
            }
        });

        /**
         * Set up Bottom Navigation View
         */
        bottomNav.setSelectedItemId(R.id.home);

        //Perform item selected listener
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //Check which item is selected
                switch (item.getItemId()) {
                    case R.id.home:
                        return true;
                    case R.id.chatbot:
                        startActivity(new Intent(getApplicationContext(), ChatBotActivity.class));
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
    }



}

