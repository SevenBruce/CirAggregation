package edu.bjut.boudia;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class Utils {
    
    /**
     * Hashing 2 a big number mod by a public parameter
     *
     * @param input
     *            String id, BigInteger xi, Element ci, BigInteger di, long ti
     * @return BigInteger
     */
    public static BigInteger hash2Big(String orgStr, BigInteger order) {
        BigInteger bi = new BigInteger(orgStr.getBytes());
        bi = bi.mod(order);
        return bi;
    }

    /**
     * generate a random long identity
     *
     * @param input
     *            null
     * @return long
     */
    public static long randomlong() {
        Random rnd = new Random();
        long seed = System.nanoTime();
        rnd.setSeed(seed);
        BigInteger result = BigInteger.probablePrime(1000, rnd);
        return Math.abs(result.longValue());
    }

    /**
     * generate a random int number that is less than 999
     *
     * @param num
     *            null
     * @return int
     */
    public static int randomInt(int num) {
        Random rnd = new Random();
        long seed = System.nanoTime();
        rnd.setSeed(seed);
        int result = rnd.nextInt(num);
        return result;
    }

    /**
     * Hashing a string to an Element in the Elliptic Curve
     *
     * @param input
     *            String originalString
     * @return Element Element of G1
     */
    public static Element hash2ElementG1(String originalString, Pairing pairing) {
        byte[] oiginalBytes = originalString.getBytes(StandardCharsets.UTF_8);
        Element result = pairing.getG1().newElementFromHash(oiginalBytes, 0, oiginalBytes.length);
        return result;
    }

    /**
     * generate a random long identity
     *
     * @param input
     *            null
     * @return long
     */
    public static BigInteger randomBig() {
        Random rnd = new Random();
        long seed = System.nanoTime();
        rnd.setSeed(seed);
        BigInteger ranBig = new BigInteger(1024, rnd);
        return ranBig;
    }

    /**
     * generate a random long identity
     *
     * @param input
     *            null
     * @return long
     */
    public static BigInteger randomBig(BigInteger mod) {
        Random rnd = new Random();
        long seed = System.nanoTime();
        rnd.setSeed(seed);
        BigInteger ranBig = new BigInteger(1024, rnd);
        ranBig = ranBig.mod(mod);
        return ranBig;
    }
    
//	/**
//	 * generate a random long identity
//	 *
//	 * @param input
//	 *            null
//	 * @return long
//	 */
//	public static BigInteger randomFai() {
//		Random rnd = new Random();
//		long seed = System.nanoTime();
//		rnd.setSeed(seed);
//		BigInteger ranBig = new BigInteger(61, rnd);
//		ranBig = ranBig.mod(Params.smallMod);
//		return ranBig;
//	}
}
