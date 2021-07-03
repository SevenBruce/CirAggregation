package edu.bjut.homo;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.util.StopWatch;

import edu.bjut.homo.messages.ParamsECC;
import edu.bjut.homo.messages.RegBack;
import edu.bjut.homo.messages.RegMessage;
import edu.bjut.homo.messages.RepMessage;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class Agg {

    private ArrayList<RepMessage> alRep = new ArrayList<RepMessage>();

    private long id;
    private Pairing pairing;
    private Element generator;
    private Element did;
    private Element rid;
    private long serverId;

    private Element rx;
    private BigInteger order;
    private BigInteger sumCi;
    private BigInteger[] groupSumCi;
    
    private BigInteger nsquare;
    private StopWatch stopWatch;

    public StopWatch getStopWatch() {
        return stopWatch;
    }

    public Agg(ParamsECC ps) throws IOException {
        this.stopWatch = new StopWatch("agg");
        this.stopWatch.start("agg_init");
        this.id = Utils.randomlong();
        this.pairing = ps.getPairing();
        this.rid = Utils.hash2Element(this.id, pairing);

        this.generator = ps.getGenerator();
        this.rx = ps.getRx();
        this.order = pairing.getG1().getOrder();
        this.sumCi = BigInteger.ONE;
        this.stopWatch.stop();
    }

    public RegMessage genRegMesssage() {
        this.stopWatch.start("reg");
        Long t = System.currentTimeMillis();
        String hash = Utils.sha256(Long.toString(this.id) + Long.toString(t));
        RegMessage reg = new RegMessage(this.id, t, hash);
        this.stopWatch.stop();
        return reg;
    }

    public void getRegBack(RegBack back) {
        this.stopWatch.start("reg_response");
        if (null == back) {
            System.out.println("Reg failed");
            return;
        }
        String hash = Utils.sha256(Long.toString(this.id) + back.getDid().toString());
        if (hash.equals(back.getHash())) {
            this.did = back.getDid();
        }
        this.stopWatch.stop();
    }

    public RepMessage getRepMessage(RepMessage rep) throws IOException {
        this.stopWatch.start("resp_agg");
        alRep.add(rep);
        
        if (alRep.size() < Params.METERS_NUM)
            return null;

        if (checkingIncomeMessage() == false) {
            System.out.println("check failed at the agg side");
            return null;
        }
        RepMessage rep_msg = generateRep();
        this.stopWatch.stop();
        return rep_msg;
    }

    public void setServerId(long id) {
        this.serverId = id;
    }
    
    public void setNsquare(BigInteger nsquare) {
        this.nsquare = nsquare;
    }


    public void clearReportMessage() throws IOException {
        alRep.clear();
    }

    private boolean checkingIncomeMessage() throws IOException {
        Iterator<RepMessage> itRep = alRep.iterator();

        sumCi = BigInteger.ONE;
        while (itRep.hasNext()) {
            RepMessage rep = itRep.next();
            sumCi = (sumCi.multiply(rep.getCi())).mod(this.nsquare);
            
            Element ri = Utils.hash2Element(rep.getId(), pairing);
            Element kij = pairing.pairing(ri, this.did);
//			System.out.println(kij + "AGG");
            String temStr = rep.getCi().toString() + rep.getId() + rep.getTi() + kij.toString();
            String v = Utils.sha256(temStr);

            if (!v.equals(rep.getV()))
                return false;
        }
        return true;
    }

    private RepMessage generateRep() throws IOException {
        RepMessage rep = generateSingleRep(sumCi);
        return rep;
    }

    private RepMessage generateSingleRep(BigInteger ci) throws IOException {
        long ti = System.currentTimeMillis();

        Element rj = Utils.hash2Element(this.serverId, pairing);
        Element kij = pairing.pairing(this.did, rj);

        String temStr = ci.toString() + Long.toString(this.id) + ti + kij.toString();
        String v = Utils.sha256(temStr);

        return new RepMessage(this.id, ci, v, ti);
    }

    public long getId() {
        return this.id;
    }

}
