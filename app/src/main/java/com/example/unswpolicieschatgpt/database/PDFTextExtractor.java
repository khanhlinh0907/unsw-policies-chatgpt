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