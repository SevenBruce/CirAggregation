import java.io.IOException;
import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.Point;
import messages.Cij;
import messages.ParamsECC;
import messages.RegMessage;
import messages.RegMessageFromServer2User;
import messages.RepMessage2;
import messages.Sij;

public class Meters {

	private long id;
	private Pairing pairing;

	private BigInteger aij;
	private Element pij;

	private Element aggj_uij_shardKeyWithAgg;

	private Element yi;
	private BigInteger fi;

	private Element g;
	private Element rsi;
	private BigInteger order;

	public Meters(ParamsECC ps) throws IOException {
		super();

		this.id = Utils.randomlong();
		this.pairing = ps.getPairing();

		this.g = ps.getGeneratorOfG1();

		this.order = pairing.getG1().getOrder();
		this.aij = Utils.randomBig(order);
		this.pij = this.g.duplicate().pow(this.aij);
	}

	/**
	 * A meter sends its identity and public key to aggregator for registration
	 */
	public RegMessage genRegMesssage() {
		RegMessage reg = new RegMessage(this.id, this.pij);
		return reg;
	}

	public void getRegFromCC(RegMessageFromServer2User reg) {
		this.yi = reg.getYi();
		this.fi = reg.getFi();
	}

	public long getId() {
		return this.id;
	}

	/**
	 * A meter get the registration messsage from the aggregator, it has to
	 * update its key to encrypt meter's consumption data
	 */
	public void getRegBackFromAgg(Element pj) {
		this.aggj_uij_shardKeyWithAgg = pj.duplicate().pow(this.aij);
		// System.out.println(aggj_uij_shardKeyWithAgg);
	}

	/**
	 * A meter report multiple types of data to aggregator at a time
	 */
	public RepMessage2 genRepMessage() throws IOException {

		long ts = System.currentTimeMillis();
		ts = ts / 1000000;
		ts = ts * 1000000;

		BigInteger rij1 = Utils.randomBig(this.order);
		Element rijG = this.g.duplicate().pow(rij1);

		BigInteger temData = BigInteger.valueOf(Utils.randomInt(Params.UPBOUND_LIMIT_OF_METER_DATA));
//		System.out.println(dataI);
		temData = temData.subtract(this.fi).mod(this.order);

		Element data = this.g.duplicate().mul(temData);
		data.add(this.yi.duplicate().pow(rij1));
		Cij cij = new Cij(rijG, data);

		// signature
		BigInteger rij2 = Utils.randomBig(this.order);
		Element rij2G = this.g.duplicate().pow(rij2);

		BigInteger rij2Reverse = rij2.modInverse(this.order);
		BigInteger D = Utils.hash2Big(cij.toString() + this.id + ts, this.order);
		BigInteger r = ((Point) rij2G).getX().toBigInteger();
		BigInteger aijr = this.aij.multiply(r);
		BigInteger zij = (aijr.add(D)).multiply(rij2Reverse);

		Sij sij = new Sij(rij2G, zij);

		return new RepMessage2(cij, this.id, ts, sij);

	}

}
