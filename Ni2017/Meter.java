import java.io.IOException;
import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import messages.MeterBackOC;
import messages.TaBack2OC;
import messages.ParamsECC;
import messages.RegMessage;
import messages.RepAgg;
import messages.RepMessage;
import messages.TaBack2Meter;

public class Meter {

	private long id;
	private Pairing pairing;

	private BigInteger si;
	private BigInteger k;
	private Element Si;

	private Element Vi;
	private Element[] wiwij;

	private int tl = 0;
	BigInteger order;
	private Element h;

	private Element g;
	private Element ge;

	public Meter(ParamsECC ps) throws IOException {
		super();

		this.id = Utils.randomlong();
		this.pairing = ps.getPairing();

		this.g = ps.getGeneratorOfG();
		this.ge = this.pairing.pairing(this.g, this.g);
		this.order = pairing.getG1().getOrder();
	}

	public RegMessage genResMessage() {
		return new RegMessage(this.id);
	}

	/**
	 * A meter get registration message from OC
	 */
	public void getRegBackOC(MeterBackOC back) {
		this.h = back.getH();
		this.Vi = back.getVi();
		this.wiwij = back.getWiwij();
	}

	/**
	 * A meter get registration message from TA
	 */
	public void getRegBackTA(TaBack2Meter back) {
		this.k = back.getK();
		this.si = back.getSi();
		this.Si = this.g.duplicate().mul(this.si);
	}

	public long getId() {
		return this.id;
	}

	/**
	 * A meter report its data to aggregator
	 */
	public RepMessage genRepMessage() throws IOException {

		BigInteger rl = Utils.hash2Big(this.k.toString() + this.tl, this.order);
		Element Cl = this.g.duplicate().mul(rl);
		int temMi = Utils.randomInt(Params.SINGLE_METER_REPROTING_RANGE);
		BigInteger mi = BigInteger.valueOf(temMi);
		Element Di = getDi(mi, rl);

		BigInteger ui = Utils.randomBig(this.order);
		Element Zi = this.wiwij[mi.intValue()].duplicate().mul(ui);

		BigInteger pi1 = Utils.randomBig(this.order);
		BigInteger pi2 = Utils.randomBig(this.order);
		BigInteger pi3 = Utils.randomBig(this.order);
		Element Ti = getTi(pi1, pi2, rl);

		Element Xi = getXi(pi1, pi3, Zi);
		BigInteger ci = getCi(Cl, Di, Zi, tl++);

		BigInteger zmi = getZmi(pi1, ci, mi);
		BigInteger zsi = getZsi(pi2, ci, si);
		BigInteger zui = getZui(pi3, ci, ui);
		return new RepMessage(this.id, Cl, Di, Zi, Ti, Xi, ci, zmi, zsi, zui, this.Vi);
	}

	private Element getDi(BigInteger mi, BigInteger rl) {
		Element first = this.g.duplicate().mul(mi);
		Element second = this.h.duplicate().mul(rl.multiply(this.si).mod(this.order));
		return first.duplicate().mul(second);
	}

	private Element getTi(BigInteger pi1, BigInteger pi2, BigInteger rl) {
		Element first = this.g.duplicate().mul(pi1);
		Element second = this.h.duplicate().mul(rl.multiply(pi2).mod(this.order));
		return first.duplicate().mul(second);
	}

	private Element getXi(BigInteger pi1, BigInteger pi3, Element Zi) {
		Element first = this.pairing.pairing(Zi, this.g);
		BigInteger tem = pi1.negate().mod(this.order);
		first = first.duplicate().mul(tem);
		Element second = this.ge.duplicate().mul(pi3);
		return first.duplicate().mul(second);
	}

	private BigInteger getCi(Element Cl, Element Di, Element Zi, int tl) {
		String tem = Cl.toString();
		tem = tem + Di.toString();
		tem = tem + Zi.toString();
		tem = tem + tl;
		return Utils.hash2Big(tem, order);
	}

	private BigInteger getZmi(BigInteger pi1, BigInteger ci, BigInteger mi) {
		BigInteger tem = ci.multiply(mi);
		tem = tem.mod(this.order);
		return (pi1.subtract(tem)).mod(this.order);
	}

	private BigInteger getZsi(BigInteger pi2, BigInteger ci, BigInteger si) {
		BigInteger tem = ci.multiply(si);
		tem = tem.mod(this.order);
		return (pi2.subtract(tem)).mod(this.order);
	}

	private BigInteger getZui(BigInteger pi3, BigInteger ci, BigInteger ui) {
		BigInteger tem = ci.multiply(ui);
		tem = tem.mod(this.order);
		return (pi3.subtract(tem)).mod(this.order);
	}

}
