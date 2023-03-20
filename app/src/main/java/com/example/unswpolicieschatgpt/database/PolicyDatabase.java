package com.example.unswpolicieschatgpt.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.unswpolicieschatgpt.MainActivity;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.cos.COSStream;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.IOException;
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
    PolicyDatabase database;

    //Create Dao
    public abstract PolicyDao mainDao();


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

    public Policy getPolicyFromPDF(String text) {
        //Table schema
        String title, purpose, scope, content, responsible_officer, contact_officer, parent_doc;
        //Find title;
        //Note: Every PDF file page starts with a series of dash '-' so need to eliminate these

        String titleString = text.substring(0, text.indexOf("Page 1 of"));
        title = titleString.replace(String.valueOf('-'), "");
        System.out.println("Title: " + title);

        /**
         * Find purpose, scope and content of the document
         */
        //Find purpose
        String purpose_key = "Purpose";
        purpose = text.substring(text.indexOf(purpose_key) + purpose_key.length(), text.indexOf("Scope"));
        System.out.println("Purpose: " + purpose);

        //Find scope and content
        //Note: Every Procedure documents have an extra column local_doc_permit, Policy don't have

        String scope_key = "Scope";
        String policy_content_key = "Policy Provisions";
        String procedure_content_key = "Procedure Processes and Actions";

        if (title.contains("Policy")) {
            //if the document is a policy
            scope = text.substring(text.indexOf(scope_key) + scope_key.length(),
                    text.indexOf("Policy Provisions"));
            System.out.println(scope);
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
        System.out.println("Scope: " + scope);
        System.out.println("Content: " + content);

        /**
         * Find accountabilities
         */
        String res_officer_key = "Responsible Officer";
        //Find responsible officer
        responsible_officer = text.substring(text.indexOf(res_officer_key) + res_officer_key.length(),
                text.indexOf("Contact Officer"));
        System.out.println("Responsible Officer " + responsible_officer);
        //Find contact officer
        String contact_officer_key = "Contact Officer";
        contact_officer = text.substring(text.indexOf(contact_officer_key) + contact_officer_key.length(),
                text.indexOf("Supporting Information"));
        System.out.println("Contact Officer " + contact_officer);


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

        Policy newPolicy = new Policy(title, purpose, scope, content, responsible_officer, contact_officer, parent_doc);

        return newPolicy;
    }
}
