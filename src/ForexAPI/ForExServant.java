package ForexAPI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import UI.RMIRegistryUI;

public class ForExServant extends UnicastRemoteObject implements ForExInterface {

	private ForExAPI api;
	//constructor
	public ForExServant() throws RemoteException {
		super();
		api = new ForExAPI();
	}

	//object generator
	public static ForExServant init() throws RemoteException {
		return new ForExServant();
	}

	//methods
	@Override
	public Vector<ForexCurrency> getCurrencies() throws RemoteException {
		RMIRegistryUI.addToLogs(String.format("[%s] Fetched available currencies", FOREX_API_ENDPOINT));
		return api.getSymbols();
	}

	@Override
	public DefaultTableModel getResult(double amount, ForexCurrency baseCurrency, List<ForexCurrency> target)
			throws RemoteException {
		RMIRegistryUI.addToLogs(String.format("[%s] Fetched converted amounts", FOREX_API_ENDPOINT));
		return api.convert(amount, baseCurrency, target);
	}

	@Override
	public String getFetchDate() throws RemoteException {
		RMIRegistryUI.addToLogs(String.format("[%s] Fetched conversion date", FOREX_API_ENDPOINT));
		return api.getFetchDate();
	}
}
