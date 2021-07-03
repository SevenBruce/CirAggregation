package edu.bjut.cir.messages;

import it.unisa.dia.gas.jpbc.Element;

public class MeterRegBack {
    
    private String xi;
    private Element front;
    private Element rear;
    
    public MeterRegBack(String xi, Element front, Element rear) {
        super();
        this.xi = xi;
        this.front = front;
        this.rear = rear;
    }
    
    public String getXi() {
        return xi;
    }
    public void setXi(String xi) {
        this.xi = xi;
    }
    public Element getFront() {
        return front;
    }
    public void setFront(Element front) {
        this.front = front;
    }
    public Element getRear() {
        return rear;
    }
    public void setRear(Element rear) {
        this.rear = rear;
    }
}
