package messages;

public class RegMessage {
	
	private long id;	
	private long t;
	private String hash;
	public RegMessage(long id, long t, String hash) {
		super();
		this.id = id;
		this.t = t;
		this.hash = hash;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getT() {
		return t;
	}
	public void setT(long t) {
		this.t = t;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	
}
