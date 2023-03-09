package com.example.unswpolicieschatgpt.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

public interface DocumentDao {
    //Insert query
    @Insert(onConflict = REPLACE)
    void insert(Document document);

    //Delete query
    @Delete
    void delete(Document document);

    //Delete all query
    @Delete
    void reset(List<Document> document);

    //Update query
    @Query("UPDATE Document " +
            "SET pdf_url = :sPdf_url, title = :sTitle, purpose = :sPurpose, scope = :sScope, " +
            "local_doc_permit = :sLocal_doc_permit, content = :sContent, parent_doc = :sParent_doc, " +
            "contact_officer = :sContact_officer, responsible_officer = :sResponsible_officer " +
            "WHERE id = :sID")
    void update(String sPdf_url, String sTitle, String sPurpose, String sScope, String sLocal_doc_permit,
                String sContent, String sParent_doc, String sContact_officer, String sResponsible_officer,
                int sID);

    //Get all data query
    @Query("SELECT * FROM Document")
    List<Document> getAll();
}

