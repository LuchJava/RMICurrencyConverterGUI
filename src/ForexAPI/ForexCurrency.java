package ForexAPI;

import java.io.Serializable;
import java.util.Currency;

public class ForexCurrency implements Serializable, Comparable<ForexCurrency> {
	private String symbol;
	public ForexCurrency(String symbol) {
		this.symbol = symbol;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public String getDisplayName() {
		return Currency.getInstance(symbol).getDisplayName();
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s)", getDisplayName(), symbol);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ForexCurrency) {
			return this.symbol.equals(((ForexCurrency) obj).symbol);
		}else {
			return false;
		}
	}

	@Override
	public int compareTo(ForexCurrency o) {
		return this.getDisplayName().compareTo(o.getDisplayName());
	}

}
