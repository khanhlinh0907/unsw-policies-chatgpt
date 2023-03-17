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

        // set up the policy category spinner
        Spinner spinner = findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.policy_categories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String selectedCategory = adapterView.getItemAtPosition(i).toString();
//                adapter.getFilter().filter(selectedCategory);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                adapter.getFilter().filter("");
//            }
//        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCategory = adapterView.getItemAtPosition(i).toString();
                if (selectedCategory.equals("All Categories")) {
                    adapter.getFilter().filter("");
                } else {
                    adapter.getFilter().filter(selectedCategory);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                adapter.getFilter().filter("");
            }
        });

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
//        policyList.add(new Policy("Assessment Design Procedure", "Assessments"));
//        policyList.add(new Policy("Assessment Implementation Procedure", "Assessments"));
//        policyList.add(new Policy("Assessment Policy", "Assessments"));

        policyList.add(new Policy("Employment", "Employment"));
        policyList.add(new Policy("Employment Policy", "Employment"));
        policyList.add(new Policy("Engagement", "Engagement"));
        policyList.add(new Policy("Facilities and IT", "Facilities and IT"));
        policyList.add(new Policy("Finance and Procurement", "Finance and Procurement"));
        policyList.add(new Policy("Governance and Mgmt", "Governance and Mgmt"));
        policyList.add(new Policy("Health and Safety", "Health and Safety"));
        policyList.add(new Policy("Research and Training", "Research and Training"));
        policyList.add(new Policy("Student Mgmt Support", "Student Mgmt Support"));
        policyList.add(new Policy("Teaching and Learning", "Teaching and Learning"));
        policyList.add(new Policy("Assessments Design Procedure", "Assessments"));
        policyList.add(new Policy("Assessments Policy", "Assessments"));
        policyList.add(new Policy("Assessments Implementation Procedure", "Assessments"));
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

    @Override
    // React to user interaction with the menu
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortName:
                adapter.sort(PolicyAdapter.SORT_METHOD_NAME);
                return true;
            case R.id.sortNameReverse:
                adapter.sort(PolicyAdapter.SORT_METHOD_NAME_REVERSE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
