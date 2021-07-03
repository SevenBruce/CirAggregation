package edu.bjut.cir;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Hex;


public class Utils {

    /**
     * Hashing with SHA256
     *
     * @param input
     *            String to hash
     * @return String hashed
     */
    public static String sha256(String input) {
        String sha256 = null;
        try {
            MessageDigest msdDigest = MessageDigest.getInstance("SHA-256");
            msdDigest.update(input.getBytes("UTF-8"), 0, input.length());
            sha256 = Hex.encodeHexString(msdDigest.digest());
        } catch (Exception e) {
            Logger.getLogger(sha256).log(Level.SEVERE, null, e);
        }
        return sha256;
    }

    
    /**
     * Hashing 2 a big number mod by a public parameter
     *
     * @param input
     *            String id, BigInteger xi, Element ci, BigInteger di, long ti
     * @return BigInteger
     */
    public static BigInteger hash2Big(String orgStr) {
        BigInteger bi = new BigInteger(orgStr.getBytes());
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
     * generate a random int number no big than "num"
     *
     * @param num
     *            
     * @return int
     */
    public static int randomInt(int num) {
        Random rnd = new Random();
        long seed = System.nanoTime();
        rnd.setSeed(seed);
        int result = rnd.nextInt(num);
        return result;
    }
    
//	/**
//	 * generate a random double
//	 *
//	 * @param input
//	 *            count
//	 * @return int
//	 */
//	public static int randomInt(int count) {
//		Random rnd = new Random();
//		long seed = System.nanoTime();
//		rnd.setSeed(seed);
//		
//		double tem = rnd.nextInt(PublicParams.BOUNDS_0F_RANDO_NUMBER[count]*2);
//		tem = tem - PublicParams.BOUNDS_0F_RANDO_NUMBER[count];
//		tem = tem /PublicParams.BOUNDS_0F_HANDLERS[count];
//		tem = tem * 100;
//		return (int)tem;
//	}

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
//	public static BigInteger randomBig(int num) {
//		Random rnd = new Random();
//		long seed = System.nanoTime();
//		rnd.setSeed(seed);
//		BigInteger ranBig = new BigInteger(num, rnd);
//		return ranBig;
//	}

    
}
