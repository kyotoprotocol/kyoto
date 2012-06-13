/**
 * 
 */
package uk.ac.ic.kyoto.countries;

/**
 * @author cmd08 and farhanrahman
 *
 */
public class Offer{
	final int quantity;
	final int unitCost;
	final TradeType type;

	public static String TRADE_PROPOSAL = "Trade proposal";	

	public Offer(int quantity, int unitCost, TradeType type) {
		this.quantity = quantity;
		this.unitCost = unitCost;
		this.type = type;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getUnitCost() {
		return unitCost;
	}

	public int getTotalCost() {
		return unitCost * quantity;
	}

	public TradeType getType(){
		return this.type;
	}

	@Override
	public String toString() {
		return "Trade: "+quantity+" @ "+unitCost; 
	}

	public boolean equals(Offer t){
		if(this == t) {
			return true;
		} else if (	this.quantity == t.getQuantity() && 
					this.unitCost == t.getUnitCost() && 
					this.type == t.getType()) {
			return true;
		} else if (	this.quantity == -t.getQuantity() &&
					this.unitCost == t.getUnitCost() && 
					this.type == t.reverse().getType()){
			return true;
		} else if ( this.quantity == t.getQuantity() &&
					this.unitCost == -t.getUnitCost() &&
					this.type == t.reverse().getType()){
			return true;
		} else {
			return false;
		}
	}

	public Offer reverse(){
		TradeType t = this.type.equals(TradeType.BUY)?TradeType.SELL:TradeType.BUY;
		return new Offer(this.quantity, this.unitCost, t);
	}

}