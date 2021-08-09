package edu.bjut.cir;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.util.StopWatch;

import edu.bjut.cir.messages.MeterRegBack;
import edu.bjut.cir.messages.MeterRegMessage;
import edu.bjut.cir.messages.ParamsECC;
import edu.bjut.cir.messages.RegBack;
import edu.bjut.cir.messages.RegMessage;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class TTP {

    private Pairing pairing;
    private Element generator;

    private String x;
    private String xj;
    private String xu;

    ArrayList<Long> alId = new ArrayList<Long>();
    ArrayList<String> alXi = new ArrayList<String>();
    ArrayList<Element> alKeys = new ArrayList<Element>();

    private StopWatch stopWatch;

    public StopWatch getStopWatch() {
        return stopWatch;
    }

    public void setStopWatch(StopWatch stopWatch) {
        this.stopWatch = stopWatch;
    }

    public TTP() {
        this.stopWatch = new StopWatch("TTP");
        intializeEllipticCruve();
    }

    public ParamsECC publishParamsECC() {
        ParamsECC pi = new ParamsECC(this.pairing, this.generator);
        return pi;
    }

    // initialization of the Elliptic Curve
    private void intializeEllipticCruve() {
        pairing = PairingFactory.getPairing("cg.properties");
        generator = pairing.getG1().newRandomElement().getImmutable();
    }

    public RegBack getAggregatorRegMessage(RegMessage reg) throws IOException {
        this.xj = Utils.sha256(String.valueOf(reg.getId()) + this.xu);
        RegBack back = new RegBack(xj);
        return back;
    }

    public RegBack getUtilitySupplierRegMessage(RegMessage reg) throws IOException {
        this.xu = Utils.sha256(String.valueOf(reg.getId()) + this.x);
        RegBack back = new RegBack(xu);
        return back;
    }

    public void getMeterRegMessage(MeterRegMessage reg) {
        this.stopWatch.start("get_meter_reg");
        String xi = Utils.sha256(String.valueOf(reg.getId()) + this.xj);
        alId.add(reg.getId());
        alXi.add(xi);
        alKeys.add(reg.getRi());
        this.stopWatch.stop();
    }

    public MeterRegBack assignMeterKeys(Long id, int arraySize) {
        this.stopWatch.start("assign_keys");
        int index = alId.indexOf(id);
        String xi = alXi.get(index);

        int arrays = alId.size() / arraySize - 1;
        int arrayNum = (index / arraySize);
        int arrayIndexPre = (index - 1 + arraySize) % arraySize;
        int arrayIndexRaer = (index + 1 + arraySize) % arraySize;

        if (arrayNum >= arrays) {
            arrayNum = arrays;
            int lastSize = alId.size() - (alId.size() / arraySize - 1) * arraySize;

            arrayIndexPre = index - arrayNum * arraySize - 1 + lastSize;
            arrayIndexRaer = index - arrayNum * arraySize + 1;

            arrayIndexPre = arrayIndexPre % lastSize;
            arrayIndexRaer = arrayIndexRaer % lastSize;
        }
        arrayIndexPre = arrayNum * arraySize + arrayIndexPre;
        arrayIndexRaer = arrayNum * arraySize + arrayIndexRaer;

//		System.out.println(index + " f : " + arrayIndexPre);
//		System.out.println(index + " r : " + arrayIndexRaer);
//		System.out.println();
        
        Element front = alKeys.get(arrayIndexPre);
        Element rear = alKeys.get(arrayIndexRaer);
        MeterRegBack back = new MeterRegBack(xi, front, rear);
        this.stopWatch.stop();
        return back;
    }
    
    public void Clear(){
        alId.clear();
        alXi.clear();
        alKeys.clear();
    }

}
