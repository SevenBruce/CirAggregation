package messages;

import it.unisa.dia.gas.jpbc.Element;

public class Cij {
	
	private Element rij1G;
	private Element data;
	
	public Cij(Element rij1g, Element data) {
		super();
		rij1G = rij1g;
		this.data = data;
	}
	
	public Element getRij1G() {
		return rij1G;
	}
	public void setRij1G(Element rij1g) {
		rij1G = rij1g;
	}
	public Element getData() {
		return data;
	}
	public void setData(Element signature) {
		this.data = signature;
	}
	
	public String toString(){
		String result = this.rij1G.toString();
		return result;
	}
}
