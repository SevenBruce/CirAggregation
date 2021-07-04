package edu.bjut.cir;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import org.springframework.util.StopWatch;

import edu.bjut.cir.messages.MeterRegBack;
import edu.bjut.cir.messages.MeterRegMessage;
import edu.bjut.cir.messages.ParamsECC;
import edu.bjut.cir.messages.PublicInfo;
import edu.bjut.cir.messages.RepMessage;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class Meter {

    private long id;
    private Pairing pairing;
    private BigInteger order;
    private BigInteger ki;
    private Element ri;
    private Element g;

    // sk i i-1
    private String skFont;
    private String skRear;
    private String xi;

    private BigInteger n;
    private BigInteger nsquare;
    private BigInteger gg;
    private int bitLength;
    private BigInteger[] a;
    private BigInteger[] z;
    private StopWatch stopWatch;


    public StopWatch getStopWatch() {
        return stopWatch;
    }

    public Meter(ParamsECC ps) throws IOException {
        this.id = Utils.randomlong();
        this.stopWatch = new StopWatch("meter_" + id);
        this.pairing = ps.getPairing();
        this.g = ps.getGenerator();

        this.order = pairing.getG1().getOrder();
        this.ki = Utils.randomBig(this.order);
        this.ri = this.g.duplicate().mul(this.ki);
    }

    public void setPublicInfo(PublicInfo pubInfo) {
        this.n = pubInfo.getN();
        this.gg = pubInfo.getG();
        this.nsquare = pubInfo.getNsquare();
        this.bitLength = pubInfo.getBitLength();

        this.a = new BigInteger[pubInfo.getA().length];
        this.z = new BigInteger[pubInfo.getZ().length];
        System.arraycopy(pubInfo.getA(), 0, this.a, 0, pubInfo.getA().length);
        System.arraycopy(pubInfo.getZ(), 0, this.z, 0, pubInfo.getZ().length);
    }

    public MeterRegMessage genRegMesssage() {
        this.stopWatch.start("gen_reg");
        MeterRegMessage reg = new MeterRegMessage(this.id, this.ri);
        this.stopWatch.stop();
        return reg;
    }

    public void getRegBack(MeterRegBack back) {
        this.stopWatch.start("get_regback");
        if (null == back)
            return;

        this.xi = back.getXi();
        this.skFont = Utils.sha256(back.getFront().duplicate().mul(this.ki).toString());
        this.skRear = Utils.sha256(back.getRear().duplicate().mul(this.ki).toString());
        this.stopWatch.stop();
    }

    public RepMessage genSignedRepMessage() throws IOException {
        this.stopWatch.start("signed_rep");
        BigInteger mi = BigInteger.valueOf(Utils.randomInt(Params.SINGLE_METER_REPROTING_RANGE));
        // System.out.println(" mi : " + mi);

        long ti = System.currentTimeMillis();
        BigInteger ni = getNi(ti);
        BigInteger ci = encryptData(mi, ni);
        // BigInteger ci = encryptData(BigInteger.valueOf(400), ni);
        RepMessage repMsg = generateReportMessage(ci, ti); 
        this.stopWatch.stop();
        return repMsg;
    }
    
    private RepMessage generateReportMessage(BigInteger ci, Long ti) throws IOException {
        String temStr = ci.toString() + Long.toString(this.id) + ti + this.xi.toString();
        String v = Utils.sha256(temStr);

        return new RepMessage(this.id, ci, v, ti);
    }

    private BigInteger encryptData(BigInteger mi, BigInteger ni) {
        BigInteger r = new BigInteger(bitLength, new Random());
        BigInteger shiftedData = shiftData(mi, ni);
        BigInteger ci = this.gg.modPow(shiftedData, nsquare).multiply(r.modPow(n, nsquare)).mod(nsquare);
        return ci;
    }

    private BigInteger getNi(long ti) {
        String tem = Utils.sha256(this.skFont) + Long.toHexString(ti);
//		String tem = Utils.sha256(ti + this.skFont);
//		String tem = Utils.sha256(this.skFont + ti);
        BigInteger ni = new BigInteger(tem, 16);
        
        tem = Utils.sha256(this.skRear) + Long.toHexString(ti);
//		tem = Utils.sha256(ti + this.skRear);
//		tem = Utils.sha256(this.skRear+ ti);
//		System.out.println(tem);
//		System.out.println(ti);
        ni = ni.subtract(new BigInteger(tem, 16));
        return ni;
    }

    private BigInteger shiftData(BigInteger mi, BigInteger ni) {
        BigInteger result = BigInteger.ONE;

        int subscript = findSubscript(mi, this.a);
        
        BigInteger ail = this.a[subscript];
        BigInteger zi = this.z[subscript];
        BigInteger zil = this.z[subscript + Params.RANGE_SIZE];

        // System.out.println(" sub : " + subscript);
        // System.out.println(" mi: " + mi);
        // System.out.println(" ail: " + ail);
        // System.out.println(" zi: " + zi);
        // System.out.println(" zil: " + mi.subtract(ail));

        result = (mi.subtract(ail)).multiply(zi);
        result = result.add(zil).add(ni);
        result = result.add(ni);
        return result;
    }

    private int findSubscript(BigInteger mi, BigInteger[] a) {
        for (int i = 0; i < a.length; i++) {
            if (mi.compareTo(a[i]) < 0) {
                return (i - 1);
            }
        }
        return -1;
    }

    public Long getMeterId() {
        return this.id;
    }

}
