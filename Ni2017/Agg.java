import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import messages.ParamsECC;
import messages.RegMessage;
import messages.RepAgg;
import messages.RepMessage;

public class Agg {

	private ArrayList<RepMessage> alRep = new ArrayList<RepMessage>();

	private long id;
	private Pairing pairing;
	private Element g;
	private Element ge;
	private BigInteger y;
	private Element Y;

	private Element h;
	private BigInteger order;

	public Agg(ParamsECC ps) throws IOException {
		super();
		this.id = Utils.randomlong();
		this.pairing = ps.getPairing();

		this.order = pairing.getG1().getOrder();
//		this.y = pairing.getZr().newRandomElement().getImmutable();
		this.y =Utils.randomBig(order);

		this.g = ps.getGeneratorOfG();
		this.ge = this.pairing.pairing(this.g, this.g);
		this.Y = ge.duplicate().mul(this.y);
	}

	public RepAgg getRepMessage(RepMessage rep) throws IOException {

		alRep.add(rep);
		if (alRep.size() < Params.METERS_NUM)
			return null;

		if (false == checkingIncomeMessage()) {
			System.out.println("check failed at the agg side");
			return null;
		}
		return sumUpData();
	}

	public void setH(Element h) throws IOException {
		this.h = h;
	}

	public Element getY() {
		return this.Y;
	}

	private RepAgg sumUpData() throws IOException {
		Element C = getC();
		Element D = this.pairing.getGT().newOneElement();
		Element Dprime = this.pairing.getG1().newOneElement();
		Element Z = this.pairing.getGT().newOneElement();
		Element F = this.pairing.getGT().newOneElement();
		Element T = this.pairing.getG1().newZeroElement();
		Element X = this.pairing.getGT().newOneElement();

		BigInteger zm = BigInteger.ZERO;
		BigInteger zs = BigInteger.ZERO;
		BigInteger zu = BigInteger.ZERO;

		Iterator<RepMessage> itRep = alRep.iterator();
		itRep = alRep.iterator();

		while (itRep.hasNext()) {
			RepMessage rep = itRep.next();

			Element temD = getPairing(this.g.duplicate().mul(y), rep.getDi());
			D = D.duplicate().mul(temD);

			Dprime = Dprime.duplicate().mul(rep.getDi().duplicate().pow(rep.getCi()));

			Element temZ = getPairing(rep.getZi(), rep.getVi());
			temZ = temZ.duplicate().mul(rep.getCi());
			Z = Z.duplicate().mul(temZ);

			Element temF = getPairing(rep.getZi(), this.g);
			temF = temF.duplicate().mul(rep.getZmi().negate().mod(this.order));
			F = F.duplicate().mul(temF);
			T = T.duplicate().mul(rep.getTi());
			X = X.duplicate().mul(rep.getXi());

			zm = zm.add(rep.getZmi());
			zs = zs.add(rep.getZsi());
			zu = zu.add(rep.getZui());
		}
		RepAgg repAgg = new RepAgg(this.id, getCl(), C, D, Dprime, Z, F, T, X, zm, zs, zu);
		clearReportMessage();
		return repAgg;
	}

	private Element getPairing(Element A, Element B) {
		return this.pairing.pairing(A, B);
	}

	private Element getC() {
		Iterator<RepMessage> itRep = alRep.iterator();
		itRep = alRep.iterator();
		RepMessage repC = itRep.next();
		Element c = getPairing(this.g.duplicate().mul(this.y), repC.getCl().duplicate());
		return c;
	}

	private Element getCl() {
		Iterator<RepMessage> itRep = alRep.iterator();
		itRep = alRep.iterator();
		RepMessage repC = itRep.next();
		return repC.getCl();
	}

	private boolean oneMessageCheck6(RepMessage rep) throws IOException {

		Element left = rep.getXi();
		Element right = getRight6(rep);

		if (!left.equals(right)) {
			System.out.println("agg 666666 failed!");
			return false;
		}
		return true;
	}

	private Element getRight6(RepMessage rep) {
		Element first = this.pairing.pairing(rep.getZi(), rep.getVi());
		BigInteger tem = rep.getCi().mod(this.order);
		first = first.duplicate().mul(tem);

		Element second = this.pairing.pairing(rep.getZi(), this.g);
		tem = (rep.getZmi().negate()).mod(this.order);
		second = second.mul(tem);

		Element third = this.ge.duplicate().mul(rep.getZui());
		return first.duplicate().mul(second).duplicate().mul(third);
	}

	private boolean oneMessageCheck5(RepMessage rep) throws IOException {

		Element left = getLeft5(rep);
		Element right = this.pairing.pairing(rep.getCl(), this.h);

		if (!left.equals(right)) {
			System.out.println("agg 555555555555 failed!");
			return false;
		}

		return true;
	}

	private Element getLeft5(RepMessage rep) {

		Element leftLeft = rep.getDi().duplicate().mul(rep.getCi().negate().mod(this.order));
		Element tem = this.g.duplicate().mul(rep.getZmi().negate().mod(this.order));
		leftLeft = rep.getTi().duplicate().add(leftLeft).duplicate().add(tem);
		leftLeft = leftLeft.mul(rep.getZsi().modInverse(this.order));

		Element left = this.pairing.pairing(leftLeft, this.g);
		return left;
	}

	private boolean checkingIncomeMessage() throws IOException {

		Iterator<RepMessage> itRep = alRep.iterator();
		while (itRep.hasNext()) {
			RepMessage rep = itRep.next();
			if (!oneMessageCheck5(rep)) {
				return false;
			}
			if (!oneMessageCheck6(rep)) {
				return false;
			}
		}
		return true;
	}

	private void clearReportMessage() throws IOException {
		alRep.clear();
	}

}
