package ForexAPI;

import java.util.Arrays;

public class Test {

	public static void main(String[] args) {
		ForExAPI api = new ForExAPI();
		if(api.getSymbols().contains(new ForexCurrency("USD"))) {
			System.out.println("Hmmmm...");
		}
	}

}
