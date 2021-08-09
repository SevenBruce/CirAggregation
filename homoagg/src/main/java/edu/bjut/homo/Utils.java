package edu.bjut.homo;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Hex;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

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
     * Hashing with SHA256
     *
     * @param input
     *            String to hash
     * @return String hashed
     */
    public static Element hash2Element(String originalString,Pairing pairing) {
        byte[] oiginalBytes = originalString.getBytes(StandardCharsets.UTF_8);
        Element result = pairing.getG1().newElementFromHash(oiginalBytes, 0, oiginalBytes.length);
        return result;
    }
    
    
    /**
     * Hashing with SHA256
     *
     * @param input
     *            String to hash
     * @return String hashed
     */
    public static Element hash2Element(long originalString,Pairing pairing) {
        return hash2Element(String.valueOf(originalString),pairing);
    }
    
//	/**
//	 * Hashing with SHA256
//	 *
//	 * @param input
//	 *            String to hash
//	 * @return String hashed
//	 */
//	public static byte[] sha2561(String input) {
//		try {
//			MessageDigest msdDigest = MessageDigest.getInstance("SHA-256");
//			msdDigest.update(input.getBytes("UTF-8"), 0, input.length());
//			return msdDigest.digest();
//		} catch (Exception e) {
//			
//		}
//		return null;
//	}
    
    /**
     * Hashing 2 a big number mod by a public parameter order
     * 
     * @param String orgStr, BigInteger order
     *       
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
        long seed = System.currentTimeMillis();
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

    /**
     * generate a random BigInteger
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
     * generate a random bigInteger and Mod the bigIneteger Mod
     *
     * @param input
     *           
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
    
    /**
     * generate a random bigInteger
     *
     * @param num
     * 
     * @return long
     */
    public static BigInteger randomBig(int num) {
        Random rnd = new Random();
        long seed = System.nanoTime();
        rnd.setSeed(seed);
        BigInteger ranBig = new BigInteger(num, rnd);
        return ranBig;
    }

    
}
