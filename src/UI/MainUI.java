package UI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import java.awt.Dimension;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;

import ForexAPI.ForExInterface;
import ForexAPI.ForexCurrency;

import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import java.awt.FlowLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.Box;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import javax.swing.JList;

public class MainUI extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JTable tblResult;
	private JButton btnConnect;
	private JButton btnConvert;
	private JComboBox<ForexCurrency> cmbBaseInput;
	private static boolean isConnectedToAPI = false;
	private static String connectionString = "";
	private JTextField txtServerIP;
	private JFormattedTextField txtServerPort;
	private JTextField txtAPIEndpoint;
	private static ForExInterface forexInterface;
	private static MainUI THIS;
	private static final String APP_NAME = "Foreign Exchange Calculator";
	private static final String START_CONNECT = "CONNECT";
	private static final String STOP_CONNECT = "DISCONNECT";
	private JTextField txtResultCurr;
	private JTextField txtFetchDate;
	private JRadioButton rdbtnAllCurr;
	private JList<ForexCurrency> listTargetCurr;
	private JFormattedTextField txtAmount;
	private JTextField txtResultAmount;
	private ForexCurrency baseCurrency;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI frame = new MainUI();
					THIS = frame;
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					SwingUtilities.updateComponentTreeUI(frame);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainUI() {
		setMinimumSize(new Dimension(740, 600));
		setPreferredSize(new Dimension(740, 600));
		setName("ForExJava");
		setTitle("Foreign Exchange Calculator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 740, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panelNorth = new JPanel();
		contentPane.add(panelNorth, BorderLayout.NORTH);
		panelNorth.setLayout(new BorderLayout(0, 0));

		JLabel lblAppName = new JLabel(APP_NAME+" by Luchavez");
		panelNorth.add(lblAppName, BorderLayout.CENTER);
		lblAppName.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.PLAIN, 30));
		lblAppName.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblAppName.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel panelMenu = new JPanel();
		panelNorth.add(panelMenu, BorderLayout.SOUTH);
		panelMenu.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		btnConnect = new JButton(START_CONNECT);
		btnConnect.addActionListener(this);

		JLabel lblRmi = new JLabel("rmi://");
		panelMenu.add(lblRmi);

		txtServerIP = new JTextField();
		txtServerIP.setText("127.0.0.1");
		txtServerIP.setHorizontalAlignment(SwingConstants.CENTER);
		txtServerIP.setToolTipText("Enter RMI Registry IP Address...");
		panelMenu.add(txtServerIP);
		txtServerIP.setColumns(10);

		JLabel label = new JLabel(":");
		panelMenu.add(label);

		txtServerPort = new JFormattedTextField(new Integer(0));
		txtServerPort.setHorizontalAlignment(SwingConstants.CENTER);
		txtServerPort.setToolTipText("Enter RMI Registry Server Port...");
		panelMenu.add(txtServerPort);
		txtServerPort.setColumns(10);

		JLabel label_1 = new JLabel("/");
		panelMenu.add(label_1);

		txtAPIEndpoint = new JTextField();
		txtAPIEndpoint.setText("forex");
		txtAPIEndpoint.setHorizontalAlignment(SwingConstants.CENTER);
		txtAPIEndpoint.setToolTipText("Enter ForEx API Endpoint...");
		panelMenu.add(txtAPIEndpoint);
		txtAPIEndpoint.setColumns(10);

		Component horizontalGlue = Box.createHorizontalGlue();
		panelMenu.add(horizontalGlue);
		panelMenu.add(btnConnect);

		JPanel panelWest = new JPanel();
		panelWest.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 3), "Input", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		contentPane.add(panelWest, BorderLayout.WEST);

		JLabel lblBaseInput = new JLabel("Base Currency");
		lblBaseInput.setBorder(new EmptyBorder(0, 5, 0, 5));
		lblBaseInput.setAlignmentX(0.5f);

		cmbBaseInput = new JComboBox<ForexCurrency>();
		cmbBaseInput.setPreferredSize(new Dimension(100, 20));
		cmbBaseInput.setMinimumSize(new Dimension(100, 20));
		cmbBaseInput.setMaximumSize(new Dimension(100, 32767));
		cmbBaseInput.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				baseCurrency = (ForexCurrency) cmbBaseInput.getSelectedItem();
			}
		});

		txtAmount = new JFormattedTextField(new Double(0));
		txtAmount.setHorizontalAlignment(SwingConstants.CENTER);
		txtAmount.setPreferredSize(new Dimension(100, 20));
		txtAmount.setMinimumSize(new Dimension(100, 20));
		txtAmount.setMaximumSize(new Dimension(100, 2147483647));

		JLabel lblAmount = new JLabel("Amount");
		lblAmount.setBorder(new EmptyBorder(0, 5, 0, 5));
		lblAmount.setAlignmentX(0.5f);

		JScrollPane scrollTargetCurr = new JScrollPane();
		scrollTargetCurr.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollTargetCurr.setPreferredSize(new Dimension(120, 50));
		scrollTargetCurr.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JLabel lblTargetCurr = new JLabel("Target Currency");
		lblTargetCurr.setBorder(new EmptyBorder(0, 5, 0, 5));
		lblTargetCurr.setAlignmentX(0.5f);

		rdbtnAllCurr = new JRadioButton("All");
		rdbtnAllCurr.addActionListener(this);

		btnConvert = new JButton("CONVERT");
		btnConvert.addActionListener(this);
		GroupLayout gl_panelWest = new GroupLayout(panelWest);
		gl_panelWest.setHorizontalGroup(
				gl_panelWest.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelWest.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_panelWest.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollTargetCurr, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
								.addComponent(lblAmount, GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
								.addComponent(cmbBaseInput, 0, 164, Short.MAX_VALUE)
								.addComponent(lblBaseInput, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
								.addGroup(Alignment.TRAILING, gl_panelWest.createSequentialGroup()
										.addComponent(lblTargetCurr, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
										.addGap(27)
										.addComponent(rdbtnAllCurr))
								.addComponent(txtAmount, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
								.addComponent(btnConvert, GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
						.addContainerGap())
				);
		gl_panelWest.setVerticalGroup(
				gl_panelWest.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelWest.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblBaseInput)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(cmbBaseInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(lblAmount)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(txtAmount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_panelWest.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblTargetCurr)
								.addComponent(rdbtnAllCurr))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(scrollTargetCurr, GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(btnConvert)
						.addContainerGap())
				);

		listTargetCurr = new JList();
		scrollTargetCurr.setViewportView(listTargetCurr);
		panelWest.setLayout(gl_panelWest);

		JPanel panelCenter = new JPanel();
		contentPane.add(panelCenter, BorderLayout.CENTER);
		panelCenter.setLayout(new BorderLayout(0, 0));
		panelCenter.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 3), "Result", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		JPanel panelCenterNorth = new JPanel();
		panelCenter.add(panelCenterNorth, BorderLayout.NORTH);
		panelCenterNorth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblBaseResult = new JLabel("Base Currency:");
		panelCenterNorth.add(lblBaseResult);

		txtResultCurr = new JTextField();
		txtResultCurr.setEnabled(false);
		txtResultCurr.setHorizontalAlignment(SwingConstants.CENTER);
		panelCenterNorth.add(txtResultCurr);
		txtResultCurr.setColumns(10);
		
		JLabel lblAmountResult = new JLabel("Amount:");
		panelCenterNorth.add(lblAmountResult);
		
		txtResultAmount = new JTextField();
		txtResultAmount.setPreferredSize(new Dimension(100, 20));
		txtResultAmount.setEnabled(false);
		txtResultAmount.setHorizontalAlignment(SwingConstants.CENTER);
		panelCenterNorth.add(txtResultAmount);

		JLabel lblFetchDate = new JLabel("Fetch Date:");
		panelCenterNorth.add(lblFetchDate);

		txtFetchDate = new JTextField();
		txtFetchDate.setEnabled(false);
		txtFetchDate.setHorizontalAlignment(SwingConstants.CENTER);
		panelCenterNorth.add(txtFetchDate);
		txtFetchDate.setColumns(10);

		JScrollPane scrollCenterCenter = new JScrollPane();
		scrollCenterCenter.setMinimumSize(new Dimension(740, 450));
		panelCenter.add(scrollCenterCenter, BorderLayout.CENTER);

		tblResult = new JTable();
		scrollCenterCenter.setViewportView(tblResult);

		JLabel lblSouth = new JLabel("*This app is using the RESTful API of http://exchangeratesapi.io.");
		lblSouth.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.PLAIN, 14));
		lblSouth.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPane.add(lblSouth, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnConnect) {
			connectToAPI(btnConnect);
		}else if(e.getSource() == btnConvert) {
			convert();
		}else if(e.getSource() == rdbtnAllCurr) {
			if(rdbtnAllCurr.isSelected()) {
				listTargetCurr.setEnabled(false);
			}else {
				listTargetCurr.setEnabled(true);
			}
		}
		refreshUIComponents();
	}

	//Refresh All 

	public void refreshUIComponents() {
		try {
			if(forexInterface != null) {
				txtServerIP.setEnabled(false);
				txtServerPort.setEnabled(false);
				txtAPIEndpoint.setEnabled(false);
				btnConnect.setText(STOP_CONNECT);
				cmbBaseInput.setModel(new DefaultComboBoxModel<ForexCurrency>(forexInterface.getCurrencies()));
				cmbBaseInput.setSelectedItem(baseCurrency);
				listTargetCurr.setModel(new DefaultComboBoxModel<ForexCurrency>(forexInterface.getCurrencies()));	
			}else {
				txtServerIP.setEnabled(true);
				txtServerPort.setEnabled(true);
				txtAPIEndpoint.setEnabled(true);
				btnConnect.setText(START_CONNECT);
			}
		} catch (Exception e) {
			showError("Failed to connect to "+ForExInterface.FOREX_API_NAME+": "+e.getMessage());
		}
	}

	public void connectToAPI(JButton btn){
		txtServerIP.requestFocusInWindow();
		txtServerPort.requestFocusInWindow();
		txtAPIEndpoint.requestFocusInWindow();
		if(btn.getText().equalsIgnoreCase(START_CONNECT)) {
			connectionString = String.format("rmi://%s:%s/%s", txtServerIP.getText(), (int) txtServerPort.getValue(), txtAPIEndpoint.getText());
			try {
				forexInterface = (ForExInterface) Naming.lookup(connectionString);
				if(forexInterface != null) {
					isConnectedToAPI = true;
					showInfo("Successfully CONNECTED to "+ForExInterface.FOREX_API_NAME+".");
				}
			} catch (Exception e) {
				showError("Failed to request from "+ForExInterface.FOREX_API_NAME+".");
			}
		}else {
			forexInterface = null; //destroy connection to that reference
			showInfo("Successfully DISCONNECTED to "+ForExInterface.FOREX_API_NAME+".");
		}
	}

	public void convert() {
		txtAmount.requestFocusInWindow();
		try {
			if(rdbtnAllCurr.isSelected()) {
				tblResult.setModel(forexInterface.getResult((double) txtAmount.getValue(), (ForexCurrency) cmbBaseInput.getSelectedItem(), null));
			}else {
				tblResult.setModel(forexInterface.getResult((double) txtAmount.getValue(), (ForexCurrency) cmbBaseInput.getSelectedItem(), listTargetCurr.getSelectedValuesList()));
			}
			txtResultCurr.setText(((ForexCurrency) cmbBaseInput.getSelectedItem()).getSymbol());
			txtResultAmount.setText(txtAmount.getText());
			txtFetchDate.setText(forexInterface.getFetchDate());
		} catch (RemoteException e) {
			showError("Failed to fetch result from API. Either RMI Server or ForEx API might be down.");
		}

	}

	//JOptionPane dialogs

	public static void showInfo(String message) {
		JOptionPane.showMessageDialog(THIS, message, APP_NAME, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showError(String message) {
		JOptionPane.showMessageDialog(THIS, message, APP_NAME, JOptionPane.WARNING_MESSAGE);
	}
	
	//Random stuff
	
	public static NumberFormatter getMoneyOnly() {
		NumberFormat numFormat = new DecimalFormat("#0,000.00"); 
		return new NumberFormatter(numFormat);
	}
}
