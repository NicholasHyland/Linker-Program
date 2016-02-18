public class Symbol {

    public String symbol;
    public int address;
    public boolean used = false;
    public boolean defined = false;

    public void isUsed() {
    	this.used = true;
    }
}
