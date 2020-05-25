package messages;

import java.math.BigInteger;

public class RepMessage {
	private long id;
	private BigInteger ci;
	private String v;
	private long ti;
	public RepMessage(long id, BigInteger ci, String v, long ti) {
		super();
		this.id = id;
		this.ci = ci;
		this.v = v;
		this.ti = ti;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public BigInteger getCi() {
		return ci;
	}
	public void setCi(BigInteger ci) {
		this.ci = ci;
	}
	public String getV() {
		return v;
	}
	public void setV(String v) {
		this.v = v;
	}
	public long getTi() {
		return ti;
	}
	public void setTi(long ti) {
		this.ti = ti;
	}

}
