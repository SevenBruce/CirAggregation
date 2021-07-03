package edu.bjut.ni.messages;

import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;

public class RepAgg {
    private long gw;

    private Element c;
    private Element cl;
    private Element d;
    private Element dprime;
    private Element z;
    private Element f;
    private Element t;
    private Element x;

    private BigInteger zm;
    private BigInteger zs;
    private BigInteger zu;

    public RepAgg(long gw, Element cl, Element c, Element d, Element dprime, Element z, Element f, Element t, Element x,
            BigInteger zm, BigInteger zs, BigInteger zu) {
        super();
        this.gw = gw;
        this.cl = cl;
        this.c = c;
        this.d = d;
        this.dprime = dprime;
        this.z = z;
        this.f = f;
        this.t = t;
        this.x = x;
        this.zm = zm;
        this.zs = zs;
        this.zu = zu;
    }

    public long getGw() {
        return gw;
    }

    public void setGw(long gw) {
        this.gw = gw;
    }

    public Element getCl() {
        return cl;
    }

    public void setCl(Element cl) {
        this.cl = cl;
    }

    public Element getC() {
        return c;
    }

    public void setC(Element c) {
        this.c = c;
    }

    public Element getD() {
        return d;
    }

    public void setD(Element d) {
        this.d = d;
    }

    public Element getDprime() {
        return dprime;
    }

    public void setDprime(Element dprime) {
        this.dprime = dprime;
    }

    public Element getZ() {
        return z;
    }

    public void setZ(Element z) {
        this.z = z;
    }

    public Element getF() {
        return f;
    }

    public void setF(Element f) {
        this.f = f;
    }

    public Element getT() {
        return t;
    }

    public void setT(Element t) {
        this.t = t;
    }

    public Element getX() {
        return x;
    }

    public void setX(Element x) {
        this.x = x;
    }

    public BigInteger getZm() {
        return zm;
    }

    public void setZm(BigInteger zm) {
        this.zm = zm;
    }

    public BigInteger getZs() {
        return zs;
    }

    public void setZs(BigInteger zs) {
        this.zs = zs;
    }

    public BigInteger getZu() {
        return zu;
    }

    public void setZu(BigInteger zu) {
        this.zu = zu;
    }

}
