package edu.bjut.boudia.messages;

public class RepMessage2 {
    
    private Cij cij;
    private long id;
    private long ts;
    private Sij sij;
    
    public RepMessage2(Cij cij, long id, long ts, Sij sij) {
        super();
        this.cij = cij;
        this.id = id;
        this.ts = ts;
        this.sij = sij;
    }
    
    public Cij getCij() {
        return cij;
    }
    public void setCij(Cij cij) {
        this.cij = cij;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getTs() {
        return ts;
    }
    public void setTs(long ts) {
        this.ts = ts;
    }
    public Sij getSij() {
        return sij;
    }
    public void setSij(Sij sij) {
        this.sij = sij;
    }
}
