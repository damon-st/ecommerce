package com.damon.ecommerce.Chat;

public class Chat {
    private String  sender,receiver,pid;
    public Chat() {
    }

    public Chat(String sender, String receiver,String pid) {
        this.sender = sender;
        this.receiver = receiver;
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
