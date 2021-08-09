package edu.bjut.cir.messages;

import it.unisa.dia.gas.jpbc.Element;

public class MeterInfo {
      private long id;
      private Element ri;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public Element getRi() {
        return ri;
    }
    public void setRi(Element ri) {
        this.ri = ri;
    }
    public MeterInfo(long id, Element ri) {
        super();
        this.id = id;
        this.ri = ri;
    }
      
}
