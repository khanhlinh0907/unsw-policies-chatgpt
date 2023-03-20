package com.example.unswpolicieschatgpt.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

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
import java.io.InputStream;
import java.net.URL;


public class PDFTextExtractor {
    private Context context;

    public PDFTextExtractor(Context context) {
        this.context = context.getApplicationContext();
    }

    public void PDFTextExtractor(URL url) {
        new AsyncTask<URL, Void, String>() {

            @Override
            protected String doInBackground(URL... urls) {
                //Initialize PDFBoxResourceLoader
                PDFBoxResourceLoader.init(context);

                PDDocument pdfDocument = new PDDocument();
                try {
                    //Load PDF document from URL
                    InputStream inputStream = urls[0].openStream();
                    pdfDocument = PDDocument.load(inputStream);

                    //Extract text from PDF document using PDFTextStripper
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    String text = pdfStripper.getText(pdfDocument);

                    //Close the PDF Document
                    pdfDocument.close();

                    //Return the extracted test
                    return text;
                    /**
                     * Remove footer
                     *//*
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
*/


                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String text) {
                if (text!= null) {
                    new InsertPolicyTask().execute(text);

                }
            }
        }.execute(url);
    }

    private class InsertPolicyTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            PolicyDatabase database = Room.databaseBuilder(context,
                    PolicyDatabase.class, "Policy_Database").build();
            Policy newPolicy = database.getPolicyFromPDF(strings[0]);
            database.mainDao().insert(newPolicy);
            database.close();
            return null;
        }
    }


}