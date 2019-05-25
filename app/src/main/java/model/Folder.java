package model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Folder implements Serializable {
    private int id;
    private String name;
    private Rule rule;
    private ArrayList<Folder> childFolders;
    private Folder parentFolder;
    private ArrayList<Message> messages;
    private String word;

    public Folder(int id, String name, Rule rule, ArrayList<Folder> childFolders, Folder parentFolder, ArrayList<Message> messages,String word) {
        this.id = id;
        this.name = name;
        this.rule = rule;
        this.childFolders = childFolders;
        this.parentFolder = parentFolder;
        this.messages = messages;
        this.word=word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Folder() {
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

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public ArrayList<Folder> getChildFolders() {
        return childFolders;
    }

    public void setChildFolders(ArrayList<Folder> childFolders) {
        this.childFolders = childFolders;
    }

    public Folder getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(Folder parentFolder) {
        this.parentFolder = parentFolder;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
