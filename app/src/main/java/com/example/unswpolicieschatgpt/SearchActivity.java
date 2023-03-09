package com.example.unswpolicieschatgpt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements PolicyRecyclerViewInterface {
    BottomNavigationView bottomNav;

    // Policy RecyclerView
    static final String TAG = "SearchActivity";
    RecyclerView mRecyclerView;
    private List<Policy> policyList = new ArrayList<>();
    private PolicyAdapter adapter;

    private Spinner mSpinner;

    private List <String> mCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Policy RecyclerView
        mRecyclerView = findViewById(R.id.rvList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

        getPolicyData();
        adapter = new PolicyAdapter(policyList, (PolicyRecyclerViewInterface) this);
        mRecyclerView.setAdapter(adapter);

        // Category Filter and Spinner
//        mCategories = getCategories();
//        mSpinner = findViewById(R.id.spinner);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_spinner_dropdown_item, mCategories);
//        mSpinner.setAdapter(adapter);
//        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String category = mCategories.get(i);
//                adapter.setSelectedCategory(category);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                adapter.setSelectedCategory("");
//            }
//        });

            //Bottom Navigation View
        bottomNav = findViewById(R.id.bottomNavigationView);

        /**
         * Set up Bottom Navigation View
         */
        bottomNav.setSelectedItemId(R.id.search);

        //Perform item selected listener
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //Check which item is selected
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.chatbot:
                        startActivity(new Intent(getApplicationContext(), ChatBotActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.search:
                        return true;
                }
                return false;
            }
        });

    }

    private void getPolicyData(){
        policyList.add(new Policy("Assessment Design Procedure", "Assessment"));
        policyList.add(new Policy("Assessment Implementation Procedure", "Assessment"));
        policyList.add(new Policy("Assessment Policy", "Assessment"));
        policyList.add(new Policy("Plagiarism Policy", "Assessment"));
        policyList.add(new Policy("Marking Policy", "Assessment"));
        policyList.add(new Policy("Grading Policy", "Assessment"));
    }

    @Override
    public void onPolicyClick(int position) {
        Policy policy = policyList.get(position);
        Toast.makeText(this, policy.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.policy_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "Line 58:: Query = "+s);
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "Line 58:: Query = "+s);
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }
}
