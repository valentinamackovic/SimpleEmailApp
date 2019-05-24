package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Tag implements Serializable {

    private int id;
    private String name;
    private ArrayList<Message> messages;

    public Tag(){
        super();
    }

    public Tag(int id, String name, ArrayList<Message> messages) {
        this.id = id;
        this.name = name;
        this.messages = messages;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
