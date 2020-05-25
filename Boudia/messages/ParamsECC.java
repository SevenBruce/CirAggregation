package messages;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class ParamsECC {
	
	private Pairing pairing;
	private Element generatorOfG1;
	private Element ycc;
	
	public ParamsECC(Pairing pairing, Element generatorOfG1, Element ycc) {
		super();
		this.pairing = pairing;
		this.generatorOfG1 = generatorOfG1;
		this.ycc = ycc;
	}
	
	public Pairing getPairing() {
		return pairing;
	}
	public void setPairing(Pairing pairing) {
		this.pairing = pairing;
	}

	public Element getGeneratorOfG1() {
		return generatorOfG1;
	}

	public void setGeneratorOfG1(Element generatorOfG1) {
		this.generatorOfG1 = generatorOfG1;
	}

	public Element getYcc() {
		return ycc;
	}

	public void setYcc(Element ycc) {
		this.ycc = ycc;
	}

	
	
}
