package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Message {

    private int id;
    private Contact from;
    private List<Contact> to;
    private Date dateTime;
    private String subject;
    private String content;
    private boolean cc;
    private boolean bcc;
    private ArrayList<Attachment> attachments;
    private ArrayList<Tag> tags;

    public Message(){
        super();
    }

    public Message(int id, Contact from, List<Contact> to, Date dateTime, String subject, String content, boolean cc, boolean bcc, ArrayList<Attachment> attachments, ArrayList<Tag> tags) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.dateTime = dateTime;
        this.subject = subject;
        this.content = content;
        this.cc = cc;
        this.bcc = bcc;
        this.attachments = attachments;
        this.tags = tags;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Contact getFrom() {
        return from;
    }

    public void setFrom(Contact from) {
        this.from = from;
    }

    public List<Contact> getTo() {
        return to;
    }

    public void setTo(List<Contact> to) {
        this.to = to;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCc() {
        return cc;
    }

    public void setCc(boolean cc) {
        this.cc = cc;
    }

    public boolean isBcc() {
        return bcc;
    }

    public void setBcc(boolean bcc) {
        this.bcc = bcc;
    }
}
