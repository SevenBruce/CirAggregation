package edu.bjut.boudia.messages;

import it.unisa.dia.gas.jpbc.Element;

public class RegMessage {
    private Element key;
    private long id;
    
    public RegMessage(long id, Element key) {
        super();
        this.key = key;
        this.id = id;
    }
    public Element getKey() {
        return key;
    }
    public void setKey(Element key) {
        this.key = key;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    
    
    
}
