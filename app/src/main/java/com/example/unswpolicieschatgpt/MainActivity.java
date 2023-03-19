package com.example.unswpolicieschatgpt;

import android.content.Intent;
import android.os.Bundle;

import com.example.unswpolicieschatgpt.database.Policy;
import com.example.unswpolicieschatgpt.database.PolicyDao;
import com.example.unswpolicieschatgpt.database.PolicyDatabase;
import com.example.unswpolicieschatgpt.database.PDFTextExtractor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.view.View;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button openPDF;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Bottom Navigation View
        bottomNav = findViewById(R.id.bottomNavigationView);

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

        openPDF = findViewById(R.id.openPDF);

        /*openPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                intent.putExtra("pdf_url", "https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/assessmentdesignprocedure.pdf");

                startActivity(intent);
            }
        });

         */
        openPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PDFBoxResourceLoader.init(getApplicationContext());

                            //Create DocumentDatabase
                            PolicyDatabase database = Room.databaseBuilder(getApplicationContext(),
                                    PolicyDatabase.class, "Document_Database").allowMainThreadQueries().build();
                            PolicyDao policyDao = database.mainDao();
                            //Add documents to Room Database
                            ArrayList<URL> urlList = database.insertURLList();
                            for (URL url : urlList) {
                                PDFTextExtractor textExtractor = new PDFTextExtractor();
                                Policy policy = textExtractor.PDFTextExtractor(MainActivity.this, url);
                                policy.setPdf_url(url);
                                policyDao.insert(policy);
                            }

                            //Get all documents in the database
                            List<Policy> policyList = policyDao.getAll();

                            /**
                             * Test the DocumentDatabase
                             */
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView docTitle = findViewById(R.id.docTitle);
                                    //TextView purpose = findViewById(R.id.purpose);
                                    TextView responsible_officer = findViewById(R.id.responsible_officer);
                                    TextView contact_officer = findViewById(R.id.contact_officer);


                                    Policy selectedDoc = policyList.get(0);
                                    docTitle.setText(selectedDoc.getTitle());
                                    //purpose.setText(selectedDoc.getPurpose());
                                    responsible_officer.setText(selectedDoc.getResponsible_officer());
                                    contact_officer.setText(selectedDoc.getContact_officer());
                                }
                            });

                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }).start();
            }
            });
    }
        /*
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

         */
    }
