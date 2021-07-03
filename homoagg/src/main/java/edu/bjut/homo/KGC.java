package edu.bjut.homo;
import java.io.IOException;
import java.math.BigInteger;

import org.springframework.util.StopWatch;

import edu.bjut.homo.messages.ParamsECC;
import edu.bjut.homo.messages.RegBack;
import edu.bjut.homo.messages.RegMessage;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class KGC {
    
    private Pairing pairing;
    private Element generator;
    private BigInteger dx;
    private Element rx;
    private StopWatch stopWatch;

    public KGC() {
        this.stopWatch = new StopWatch("kgc");
        this.stopWatch.start("kgc_init");
        intializeEllipticCruve();
        this.stopWatch.stop();
    }
    
    public ParamsECC publishParamsECC() {
        ParamsECC pi = new ParamsECC(this.pairing, this.generator, this.rx);
        return pi;
    }

    // initialization of the Elliptic Curve
    private void intializeEllipticCruve() {
        pairing = PairingFactory.getPairing("cg.properties");
        generator = pairing.getG1().newRandomElement().getImmutable();
        this.dx = Utils.randomBig(pairing.getG1().getOrder());
        this.rx = this.generator.duplicate().mul(this.dx);
    }
    
    public RegBack getRegMessage(RegMessage reg) throws IOException {
        this.stopWatch.start("reg_response");
        String hash = Utils.sha256(Long.toString(reg.getId()) + Long.toString(reg.getT()));
        RegBack reg_back = null;
        if (hash.equals(reg.getHash())) {
            
            // byte[] temByte = Utils.sha2561(Long.toString(reg.getId()));
            // Element rid = pairing.getG1().newElementFromHash(temByte,0,temByte.length);
            Element rid =  Utils.hash2Element(Long.toString(reg.getId()),pairing);
            Element did = rid.duplicate().mul(this.dx);
            
            String temHash = Utils.sha256(Long.toString(reg.getId()) + did.toString());
            reg_back = new RegBack(did, temHash);
        } else {
            System.out.println("reg failed!");
        }
        this.stopWatch.stop();
        return reg_back;
    }

}
