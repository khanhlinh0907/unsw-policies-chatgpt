package com.example.unswpolicieschatgpt.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.unswpolicieschatgpt.MainActivity;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//Add database entities
@Database(entities = {Policy.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class PolicyDatabase extends RoomDatabase {
    //Define database name
    private static final String DATABASE_NAME = "Document_Database";
    //Create database instance
    private static PolicyDatabase database;

    //Create Dao
    public abstract PolicyDao mainDao();

    public ArrayList<URL> insertURLList() throws MalformedURLException {
        ArrayList<URL> urlList = new ArrayList<>();
        urlList.add(new URL("https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/assessmentdesignprocedure.pdf"));
        urlList.add(new URL("https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/assessmentimplementationprocedure.pdf"));
        urlList.add(new URL("https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/assessmentpolicy.pdf"));
        urlList.add(new URL("https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/academicprogressionenrolmentpolicy.pdf"));
        urlList.add(new URL("https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/enrolmentwithdrawalprocedure.pdf"));
        urlList.add(new URL("https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/rplprocedure.pdf"));
        return urlList;
    }

    public ArrayList<Policy> getDatabase(Context context) throws MalformedURLException {
        PolicyDao policyDao = database.mainDao();
        //Add documents to Room Database
        ArrayList<URL> urlList = database.insertURLList();
        for (URL url : urlList) {
            PDFTextExtractor textExtractor = new PDFTextExtractor();
            Policy policy = textExtractor.PDFTextExtractor(context, url);
            policy.setPdf_url(url);
            policyDao.insert(policy);
        }
        //Get all documents in the database
        ArrayList<Policy> policyList = policyDao.getAll();
        return policyList;
    }
}
