package com.example.unswpolicieschatgpt.chatgptapi;

import com.theokanning.openai.embedding.Embedding;

import java.util.List;

public class VectorContent {

    private List<Double> embedding;
    private int index;
    private String object;

    public List<Double> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Double> embedding) {
        this.embedding = embedding;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

}
