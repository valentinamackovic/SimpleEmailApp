package model;

import java.io.Serializable;
import java.util.Base64;

public class Photo implements Serializable {

    private int id;
    private Contact contact;
    private String data;

    public Photo() {
        super();
    }

    public Photo(int id, Contact contact,String data) {
        this.id = id;
        this.contact = contact;
        this.data=data;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
