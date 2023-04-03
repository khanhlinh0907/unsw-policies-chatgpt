package com.example.unswpolicieschatgpt;

public class ConversationMessage {

    private String messageType;
    private String messageContents;


    public ConversationMessage(String type, String content) {
        this.messageType = type;
        this.messageContents = content;

    }


    public String getMessageType() {
        return messageType;
    }

    public String getMessageContents() {
        return messageContents;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setMessageContents(String messageContents) {
        this.messageContents = messageContents;
    }
}
