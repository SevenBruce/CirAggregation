package edu.bjut.boudia;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.util.StopWatch;

import edu.bjut.boudia.messages.Cij;
import edu.bjut.boudia.messages.ParamsECC;
import edu.bjut.boudia.messages.RegMessage;
import edu.bjut.boudia.messages.RepMessage2;
import edu.bjut.boudia.messages.Sij;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.Point;

public class Agg {

    private ArrayList<RepMessage2> alRep = new ArrayList<RepMessage2>();

    private long id;
    private Pairing pairing;
    private Element g;
    private BigInteger aj;
    private Element pj;

    private BigInteger order;
    private Element kcc_aggj_sharedKeyWithCC;

    ArrayList<Long> alId = new ArrayList<Long>();
    ArrayList<Element> alKeys = new ArrayList<Element>();
    ArrayList<Element> alSharedKeysWithUser = new ArrayList<Element>();

    private StopWatch stopWatch;
    public Agg(ParamsECC ps) throws IOException {
        this.stopWatch = new StopWatch("agg");
        this.id = Utils.randomlong();
        this.pairing = ps.getPairing();

        this.order = pairing.getG1().getOrder();
        this.aj = Utils.randomBig(order);

        this.g = ps.getGeneratorOfG1();
        this.pj = this.g.duplicate().pow(this.aj);
        this.kcc_aggj_sharedKeyWithCC = ps.getYcc().duplicate().pow(this.aj);
        // System.out.println(this.kcc_aggj_sharedKeyWithCC);
    }

    public Element getMeterRegMessage(RegMessage reg) {
        this.stopWatch.start("regMsg");
        alId.add(reg.getId());
        alKeys.add(reg.getKey());

        Element temKey = reg.getKey().duplicate().pow(this.aj);
        alSharedKeysWithUser.add(temKey);
        this.stopWatch.stop();
        // System.out.println(temKey);
        return this.pj;
    }

    public void reSetRegMessages() {
        alId.clear();
        alKeys.clear();
        alSharedKeysWithUser.clear();
        ;
    }

    public RegMessage genRegMesssage() {
        this.stopWatch.start("agg_reg");
        RegMessage reg = new RegMessage(this.id, this.pj);
        this.stopWatch.stop();
        return reg;
    }

    public RepMessage2 getRepMessage(RepMessage2 repMessage) throws IOException {

        alRep.add(repMessage);

        if (alRep.size() < Params.METERS_NUM)
            return null;

        this.stopWatch.start("repMsg");
        if (false == checkingIncomeMessage()) {
            System.out.println("check failed at the agg side");
            return null;
        } else {
            // System.out.println("passed ");
        }
        RepMessage2 repMsg2 = generateReportingMessage(sumUpReportingData());
        this.stopWatch.stop();
        return repMsg2;
    }

    private Cij sumUpReportingData() throws IOException {

        Element data = pairing.getG1().newZeroElement();

        Iterator<RepMessage2> itRep = alRep.iterator();
        if (!itRep.hasNext()) {
            return null;
        }

        Element sumRij1G = pairing.getG1().newOneElement();
        RepMessage2 rep;

        while (itRep.hasNext()) {
            rep = itRep.next();
            sumRij1G.add(rep.getCij().getRij1G());

            data.add(rep.getCij().getData());
        }
        clearReportMessage();
        return new Cij(sumRij1G, data);
    }

    private boolean checkingIncomeMessage() throws IOException {

        Element left = PrepareLeft();
        Element right = PrepareRight();

        if (!left.equals(right)) {
            System.out.println("right not equal to left failed!");
            System.exit(1);
        } else {
            // System.out.println("preparing data, please wait!!!");
        }
        return true;
    }

    private Element PrepareLeft() {

        Iterator<RepMessage2> itRep = alRep.iterator();

        if (!itRep.hasNext()) {
            return null;
        }

        Element temResult = pairing.getG1().newZeroElement();
        while (itRep.hasNext()) {
            temResult.add(itRep.next().getSij().getRij2G());
        }
        return temResult;
    }

    private Element PrepareRight() {

        Iterator<RepMessage2> itRep = alRep.iterator();

        if (!itRep.hasNext()) {
            return null;
        }

        // the sum of uij
        BigInteger temHash;
        BigInteger uij;
        BigInteger sumUij = BigInteger.ZERO;
        BigInteger w;

        // the sum of vijPij
        BigInteger vij;
        BigInteger r;
        Element sumVijPij = pairing.getG1().newZeroElement();
        Element temVijPij;

        while (itRep.hasNext()) {
            RepMessage2 rep = itRep.next();
            // the sum of uij
            temHash = Utils.hash2Big(rep.getCij().toString() + rep.getId() + rep.getTs(), this.order);
            w = rep.getSij().getZij().modInverse(this.order);
            uij = temHash.multiply(w).mod(this.order);
            sumUij = sumUij.add(uij).mod(this.order);

            // the sum of vijPij
            r = ((Point) rep.getSij().getRij2G()).getX().toBigInteger().mod(this.order);
            vij = r.multiply(w).mod(this.order);
            temVijPij = getPublicKeyById(rep.getId()).duplicate().mul(vij);
            sumVijPij.add(temVijPij);
        }
        Element result = this.g.duplicate().mul(sumUij);
        result.add(sumVijPij);
        return result;
    }

    private Element getPublicKeyById(long id) {
        int index = alId.indexOf(id);
        return alKeys.get(index);
    }

    private RepMessage2 generateReportingMessage(Cij cij) throws IOException {
        long ts = System.currentTimeMillis();
        ts = ts / 1000000;
        ts = ts * 1000000;

        // signature
        BigInteger rj = Utils.randomBig(this.order);
        Element rjG = this.g.duplicate().pow(rj);

        BigInteger rjReverse = rj.modInverse(this.order);
        BigInteger D = Utils.hash2Big(cij.toString() + this.id + ts, this.order);
        BigInteger r = ((Point) rjG).getX().toBigInteger();
        BigInteger ajr = this.aj.multiply(r);
        BigInteger zj = (ajr.add(D)).multiply(rjReverse).mod(this.order);

        Sij sij = new Sij(rjG, zj);

        return new RepMessage2(cij, this.id, ts, sij);
    }

    private void clearReportMessage() throws IOException {
        alRep.clear();
    }

}
