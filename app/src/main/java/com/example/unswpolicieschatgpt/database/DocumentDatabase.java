package com.example.unswpolicieschatgpt.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

//Add database entities
@Database(entities = {Document.class}, version = 1, exportSchema = false)
public abstract class DocumentDatabase extends RoomDatabase {
    //Define database name
    private static final String DATABASE_NAME = "Document_Database";
    //Create database instance
    private static DocumentDatabase database;

    //Create Dao
    public abstract DocumentDao mainDao();

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

    public void insertDocumentToDatabase() {

    }
    }
