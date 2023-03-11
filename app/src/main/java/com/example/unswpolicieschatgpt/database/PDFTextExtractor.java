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


public class PDFTextExtractor {

    public String PDFTextExtractor(Context context, URL url) {
        PDFBoxResourceLoader.init(context);
        PDDocument pdfDocument = new PDDocument();
        try {
            pdfDocument = PDDocument.load(url.openStream());
            System.out.println(pdfDocument.getNumberOfPages());
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(pdfDocument);

            //Add to Room database

            DocumentDatabase database = Room.databaseBuilder(context, DocumentDatabase.class, "pdf-text-db").build();
            Document pdfText = new Document();
            pdfText.setId(1);
            pdfText.setContent(text);
            database.mainDao().insert(pdfText);

            return text;

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