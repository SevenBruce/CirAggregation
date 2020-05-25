package messages;

import java.math.BigInteger;

public class PublicInfo {

	private BigInteger n;
	private BigInteger nsquare;
	private BigInteger[] gg;
	private int bitLength;

	public PublicInfo(BigInteger n, BigInteger nsquare, BigInteger[] gg, int bitLength) {
		super();
		this.n = n;
		this.nsquare = nsquare;
		this.gg = new BigInteger[gg.length];
		System.arraycopy(gg, 0, this.gg, 0, gg.length);

		this.bitLength = bitLength;
	}

	public BigInteger[] getGg() {
		return gg;
	}

	public void setGg(BigInteger[] gg) {
		System.arraycopy(gg, 0, this.gg, 0, gg.length);
	}

	public BigInteger getN() {
		return n;
	}

	public void setN(BigInteger n) {
		this.n = n;
	}

	public BigInteger getNsquare() {
		return nsquare;
	}

	public void setNsquare(BigInteger nsquare) {
		this.nsquare = nsquare;
	}

	public int getBitLength() {
		return bitLength;
	}

	public void setBitLength(int bitLength) {
		this.bitLength = bitLength;
	}

}
