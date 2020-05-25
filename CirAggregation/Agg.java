import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import messages.ParamsECC;
import messages.RegBack;
import messages.RegMessage;
import messages.RepMessage;

public class Agg {

	private ArrayList<RepMessage> alRep = new ArrayList<RepMessage>();

	private long id;
	private String xj;

	private BigInteger sumCi;
	private BigInteger nsquare;

	public Agg() throws IOException {
		super();
		this.id = Utils.randomlong();
		this.sumCi = BigInteger.ONE;
	}

	public RegMessage genRegMesssage() {
		RegMessage reg = new RegMessage(this.id);
		return reg;
	}

	public void getRegBack(RegBack back) {
		if (null == back) {
			System.out.println("Reg failed");
			return;
		}
		this.xj = back.getKey();
	}

	public RepMessage getRepMessage(RepMessage rep) throws IOException {

		alRep.add(rep);

		if (alRep.size() < Params.METER_NUM)
			return null;

		if (checkingIncomeMessage() == false) {
			System.out.println("check failed at the agg side");
			return null;
		}

		return generateRep();
	}

	public void setNsquare(BigInteger nsquare) {
		this.nsquare = nsquare;
	}

	private boolean checkingIncomeMessage() throws IOException {
		Iterator<RepMessage> itRep = alRep.iterator();

		sumCi = BigInteger.ONE;
		while (itRep.hasNext()) {
			RepMessage rep = itRep.next();

			String temStr = rep.getCi().toString() + rep.getId() + rep.getTi()
					+ Utils.sha256(Long.toString(rep.getId()) + this.xj);

			String v = Utils.sha256(temStr);
			if (!v.equals(rep.getV()))
				return false;
			sumCi = (sumCi.multiply(rep.getCi())).mod(this.nsquare);
		}
		return true;
	}

	private RepMessage generateRep() throws IOException {
		RepMessage rep = generateSingleRep(sumCi);
		return rep;
	}

	private RepMessage generateSingleRep(BigInteger ci) throws IOException {
		long ti = System.currentTimeMillis();
		String temStr = ci.toString() + Long.toString(this.id) + ti + this.xj;
		String v = Utils.sha256(temStr);

		return new RepMessage(this.id, ci, v, ti);
	}

	public long getId() {
		return this.id;
	}

	public void clearReportMessage() {
		this.alRep.clear();
	}

}
