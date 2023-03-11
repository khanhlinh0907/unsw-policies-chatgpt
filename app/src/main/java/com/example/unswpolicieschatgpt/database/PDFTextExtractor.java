package com.example.unswpolicieschatgpt.database;

import android.content.Context;
import android.net.Uri;

import androidx.room.Room;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
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
    final String DB_URL = "jdbc:mysql://localhost/documentdatabase";
    // Define the schema of the table
    final String TABLE_NAME = "DOCUMENT";

    public String PDFTextExtractor(Context context, URL url) {
        PDFBoxResourceLoader.init(context);
        PDDocument pdfDocument = new PDDocument();
        try {
            pdfDocument = PDDocument.load(url.openStream());
            System.out.println(pdfDocument.getNumberOfPages());
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(pdfDocument);

            setupDatabase(text);

            return text;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
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

    private void setupDatabase(String text) throws SQLException {
        /**
         *  Create MySQL table to store database
         */
        //Connect to the database

        Connection conn = DriverManager.getConnection(DB_URL);
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
        insertDocument(text);
    }

    private void insertDocument(String text) throws SQLException {
        //Table schema
        String title, purpose, scope, content, responsible_officer, contact_officer;
        //Find title;
        title = text.substring(0, text.indexOf("Page 1 of 6"));

        /**
         * Find purpose, scope and content of the document
         */
        //Find purpose
        purpose = text.substring(text.indexOf("Purpose") + 1, text.indexOf("Scope"));

        //Find scope and content
        //Note: Every Procedure documents have an extra column local_doc_permit, Policy don't have

        if (title.contains("Policy")) {
            //if the document is a policy
            scope = text.substring(text.indexOf("Scope") + 1,
                    text.indexOf("Policy Provisions"));
            content = text.substring(text.indexOf("Policy Provisions") + 1,
                    text.indexOf("Accountabilities"));
        } else {
            scope = text.substring(text.indexOf("Scope") + 1,
                    text.indexOf("Are Local Documents on this subject permitted?"));
            content = text.substring(
                    text.indexOf("Procedure Processes and Actions") + 1,
                    text.indexOf("Accountabilities"));
        }

        /**
         * Find accountabilities
         */
        //Find responsible officer
        responsible_officer = text.substring(text.indexOf("Accountabilities") + 1,
                text.indexOf("Contact Officer"));

        //Find contact officer
        contact_officer = text.substring(text.indexOf("Contact Officer") + 1,
                text.indexOf("Supporting Information"));

        /**
         * Find supporting information
         */
        // Procedure documents have parent_doc as Policy document
        if (title.contains("Procedure")) {
            String parent_doc = text.substring(text.indexOf("Parent Document") + 1,
                    text.indexOf("Supporting Documents"));
        }

        Connection conn = DriverManager.getConnection(DB_URL);
        Statement st = conn.createStatement();
        PreparedStatement pSt = conn.prepareStatement("INSERT OR IGNORE INTO " + TABLE_NAME
                + "(id, title, purpose, scope, content, responsible_officer, contact_officer)"
                + "VALUES (?,?,?,?,?,?,?)"
        );

        /**
         * Insert data into database using PreparedStatement
         */
        pSt.setInt(1, 1);
        pSt.setString(2, title);
        pSt.setString(3, purpose);
        pSt.setString(4, scope);
        pSt.setString(5, content);
        pSt.setString(6, responsible_officer);
        pSt.setString(7, contact_officer);

        st.close();
        conn.close();
    }
}