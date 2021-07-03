package edu.bjut.homo;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import org.springframework.util.StopWatch;

import edu.bjut.homo.messages.ParamsECC;
import edu.bjut.homo.messages.PublicInfo;
import edu.bjut.homo.messages.RegBack;
import edu.bjut.homo.messages.RegMessage;
import edu.bjut.homo.messages.RepMessage;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class Server {

    /**
     * p and q are two large primes. lambda = lcm(p-1, q-1) =
     * (p-1)*(q-1)/gcd(p-1, q-1).
     */
    private BigInteger p, q, lambda;
    /**
     * n = p*q, where p and q are two large primes.
     */
    public BigInteger n;
    /**
     * nsquare = n*n
     */
    public BigInteger nsquare;
    /**
     * a random integer in Z*_{n^2} where gcd (L(g^lambda mod n^2), n) = 1.
     */
    private BigInteger g;
    /**
     * number of bits of modulus
     */
    private int bitLength;

    private Pairing pairing;
    private Element generator;
    private BigInteger order;
    private BigInteger s;
    private Element rx;

    private Element ri;
    private Element di;
    private long id;

    BigInteger[] gg;
    BigInteger[] a;

    private StopWatch stopWatch;


    public StopWatch getStopWatch() {
        return stopWatch;
    }

    public Server(ParamsECC ps) throws IOException {
        this.stopWatch = new StopWatch("server");
        this.stopWatch.start("server_init");
        // multiple data report with square
        this.id = Utils.randomlong();
        this.pairing = ps.getPairing();
        this.ri = Utils.hash2Element(this.id, pairing);
        this.order = pairing.getG1().getOrder();

        KeyGeneration(1024, 64);
        this.stopWatch.stop();
    }

    // initialization of the vector to separate data into different colums
    public PublicInfo getPublicInfo() {
        a = new BigInteger[Params.NUMBER_OF_REPORTING_DATA_TYPE];

        initializeVectorA();

        initializeGeneratorArray();
        PublicInfo pi = new PublicInfo(this.n, this.nsquare, this.gg, this.bitLength);
        return pi;
    }

    public long getId() {
        return this.id;
    }

    /**
     * Sets up the public key and private key.
     * 
     * @param bitLengthVal
     *            number of bits of modulus.
     * @param certainty
     *            The probability that the new BigInteger represents a prime
     *            number will exceed (1 - 2^(-certainty)). The execution time of
     *            this constructor is proportional to the value of this
     *            parameter.
     */
    public void KeyGeneration(int bitLengthVal, int certainty) {
        bitLength = bitLengthVal;
        /*
         * Constructs two randomly generated positive BigIntegers that are
         * probably prime, with the specified bitLength and certainty.
         */
        p = new BigInteger(bitLength / 2, certainty, new Random());
        q = new BigInteger(bitLength / 2, certainty, new Random());

        n = p.multiply(q);
        nsquare = n.multiply(n);

        g = new BigInteger("2");
        lambda = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE))
                .divide(p.subtract(BigInteger.ONE).gcd(q.subtract(BigInteger.ONE)));
        /* check whether g is good. */
        if (g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).gcd(n).intValue() != 1) {
            System.out.println("g is not good. Choose g again.");
            System.exit(1);
        }
    }

    /**
     * Encrypts plaintext m. ciphertext c = g^m * r^n mod n^2. This function
     * automatically generates random input r (to help with encryption).
     * 
     * @param m
     *            plaintext as a BigInteger
     * @return ciphertext as a BigInteger
     */
    public BigInteger Encryption(BigInteger m) {
        BigInteger r = new BigInteger(bitLength, new Random());
        return g.modPow(m, nsquare).multiply(r.modPow(n, nsquare)).mod(nsquare);
    }

    /**
     * Decrypts ciphertext c. plaintext m = L(c^lambda mod n^2) * u mod n, where
     * u = (L(g^lambda mod n^2))^(-1) mod n.
     * 
     * @param c
     *            ciphertext as a BigInteger
     * @return plaintext as a BigInteger
     */
    public BigInteger Decryption(BigInteger c) {
        BigInteger u = g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).modInverse(n);
        return c.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).multiply(u).mod(n);

    }

    private void printDifferentData(BigInteger m) {
        BigInteger[] out = new BigInteger[gg.length];
        BigInteger[] xl = new BigInteger[gg.length];
        xl[gg.length - 1] = m;

        for (int j = gg.length - 1; j > 0; j--) {
            xl[j - 1] = xl[j].mod(a[j]);
            out[j] = (xl[j].subtract(xl[j - 1])).divide(a[j]);
        }
        out[0] = xl[0];
//		printRestult(out);
    }

    private void printRestult(BigInteger[] out) {
        for (int j = 0; j < out.length; j++) {
            System.out.println(" out[ " + j + " ] : " + out[j]);
        }
    }

    private void initializeVectorA() {

        for (int i = 0; i < a.length; i++) {
            a[i] = BigInteger.ZERO;
        }

        a[0] = BigInteger.ONE;

        if (Params.NUMBER_OF_REPORTING_DATA_TYPE < 2)
            return;

        a[1] = Utils.randomBig(bitLength / 2);

        if (Params.NUMBER_OF_REPORTING_DATA_TYPE < 3)
            return;

        // the first half of the array using the SINGLE_METER_REPROTING_RANGE
        for (int i = 2; i < Params.NUMBER_OF_REPORTING_DATA_TYPE; i++) {
            a[i] = aSum().add(BigInteger.ONE);
        }

    }

    private void initializeGeneratorArray() {
        gg = new BigInteger[a.length];
        for (int i = 0; i < a.length; i++) {
            gg[i] = g.modPow(a[i], nsquare);
        }
    }

    private BigInteger aSum() {
        BigInteger sum = BigInteger.ZERO;
        for (BigInteger x : a) {
            sum = sum.add(x.multiply(BigInteger.valueOf(Params.METERS_NUM))
                    .multiply(BigInteger.valueOf(Params.SINGLE_METER_REPROTING_RANGE)));
        }
        return sum;
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
            System.out.println("Reg failed at meter side");
            return;
        }
        String hash = Utils.sha256(Long.toString(this.id) + back.getDid().toString());
        if (hash.equals(back.getHash())) {
            this.di = back.getDid();
        }
        this.stopWatch.stop();
    }

    public void getRepMessage(RepMessage rep) throws IOException {
        this.stopWatch.start("report_res");
        if (null == rep) {
            return;
        }
        if (signCheck(rep) == false) {
            System.out.println("server check failed");
        }

        BigInteger res = Decryption(rep.getCi());
        printDifferentData(res);
        this.stopWatch.stop();
    }

    private boolean signCheck(RepMessage rep) throws IOException {

        Element rj = Utils.hash2Element(rep.getId(), this.pairing);
        Element kij = pairing.pairing(rj, this.di);

        String temStr = rep.getCi().toString() + Long.toString(rep.getId()) + rep.getTi() + kij.toString();
        String v = Utils.sha256(temStr);

        if (!v.equals(rep.getV())) {
            return false;
        }
        return true;
    }

}
