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

            //Close the PDF Document
            pdfDocument.close();

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
            String policy_content_key = "Policy Provisions";
            String procedure_content_key = "Procedure Processes and Actions";

            if (title.contains("Policy")) {
                //if the document is a policy
                scope = text.substring(text.indexOf(scope_key) + scope_key.length(),
                        text.indexOf("Policy Provisions"));
                content = text.substring(text.indexOf(policy_content_key) + policy_content_key.length(),
                        text.indexOf("Accountabilities"));
            } else {
                scope = text.substring(text.indexOf(scope_key) + scope_key.length(),
                        text.indexOf("Are Local Documents"));
                content = text.substring(
                        text.indexOf(procedure_content_key) + procedure_content_key.length(),
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

            Document newDocument = new Document(title, purpose, scope, content, responsible_officer, contact_officer, parent_doc);
            pdfDocument.close();
            return newDocument;

        } catch (IOException e) {
            e.printStackTrace();

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

}