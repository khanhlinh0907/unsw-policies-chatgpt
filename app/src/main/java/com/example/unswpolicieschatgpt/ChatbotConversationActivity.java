package com.example.unswpolicieschatgpt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.unswpolicieschatgpt.chatgptapi.ChatGPTClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theokanning.openai.embedding.Embedding;

import java.util.ArrayList;
import java.util.List;

public class ChatbotConversationActivity extends AppCompatActivity {

    private Button mBackButton;
    private Button mSendButton;
    private EditText mInputField;
    private RecyclerView mConvoRv;
    private ChatbotConversationAdapter adapter;
    private ArrayList<ConversationMessage> mConversation = new ArrayList<>();;

    private ChatGPTClient chatGPTClient;

    //Firebase Database
    private DatabaseReference databaseReference;

    private String [] testPolicySection;
    private List <Embedding> sectionEmbedding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot_conversation);
        setTitle("Chatbot");

        //Get the handle to RecyclerView
        mConvoRv = findViewById(R.id.conversationRV);

        //Instantiate a Linear RecyclerView Layout Manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mConvoRv.setLayoutManager(layoutManager);

        adapter = new ChatbotConversationAdapter(mConversation);
        //Create an Adapter instance with an ArrayList of Policy objects
        mConvoRv.setAdapter(adapter);

        mBackButton = findViewById(R.id.backButton);
        mSendButton = findViewById(R.id.sendButton);
        mInputField = findViewById(R.id.message_input_field);

        //Handles back button - returns user to chabotActivity.

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatBotActivity.class);
                startActivity(intent);
            }
        });

        //Default message that starts every conversation.

        mConversation.add(new ConversationMessage("BOT", "Hello there. " +
                "I'm a UNSWer chatbot. How can I help you today?"));
        adapter.notifyDataSetChanged();

        //Code testing the ChatGPT API library.
        //REPLACE BELOW TOKEN WITH YOUR OWN API_KEY. DO NOT PUSH THE TOKEN TO GITHUB.
        String token = "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";
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

                    //mConversation.add(new ConversationMessage("BOT", "Sorry this is my fast day on the job. Let me ask my manager..."));

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

        private Embedding embedInput;
        private String matchedPolicyId;
        private String matchedSectionId;

        public SemanticThread(String userInput, String openAIKey) {
            this.userInput = userInput;
            this.openAIKey = openAIKey;
        }

        /**
         * Retrieve embeddings
         */
        @Override
        public void run() {
            chatGPTClient = new ChatGPTClient("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");
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
                        System.out.println("PolicyID: " + policyId);
                        policyIDList.add(policyId);
                        List<List<Double>> embeddingList = new ArrayList<>();
                        List<Long> embeddingIndexList = new ArrayList<>();

                        //List<Double> similarityScoreList = new ArrayList<>();
                        for (DataSnapshot contentSnapshot : policySnapshot.getChildren()) {
                            String contentId = contentSnapshot.getKey();
                            ArrayList<Double> embedding = (ArrayList<Double>) contentSnapshot.child("embedding").getValue();
                            System.out.println("Content ID: " + contentId + ", Embedding: " + embedding);
                            //Retrieve the INDEX of embeddings
                            //This index will be used to retrieve the corresponding index of policy text
                            Long embeddingIndex = (Long) contentSnapshot.child("index").getValue();
                            System.out.println("Embedding Index: " + embeddingIndex);
                            embeddingList.add(embedding);
                            embeddingIndexList.add(embeddingIndex);
                            //Calculate similarity score of individual policy section
                            //double similarityScore = cosineSimilarity(embedding, embedInput.getEmbedding());
                            //Add to list of similarity scores of all sections in 1 policy
                            //similarityScoreList.add(similarityScore);
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

                        System.out.println("PolicyID" + policyId + "has the highest similarity score of " + highestScoreOfSingleDocument);
                        System.out.println("Firebase Index of the highest score of " + policyId + "is " + firebaseIndexHighestScoreOfSingleDocument);
                        System.out.println("Actual index of the highest score of " + policyId + "is " + indexHighestScoreOfSingleDocument);

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
                    System.out.println("Highest Vector of all documents: " + highestVector);
                    System.out.println("Policy index of the highest vector: " + indexHighestScore);
                    //Find matched policyID
                    matchedPolicyId = policyIDList.get(indexHighestScore);
                    System.out.println("Matched PolicyID: " + matchedPolicyId);
                    matchedSectionId = String.valueOf(indexOfHighestScoreList.get(indexHighestScore));
                    System.out.println("Matched PolicySection: content" + matchedSectionId);

                    DatabaseReference policyRef = mFirebaseDatabase.getReference("Policy");
                    /**
                     * Find the matching content of the highest similarity vector
                     */
                    policyRef.child(matchedPolicyId).child("content" + matchedSectionId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String response = (String) dataSnapshot.getValue();
                            updateUI(response);
                            System.out.println("Response: " + response);
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
            System.out.println("Highest Similarity: " + highestSimilarityScore);
            return highestSimilarity;
        }

        /**
         * Update UI
         * @param response
         */
        private void updateUI(final String response) {
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
}