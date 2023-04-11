package com.example.unswpolicieschatgpt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.unswpolicieschatgpt.chatgptapi.ChatGPTClient;
import com.example.unswpolicieschatgpt.chatgptapi.VectorContent;
import com.example.unswpolicieschatgpt.chatgptapi.VectorData;
import com.example.unswpolicieschatgpt.database.Policy;
import com.example.unswpolicieschatgpt.database.PolicyDao;
import com.example.unswpolicieschatgpt.database.PolicyDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theokanning.openai.embedding.Embedding;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
        String token = "YOUR-API-KEY";
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

/*
        *//**
         * Create vector database -currently comment out to save API usage
         *//*
        new Thread(new Runnable() {
            @Override
            public void run() {
                PolicyDatabase policyDatabase = Room.databaseBuilder(ChatbotConversationActivity.this,
                        PolicyDatabase.class, "Policy_Database").build();
                PolicyDao mainDao = policyDatabase.mainDao();
                try {
                    policyDatabase.setupDatabase(ChatbotConversationActivity.this);
                    List<Policy> retrievedPolicyList = mainDao.getAll();
                    policyDatabase.close();

                    //Test policy section
                    Policy testPolicy = retrievedPolicyList.get(0);
                    String testContent = testPolicy.getContent();
                    testPolicySection = policyDatabase.getPolicySection(testContent);

                    //Push policy content to Firebase
                    DatabaseReference policyRef = mFirebaseDatabase.getReference("Policy").push();
                    Map<String, Object> policy = new HashMap<>();

                    for (int i = 1; i < testPolicySection.length; i++) {
                        policy.put("content" + i, testPolicySection[i]);
                    }

                    policyRef.setValue(policy);

                    //Push content vectors to Firebase
                    sectionEmbedding = chatGPTClient.createEmbeddings(Arrays.asList(testPolicySection));
                    DatabaseReference vectorRef = mFirebaseDatabase.getReference("Vector").push();

                    //Create a Map object with all vectors
                    Map<String, Object> vectors = new HashMap<>();

                    //Find the correlated policyID
                    String policyID = policyRef.getKey();
                    vectors.put("policyID", policyID);

                    for (int j = 1; j < sectionEmbedding.size(); j++) {
                        vectors.put("content" + j, sectionEmbedding.get(j));
                    }

                    //Add vectors to a new child
                    vectorRef.setValue(vectors);

                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();*/
    }

    private class SemanticThread extends Thread {
        private String userInput;
        private String openAIKey;

        private Embedding embedInput;

        public SemanticThread(String userInput, String openAIKey) {
            this.userInput = userInput;
            this.openAIKey = openAIKey;
        }

        /**
         * Retrieve embeddings
         */
        @Override
        public void run() {
            chatGPTClient = new ChatGPTClient("YOUR-API-KEY");
            //Get embeddings of user input
            embedInput = chatGPTClient.embedQuery(userInput);
            //System.out.println("Embed Input Vector" + embedInput);

            //Initialize Firebase Database
            FirebaseApp.initializeApp(getApplicationContext());
            FirebaseDatabase mFirebaseDatabase;
            mFirebaseDatabase = FirebaseDatabase.getInstance("https://unswpolicychatbot-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference mDatabase = mFirebaseDatabase.getReference();

            //Retrieve embeddings of the testing policy sections from Firebase Database
            mDatabase.child("Vector").child("-NS64UZECuTnz5pw_x56").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Ignore the last child as "PolicyID"
                    List<DataSnapshot> children = new ArrayList<>();
                    for (DataSnapshot vectorDataSnapshot: snapshot.getChildren()) {
                        children.add(vectorDataSnapshot);
                    }
                    List<DataSnapshot> childrenWithoutPolicyID = children.subList(0, children.size() - 1);

                    //Get policyID corresponding to the embeddings
                    //policyID is the last child in the snapshot
                    int lastIndex = children.size() -1;
                    String policyID = String.valueOf(children.get(lastIndex).getValue(String.class));

                    /**
                     * Retrieve embeddings of policy content
                     */
                    List<List<Double>> embeddingList = new ArrayList<>();
                    List<Long> embeddingIndexList = new ArrayList<>();
                    for (DataSnapshot vectorDataSnapshot: childrenWithoutPolicyID) {
                        ArrayList<Double> embedding = (ArrayList<Double>) vectorDataSnapshot.child("embedding").getValue();

                        //Retrieve the INDEX of embeddings
                        //This index will be used to retrieve the corresponding index of policy text
                        Long embeddingIndex = (Long) vectorDataSnapshot.child("index").getValue();
                        embeddingList.add(embedding);
                        embeddingIndexList.add(embeddingIndex);
                    }
                    //System.out.println("EmbeddingList: " + embeddingList);

                    /**
                     * Find embeddings with highest cosine similarity with user input
                     */
                    //index of embeddings with highest cosine similarity
                    int matchedEmbeddingIndex = highestSimilarity(embeddingList, embedInput);
                    //corresponding index in Firebase Database
                    Long matchedContentIndex = embeddingIndexList.get(matchedEmbeddingIndex);

                    /**
                     * Find the matching content of the highest similarity vector
                     */
                    mDatabase.child("Policy").child(policyID).child("content" + matchedContentIndex).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String response = (String) snapshot.getValue();
                            updateUI(response);
                            System.out.println("Response: " + response);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    };

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        /**
         * Find index of the embeddings with highest cosine similarity with user input
         * @param embeddingList
         * @param embedInput
         * @return
         */
        private int highestSimilarity(List<List<Double>> embeddingList, Embedding embedInput) {
            double highestSimilarity = -1;
            int highestIndex = 0;

            for (int i=0; i<embeddingList.size(); i++) {
                double similarity = cosineSimilarity(embeddingList.get(i), embedInput.getEmbedding());

                System.out.println("Similarity");
                if (highestSimilarity < similarity) {
                    highestSimilarity = similarity;
                    highestIndex = i;
                }
            }
            System.out.println("Highest Similarity: " + highestSimilarity);
            return highestIndex;
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