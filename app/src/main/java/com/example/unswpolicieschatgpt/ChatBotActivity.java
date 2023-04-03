package com.example.unswpolicieschatgpt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.unswpolicieschatgpt.chatgptapi.ChatGPTClient;
import com.example.unswpolicieschatgpt.database.Policy;
import com.example.unswpolicieschatgpt.database.PolicyDao;
import com.example.unswpolicieschatgpt.database.PolicyDatabase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theokanning.openai.embedding.Embedding;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatBotActivity extends AppCompatActivity {

    private ChatGPTClient chatGPTClient;

    private List<Embedding> embeddedParagraphs;

    private Embedding embeddedQueryOne;
    private Embedding embeddedQueryTwo;
    private Embedding embeddedQueryThree;
    //Firebase Database
    private DatabaseReference databaseReference;

    private String [] testPolicySection;
    private List <Embedding> sectionEmbedding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        /**
         * Connect Firebase Database with Android App
         */
        FirebaseApp.initializeApp(this);
        FirebaseDatabase mFirebaseDatabase;
        mFirebaseDatabase = FirebaseDatabase.getInstance("https://unswpolicychatbot-default-rtdb.asia-southeast1.firebasedatabase.app/");





        //Code testing the ChatGPT API library.
        //REPLACE BELOW TOKEN WITH YOUR OWN API_KEY. DO NOT PUSH THE TOKEN TO GITHUB.
        String token = "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";

        chatGPTClient = new ChatGPTClient(token);

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
//        }).start();





        //Policy policyTesting = new Policy();
        /*mFirebaseDatabase.getReference("Policy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Retrieve data from database
                Policy result = snapshot.getValue(Policy.class);
                
                if (result!=null) {

                    Log.d(TAG, "retrieveDataFromDB");
                    policyTesting.setTitle(result.getTitle());
                    policyTesting.setPurpose(result.getPurpose());
                    policyTesting.setContact_officer(result.getContact_officer());
                    policyTesting.setScope(result.getScope());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("databaseReference failed!");
            }
        });*/


        new Thread(new Runnable() {
            @Override
            public void run() {
                PolicyDatabase policyDatabase = Room.databaseBuilder(ChatBotActivity.this,
                        PolicyDatabase.class, "Policy_Database").build();
                PolicyDao mainDao = policyDatabase.mainDao();
                try {
                    policyDatabase.setupDatabase(ChatBotActivity.this);
                    List<Policy> retrievedPolicyList = mainDao.getAll();
                    policyDatabase.close();

                    //Test policy section
                    Policy testPolicy = retrievedPolicyList.get(0);
                    String testContent = testPolicy.getContent();
                    testPolicySection = policyDatabase.getPolicySection(testContent);
                    for (int i = 0; i < testPolicySection.length; i++) {
                        System.out.println("Section" + (i+1) + ": " + testPolicySection[i]);
                    }
                    sectionEmbedding = chatGPTClient.createEmbeddings(Arrays.asList(testPolicySection));
                    DatabaseReference vectorRef = mFirebaseDatabase.getReference("Vector").push();

                    String title = testPolicy.getTitle();
                    //Create a new child with the name as policy title

                    //Create a new child under Vector with the policy title
                    //Create a Map object with all vectors
                    Map<String, Object> vectors = new HashMap<>();
                    for (int j = 0; j < sectionEmbedding.size(); j++) {
                        vectors.put("section_" + j, sectionEmbedding.get(j));
                    }
                    vectors.put("title", title);
                    //Add vectors to a new child
                    vectorRef.setValue(vectors);

                    //Each policy vector  will store all vectors of policy sections + title of

                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();

    //Testing for accuracy of semantic matches by cosine similarity.
    //Embed two example paragraphs using ada-002 model.
        //Test paragraph 1 refers to number of exams a student can take in a day.
        //Test paragraph two refers to maximum weighting of exams.

        //Query 1 asks about having 4 exams in a day.
        //Query 2 asks about having an assessment weighted 40%.



        List<String> testParagraphs = Arrays.asList("Students will normally be required to sit no more than two examinations in a day.", "No single assessment task, including examinations but excluding research- or project-based\n" +
                "assessments and theses, will be weighted more than 60% of the overall course result. Assessment\n" +
                "requirements of accreditation bodies are exempt from this limit.");

        String queryOne = "I have four exams scheduled on the same day. Is this allowed?";
        String queryTwo = "I have a mid term exam weighted 40% of my overall mark. Is this allowed?";

        String queryThree = "Who can I contact about the Assessment Design Procedure?";

        /*

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

}
