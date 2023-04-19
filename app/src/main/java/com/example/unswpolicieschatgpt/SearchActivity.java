package com.example.unswpolicieschatgpt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.unswpolicieschatgpt.database.Policy;
import com.example.unswpolicieschatgpt.database.PolicyDao;
import com.example.unswpolicieschatgpt.database.PolicyDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity implements PolicyRecyclerViewInterface {
    private static final String TAG = "SearchActivity";
    BottomNavigationView bottomNav;
    // RecyclerView
    private RecyclerView mRecyclerView;
    private volatile List<Policy> policyList = new ArrayList<>();
    private PolicyDatabase policyDatabase;
    private PolicyAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("Browse UNSW Policy");

        //Get the ActionBar instance
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle("UNSW PolicyPilot Search");

        //Set the title and center align it
        TextView titleTextView = new TextView(this);
        titleTextView.setText("UNSW PolicyPilot Search");
        titleTextView.setTextSize(24);
        titleTextView.setTextColor(getResources().getColor(R.color.black));
        titleTextView.setGravity(Gravity.CENTER);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(titleTextView);

        //Change colour of top action bar
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.unsw_yellow)));

        // Change text colour of top action bar
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>"));

        //Get the handle to RecyclerView
        mRecyclerView = findViewById(R.id.rvList);

        //Instantiate a Linear RecyclerView Layout Manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new PolicyAdapter(policyList, this);
        //Create an Adapter instance with an ArrayList of Policy objects
        mRecyclerView.setAdapter(adapter);

        //Progression Bar
        mLoadingBar = new ProgressDialog(SearchActivity.this);
        mLoadingBar.setTitle("UNSW PolicyPilot ");
        mLoadingBar.setMessage("Loading UNSW Policies & Guidelines");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.show();




        /**
         * Setup database
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    policyDatabase = Room.databaseBuilder(SearchActivity.this,
                            PolicyDatabase.class, "Policy_Database").fallbackToDestructiveMigration().build();
                    policyDatabase.setupDatabase(SearchActivity.this);
                    policyList = policyDatabase.mainDao().getAll();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingBar.dismiss();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.policy_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        //Set search hint
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "Line 100:: Query = "+s);
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "Line 107:: Query = "+ newText);
                adapter.getFilter().filter(newText);
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
