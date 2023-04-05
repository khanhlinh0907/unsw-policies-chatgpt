package com.example.unswpolicieschatgpt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.unswpolicieschatgpt.chatgptapi.ChatGPTClient;
import com.example.unswpolicieschatgpt.chatgptapi.VectorContent;
import com.example.unswpolicieschatgpt.chatgptapi.VectorData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theokanning.openai.embedding.Embedding;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ChatbotConversationActivity extends AppCompatActivity {

    private Button mBackButton;
    private Button mSendButton;
    private EditText mInputField;
    private RecyclerView mConvoRv;
    private ChatbotConversationAdapter adapter;
    private ArrayList<ConversationMessage> mConversation = new ArrayList<>();;


    private ChatGPTClient chatGPTClient;

    private List<Embedding> embeddedParagraphs;

    private Embedding embeddedQueryOne;
    private Embedding embeddedQueryTwo;
    private Embedding embeddedQueryThree;
    //Firebase Database
    private DatabaseReference databaseReference;

    private String [] testPolicySection;
    private List <Embedding> sectionEmbedding;
//    private String userQuery;
//    private Embedding userQueryEmbedding;
//    private List<Double> calculated_similarity;


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


                    mConversation.add(new ConversationMessage("BOT", "Sorry this is my fast day on the job. Let me ask my manager..."));


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







//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    chatGPTClient.sendPrompt("Tell me about butterflies.");
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start()

//        /**
//         * Create vector database
//         */
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                PolicyDatabase policyDatabase = Room.databaseBuilder(ChatbotConversationActivity.this,
//                        PolicyDatabase.class, "Policy_Database").build();
//                PolicyDao mainDao = policyDatabase.mainDao();
//                try {
//                    policyDatabase.setupDatabase(ChatbotConversationActivity.this);
//                    List<Policy> retrievedPolicyList = mainDao.getAll();
//                    policyDatabase.close();
//
//                    //Test policy section
//                    Policy testPolicy = retrievedPolicyList.get(0);
//                    String testContent = testPolicy.getContent();
//                    testPolicySection = policyDatabase.getPolicySection(testContent);
//
//                    //Push policy content to Firebase
//                    DatabaseReference policyRef = mFirebaseDatabase.getReference("Policy").push();
//                    Map<String, Object> policy = new HashMap<>();
//
//                    for (int i = 1; i < testPolicySection.length; i++) {
//                        policy.put("content" + i, testPolicySection[i]);
//                    }
//
//                    policy.put("title", testPolicy.getTitle());
//                    policyRef.setValue(policy);
//
//                    //Push content vectors to Firebase
//                    sectionEmbedding = chatGPTClient.createEmbeddings(Arrays.asList(testPolicySection));
//                    DatabaseReference vectorRef = mFirebaseDatabase.getReference("Vector").push();
//
//                    //Create a Map object with all vectors
//                    Map<String, Object> vectors = new HashMap<>();
//
//                    //Find the correlated policyID
//                    String policyID = policyRef.getKey();
//                    vectors.put("policyID", policyID);
//
//                    for (int j = 1; j < sectionEmbedding.size(); j++) {
//                        vectors.put("content" + j, sectionEmbedding.get(j));
//                    }
//
//                    //Add vectors to a new child
//                    vectorRef.setValue(vectors);
//
//                } catch (MalformedURLException e) {
//                    throw new RuntimeException(e);
//                }
//
//            }
//        }).start();

        /**
         * Create embedding for user's query - Lachlan
         */

        /**
         * Calculate cosine similarity between user's query and database
         */
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (Embedding vector: sectionEmbedding) {
//                    //Calculate cosine similarity between user's query and current section
//                    //Test with one policy for now, not loading from firebase yet
//                    double similarity = cosineSimilarity(vector.getEmbedding(), userQueryEmbedding.getEmbedding());
//                    //Add calculation results to the list of calculated_similarity
//                    calculated_similarity.add(similarity);
//                }
//                //Find highest similarity
//                double highest_similarity = calculated_similarity.get(0);
//                for (int a = 1; a < calculated_similarity.size(); a++) {
//                    if (highest_similarity < calculated_similarity.get(a)) {
//                        highest_similarity = calculated_similarity.get(a);
//
//                        //Extract the correlated text
//                        String response = testPolicySection[a];
//                    }
//                }
//                /**
//                 * Display the response to the screen - Lachlan
//                 */
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
//
//
//            }
//        });

        //Testing for accuracy of semantic matches by cosine similarity.
        //Embed two example paragraphs using ada-002 model.
        //Test paragraph 1 refers to number of exams a student can take in a day.
        //Test paragraph two refers to maximum weighting of exams.

        //Query 1 asks about having 4 exams in a day.
        //Query 2 asks about having an assessment weighted 40%.


        /*
        List<String> testParagraphs = Arrays.asList("Students will normally be required to sit no more than two examinations in a day.", "No single assessment task, including examinations but excluding research- or project-based\n" +
                "assessments and theses, will be weighted more than 60% of the overall course result. Assessment\n" +
                "requirements of accreditation bodies are exempt from this limit.");

        String queryOne = "I have four exams scheduled on the same day. Is this allowed?";
        String queryTwo = "I have a mid term exam weighted 40% of my overall mark. Is this allowed?";

        String queryThree = "Who can I contact about the Assessment Design Procedure?";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Create embeddings of test paragraphs.
                    embeddedParagraphs = chatGPTClient.createEmbeddings(testParagraphs);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        //Embed the queries as vectors.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Create embeddings of test paragraphs.
                    embeddedQueryOne = chatGPTClient.embedQuery(queryOne);
                    embeddedQueryTwo = chatGPTClient.embedQuery(queryTwo);
                    embeddedQueryThree = chatGPTClient.embedQuery(queryThree);



                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (!embeddedParagraphs.isEmpty()) {
                                // Compute the cosine similarity between the three vectors

                                //Paragraph One and Query One
                                double similarity11 = cosineSimilarity(embeddedParagraphs.get(0).getEmbedding(), embeddedQueryOne.getEmbedding());
                                System.out.println("Cosine similarity between par1 and query1: " + similarity11);

                                //Paragraph One and Query Two
                                double similarity12 = cosineSimilarity(embeddedParagraphs.get(0).getEmbedding(), embeddedQueryTwo.getEmbedding());
                                System.out.println("Cosine similarity between par1 and query2: " + similarity12);

                                //Paragraph Two and Query One
                                double similarity21 = cosineSimilarity(embeddedParagraphs.get(1).getEmbedding(), embeddedQueryOne.getEmbedding());
                                System.out.println("Cosine similarity between par2 and query1: " + similarity21);

                                //Paragraph Two and Query Two
                                double similarity22 = cosineSimilarity(embeddedParagraphs.get(1).getEmbedding(), embeddedQueryTwo.getEmbedding());
                                System.out.println("Cosine similarity between par2 and query2: " + similarity22);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

         */
    }

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



    private class SemanticThread extends Thread {
        private String userInput;
        private String openAIEndpoint;
        private String openAIKey;
        private List<Double> calculated_similarity;

        private List<VectorContent> similarVectorContent;

        private Embedding embedInput;
        private VectorData vectorData;
        private List<VectorContent> contentList;


        public SemanticThread(String userInput, String openAIKey) {
            this.userInput = userInput;
            this.openAIKey = openAIKey;
        }

        @Override
        public void run() {
            // Code to send user input to OpenAI and retrieve embedding goes here
            chatGPTClient = new ChatGPTClient("token");

            System.out.println(userInput);
            embedInput = chatGPTClient.embedQuery(userInput);


            //Retrieve vectors from Firebase.
            FirebaseApp.initializeApp(getApplicationContext());
            FirebaseDatabase mFirebaseDatabase;
            mFirebaseDatabase = FirebaseDatabase.getInstance("https://unswpolicychatbot-default-rtdb.asia-southeast1.firebasedatabase.app/");

            DatabaseReference mDatabase = mFirebaseDatabase.getReference();


            mDatabase.child("Vector").child("-NS64UZECuTnz5pw_x56").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Handling successful retrieval of vectors.
                    //Vector Data represents Firebase representation of contents.
                    VectorData vectorData = snapshot.getValue(VectorData.class);
                    Log.e("firebase", "Success");

                    System.out.println("semantic thread: vector data:  " +  vectorData);
                    System.out.println("semantic thread: snapshot: " + snapshot.getValue());

                    contentList = new ArrayList<>();

                    System.out.println("semantic thread: content List " + contentList);


                    //ContentList retrieves all contents of VectorData class.
                    contentList = vectorData.getVectorContent();


                    //Calculate cosine similarity between each vector and the query.
                    for (int i=0; i<contentList.size(); i++) {
                        double similarity = cosineSimilarity(contentList.get(i).getEmbedding(), embedInput.getEmbedding());


                        //Creates two lists. One to store the vectors and content, and one to store the similarity scores.
                        similarVectorContent.add(contentList.get(i));
                        calculated_similarity.add(similarity);
                    }


                    //Finding the highest similarity both content and score wise.

                    double highestSimilarity = calculated_similarity.get(0);
                    VectorContent highestVector = contentList.get(0);

                    for (int j = 1; j < contentList.size(); j++) {
                        if (highestSimilarity < calculated_similarity.get(j)) {

                            //Sets similarity score to higher value.
                            highestSimilarity = calculated_similarity.get(j);

                            //Sets vector content with highest similarity score.
                            highestVector = contentList.get(j);
                        }
                    }

                    //Use the highest vector to run another firebase query to find the relevant paragraph.
                    mDatabase.child("Policy").child("NS64UZECuTnz5pw_x56").child("content" + highestVector.getIndex()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String response = (String) snapshot.getValue();

                            updateUI(response);
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


//
//            mDatabase.child("Vector").child("NS4bAh0YoMrxADkVnbw").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    //Handling successful retrieval of vectors.
//                    //Vector Data represents Firebase representation of contents.
//                    VectorData vectorData = snapshot.getValue(VectorData.class);
//                    Log.e("firebase", "Success");
//
//                    System.out.println("semantic thread: vector data:  " +  vectorData);
//                    System.out.println("semantic thread: snapshot: " + snapshot.getValue());
//                    System.out.println("semantic thread: content List " + contentList);
//
//
//                    //ContentList retrieves all contents of VectorData class.
//                    contentList = vectorData.getVectorContent();
//
//
//                    //Calculate cosine similarity between each vector and the query.
//                    for (int i=0; i<contentList.size(); i++) {
//                        double similarity = cosineSimilarity(contentList.get(i).getEmbedding(), embedInput.getEmbedding());
//
//
//                        //Creates two lists. One to store the vectors and content, and one to store the similarity scores.
//                        similarVectorContent.add(contentList.get(i));
//                        calculated_similarity.add(similarity);
//                    }
//
//
//                    //Finding the highest similarity both content and score wise.
//
//                    double highestSimilarity = calculated_similarity.get(0);
//                    VectorContent highestVector = contentList.get(0);
//
//                    for (int j = 1; j < contentList.size(); j++) {
//                        if (highestSimilarity < calculated_similarity.get(j)) {
//
//                            //Sets similarity score to higher value.
//                            highestSimilarity = calculated_similarity.get(j);
//
//                            //Sets vector content with highest similarity score.
//                            highestVector = contentList.get(j);
//                        }
//                    }
//
//                    //Use the highest vector to run another firebase query to find the relevant paragraph.
//                    mDatabase.child("Policy").child("NS4bA03vpGJARciijL4").child("content" + highestVector.getIndex()).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            String response = snapshot.getValue().toString();
//
//                            updateUI(response);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });

//
//
//            mDatabase.child("Vector").child("NS4bA03vpGJARciijL4").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                    if (!task.isSuccessful()) {
//                        //Handling if firebase call is unsuccessful.
//                        Log.e("firebase", "Error getting data", task.getException());
//                    } else {
//                        //Handling successful retrieval of vectors.
//                        //Vector Data represents Firebase representation of contents.
//                        vectorData = task.getResult().getValue(VectorData.class);
//                        Log.e("firebase", "Success");
//
//                        System.out.println("semantic thread: vector data:  " +  vectorData);
//                        System.out.println("semantic thread: task: " + task.getResult());
//                        System.out.println("semantic thread: content List " + contentList);
//
//                        //ContentList retrieves all contents of VectorData class.
//                        contentList = vectorData.getVectorContent();
//
//
//                        //Calculate cosine similarity between each vector and the query.
//                        for (int i=0; i<contentList.size(); i++) {
//                            double similarity = cosineSimilarity(contentList.get(i).getEmbedding(), embedInput.getEmbedding());
//
//
//                            //Creates two lists. One to store the vectors and content, and one to store the similarity scores.
//                            similarVectorContent.add(contentList.get(i));
//                            calculated_similarity.add(similarity);
//                        }
//
//
//                        //Finding the highest similarity both content and score wise.
//
//                        double highestSimilarity = calculated_similarity.get(0);
//                        VectorContent highestVector = contentList.get(0);
//
//                        for (int j = 1; j < contentList.size(); j++) {
//                            if (highestSimilarity < calculated_similarity.get(j)) {
//
//                                //Sets similarity score to higher value.
//                                highestSimilarity = calculated_similarity.get(j);
//
//                                //Sets vector content with highest similarity score.
//                                highestVector = contentList.get(j);
//                            }
//                        }
//
//                        //Use the highest vector to run another firebase query to find the relevant paragraph.
//                        mDatabase.child("Policy").child("NS4bA03vpGJARciijL4").child("content" + highestVector.getIndex()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                                String response = task.getResult().toString();
//
//                                updateUI(response);
//                            }
//                        });
//
//
//                    }
//                }
//            });

            //Extract the embedding material from VectorData. VectorData contains a list of VectorContents - which has embeddings, index and object attributes.

//            List<List<Embedding>> contentList = new ArrayList<>();
//
//            List<Embedding> sectionEmbedding = new ArrayList<>();
//
//
//            for (VectorContent vectorContent : resultVector.getVectorContent()) {
//                sectionEmbedding.add(vectorContent.getEmbedding());
//
//            }


            //Calculating cosine simlarity between user query and each embedding.

//            for (int a = 0; a < sectionEmbedding.size(); a++) {
//
//                //Calculate cosine similarity between user's query and current section
//                //Test with one policy for now, not loading from firebase yet
//                double similarity = cosineSimilarity(sectionEmbedding.get(a), embedInput.getEmbedding());
//                //Add calculation results to the list of calculated_similarity
//                calculated_similarity.add(similarity);
//            }
//            //Find highest similarity
//            double highest_similarity = calculated_similarity.get(0);
//
//            String response = "";
//
//            for (int a = 1; a < calculated_similarity.size(); a++) {
//                if (highest_similarity < calculated_similarity.get(a)) {
//                    highest_similarity = calculated_similarity.get(a);
//
//                    //Extract the correlated text
//                    response = testPolicySection[a];
//                }
//            }
//            updateUI(response);
        }

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

}