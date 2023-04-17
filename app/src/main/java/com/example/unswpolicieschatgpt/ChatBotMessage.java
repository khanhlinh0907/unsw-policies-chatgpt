package com.example.unswpolicieschatgpt;

public class ChatBotMessage {

    private String topic;
    private String date;
    private String time;

    public ChatBotMessage(String chatBotMessageTopic, String chatBotMessageDate, String chatBotMesssageTime){
        this.topic = chatBotMessageTopic;
        this.date = chatBotMessageDate;
        this.time = chatBotMesssageTime;
    }

    public String getTopic(){
        return topic;
    }

    public void setTopic(String topic){
        this.topic = topic;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time = time;
    }

}
