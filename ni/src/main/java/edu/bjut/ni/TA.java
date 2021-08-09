package edu.bjut.ni;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.springframework.util.StopWatch;

import edu.bjut.ni.messages.ParamsECC;
import edu.bjut.ni.messages.TaBack2Meter;
import edu.bjut.ni.messages.TaBack2OC;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class TA {

    private Pairing pairing;
    private Element generator;

    ArrayList<BigInteger> alRandNumber = new ArrayList<BigInteger>();
    Random rnd = new Random();
    BigInteger s0;
    BigInteger k;
    BigInteger order;
    Iterator<BigInteger>itRandNumber;

    private StopWatch stopWatch;

    public StopWatch getStopWatch() {
        return stopWatch;
    }

    public void setStopWatch(StopWatch stopWatch) {
        this.stopWatch = stopWatch;
    }
    
    public TA(int meter) {
        this.stopWatch = new StopWatch("ta");
        intializeEllipticCruve();
        genSs(meter);
    }

    public ParamsECC getParamsECC() {
        ParamsECC pi = new ParamsECC(this.pairing, this.generator);
        return pi;
    }

    // initialization of the Elliptic Curve
    private void intializeEllipticCruve() {
        pairing = PairingFactory.getPairing("cg.properties");
        generator = pairing.getG1().newRandomElement().getImmutable();
        order = pairing.getG1().getOrder();
    }

    public void genSs(int meter){
        s0 = BigInteger.ZERO;
        for(int i = 0; i< meter;i++){
            BigInteger tem = Utils.randomBig(this.order);
            alRandNumber.add(tem);
            s0 = s0.add(tem);
        }
        s0 = s0.negate().mod(this.order);
        k = Utils.randomBig(this.order);
        itRandNumber = alRandNumber.iterator();
    }

    public TaBack2OC getKeyFromTA() {
        TaBack2OC back = new TaBack2OC(s0);
        return back;
    }
    
    public TaBack2Meter getMeterKeys() {
        this.stopWatch.start("ta_meter_key");
        TaBack2Meter back = new TaBack2Meter(itRandNumber.next(),k);
        this.stopWatch.stop();
        return back;
    }
    
    public void reSetIterator(){
        itRandNumber = alRandNumber.iterator();
    }

}
