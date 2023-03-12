package com.example.unswpolicieschatgpt;

import android.content.Intent;
import android.os.Bundle;

import com.example.unswpolicieschatgpt.database.Document;
import com.example.unswpolicieschatgpt.database.PDFTextExtractor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
        /*
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        */

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
                            URL pdfURL = new URL("https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/assessmentdesignprocedure.pdf");
                            PDFTextExtractor textExtractor = new PDFTextExtractor();
                            textExtractor.PDFTextExtractor(MainActivity.this, pdfURL);

                            //Display text for testing
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView docTitle = findViewById(R.id.docTitle);
                                    TextView purpose = findViewById(R.id.purpose);
                                    TextView scope = findViewById(R.id.scope);
                                    TextView docContent = findViewById(R.id.docContent);
                                    TextView responsible_officer = findViewById(R.id.responsible_officer);
                                    TextView contact_officer = findViewById(R.id.contact_officer);
                                    TextView parent_doc = findViewById(R.id.parentDoc);


                                    //Test take data out of database
                                    ArrayList<Document> documentList = new ArrayList<>();
                                    final String DB_URL = "jdbc:mysql://localhost/documentdatabase";
                                    Connection conn = null;
                                    try {
                                        conn = DriverManager.getConnection(DB_URL);
                                        Statement st = conn.createStatement();
                                        String query = "SELECT * FROM DOCUMENT" ;
                                        ResultSet rs = st.executeQuery(query);
                                        while (rs.next()) {
                                            documentList.add(new Document(rs.getInt(1), rs.getString(2),
                                                    rs.getString(3), rs.getString(4), rs.getString(5),
                                                    rs.getString(6), rs.getString(7), rs.getString(8)));
                                        }
                                        Document selectedDoc = documentList.get(0);
                                        docTitle.setText(selectedDoc.getTitle());
                                        purpose.setText(selectedDoc.getPurpose());
                                        scope.setText(selectedDoc.getScope());
                                        docContent.setText(selectedDoc.getContent());
                                        responsible_officer.setText(selectedDoc.getResponsible_officer());
                                        contact_officer.setText(selectedDoc.getContact_officer());
                                        parent_doc.setText(selectedDoc.getParent_doc());

                                        rs.close();
                                        st.close();

                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }







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
