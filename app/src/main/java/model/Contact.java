package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Contact implements Serializable {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private Photo photo;
    private String display;
    private String format;
    private ArrayList<Message> messagesTo = new ArrayList<>();
    private ArrayList<Message> messagesFrom =  new ArrayList<>();
    private ArrayList<Message> messagesCc = new ArrayList<>();
    private ArrayList<Message> messagesBcc = new ArrayList<>();

    public Contact() {
        super();
    }

    public Contact(int id, String firstName, String lastName, String email, Photo photo, String display, String format, ArrayList<Message> messagesTo, ArrayList<Message> messagesFrom, ArrayList<Message> messagesCc, ArrayList<Message> messagesBcc) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.photo = photo;
        this.display = display;
        this.format = format;
        this.messagesTo = messagesTo;
        this.messagesFrom = messagesFrom;
        this.messagesCc = messagesCc;
        this.messagesBcc = messagesBcc;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDisplay() {
        return display;
    }

    public String getFormat() {
        return format;
    }

    public void setMessagesCc(ArrayList<Message> messagesCc) {
        this.messagesCc = messagesCc;
    }

    public void setMessagesBcc(ArrayList<Message> messagesBcc) {
        this.messagesBcc = messagesBcc;
    }

    public ArrayList<Message> getMessagesCc() {
        return messagesCc;
    }

    public ArrayList<Message> getMessagesBcc() {
        return messagesBcc;
    }

    public ArrayList<Message> getMessagesTo() {
        return messagesTo;
    }

    public void setMessagesTo(ArrayList<Message> messagesTo) {
        this.messagesTo = messagesTo;
    }

    public ArrayList<Message> getMessagesFrom() {
        return messagesFrom;
    }

    public void setMessagesFrom(ArrayList<Message> messagesFrom) {
        this.messagesFrom = messagesFrom;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
