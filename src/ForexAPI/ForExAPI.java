package ForexAPI;
import okhttp3.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.json.JSONObject;
public class ForExAPI {
	private static final String API_SITE = "https://api.exchangeratesapi.io/latest";
	private JSONObject rates = null;
	private final OkHttpClient httpClient = new OkHttpClient();
	private boolean isConnected = false;
	private ArrayList<ForexCurrency> symbols = null;
	
	//Constructor
	public ForExAPI() {
		symbols = new ArrayList<ForexCurrency>();
		getRates();
	}
	
	//Getters
	
	public JSONObject getRates(String baseCurrency, boolean forceRefetch){
		try {
			fetchRates(baseCurrency, forceRefetch);
			return rates.getJSONObject("rates");
		} catch (Exception e) {
			System.err.println("Error: Cannot fetch currency rates. Possible internet connection error.");
			return null;
		}
	}
	
	public JSONObject getRates(){
		return getRates("", false);
	}
	
	public JSONObject getRates(ForexCurrency baseCurrency, boolean forceRefetch) {
		return getRates(baseCurrency != null ? baseCurrency.getSymbol() : "", forceRefetch);
	}
	
	public String getFetchDate() {
		try {
			return rates.getString("date");
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getBaseCurrency() {
		try {
			return rates.getString("base");
		} catch (Exception e) {
			return Currency.getInstance(Locale.getDefault()).getCurrencyCode();
		}
	}
	
	public Vector<ForexCurrency> getSymbols() {
		return new Vector<ForexCurrency>(symbols);
	}
	
	public static String getFullCurrency(String key) {
		return Currency.getInstance(key).getDisplayName();
	}
	
	public boolean hasConnection() {
		try {
			fetchRates(null, true);
			return isConnected;
		} catch (Exception e) {
			return false;
		}
		
	}
	
	//Mutators
	private JSONObject sendGet(String url) throws Exception {
		Request request = new Request.Builder().url(url).build();
        try (Response response = httpClient.newCall(request).execute()) {
        	if (!response.isSuccessful()) {
        		isConnected = false;
        		throw new IOException("Unexpected code " + response);
        	}
        	isConnected = true;
        	return new JSONObject(response.body().string());           
        }
    }
	private void fetchRates(String baseCurrency, boolean forceRefetch) throws Exception {
		baseCurrency = baseCurrency.isEmpty() ? getBaseCurrency() : baseCurrency.trim().toUpperCase();
		if(rates == null || !getBaseCurrency().equalsIgnoreCase(baseCurrency) || forceRefetch) {
			rates = sendGet(API_SITE+"?base="+baseCurrency);
			//Refresh List of Currency Symbols for Optimization
			Iterator<String> keys = rates.getJSONObject("rates").keys();
			if(keys.hasNext()) symbols.clear();
			while(keys.hasNext()) {
				symbols.add(new ForexCurrency(keys.next()));
			}
			Collections.sort(symbols);
		}
	}
	
	//Displays
	public String convert(String baseCurrency) {
		return convert(1, baseCurrency, null, false);
	}
	public String convert(double amount, String baseCurrency) {
		return convert(amount, baseCurrency, null, false);
	}
	public String convert(String baseCurrency, String target) {
		return convert(1, baseCurrency, target, false);
	}
	public String convert(String baseCurrency, String target, boolean forceRefetch) {
		return convert(1, baseCurrency, target, forceRefetch);
	}
	
	public String convert(double amount, String baseCurrency, String target) {
		return convert(amount, baseCurrency, target, false);
	}
	
	public String convert(double amount, String baseCurrency, String target, boolean forceRefetch) {
		String message = "", targets[] = null;
		Iterator<String> keys = getRates(baseCurrency, forceRefetch).keys();
		targets = target != null ? target.split(",") : null;
		if(targets == null) {
			while(keys.hasNext()) {
	    		String key = keys.next();
	    		double exchange = amount * getRates().getDouble(key);
	    		message+=String.format("%,3.2f %s = %,3.2f %s (%s)%n", amount, getBaseCurrency(), exchange, key, ForExAPI.getFullCurrency(key));
	    	}
		}
		else {
			for (String key : targets) {
				key = key.trim().toUpperCase(); //clean first, user might have inserted spaces
				if(getRates().has(key)) {
					double exchange = amount * getRates().getDouble(key);
					message+=String.format("%,3.2f %s = %,3.2f %s (%s)%n", amount, getBaseCurrency(), exchange, key, ForExAPI.getFullCurrency(key));
				}
			}
		}
		if(message.length() != 0) {
			message = String.format("Exchange Rate Fetch Date: %s%nBase Currency: %s (%s)%n%n%s", getFetchDate(), getBaseCurrency(), getFullCurrency(getBaseCurrency()), message);
		}
    	return message;
	}
	
	public DefaultTableModel convert(double amount, ForexCurrency baseCurrency, List<ForexCurrency> target) {
		return convert(amount, baseCurrency, target, false);
	}
	
	public DefaultTableModel convert(double amount, ForexCurrency baseCurrency, List<ForexCurrency> target, boolean forceRefetch) {
		String[][] data;
		String columnNames[] = {"Amount", "Symbol", "Name"};
		getRates(baseCurrency, forceRefetch).keySet().toArray();
		if(target != null && target.size() > 0) {
			data = addToData(amount, new Vector<ForexCurrency>(target), rates.getJSONObject("rates"));
		}else {
			data = addToData(amount, getSymbols(), rates.getJSONObject("rates"));
		}
    	return new DefaultTableModel(data, columnNames);
	}
	
	private static String[][] addToData(double amount, Vector<ForexCurrency> list, JSONObject rates) {
		String[][] data = new String[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			ForexCurrency currency = list.get(i);
			if(rates.has(currency.getSymbol())) {
				double exchange = amount * rates.getDouble(currency.getSymbol());
				data[i] = new String[]{String.format("%,3.2f", exchange), currency.getSymbol(), currency.getDisplayName()};
			}
		}
		return data;
	}
	
	public String displaySymbols() {
		String message = "";
		Iterator<String> keys = getRates().keys();
		while(keys.hasNext()) {
			String key = keys.next();
			message+=String.format("%s (%s)%n", key, ForExAPI.getFullCurrency(key));
		}
		if(!message.isEmpty()) {
			message = "Here are the available currencies:\n" + message;
		}
		return message;
	}
}