package edu.bjut.boudia.messages;

import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;

public class RegMessageFromServer2User {
    
    private Element yi;
    private BigInteger fi;
    
    public RegMessageFromServer2User(Element yi, BigInteger fi) {
        super();
        this.yi = yi;
        this.fi = fi;
    }
    public Element getYi() {
        return yi;
    }
    public void setYi(Element yi) {
        this.yi = yi;
    }
    public BigInteger getFi() {
        return fi;
    }
    public void setFi(BigInteger fi) {
        this.fi = fi;
    }
    
    
}
