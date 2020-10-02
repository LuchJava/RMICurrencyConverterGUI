package UI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;
import javax.swing.JOptionPane;
import ForexAPI.ForExServant;
import me.alexpanov.net.FreePortFinder;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.awt.event.ActionEvent;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

public class RMIRegistryUI extends JFrame implements ActionListener	 {

	private static Registry registry = null;
	private static RMIRegistryUI THIS = null;
	private static final String APP_NAME = "RMI Registry Server";
	private static final String START_SERVER = "START "+APP_NAME;
	private static final String STOP_SERVER = "STOP "+APP_NAME;
	private static int SERVER_PORT;
	private static String SERVER_IP = "localhost";
	private JPanel contentPane;
	private JButton btnToggleForex;
	private JButton btnToggleServer;
	private JTextArea txtAreaLogs;
	private static SimpleDateFormat formatter = new SimpleDateFormat("[MM/hh/yy hh:mm a]");
	private JLabel lblServer;
	private JLabel lblForex;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//Start UI
					RMIRegistryUI frame = new RMIRegistryUI();
					THIS = frame;
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					SwingUtilities.updateComponentTreeUI(frame);
					//Logs
					System.setOut(new PrintStream(new TextAreaOutputStream(frame.txtAreaLogs)));
					//Make UI Visible
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
					//Make first log
					addToLogs("UI showed successfully.");
				} catch (Exception e) {
					showError(e.getLocalizedMessage());
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RMIRegistryUI() {
		setMinimumSize(new Dimension(540, 600));
		
		setLocale(new Locale("en", "PH"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 540, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new CompoundBorder(new LineBorder(new Color(0, 0, 0), 2), new EmptyBorder(5, 5, 5, 5))));
		contentPane.add(panel_1, BorderLayout.CENTER);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWeights = new double[]{1.0};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0};
		panel_1.setLayout(gbl_panel_1);

		btnToggleServer = new JButton(START_SERVER);
		btnToggleServer.addActionListener(this);
		btnToggleServer.setPreferredSize(new Dimension(200, 40));
		btnToggleServer.setMinimumSize(new Dimension(200, 40));
		btnToggleServer.setMaximumSize(new Dimension(200, 40));
		btnToggleServer.setFocusable(false);
		btnToggleServer.setAlignmentX(0.5f);
		GridBagConstraints gbc_btnToggleServer = new GridBagConstraints();
		gbc_btnToggleServer.insets = new Insets(0, 0, 5, 0);
		gbc_btnToggleServer.gridx = 0;
		gbc_btnToggleServer.gridy = 0;
		panel_1.add(btnToggleServer, gbc_btnToggleServer);
		
		lblServer = new JLabel("");
		lblServer.setFont(new Font("Arial", Font.BOLD, 12));
		GridBagConstraints gbc_lblServer = new GridBagConstraints();
		gbc_lblServer.insets = new Insets(0, 0, 5, 0);
		gbc_lblServer.gridx = 0;
		gbc_lblServer.gridy = 1;
		panel_1.add(lblServer, gbc_lblServer);

		btnToggleForex = new JButton(ForExServant.START_FOREX);
		GridBagConstraints gbc_btnToggleForex = new GridBagConstraints();
		gbc_btnToggleForex.insets = new Insets(0, 0, 5, 0);
		gbc_btnToggleForex.gridx = 0;
		gbc_btnToggleForex.gridy = 2;
		panel_1.add(btnToggleForex, gbc_btnToggleForex);
		btnToggleForex.setFocusable(false);
		btnToggleForex.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnToggleForex.setMinimumSize(new Dimension(200, 40));
		btnToggleForex.setMaximumSize(new Dimension(200, 40));
		btnToggleForex.setPreferredSize(new Dimension(200, 40));
		
		lblForex = new JLabel();
		lblForex.setFont(new Font("Arial", Font.BOLD, 12));
		GridBagConstraints gbc_lblForex = new GridBagConstraints();
		gbc_lblForex.insets = new Insets(0, 0, 5, 0);
		gbc_lblForex.gridx = 0;
		gbc_lblForex.gridy = 3;
		panel_1.add(lblForex, gbc_lblForex);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Logs", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 4;
		panel_1.add(scrollPane, gbc_scrollPane);
		
		txtAreaLogs = new JTextArea();
		txtAreaLogs.setFont(new Font("Monospaced", Font.PLAIN, 13));
		txtAreaLogs.setForeground(Color.WHITE);
		txtAreaLogs.setEditable(false);
		txtAreaLogs.setBackground(Color.BLACK);
		scrollPane.setViewportView(txtAreaLogs);
		btnToggleForex.addActionListener(this);

		JPanel panelNorth = new JPanel();
		contentPane.add(panelNorth, BorderLayout.NORTH);
		panelNorth.setLayout(new BorderLayout(0, 0));

		JLabel lblAppName = new JLabel(APP_NAME+" by Luchavez");
		panelNorth.add(lblAppName, BorderLayout.NORTH);
		lblAppName.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.PLAIN, 30));
		lblAppName.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel panelMenu = new JPanel();
		panelNorth.add(panelMenu, BorderLayout.SOUTH);
		panelMenu.setLayout(new BorderLayout(0, 0));

		Box horizontalBox = Box.createHorizontalBox();
		contentPane.add(horizontalBox, BorderLayout.SOUTH);

		Box verticalBox = Box.createVerticalBox();
		verticalBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		horizontalBox.add(verticalBox);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnToggleServer) toggleRMIServer(btnToggleServer);
		else if(e.getSource() == btnToggleForex) toggleForexAPI(btnToggleForex);
		refreshUIComponents();
	}
	
	//Refresh All 
	
	public void refreshUIComponents() {
		//refresh content on server label
		
		try {
			if(registry != null) {
				lblServer.setText(String.format("%s running at port %d", APP_NAME, SERVER_PORT));
				try {
					if(registry.lookup(ForExServant.FOREX_API_ENDPOINT) != null) {
						lblForex.setText(String.format("%s now available at rmi://%s:%d/%s", ForExServant.FOREX_API_NAME, SERVER_IP, SERVER_PORT, ForExServant.FOREX_API_ENDPOINT));
					}else {
						throw new Exception();
					}
				} catch (Exception e) {
					lblForex.setText("");
				}
			}else {
				throw new Exception();
			}
		} catch (Exception e) {
			lblServer.setText("");
			lblForex.setText("");
		}

		//refresh label on ForEx API button
		try {
			if(registry != null && registry.lookup(ForExServant.FOREX_API_ENDPOINT) != null) btnToggleForex.setText(ForExServant.STOP_FOREX);
			else btnToggleForex.setText(ForExServant.START_FOREX);
		} catch (Exception e) {
			btnToggleForex.setText(ForExServant.START_FOREX);
		}
	}
	
	//Toggles for RMI Server
	
	public static void toggleRMIServer(JButton btn) {
		try {
			if (btn.getText().equalsIgnoreCase(START_SERVER)) {
				registry = LocateRegistry.createRegistry(getRandomPort());
				showInfo(APP_NAME+" is turned ON on port "+SERVER_PORT);
				btn.setText(STOP_SERVER);
			} else {
				//Turn off API's first
				for (String api : registry.list()) {
					registry.unbind(api);
				}
				//Turn off RMI server
				UnicastRemoteObject.unexportObject(registry, true);
				registry = null;
				showInfo(APP_NAME+" turned OFF");
				btn.setText(START_SERVER);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e, APP_NAME, JOptionPane.ERROR_MESSAGE);
		}
	}

	//Toggles for Forex API
	
	public static void toggleForexAPI(JButton btn) {
		if(btn.getText().equalsIgnoreCase(ForExServant.START_FOREX)) {
			turnOnForexAPI(btn);
		}else {
			turnOffForexAPI(btn);
		}
	}
	
	public static void turnOnForexAPI(JButton btn) {
		try {
			registry.rebind(ForExServant.FOREX_API_ENDPOINT, ForExServant.init());
			showInfo(ForExServant.FOREX_API_NAME+" is turned ON");
		} catch (Exception e) {
			showError("Failed to turn on "+ForExServant.FOREX_API_NAME+". Check if "+APP_NAME+" is turned on already.");
		}
	}
	
	public static void turnOffForexAPI(JButton btn) {
		try {
			registry.unbind(ForExServant.FOREX_API_ENDPOINT);
			showInfo(ForExServant.FOREX_API_NAME+" is turned OFF");				
		} catch (Exception e) {
			showError(ForExServant.FOREX_API_NAME+" is turned OFF already.");
		}
	}
	
	//JOptionPane dialogs
	
	public static void showInfo(String message) {
		JOptionPane.showMessageDialog(THIS, message, APP_NAME, JOptionPane.INFORMATION_MESSAGE);
		addToLogs(message);
	}
	
	public static void showError(String message) {
		JOptionPane.showMessageDialog(THIS, message, APP_NAME, JOptionPane.WARNING_MESSAGE);
		addToLogs(message);
	}
	
	//Some random methods
	
	private static int getRandomPort() {
		int port = FreePortFinder.findFreeLocalPort();
		SERVER_PORT = port;
		return port;
	}
	
	public static void addToLogs(String message) {
		System.out.printf("%s %s\n", formatter.format(new Date()), message);
	}
	
}
