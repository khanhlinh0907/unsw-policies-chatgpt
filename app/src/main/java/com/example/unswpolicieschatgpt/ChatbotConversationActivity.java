package com.example.unswpolicieschatgpt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.unswpolicieschatgpt.chatgptapi.ChatGPTClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theokanning.openai.embedding.Embedding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.Map;
import java.util.Objects;
import java.util.Vector;

public class ChatbotConversationActivity extends AppCompatActivity {

    private ImageButton mBackButton;
    private Button mSendButton;
    private EditText mInputField;
    private RecyclerView mConvoRv;
    private ChatbotConversationAdapter adapter;
    private ArrayList<ConversationMessage> mConversation = new ArrayList<>();;

    private ChatGPTClient chatGPTClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot_conversation);
        setTitle("PolicyPilot");

        //Get the ActionBar instance
        ActionBar actionBar = getSupportActionBar();

        //Set the back icon using the vector drawable resource ID
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        //Show the back button in the ActionBar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("UNSW PolicyPilot");
        //actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);

        //Change colour of top action bar
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.unsw_yellow)));

        //Change text colour of top action bar
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>"));

        //Get the handle to RecyclerView
        mConvoRv = findViewById(R.id.conversationRV);

        //Instantiate a Linear RecyclerView Layout Manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mConvoRv.setLayoutManager(layoutManager);

        adapter = new ChatbotConversationAdapter(mConversation);
        //Create an Adapter instance with an ArrayList of Policy objects
        mConvoRv.setAdapter(adapter);

        mSendButton = findViewById(R.id.sendButton);
        mInputField = findViewById(R.id.message_input_field);


        //Default message that starts every conversation.

        mConversation.add(new ConversationMessage("BOT", "Hello there. " +
                "I'm UNSW PolicyPilot who will guide you through UNSW policies & guidelines. How can I help you today?"));
        adapter.notifyDataSetChanged();

        //Code testing the ChatGPT API library.
        //REPLACE BELOW TOKEN WITH YOUR OWN API_KEY. DO NOT PUSH THE TOKEN TO GITHUB.
        String token = "API_KEY";
        chatGPTClient = new ChatGPTClient(token);

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

                    //Starts semantic search process.
                    SemanticThread semanticThread = new SemanticThread(userInput, token);
                    semanticThread.start();
                }

                else {
                    System.out.println("ChatbotConversationActivity: No Text Found.");
                }

            }
        });



        /**
         * Connect Firebase Database with Android App
         */
        FirebaseApp.initializeApp(this);
        FirebaseDatabase mFirebaseDatabase;
        mFirebaseDatabase = FirebaseDatabase.getInstance("https://unswpolicychatbot-default-rtdb.asia-southeast1.firebasedatabase.app/");
    }

    private class SemanticThread extends Thread {
        private String userInput;
        private String openAIKey;
        private OkHttpClient client = new OkHttpClient();


        private Embedding embedInput;
        private String matchedPolicyId;
        private String matchedSectionId;
        private String policyTitle;

        private String pdf_url;


        public SemanticThread(String userInput, String openAIKey) {
            this.userInput = userInput;
            this.openAIKey = openAIKey;
        }

        /**
         * Retrieve embeddings
         */
        @Override
        public void run() {
            chatGPTClient = new ChatGPTClient("API_KEY");
            //Get embeddings of user input
            embedInput = chatGPTClient.embedQuery(userInput);

            //Initialize Firebase Database
            FirebaseApp.initializeApp(getApplicationContext());
            FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance("https://unswpolicychatbot-default-rtdb.asia-southeast1.firebasedatabase.app/");

            //Retrieve embeddings of the testing policy sections from Firebase Database
            DatabaseReference vectorRef = mFirebaseDatabase.getReference("Vector");
            List<Double> highestScoreList = new ArrayList<>();
            List<Long> indexOfHighestScoreList = new ArrayList<>();
            List<String> policyIDList = new ArrayList<>();

            vectorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot policySnapshot : dataSnapshot.getChildren()) {
                        String policyId = policySnapshot.getKey();

                        policyIDList.add(policyId);
                        List<List<Double>> embeddingList = new ArrayList<>();
                        List<Long> embeddingIndexList = new ArrayList<>();

                        //List<Double> similarityScoreList = new ArrayList<>();
                        for (DataSnapshot contentSnapshot : policySnapshot.getChildren()) {
                            String contentId = contentSnapshot.getKey();
                            ArrayList<Double> embedding = (ArrayList<Double>) contentSnapshot.child("embedding").getValue();

                            //Retrieve the INDEX of embeddings
                            //This index will be used to retrieve the corresponding index of policy text
                            Long embeddingIndex = (Long) contentSnapshot.child("index").getValue();
                            embeddingList.add(embedding);
                            embeddingIndexList.add(embeddingIndex);

                        }
                        //Find the highest similarity score of each document
                        List findHighestScore = findHighestSimilarityScore(embeddingList, embedInput);
                        double highestScoreOfSingleDocument = (double) findHighestScore.get(0);
                        //Find the index of policy section with the highest similarity - this is the order appeared in Firebase
                        int firebaseIndexHighestScoreOfSingleDocument = (int) findHighestScore.get(1);
                        //Add the highest score of single document to highestScoreList
                        highestScoreList.add(highestScoreOfSingleDocument);
                        //Find the actual index of policy section with the highest score
                        Long indexHighestScoreOfSingleDocument = embeddingIndexList.get(firebaseIndexHighestScoreOfSingleDocument);
                        //Add the actual index of policy section with the highest score
                        indexOfHighestScoreList.add(indexHighestScoreOfSingleDocument);

                    }
                    /**
                     * Find the highest similarity score of all documents
                     */

                    double highestVector = highestScoreList.get(0);
                    // Index of the policy having highest similarity
                    int indexHighestScore = 0;
                    for (int i = 0; i < highestScoreList.size(); i++) {
                        if (highestScoreList.get(i) > highestVector) {
                            highestVector = highestScoreList.get(i);
                            indexHighestScore = i;
                        }
                    }
                    //Find matched policyID
                    matchedPolicyId = policyIDList.get(indexHighestScore);
                    System.out.println("Matched PolicyID: " + matchedPolicyId);
                    matchedSectionId = String.valueOf(indexOfHighestScoreList.get(indexHighestScore));
                    System.out.println("Matched PolicySection: content" + matchedSectionId);


                    DatabaseReference policyRef = mFirebaseDatabase.getReference("Policy");

                    //Get policy title
                    policyRef.child(matchedPolicyId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            policyTitle = (String) snapshot.child("title").getValue();
                            System.out.println("Policy Title:" + policyTitle);
                            pdf_url = (String) snapshot.child("pdf_url").getValue();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    /**
                     * Find the matching content of the highest similarity vector
                     */
                    policyRef.child(matchedPolicyId).child("content" + matchedSectionId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String response = (String) dataSnapshot.getValue();
                            String chatGPTPrompt = "Now you are UNSW AI Assistant." +
                                    "Here is the user's query: " + userInput +
                                    "Context you have: " + response.trim() +
                                    "Policy title: " + policyTitle +
                                    "Reply to user's query." +
                                    "Add this sentence at the end only if the context is relevant to user's query: For more details please refer to" + policyTitle +
                                    "Ignore the context and no need to provide the last sentence if you think it's irrelevant to user's query.";

                            new ChatGPTTask().execute(chatGPTPrompt);
                            System.out.println("ChatGPT Prompt: " + chatGPTPrompt);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });

        }

        /**
         * Solution 1: ChatGPTTask
         * Error: It prints out the prompt instead of ChatGPT response
         */
        private class ChatGPTTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String prompt = params[0];
                String response = null;

                // Call the chatGPTClient to get the response
                try {
                    response = chatGPTClient.getChatGPTResponse(prompt);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return response.trim();
            }

            @Override
            protected void onPostExecute(String response) {
                // Update the UI with the generated response
                mConversation.add(new ConversationMessage("BOT", response));
                adapter.notifyDataSetChanged();
                //displayResponse("Do you have any other questions?");


            }
        }


        /**
         * Update UI
         *
         * @param response
         */
        private void displayResponse(final String response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mConversation.add(new ConversationMessage("BOT", response));
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

                /**
     * Cosine Similarity Function
     * @param v1
     * @param v2
     * @return
     */
    public static double cosineSimilarity(List<Double> v1, List<Double> v2) {
        int minLength = Math.min(v1.size(), v2.size());
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < minLength; i++) {
            dotProduct += v1.get(i) * v2.get(i);
            norm1 += v1.get(i) * v1.get(i);
            norm2 += v2.get(i) * v2.get(i);
        }

        if (minLength == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    /**
     * Find Firebase index of the embeddings with highest cosine similarity with user input
     * @param embeddingList
     * @param embedInput
     * @return
     */
    private List findHighestSimilarityScore(List<List<Double>> embeddingList, Embedding embedInput) {
        List highestSimilarity = new ArrayList<>();
        double highestSimilarityScore = -1;
        int highestIndex = 0;

        for (int i=0; i<embeddingList.size(); i++) {
            double similarity = cosineSimilarity(embeddingList.get(i), embedInput.getEmbedding());

            if (highestSimilarityScore < similarity) {
                highestSimilarityScore = similarity;
                highestIndex = i;
            }
        }
        highestSimilarity.add(highestSimilarityScore);
        highestSimilarity.add(highestIndex);

        return highestSimilarity;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        backToChatbot();
        return true;
    }

    private void backToChatbot() {
        Intent intent = new Intent(getApplicationContext(), ChatBotActivity.class);
        startActivity(intent);
    }

}