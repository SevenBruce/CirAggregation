import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.Point;
import it.unisa.dia.gas.jpbc.Vector;
import messages.MeterBackOC;
import messages.ParamsECC;
import messages.RegMessage;
import messages.RepAgg;
import messages.TaBack2OC;

public class OperationCenter {

	private long id;
	private Pairing pairing;
	private Element g;
	private Element ge;
	private BigInteger order;

	private BigInteger x;
	private BigInteger s0;
	private Element h;
	private Element y;

	ArrayList<Long> alId = new ArrayList<Long>();
	ArrayList<Element> alVi = new ArrayList<Element>();

	public OperationCenter(ParamsECC ps) throws IOException {
		this.pairing = ps.getPairing();

		this.id = Utils.randomlong();
		this.order = pairing.getG1().getOrder();
		this.x = Utils.randomBig(this.order);

		this.g = ps.getGeneratorOfG();
		this.ge = this.pairing.pairing(this.g, this.g);
		this.h = this.g.duplicate().pow(this.x);
	}

	public void getRegBack(TaBack2OC back) {
		this.s0 = back.getS0();
	}

	public MeterBackOC getMeterReg(RegMessage reg) {
		BigInteger vi = Utils.randomBig(this.order);
		Element Vi = this.g.duplicate().mul(vi);

		alId.add(reg.getId());
		alVi.add(Vi);

		Element[] wiwijArray = new Element[Params.SINGLE_METER_REPROTING_RANGE + 1];
		for (int i = 0; i < wiwijArray.length; i++) {
			BigInteger tem = vi.add(BigInteger.valueOf(i));
			tem = tem.modInverse(this.order);
			wiwijArray[i] = this.g.duplicate().mul(tem);
		}

		MeterBackOC back = new MeterBackOC(this.h, Vi, wiwijArray);
		return back;
	}

	public void getRepMessage(RepAgg rep) throws IOException {

		if (null == rep)
			return;

		if (false == checkTheSignatureOfIncomingMessage(rep)) {
			// System.out.println("server check failed");
			return;
		}

		double con = getConsumptionData(rep);
	}

	public int getConsumptionData(RepAgg rep) {
		Element tem = rep.getC().duplicate().mul(this.x.multiply(this.s0));
		Element m = rep.getD().add(tem);
		int res = longKangaroo(this.y.duplicate(), m.duplicate(), this.order);
//		System.out.println("res is : " + res);
		return res;
	}

	public void getY(Element y) {
		this.y = y;
		prepareElementsForLongKangaroo();
	}

	private double linearSearch(Element generator, Element num, BigInteger order) {
		Element g = generator.duplicate();

		for (int i = 1; i < Params.REPORT_UPBOUND_LIMIT; i++) {
			g = generator.duplicate().mul(BigInteger.valueOf(i));
			if (g.equals(num)) {
				return i;
			}
		}
		return -1.01;
	}

	private boolean checkTheSignatureOfIncomingMessage(RepAgg rep) throws IOException {

		if (!firstCheck(rep)) {
			System.out.println("first oc failed!");
			return false;
		}

		if (!secondCheck(rep)) {
			System.out.println("second oc failed!");
			return false;
		}
		return true;
	}

	private boolean secondCheck(RepAgg rep) {
		Element left = rep.getX();

		Element second = rep.getF().duplicate();
		Element third = this.ge.duplicate().mul(rep.getZu());
		Element right = rep.getZ().duplicate().mul(second).duplicate().mul(third);

		if (!left.equals(right)) {
			// System.out.println("22222222222222222 failed!");
			return false;
		} else {
			// System.out.println("preparing data, please wait!!!");
		}
		return true;
	}

	private boolean firstCheck(RepAgg rep) {
		Element left = rep.getT();

		Element second = this.g.duplicate().mul(rep.getZm());
		Element third = rep.getCl().duplicate().mul(rep.getZs().multiply(this.x));
		Element right = rep.getDprime().duplicate().mul(second).duplicate().mul(third);

		if (!left.equals(right)) {
			System.out.println("first firstfirstfirstfirst failed!");
			return false;
		}
		return true;
	}

	private BigInteger LIMIT = BigInteger.valueOf(Params.REPORT_UPBOUND_LIMIT);
	private BigInteger aLEAP = BigInteger.valueOf(Params.LEAPES).divide(BigInteger.valueOf(4));

	Element trapSetByTamedKangaroo;
	Element[] table = new Element[32];
	int mForKangaroo;
	BigInteger dnForKangaroo;
	BigInteger[] distance = new BigInteger[32];

	private void prepareElementsForLongKangaroo() {
		/*
		 * Pollard's lambda algorithm for finding discrete logs * which are
		 * known to be less than a certain limit LIMIT ref:
		 * https://github.com/miracl/MIRACL/blob/master/source/kangaroo.cpp
		 */

		mForKangaroo = 1;
		for (BigInteger s = BigInteger.ONE;; mForKangaroo++) {
			distance[mForKangaroo - 1] = s;
			s = s.add(s);
			if ((s.add(s)).divide(BigInteger.valueOf(mForKangaroo)).compareTo(aLEAP) > 0)
				break;
		}
		Element temGenerator = this.y.duplicate();
		for (int i = 0; i < mForKangaroo; i++) {
			/* create table */
			table[i] = temGenerator.duplicate().pow(distance[i]);
			// Systemd.out.println("trap failed... : " + table[i]);
		}

		trapSetByTamedKangaroo = temGenerator.duplicate().pow(LIMIT);

		dnForKangaroo = BigInteger.ZERO;
		// System.out.println("setting trap..." + m);
		int i = 0;
		for (int j = 0; j < Params.LEAPES; j++) {
			/* set traps beyond LIMIT using tame kangaroo */
			i = Math.abs(trapSetByTamedKangaroo.toBigInteger().intValue()) % mForKangaroo;
			trapSetByTamedKangaroo.mul(table[i]);
			dnForKangaroo = (dnForKangaroo.add(distance[i])).mod(order);
		}
	}

	private int longKangaroo(Element generator, Element num, BigInteger order) {
		BigInteger dm;
		Element Num = num;
		int i = 0;
		for (dm = BigInteger.ZERO;;) {
			i = Math.abs(Num.duplicate().toBigInteger().intValue()) % mForKangaroo;
			Num.mul(table[i]);
			dm = (dm.add(distance[i])).mod(order);

			if (Num.equals(trapSetByTamedKangaroo))
				break;
			if (dm.compareTo(LIMIT.add(dnForKangaroo)) > 0) {
				System.out.println("trap failed... : " + dm);
				return -1;
			}
		}
		return (LIMIT.add(dnForKangaroo).subtract(dm)).intValue();
	}

	public Element getH() {
		// TODO Auto-generated method stub
		return this.h;
	}

}
