package model;

import java.util.ArrayList;

public class Account {
    public int id;
    public String username;
    public String password;
    public String smpt;
    public String pop3imap;
    public ArrayList<Message> messages;

    public Account(int id, String username, String password, String smpt, String pop3imap, ArrayList<Message> messages) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.smpt = smpt;
        this.pop3imap = pop3imap;
        this.messages = messages;
    }

    public Account() {
    }
}
