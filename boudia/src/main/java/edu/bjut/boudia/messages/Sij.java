package edu.bjut.boudia.messages;

import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;

public class Sij {
    
    private Element rij2G;
    private BigInteger zij;
    
    public Sij(Element rij2g, BigInteger zij) {
        super();
        rij2G = rij2g;
        this.zij = zij;
    }
    
    public Element getRij2G() {
        return rij2G;
    }
    public void setRij2G(Element rij2g) {
        rij2G = rij2g;
    }
    public BigInteger getZij() {
        return zij;
    }
    public void setZij(BigInteger zij) {
        this.zij = zij;
    }
}
