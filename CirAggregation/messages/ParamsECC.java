package messages;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class ParamsECC {
	
	private Pairing pairing;
	private Element generator;
	
	public ParamsECC(Pairing pairing, Element generator) {
		super();
		this.pairing = pairing;
		this.generator = generator;
	}
	
	public Pairing getPairing() {
		return pairing;
	}
	public void setPairing(Pairing pairing) {
		this.pairing = pairing;
	}
	public Element getGenerator() {
		return generator;
	}
	public void setGenerator(Element generator) {
		this.generator = generator;
	}
	

}
