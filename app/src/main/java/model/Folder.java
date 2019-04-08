package model;

import java.util.ArrayList;

public class Folder {
    public int id;
    public String name;
    public Rule rule;
    public ArrayList<Folder> childFolders;
    public Folder parentFolder;
    public ArrayList<Message> messages;



    public Folder() {
    }
}
