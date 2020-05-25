package messages;

import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;

public class RepMessage {
	private long smi;
	
	private Element cl;
	private Element di;
	private Element zi;
	private Element ti;
	private Element xi;

	private BigInteger ci;
	private BigInteger zmi;
	private BigInteger zsi;
	private BigInteger zui;
	
	private Element vi;
	
	public RepMessage(long smi, Element cl, Element di, Element zi, Element ti, Element xi, BigInteger ci, BigInteger zmi,
			BigInteger zsi, BigInteger zui, Element vi) {
		super();
		this.smi = smi;
		this.cl = cl;
		this.di = di;
		this.zi = zi;
		this.ti = ti;
		this.xi = xi;
		this.ci = ci;
		this.zmi = zmi;
		this.zsi = zsi;
		this.zui = zui;
		this.vi = vi;
	}
	
	public Element getVi() {
		return vi;
	}

	public void setVi(Element vi) {
		this.vi = vi;
	}

	public long getSmi() {
		return smi;
	}
	public void setSmi(long smi) {
		this.smi = smi;
	}
	public Element getCl() {
		return cl;
	}
	public void setCl(Element cl) {
		this.cl = cl;
	}
	public Element getDi() {
		return di;
	}
	public void setDi(Element di) {
		this.di = di;
	}
	public Element getZi() {
		return zi;
	}
	public void setZi(Element zi) {
		this.zi = zi;
	}
	public Element getTi() {
		return ti;
	}
	public void setTi(Element ti) {
		this.ti = ti;
	}
	public Element getXi() {
		return xi;
	}
	public void setXi(Element xi) {
		this.xi = xi;
	}
	public BigInteger getCi() {
		return ci;
	}
	public void setCi(BigInteger ci) {
		this.ci = ci;
	}
	public BigInteger getZmi() {
		return zmi;
	}
	public void setZmi(BigInteger zmi) {
		this.zmi = zmi;
	}
	public BigInteger getZsi() {
		return zsi;
	}
	public void setZsi(BigInteger zsi) {
		this.zsi = zsi;
	}
	public BigInteger getZui() {
		return zui;
	}
	public void setZui(BigInteger zui) {
		this.zui = zui;
	}
	
	
	
}
