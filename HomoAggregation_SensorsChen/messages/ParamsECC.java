package messages;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class ParamsECC {
	
	private Pairing pairing;
	private Element generator;
	private Element rx;
	
	public ParamsECC(Pairing pairing, Element generator, Element rx) {
		super();
		this.pairing = pairing;
		this.generator = generator;
		this.rx = rx;
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
	public Element getRx() {
		return rx;
	}
	public void setRx(Element rx) {
		this.rx = rx;
	}
	
	

}
