package edu.bjut.ni.messages;

import it.unisa.dia.gas.jpbc.Element;

public class MeterBackOC {
    Element h;
    Element vi;
    Element[] wiwij;
    public MeterBackOC(Element h, Element vi, Element[] wiwij) {
        super();
        this.h = h;
        this.vi = vi;
        this.wiwij = wiwij;
    }
    public Element getH() {
        return h;
    }
    public void setH(Element h) {
        this.h = h;
    }
    public Element getVi() {
        return vi;
    }
    public void setVi(Element vi) {
        this.vi = vi;
    }
    public Element[] getWiwij() {
        return wiwij;
    }
    public void setWiwij(Element[] wiwij) {
        this.wiwij = wiwij;
    }
}
