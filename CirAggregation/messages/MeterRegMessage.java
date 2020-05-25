package messages;

import it.unisa.dia.gas.jpbc.Element;

public class MeterRegMessage {
	private long id;
	private Element ri;
	
	public MeterRegMessage(long id, Element ri) {
		super();
		this.id = id;
		this.ri = ri;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Element getRi() {
		return ri;
	}
	public void setRi(Element ri) {
		this.ri = ri;
	}
}
