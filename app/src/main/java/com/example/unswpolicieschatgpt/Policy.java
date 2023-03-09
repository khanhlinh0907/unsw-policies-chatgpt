package com.example.unswpolicieschatgpt;

// Model or class for policy - for creating object for dummy data
public class Policy {
    private String mName;
    private String mCategory;

    public Policy(String name, String category) {
        mName = name;
        mCategory = category;
    }

    public String getName() {
        return mName;
    }

    public String getCategory() {
        return mCategory;
    }
}


