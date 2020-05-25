package messages;

import it.unisa.dia.gas.jpbc.Element;

public class RegBack {
	
	private Element did;
	private String hash;
	public RegBack(Element did, String hash) {
		super();
		this.did = did;
		this.hash = hash;
	}
	public Element getDid() {
		return did;
	}
	public void setDid(Element did) {
		this.did = did;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}

}
