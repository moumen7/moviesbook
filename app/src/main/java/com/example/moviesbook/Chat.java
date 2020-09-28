package com.example.moviesbook;

public class Chat {
    public String sender;
    public String receiver;
    public String message;
    public long timestamp;

    public Chat(){

    }

    public Chat(String sender, String receiver, String message, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
    }

}
