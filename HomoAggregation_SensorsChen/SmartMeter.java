import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import messages.ParamsECC;
import messages.PublicInfo;
import messages.RegBack;
import messages.RegMessage;
import messages.RepMessage;

public class SmartMeter {

	private long id;
	private Pairing pairing;
	private Element did;
	private Element rid;
	private BigInteger order;

	private BigInteger n;
	private BigInteger nsquare;
	private BigInteger[] gg;
	private int bitLength;
	
	private long aggId;

	public SmartMeter(ParamsECC ps) throws IOException {
		super();
		this.id = Utils.randomlong();
		this.pairing = ps.getPairing();
		this.rid = Utils.hash2Element(this.id, pairing);
		
		this.order = pairing.getG1().getOrder();
	}
	
	public void setAggId(long id){
		this.aggId = id;
	}
	
	public void setPublicInfo(PublicInfo pubInfo){
		this.n = pubInfo.getN();
		this.gg = new BigInteger[pubInfo.getGg().length];
		System.arraycopy(pubInfo.getGg(), 0, this.gg, 0, this.gg.length);
		this.nsquare = pubInfo.getNsquare();
		this.bitLength = pubInfo.getBitLength();
	}

	public RegMessage genRegMesssage() {
		Long t = System.currentTimeMillis();
		String hash = Utils.sha256(Long.toString(this.id) + Long.toString(t));
		RegMessage reg = new RegMessage(this.id, t, hash);
		return reg;
	}

	public void getRegBack(RegBack back) {
		if (null == back) {
			System.out.println("Reg failed at meter side");
			return;
		}
		String hash = Utils.sha256(Long.toString(this.id) + back.getDid().toString());
		if (hash.equals(back.getHash())) {
			this.did = back.getDid();
		}
	}
	

	public RepMessage genSingleRepMessage(int count) throws IOException {

		BigInteger mi, tem, c = BigInteger.ONE;
		for (int i = 0; i < count; i++) {
			mi = BigInteger.valueOf(Utils.randomInt(Params.SINGLE_METER_REPROTING_RANGE));
			tem = gg[i].modPow(mi, nsquare);
			c = (c.multiply(tem)).mod(nsquare);
		}

		BigInteger r = new BigInteger(bitLength, new Random());
		BigInteger ci = c.multiply(r.modPow(n, nsquare)).mod(nsquare);

		return generateReportingMessageBasedOnCi(ci);
	}

	private RepMessage generateReportingMessageBasedOnCi(BigInteger ci) throws IOException {
		long ti = System.currentTimeMillis();
		
		Element rj = Utils.hash2Element(aggId, pairing);

		Element kij = pairing.pairing(this.did, rj);
//		System.out.println(did + "DID");
//		System.out.println(kij + "METER");
		String temStr = ci.toString() + Long.toString(this.id) + ti + kij.toString();
		String v = Utils.sha256(temStr);

		return new RepMessage(this.id, ci, v, ti);
	}

}
