package ForexAPI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public interface ForExInterface extends Remote {
	public static final String FOREX_API_ENDPOINT = "forex";
	public static final String FOREX_API_NAME = "Forex API";
	public static final String START_FOREX = "START "+FOREX_API_NAME;
	public static final String STOP_FOREX = "STOP "+FOREX_API_NAME;
	public Vector<ForexCurrency> getCurrencies() throws RemoteException;
	public DefaultTableModel getResult(double amount, ForexCurrency baseCurrency, List<ForexCurrency> target) throws RemoteException;
	public String getFetchDate() throws RemoteException;
}
