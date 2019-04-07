package model;

import java.util.ArrayList;

public class Contact {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private Photo photo;
    private ArrayList<Message> messagesTo;
    private ArrayList<Message> messagesFrom;

    public Contact() {
        super();
    }

    public Contact(int id, String firstName, String lastName, String email,
                   Photo photo, ArrayList<Message> messagesTo, ArrayList<Message> messagesFrom) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.photo = photo;
        this.messagesTo = messagesTo;
        this.messagesFrom = messagesFrom;
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
