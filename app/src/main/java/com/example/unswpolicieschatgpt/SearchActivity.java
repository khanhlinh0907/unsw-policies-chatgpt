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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unswpolicieschatgpt.database.Policy;
import com.example.unswpolicieschatgpt.database.PolicyDao;
import com.example.unswpolicieschatgpt.database.PolicyDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements PolicyRecyclerViewInterface {
    private static final String TAG = "SearchActivity";
    BottomNavigationView bottomNav;
    // RecyclerView
    private RecyclerView mRecyclerView;
    private List<Policy> policyList = new ArrayList<>();
    private PolicyDatabase policyDatabase;
    private PolicyAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("Browse UNSW Policy");

        //Get the handle to RecyclerView
        mRecyclerView = findViewById(R.id.rvList);

        //Instantiate a Linear RecyclerView Layout Manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new PolicyAdapter(policyList, this);
        //Create an Adapter instance with an ArrayList of Policy objects
        mRecyclerView.setAdapter(adapter);

        /**
         * Setup database
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    policyDatabase.setupDatabase(SearchActivity.this);
                    List<Policy> retrievedPolicyList = policyDatabase.mainDao().getAll();
                    policyList.clear();
                    policyList.addAll(retrievedPolicyList);

                    //Test policy section
                    String testContent = retrievedPolicyList.get(0).getContent();
                    String [] testPolicySection = policyDatabase.getPolicySection(testContent);
                    for (int i = 0; i < testPolicySection.length; i++) {
                        System.out.println("Section" + (i+1) + ": " + testPolicySection[i]);
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new PolicyAdapter(policyList, SearchActivity.this);
                            mRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            policyDatabase.close();
                        }
                    });
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();





        /**
         * Set up the policy category spinner
         */

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
//                if (selectedCategory.equals("All Categories")) {
//                    adapter.getFilter().filter("");
//                } else {
//                    adapter.getFilter().filter(selectedCategory);
//                }
                if (selectedCategory.equals("Any category")) {
                    adapter.getFilter().filter(null);
                } else {
                    adapter.getFilter().filter(selectedCategory);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                adapter.getFilter().filter("");
            }
        });

        /**
         * Set up Bottom Navigation View
         */
        bottomNav = findViewById(R.id.bottomNavigationView);
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

    @Override
    public void onPolicyClick(URL url) {
        // Retrieve the selected policy from the database using its ID
        Log.d("SearchActivity", "onPolicyClick: url = " + url);
        Intent intent = new Intent(SearchActivity.this, WebActivity.class);

        //Changed URL to String to allow it ot be passed to the Intent. Resolve issue - does WebView require URl object or URL as a String?
        intent.putExtra(WebActivity.INTENT_MESSAGE, url.toString());
        startActivity(intent);
    }

    /**
     * Load PDF file to WebActivity when user taps into policy title
     * @param
     */
    /*private void launchWebActivity(String policyId) {
        Intent intent = new Intent(SearchActivity.this, WebActivity.class);
        intent.putExtra(WebActivity.INTENT_MESSAGE, policyId);
        startActivity(intent);
    }*/

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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
