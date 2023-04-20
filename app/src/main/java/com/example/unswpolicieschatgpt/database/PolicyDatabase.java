package com.example.unswpolicieschatgpt.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.unswpolicieschatgpt.SearchActivity;
import com.example.unswpolicieschatgpt.chatgptapi.ChatGPTClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theokanning.openai.embedding.Embedding;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Add database entities
@Database(entities = {Policy.class}, version = 3, exportSchema = false)
@TypeConverters({Converters.class})

public abstract class PolicyDatabase extends RoomDatabase {
    //Define database name
    private static final String DATABASE_NAME = "Document_Database";

    //Create Dao
    public abstract PolicyDao mainDao();

    /**
     * List of policy document URLs
     * @return
     * @throws MalformedURLException
     */
    public ArrayList<URL> getURLList() throws MalformedURLException {
        ArrayList<URL> urlList = new ArrayList<>();
        urlList.add(new URL("https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/assessmentdesignprocedure.pdf"));
        urlList.add(new URL("https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/assessmentimplementationprocedure.pdf"));
        urlList.add(new URL("https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/assessmentpolicy.pdf"));
        urlList.add(new URL("https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/academicprogressionenrolmentpolicy.pdf"));
        urlList.add(new URL("https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/enrolmentwithdrawalprocedure.pdf"));
        urlList.add(new URL("https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/rplprocedure.pdf"));
        return urlList;
    }

    public void setupDatabase(Context context) throws MalformedURLException {
        //Create database instance
        PolicyDatabase mDatabase = Room.databaseBuilder(context,
                PolicyDatabase.class, "Policy_Database").fallbackToDestructiveMigration().build();
        mDatabase.clearAllTables();
        ArrayList<URL> urlList = mDatabase.getURLList();
        for (URL url: urlList) {
            //Load PDF into text
            String pdfText = mDatabase.loadPDF(url, context);
            //Create new Policy from text
            Policy newPolicy = mDatabase.getPolicyFromPDF(pdfText);
            //Set URL to policy
            newPolicy.setPdf_url(String.valueOf(url));
            Log.d("PDF_URL", "URL for policy " + newPolicy.getTitle() + " set to " + newPolicy.getPdf_url());
            //Add new policy to Room Database
            //Check if a policy with the same PDF URL already exists in the database
            Policy existingPolicy = mDatabase.mainDao().getByPdfUrl(String.valueOf(url));
            if (existingPolicy != null) {
                Log.d("POLICY_INSERT", "Policy with PDF URL " + url + " already exists in database");
            } else {
                //Insert new policy into Room Database
                mDatabase.mainDao().insert(newPolicy);
                Log.d("POLICY_INSERT", "Added new policy to Room database");
            }
        }
    }
    public String loadPDF(URL pdf_url, Context context) {
        PDFBoxResourceLoader.init(context);

        PDDocument pdfDocument = new PDDocument();
        try {
            //Load PDF document from URL
            InputStream inputStream = pdf_url.openStream();
            pdfDocument = PDDocument.load(inputStream);

            //Extract text from PDF document using PDFTextStripper
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(pdfDocument);

            //Close the PDF Document
            pdfDocument.close();

            //Return the extracted test
            return text;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create Policy Objects from extracted text from PDF files
     * @param text
     * @return
     */
    public Policy getPolicyFromPDF(String text) {
        //Table schema
        String title, purpose, scope, content, responsible_officer, contact_officer, parent_doc;
        //Find title;
        //Note: Every PDF file page starts with a series of dash '-' so need to eliminate these

        String titleString = text.substring(0, text.indexOf("Page 1 of"));
        title = titleString.replaceAll("^\\p{Pc}+", "").trim();

        /**
         * Find purpose, scope and content of the document
         */
        //Find purpose
        String purpose_key = "Purpose";
        purpose = text.substring(text.indexOf(purpose_key) + purpose_key.length(), text.indexOf("Scope"));

        //Find scope and content
        //Note: Every Procedure documents have an extra column local_doc_permit, Policy don't have

        String scope_key = "Scope";
        String policy_content_key = "Policy Provisions";
        String procedure_content_key = "Procedure Processes and Actions";

        if (title.contains("Policy")) {
            //if the document is a policy
            scope = text.substring(text.indexOf(scope_key) + scope_key.length(),
                    text.indexOf("Policy Provisions"));
            content = text.substring(text.indexOf(policy_content_key) + policy_content_key.length(),
                    text.indexOf("Accountabilities"));
        } else if (title.contains("Procedure")) {
            scope = text.substring(text.indexOf(scope_key) + scope_key.length(),
                    text.indexOf("Are Local Documents"));
            content = text.substring(
                    text.indexOf(procedure_content_key) + procedure_content_key.length(),
                    text.indexOf("Accountabilities"));
        } else {
            scope = null;
            content = null;
        }

        /**
         * Find accountabilities
         */
        String res_officer_key = "Responsible Officer";
        //Find responsible officer
        responsible_officer = text.substring(text.indexOf(res_officer_key) + res_officer_key.length(),
                text.indexOf("Contact Officer"));
        //Find contact officer
        String contact_officer_key = "Contact Officer";
        contact_officer = text.substring(text.indexOf(contact_officer_key) + contact_officer_key.length(),
                text.indexOf("Supporting Information"));


        /**
         * Find supporting information
         */
        String parent_doc_key = "Parent Document (Policy)";
        // Procedure documents have parent_doc as Policy document
        if (title.contains("Procedure")) {
            parent_doc = text.substring(text.indexOf(parent_doc_key) + parent_doc_key.length(),
                    text.indexOf("Supporting Documents"));
        } else {
            parent_doc = null;
        }

        Policy newPolicy = new Policy(title, purpose, scope, content, contact_officer, responsible_officer, parent_doc);

        return newPolicy;
    }

    /**
     * Upload policy database into Firebase Database
     */
    public static class UploadTask extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        private PolicyDatabase mDatabase;
        private ChatGPTClient chatGPTClient = new ChatGPTClient("YOUR_API_KEY");

        FirebaseDatabase mFirebaseDatabase;

        public UploadTask(Context context) {
            mContext = context;
            //mDatabase = database;
            FirebaseApp.initializeApp(context);
            mFirebaseDatabase = FirebaseDatabase.getInstance("https://unswpolicychatbot-default-rtdb.asia-southeast1.firebasedatabase.app/");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get the data from the Room database
            PolicyDatabase mDatabase = Room.databaseBuilder(mContext,
                    PolicyDatabase.class, "Policy_Database").fallbackToDestructiveMigration().build();
            try {
                mDatabase.setupDatabase(mContext);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            List<Policy> policyList = mDatabase.mainDao().getAll();

            /**
             * Upload policy information to Firebase
              */

            DatabaseReference policyRef = mFirebaseDatabase.getReference("Policy");

            // Count number of children in policyRef
            policyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long count = dataSnapshot.getChildrenCount();

                    // Use the count variable here
                    if (count < policyList.size()) {
                        for (Policy item : policyList) {
                            String [] policySection = item.getPolicySection(item.getContent());
                            //Get the ID of policy and set as the unique identifier for Policy child node in Firebase
                            policyRef.child(String.valueOf(item.getId())).setValue(item);

                            //Add policy sections to Firebase child node
                            Map<String, Object> policy = new HashMap<>();

                            for (int i = 1; i < policySection.length; i++) {
                                policy.put("content" + i, policySection[i]);
                            }
                            policyRef.child(String.valueOf(item.getId())).updateChildren(policy);
                        }

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });
            /**
             * Upload vectors of policy information to Firebase Database
             */
            DatabaseReference vectorRef = mFirebaseDatabase.getReference("Vector");

            // Count number of children in vectorRef
            vectorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long countVector = dataSnapshot.getChildrenCount();

                    // Use the count variable here
                    if (countVector < policyList.size()) {
                        for (Policy item : policyList) {
                            String [] policySection = item.getPolicySection(item.getContent());

                            new Thread(() -> {
                                //Convert policy sections into vectors
                                List<Embedding> policyEmbedding = chatGPTClient.createEmbeddings(Arrays.asList(policySection));

                                //Create a Map object with all vectors
                                Map<String, Object> vectors = new HashMap<>();
                                for (int i = 1; i < policyEmbedding.size(); i++) {
                                    vectors.put("content" + i, policyEmbedding.get(i));
                                }
                                //Add vectors to a new child
                                vectorRef.child(String.valueOf(item.getId())).setValue(vectors);
                            }).start();

                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Update the UI here if necessary
        }
    }
}
