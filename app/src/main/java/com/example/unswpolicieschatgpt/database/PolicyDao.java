package com.example.unswpolicieschatgpt.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Dao
public interface PolicyDao {
    //Insert query
    @Insert(onConflict = REPLACE)
    void insert(Policy policy);

    //Delete query
    @Delete
    void delete(Policy policy);

    //Delete all query
    @Delete
    void reset(List<Policy> policy);

    //Update query
    @Query("UPDATE Policy " +
            "SET pdf_url = :sPdf_url, title = :sTitle, purpose = :sPurpose, scope = :sScope, " +
            "local_doc_permit = :sLocal_doc_permit, content = :sContent, parent_doc = :sParent_doc, " +
            "contact_officer = :sContact_officer, responsible_officer = :sResponsible_officer " +
            "WHERE id = :sID")
    void update(String sPdf_url, String sTitle, String sPurpose, String sScope, String sLocal_doc_permit,
                String sContent, String sParent_doc, String sContact_officer, String sResponsible_officer,
                int sID);

    //Get all data query
    @Query("SELECT * FROM Policy")
    List<Policy> getAll();

    //Get all policy titles
    @Query("SELECT title FROM Policy")
    List<String> getAllTitle();

    //Get policy url by title
    @Query("SELECT pdf_url FROM Policy WHERE title = :sTitle")
    URL getPolicyURLByTitle(String sTitle);

    //Get policy by url
    @Query("SELECT * FROM Policy WHERE pdf_url = :sPdfUrl")
    Policy getByPdfUrl(String sPdfUrl);
}

