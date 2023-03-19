package com.example.unswpolicieschatgpt;

// Model or class for policy - for creating object for dummy data
public class Policy {
    private String name;
    private String category;

    public Policy(String policyName, String policyCategory) {
        this.name = policyName;
        this.category = category;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


