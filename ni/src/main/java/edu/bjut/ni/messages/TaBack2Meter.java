package edu.bjut.ni.messages;
import java.math.BigInteger;

public class TaBack2Meter {
    
    BigInteger si;
    BigInteger k;
    public TaBack2Meter(BigInteger si, BigInteger k) {
        this.si = si;
        this.k = k;
    }
    public BigInteger getSi() {
        return si;
    }
    public void setSi(BigInteger si) {
        this.si = si;
    }
    public BigInteger getK() {
        return k;
    }
    public void setK(BigInteger k) {
        this.k = k;
    }
    
}
