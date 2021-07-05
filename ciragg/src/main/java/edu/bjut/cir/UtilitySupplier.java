package edu.bjut.cir;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import org.springframework.util.StopWatch;

import edu.bjut.cir.messages.PublicInfo;
import edu.bjut.cir.messages.RegBack;
import edu.bjut.cir.messages.RegMessage;
import edu.bjut.cir.messages.RepMessage;

public class UtilitySupplier {

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

    private long id;
    private String xu;
    private BigInteger[] z;
    private BigInteger[] a;

    private StopWatch stopWatch;

    public StopWatch getStopWatch() {
        return stopWatch;
    }

    public void setStopWatch(StopWatch stopWatch) {
        this.stopWatch = stopWatch;
    }
    
    public UtilitySupplier() throws IOException {
        KeyGeneration(1024, 64);
        this.id = Utils.randomlong();
        this.stopWatch = new StopWatch("UtilSup_" + id);
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

    public BigInteger EncryptionXi(BigInteger m) {
        BigInteger r = new BigInteger(bitLength, new Random());
        return g.modPow(m, nsquare).multiply(r.modPow(BigInteger.valueOf(11), nsquare).modPow(n, nsquare)).mod(nsquare);
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

    // initialization of the vector to separate data into different colums
    public PublicInfo getPublicInfo() {
        this.stopWatch.start("getPublic");
        generateRangeZ();
        generateRangeA(this.z);
        PublicInfo pi = new PublicInfo(this.n, this.nsquare, this.g, this.bitLength, this.z, this.a);
        this.stopWatch.stop();
        return pi;
    }

    public void generateRangeZ() {
        this.z = new BigInteger[Params.RANGE_SIZE + 1];
        int range = Params.SINGLE_METER_REPROTING_RANGE / Params.RANGE_SIZE;

        for (int i = 0; i < z.length; i++) {
            z[i] = BigInteger.valueOf(range * (i));
        }
    }

    public void generateRangeA(BigInteger[] z) {
        int arraySize = Params.RANGE_SIZE;
        this.a = new BigInteger[arraySize * 2];
        a[0] = BigInteger.ONE;

        // range for the sum half
        BigInteger meterNumber = BigInteger.valueOf(Params.METER_NUM);
        BigInteger sum = BigInteger.ZERO;
        for (int i = 1; i <= arraySize; i++) {
//			System.out.println("i " + i );
            BigInteger gap = z[i].subtract(z[i - 1]);
            BigInteger product = gap.multiply(meterNumber);
            product = product.multiply(a[i - 1]);

            sum = sum.add(product);
            a[i] = sum.add(BigInteger.ONE);
        }
//		System.out.println("arraySize" + arraySize );
        // range for the counter half
        for (int i = arraySize + 1; i < a.length; i++) {
//			System.out.println("i " + i );
            sum = sum.add((meterNumber.add(BigInteger.ONE)).multiply(a[i - 1]));
            a[i] = sum.add(BigInteger.ONE);
        }
    }

    private BigInteger[] retriveData(BigInteger m) {
        BigInteger[] out = new BigInteger[a.length];
        BigInteger[] xl = new BigInteger[a.length];
        xl[a.length - 1] = m;

        for (int j = a.length - 1; j > 0; j--) {
            xl[j - 1] = xl[j].mod(a[j]);
            out[j] = (xl[j].subtract(xl[j - 1])).divide(a[j]);
        }
        out[0] = xl[0];
        printRestult(out);
        return out;
    }

     private void printRestult(BigInteger[] out) {
        int halfSize = out.length / 2;
        for (int j = halfSize; j < out.length; j++) {
//			System.out.println("count [ " + (j - halfSize) + " ] : " + out[j]);
        }
//		System.out.println();

        BigInteger sumAll = BigInteger.ZERO;
        for (int j = 0; j < halfSize; j++) {
            if (out[j + halfSize].compareTo(BigInteger.ZERO) != 0) {
                BigInteger tem = out[j + halfSize].multiply(z[j]);
                BigInteger sum = out[j].add(tem);
//				System.out.println(" sum [ " + j + " ] : " + sum);
                sumAll = sumAll.add(sum);
            } else {
//				System.out.println(" out[ " + j + " ] : " + out[j]);
            }
        }
//		System.out.println(sumAll);
    }

    public RegMessage genRegMesssage() {
        RegMessage reg = new RegMessage(this.id);
        return reg;
    }

    public void getRegBack(RegBack back) {
        if (null == back) {
            System.out.println("Reg failed");
            return;
        }
        this.xu = back.getKey();
    }

    public void getRepMessage(RepMessage rep) {
        // TODO Auto-generated method stub
        if (null == rep) {
            return;
        }
        this.stopWatch.start("rep_msg");
        if (true == checkingIncomeMessage(rep)) {
            // System.out.println("The sum is : " + Decryption(rep.getCi()));
            retriveData(Decryption(rep.getCi()));
        }
        this.stopWatch.stop();
    }

    private boolean checkingIncomeMessage(RepMessage rep) {
        String temStr = rep.getCi().toString() + rep.getId() + rep.getTi()
                + Utils.sha256(Long.toString(rep.getId()) + this.xu);
        String v = Utils.sha256(temStr);
        if (!v.equals(rep.getV()))
            return false;
        return true;
    }

}
