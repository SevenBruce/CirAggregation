package edu.bjut.boudia;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import org.springframework.util.StopWatch;

import edu.bjut.boudia.messages.ParamsECC;
import edu.bjut.boudia.messages.RegMessage;
import edu.bjut.boudia.messages.RegMessageFromServer2User;
import edu.bjut.boudia.messages.RepMessage2;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.Point;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class CC {

    private long id;
    private Pairing pairing;
    private Element g;
    private BigInteger order;

    private BigInteger xcc;
    private Element ycc;

    private BigInteger xi;
    private Element yi;
    private BigInteger fi;

    ArrayList<Long> alId = new ArrayList<Long>();
    ArrayList<Element> alKeys = new ArrayList<Element>();
    ArrayList<Element> alSharedKeysWithAgg = new ArrayList<Element>();

    private StopWatch stopWatch;

    public StopWatch getStopWatch() {
        return stopWatch;
    }

    public void setStopWatch(StopWatch stopWatch) {
        this.stopWatch = stopWatch;
    }

    public CC() throws IOException {
        this.pairing = PairingFactory.getPairing("cg.properties");

        this.id = Utils.randomlong();
        this.stopWatch = new StopWatch("cc_" + id);
        this.order = pairing.getG1().getOrder();
        this.xcc = Utils.randomBig(this.order);

        this.g = this.pairing.getG1().newRandomElement().getImmutable();
        this.ycc = this.g.duplicate().pow(this.xcc);
        initializationYi();
        prepareElementsForLongKangaroo();
    }

    private void initializationYi() {
        xi = Utils.randomBig(this.order);
        yi = this.g.duplicate().pow(xi);

        fi = Utils.randomBig(this.order);
    }

    public ParamsECC getParamsECC() {
        ParamsECC ps = new ParamsECC(this.pairing, this.g, this.ycc);
        return ps;
    }

    public RegMessageFromServer2User getPublicKeysAndReferenceNumbers() {
        this.stopWatch.start("pubkey");
        RegMessageFromServer2User reg = new RegMessageFromServer2User(this.yi, this.fi);
        this.stopWatch.stop();
        return reg;
    }

    public void getAggRegMessage(RegMessage reg) {
        this.stopWatch.start("agg_reg");
        alId.add(reg.getId());
        alKeys.add(reg.getKey());

        Element temKey = reg.getKey().duplicate().pow(this.xcc);
        // System.out.println(temKey);
        alSharedKeysWithAgg.add(temKey);
        this.stopWatch.stop();
    }

    public void getRepMessage(RepMessage2 rep) throws IOException {

        if (null == rep) {
            return;
        }
        this.stopWatch.start("repMsg");
        if (false == checkTheSignatureOfIncomingMessage(rep)) {
            System.out.println("server check failed");
            return;
        } else {
            // System.out.println("server checkeeeedddd");
        }

        double con = getConsumptionData(rep);
        this.stopWatch.stop();
        // System.out.println("Sum of data " + con);
        // System.out.println();
    }

    public double getConsumptionData(RepMessage2 rep) {
        double con;

        Element temData = rep.getCij().getData().duplicate();
        temData.sub(rep.getCij().getRij1G().duplicate().mul(this.xi));

        BigInteger temFi = this.fi.multiply(BigInteger.valueOf(Params.METERS_NUM)).mod(this.order);
        temData.add(this.g.duplicate().mul(temFi));

        // con = Utils.longKangaroo(this.g, temData);
        con = longKangaroo(this.g, temData, this.order);
        return con;
    }

    private boolean checkTheSignatureOfIncomingMessage(RepMessage2 rep) throws IOException {

        Element left = rep.getSij().getRij2G();

        // the sum of uj
        BigInteger temHash;
        BigInteger uj;
        BigInteger w;

        temHash = Utils.hash2Big(rep.getCij().toString() + rep.getId() + rep.getTs(), this.order);
        w = rep.getSij().getZij().modInverse(this.order);
        uj = temHash.multiply(w).mod(this.order);

        // the sum of vijPij
        BigInteger vj;
        BigInteger r;
        Element vjPj;
        
        r = ((Point) rep.getSij().getRij2G()).getX().toBigInteger();
        vj = r.multiply(w).mod(this.order);
        vjPj = getPublicKeyById(rep.getId()).duplicate().mul(vj);

        Element right = this.g.duplicate().mul(uj);
        right.add(vjPj);

        if (!left.equals(right)) {
            System.out.println("left ::: " + left);
            System.out.println("right::: " + right);
            System.out.println("right not equal to left failed!");
            System.exit(1);
        } else {
            // System.out.println("preparing data, please wait!!!");
        }
        return true;
    }

    private Element getPublicKeyById(long id) {
        int index = alId.indexOf(id);
        return alKeys.get(index);
    }

    private BigInteger LIMIT = BigInteger.valueOf(Params.REPORT_UPBOUND_LIMIT);
    private BigInteger aLEAP = BigInteger.valueOf(Params.LEAPES).divide(BigInteger.valueOf(4));

    Element trapSetByTamedKangaroo;
    Element[] table = new Element[32];
    int mForKangaroo;
    BigInteger dnForKangaroo;
    BigInteger[] distance = new BigInteger[32];

    private void prepareElementsForLongKangaroo() {
        /*
         * Pollard's lambda algorithm for finding discrete logs * which are
         * known to be less than a certain limit LIMIT ref:
         * https://github.com/miracl/MIRACL/blob/master/source/kangaroo.cpp
         */

        mForKangaroo = 1;
        for (BigInteger s = BigInteger.ONE;; mForKangaroo++) {
            distance[mForKangaroo - 1] = s;
            s = s.add(s);
            if ((s.add(s)).divide(BigInteger.valueOf(mForKangaroo)).compareTo(aLEAP) > 0)
                break;
        }

        Element temGenerator = this.g.duplicate();
        for (int i = 0; i < mForKangaroo; i++) {
            /* create table */
            table[i] = temGenerator.duplicate().pow(distance[i]);
            // System.out.println("trap failed... : " + table[i]);
        }

        trapSetByTamedKangaroo = temGenerator.pow(LIMIT);

        dnForKangaroo = BigInteger.ZERO;
        // System.out.println("setting trap..." + m);
        for (int j = 0; j < Params.LEAPES; j++) {
            /* set traps beyond LIMIT using tame kangaroo */
            int i = Math.abs(((Point) trapSetByTamedKangaroo).getX().toBigInteger().intValue()) % mForKangaroo;
            trapSetByTamedKangaroo.mul(table[i]);
            dnForKangaroo = (dnForKangaroo.add(distance[i])).mod(this.order);
        }
    }

    private int longKangaroo(Element generator, Element num, BigInteger order) {
        BigInteger dm;
        Element Num = num;
        int i = 0;
        for (dm = BigInteger.ZERO;;) {
            i = Math.abs(((Point) Num).getX().toBigInteger().intValue()) % mForKangaroo;
            Num.mul(table[i]);
            dm = (dm.add(distance[i])).mod(order);

            if (Num.equals(trapSetByTamedKangaroo))
                break;
            if (dm.compareTo(LIMIT.add(dnForKangaroo)) > 0) {
                System.out.println("trap failed... : " + dm);
                return -1;
            }
        }

        return (LIMIT.add(dnForKangaroo).subtract(dm)).intValue();
    }

}
