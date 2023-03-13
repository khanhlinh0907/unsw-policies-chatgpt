package com.example.unswpolicieschatgpt.database;

import android.content.Context;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.cos.COSObject;
import com.tom_roush.pdfbox.cos.COSStream;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.text.PDFTextStripper;

/*import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;*/

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class PDFTextExtractor {
    /*final String DB_URL = "jdbc:mysql://127.0.0.1:3306/documentdatabase";
    // Define the schema of the table
    final String TABLE_NAME = "DOCUMENT";*/

    public Document PDFTextExtractor(Context context, URL url) {
        PDFBoxResourceLoader.init(context);
        PDDocument pdfDocument = new PDDocument();
        try {
            pdfDocument = PDDocument.load(url.openStream());
            /**
             * Remove footer
             */
            for (int i = 0; i < pdfDocument.getNumberOfPages(); i++) {
                PDPage page = pdfDocument.getPage(i);
                COSDictionary dictionary = page.getCOSObject();
                COSBase contents = dictionary.getDictionaryObject(COSName.CONTENTS);

                if (contents instanceof COSArray) {
                    COSArray cosArray = (COSArray) contents;
                    for (int j = 0; j < cosArray.size(); j++) {
                        COSBase object = cosArray.getObject(j);
                        if (object instanceof COSStream) {
                            COSStream cosStream = (COSStream) object;
                            String content = cosStream.getString();
                            if (content != null) {
                                content = content.replaceAll("FooterText", ""); // Update the modified content.
                                cosStream.setString(COSName.CONTENTS, content);
                            }
                        }
                    }
                } else if (contents instanceof COSStream) {
                    COSStream cosStream = (COSStream) contents;
                    String content = cosStream.getString();
                    if (content != null) {
                        content = content.replaceAll("FooterText", ""); // Update the modified content.
                        cosStream.setString(COSName.CONTENTS, content);
                    }
                }
            }

            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(pdfDocument);



            //setupDatabase();
            //insertDocument(text);

            //Table schema
            String title, purpose, scope, content, responsible_officer, contact_officer, parent_doc;
            //Find title;
            //Note: Every PDF file page starts with a series of dash '-' so need to eliminate these

            title = text.substring(96, text.indexOf("Page 1 of 6"));

            /**
             * Find purpose, scope and content of the document
             */
            //Find purpose
            String purpose_key = "Purpose";
            purpose = text.substring(text.indexOf(purpose_key) + purpose_key.length(), text.indexOf("Scope"));

            //Find scope and content
            //Note: Every Procedure documents have an extra column local_doc_permit, Policy don't have

            String scope_key = "Scope";
            if (title.contains("Policy")) {
                //if the document is a policy
                scope = text.substring(text.indexOf(scope_key) + scope_key.length(),
                        text.indexOf("Policy Provisions"));
                content = text.substring(text.indexOf("Policy Provisions") + 1,
                        text.indexOf("Accountabilities"));
            } else {
                scope = text.substring(text.indexOf(scope_key) + scope_key.length(),
                        text.indexOf("Are Local Documents"));
                content = text.substring(
                        text.indexOf("Procedure Processes and Actions") + 1,
                        text.indexOf("Accountabilities"));
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

            Document newDocument = new Document(1, title, purpose, scope, content, responsible_officer, contact_officer, parent_doc);
            pdfDocument.close();
            return newDocument;

        } catch (IOException e) {
            e.printStackTrace();
        /*} catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);*/
        } finally {
            if (pdfDocument != null) {
                try {
                    pdfDocument.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }


    /*private void setupDatabase() throws ClassNotFoundException {
     */

    /**
     * Create MySQL table to store database
     *//*
        //Connect to the database
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            Statement st = conn.createStatement();
            //Create the table of it does not exist
            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + "id INTEGER PRIMARY KEY autoincrement, "
                    + "title VARCHAR(255) NOT NULL, "
                    + "URL  VARCHAR(255)) NOT NULL, "
                    + "purpose VARCHAR(255)) NOT NULL, "
                    + "scope VARCHAR(255)) NOT NULL, "
                    + "content VARCHAR(255)) NOT NULL, "
                    + "responsible_officer VARCHAR(255)) NOT NULL, "
                    + "contact_officer VARCHAR(255)) NOT NULL, "
                    + "parent_doc VARCHAR(255)), ";
            st.executeUpdate(createTableQuery);

            st.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }*/
    private void insertDocument(String text) throws SQLException {




        /*Connection conn = DriverManager.getConnection(DB_URL);
        Statement st = conn.createStatement();
        PreparedStatement pSt = conn.prepareStatement("INSERT OR IGNORE INTO " + TABLE_NAME
                + "(id, title, purpose, scope, content, responsible_officer, contact_officer," +
                "parent_doc)"
                + "VALUES (?,?,?,?,?,?,?,?)"
        );

        *//**
         * Insert data into database using PreparedStatement
         *//*
        pSt.setInt(1, 1);
        pSt.setString(2, title);
        pSt.setString(3, purpose);
        pSt.setString(4, scope);
        pSt.setString(5, content);
        pSt.setString(6, responsible_officer);
        pSt.setString(7, contact_officer);
        pSt.setString(8, parent_doc);

        st.close();
        conn.close();
    }*/
    }
}