package com.example.unswpolicieschatgpt.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.unswpolicieschatgpt.chatgptapi.ChatGPTClient;
import com.theokanning.openai.embedding.Embedding;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "Policy", indices = {@Index(value = {"pdf_url"}, unique = true)})
public class Policy {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "pdf_url")
    private String pdf_url;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "purpose")
    private String purpose;
    @ColumnInfo(name = "scope")
    private String scope;
    @ColumnInfo(name = "local_doc_permit")
    private boolean local_doc_permit;
    @ColumnInfo(name = "content")
    private String content;
    @ColumnInfo(name = "parent_doc")
    private String parent_doc; //url link
    @ColumnInfo(name = "contact_officer")
    private String contact_officer;
    @ColumnInfo(name = "responsible_officer")
    private String responsible_officer;

    // Constructors
    public Policy() {

    }

    public Policy(String title, String purpose, String scope,
                  String content, String contact_officer, String responsible_officer, String parent_doc) {
        this.title = title;
        this.purpose = purpose;
        this.scope = scope;
        this.content = content;
        this.parent_doc = parent_doc;
        this.contact_officer = contact_officer;
        this.responsible_officer = responsible_officer;
    }

    // Setter methods

    public void setId(int id) {
        this.id = id;
    }

    public void setPdf_url(String pdf_url) {
        this.pdf_url = pdf_url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setLocal_doc_permit(boolean local_doc_permit) {
        this.local_doc_permit = local_doc_permit;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setParent_doc(String parent_doc) {
        this.parent_doc = parent_doc;
    }

    public void setContact_officer(String contact_officer) {
        this.contact_officer = contact_officer;
    }

    public void setResponsible_officer(String responsible_officer) {
        this.responsible_officer = responsible_officer;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getPdf_url() {
        return pdf_url;
    }

    public String getTitle() {
        return title;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getScope() {
        return scope;
    }

    public boolean isLocal_doc_permit() {
        return local_doc_permit;
    }

    public String getContent() {
        return content;
    }

    public String getParent_doc() {
        return parent_doc;
    }

    public String getContact_officer() {
        return contact_officer;
    }

    public String getResponsible_officer() {
        return responsible_officer;
    }

    /*public ArrayList<Embedding> getEmbeddings(ChatGPTClient chatGPTClient) {
        ArrayList<Embedding> embeddings = new ArrayList<>();
        embeddings.add(chatGPTClient.embedQuery(title));
        embeddings.add(chatGPTClient.embedQuery(purpose));
        embeddings.add(chatGPTClient.embedQuery(scope));
        embeddings.add(chatGPTClient.embedQuery(contact_officer));
        embeddings.add(chatGPTClient.embedQuery(responsible_officer));
        return embeddings;
    }*/

    public String[] getPolicySection(String content) {
        String[] sectionList = content.split("(?<=\\n)(?=\\d+\\.)");
        return sectionList;
    }
}
