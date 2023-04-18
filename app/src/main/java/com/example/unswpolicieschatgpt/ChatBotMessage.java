package com.example.unswpolicieschatgpt;

public class ChatBotMessage {

    private String topic;
    private String date;
    private String time;
    private String lastMsg;

    public ChatBotMessage(String chatBotMessageTopic, String chatBotLastMsg, String chatBotMessageDate, String chatBotMesssageTime){
        this.topic = chatBotMessageTopic;
        this.lastMsg = chatBotLastMsg;
        this.date = chatBotMessageDate;
        this.time = chatBotMesssageTime;
    }

    public String getTopic(){
        return topic;
    }

    public void setTopic(String topic){
        this.topic = topic;
    }

    public String getLastMsg() { return lastMsg; }

    public void setLastMsg(String lastMsg) { this.lastMsg = lastMsg;}

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
