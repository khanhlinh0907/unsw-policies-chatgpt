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
import android.widget.EditText;
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

    //TAG
    static final String TAG = "MainActivity";

    // RecyclerView
    RecyclerView mRecyclerView;
    private List<Policy> policyList = new ArrayList<>();
    private PolicyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // RecyclerView
        mRecyclerView = findViewById(R.id.rvList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

        getData();
        adapter = new PolicyAdapter(policyList, (PolicyRecyclerViewInterface) this);
        mRecyclerView.setAdapter(adapter);

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

    // Adding Dummy Data for Policy RecyclerView
    private void getData(){
        policyList.add(new Policy("Assessment Design Procedure", "Assessments"));
        policyList.add(new Policy("Assessment Implementation Procedure", "Assessments"));
        policyList.add(new Policy("Assessment Policy", "Assessments"));
        policyList.add(new Policy("Employment", "Assessments"));
        policyList.add(new Policy("Finance", "Assessments"));
        policyList.add(new Policy("Governance", "Assessments"));
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
                Log.d(TAG, "Line 100:: Query = "+s);
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "Line 107:: Query = "+s);
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }
}
