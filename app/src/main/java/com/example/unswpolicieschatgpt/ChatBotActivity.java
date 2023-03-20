package com.example.unswpolicieschatgpt;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unswpolicieschatgpt.chatgptapi.ChatGPTClient;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.service.OpenAiService;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ChatBotActivity extends AppCompatActivity {

    private ChatGPTClient chatGPTClient;

    private List<Embedding> embeddedParagraphs;

    private Embedding embeddedQueryOne;
    private Embedding embeddedQueryTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);





        //Code testing the ChatGPT API library.
        //REPLACE BELOW TOKEN WITH YOUR OWN API_KEY. DO NOT PUSH THE TOKEN TO GITHUB.
        String token = "YOUR_API_KEY";

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
