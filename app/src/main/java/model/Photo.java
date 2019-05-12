package model;

import java.io.Serializable;

public class Photo implements Serializable {

    private int id;
    private String path;
    private Contact contact;

    public Photo() {
        super();
    }

    public Photo(int id, String path, Contact contact) {
        this.id = id;
        this.path = path;
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
