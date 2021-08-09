package edu.bjut.cir.messages;

import java.math.BigInteger;

public class PublicInfo {

    private BigInteger n;
    private BigInteger nsquare;
    private BigInteger g;
    private int bitLength;
    private BigInteger[] a;
    private BigInteger[] z;

    public PublicInfo(BigInteger n, BigInteger nsquare, BigInteger g, int bitLength, BigInteger[] a, BigInteger[] z) {
        super();
        this.n = n;
        this.nsquare = nsquare;
        this.g = g;
        this.bitLength = bitLength;
        
        this.a = new BigInteger[a.length];
        System.arraycopy(a, 0, this.a, 0, a.length);
        
        this.z = new BigInteger[z.length];
        System.arraycopy(z, 0, this.z, 0, z.length);
    }

    public BigInteger getG() {
        return g;
    }

    public void setG(BigInteger g) {
        this.g = g;
    }

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }

    public BigInteger getNsquare() {
        return nsquare;
    }

    public void setNsquare(BigInteger nsquare) {
        this.nsquare = nsquare;
    }

    public int getBitLength() {
        return bitLength;
    }

    public void setBitLength(int bitLength) {
        this.bitLength = bitLength;
    }

    public BigInteger[] getA() {
        return a;
    }

    public void setA(BigInteger[] a) {
        System.arraycopy(a, 0, this.a, 0, a.length);
    }

    public BigInteger[] getZ() {
        return z;
    }

    public void setZ(BigInteger[] z) {
        System.arraycopy(z, 0, this.z, 0, z.length);
    }

}
