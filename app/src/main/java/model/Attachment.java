package model;

public class Attachment {

    private int id;
    private byte[] data;
    private String type;
    private String name;
    private Message message;
    public Attachment(){
        super();
    }

    public Attachment(int id, byte[] data, String type, String name, Message message) {
        this.id = id;
        this.data = data;
        this.type = type;
        this.name = name;
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
