package edu.bjut.cir.messages;

public class RegMessage {
    
    private long id;
    public RegMessage(long id) {
        super();
        this.id = id;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
}
