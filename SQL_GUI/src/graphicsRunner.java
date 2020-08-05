// Waiz Khan

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.Border;

public class graphicsRunner {

	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private final static int screenX = (int)screenSize.getWidth();
	private final static int screenY = (int)screenSize.getHeight();
	
	private MysqlCon con;
	
	private String[] domainList;
	private String[] tableList;

	private JPanel mainPanel;
	
	private JPanel inputDomain;
	private JPanel inputTable;
	private JPanel result;
	
	private String[][] fullTable;
	private String[] colNames;

	
	public void addContents(Container pane) {

		// Login Prompt, verifies correct credentials
		loginScreen();

        // Populate domainList with Domains from the Database
        populateDomains();		
		
        Border b = BorderFactory.createLineBorder(Color.BLACK, 2);
        
        // Create Main Panel to contain other panels
    	mainPanel = new JPanel();      
        mainPanel.setLayout(null);
        
        // Create Left Panel
        inputDomain = new JPanel();
        inputDomain.setLayout(null);
        //inputDomain.setBorder(b);
        
        // Create Right Panel
        inputTable = new JPanel();  
        inputTable.setLayout(null);
        //inputTable.setBorder(b);
        
        // Create Result Panel
        result = new JPanel();
        result.setLayout(null);
        result.setBorder(b);
        
        // Add three panels
        inputDomain.setBounds(30, 30, (screenX-60)/2, 50);
        mainPanel.add(inputDomain);

        inputTable.setBounds(30 + (screenX-60)/2, 30, (screenX-60)/2, 50);
        mainPanel.add(inputTable);

        result.setBounds(30, 100, (screenX-60), (screenY-160));
        mainPanel.add(result);
        
        renderDomainPanel();
	
		pane.add(mainPanel);
		
	}
	
	public void renderDomainPanel() {
		
		// Add Label for Top Panel
        JLabel domainLabel = new JLabel("Select a Domain:");
		domainLabel.setFont(new Font("Arial", Font.PLAIN, 22));
		domainLabel.setBounds(15, 0, 200, 50);
		inputDomain.add(domainLabel);

		// Add drop down menu for Top Panel
		JComboBox<String> domainDropdown = new JComboBox<String>(domainList);
		domainDropdown.setFont(new Font("Arial", Font.PLAIN, 20));
		domainDropdown.setBounds(200, 0, 400, 50);
		domainDropdown.setSelectedIndex(-1);
		inputDomain.add(domainDropdown);
		
		inputDomain.revalidate();
		inputDomain.repaint();
		
		// Action to take when an option is selected in the domainDropdown
		domainDropdown.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				populateTables((String) domainDropdown.getSelectedItem());
				inputTable.removeAll();
				renderTablePanel();
				
			}
			
		});
		
	}
	
	public void renderTablePanel() {
	
		JLabel tableLabel = new JLabel("Select a Table:");
		tableLabel.setFont(new Font("Arial", Font.PLAIN, 22));
		tableLabel.setBounds(15, 0, 200, 50);
		inputTable.add(tableLabel);
		
		JComboBox<String> tableDropdown = new JComboBox<String>(tableList);
		tableDropdown.setFont(new Font("Arial", Font.PLAIN, 20));
		tableDropdown.setBounds(200, 0, 400, 50);
		tableDropdown.setSelectedIndex(-1);
		inputTable.add(tableDropdown);
		
		inputTable.revalidate();
		inputTable.repaint();
		
		tableDropdown.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				con.selectTable((String) tableDropdown.getSelectedItem());
				result.removeAll();
				populateResult();	
				
			}
			
		});
			
	}
	
	public void renderResultPanel() {
		
		// use JTable, need 2D array of values and String array of column names
		JTable table = new JTable(fullTable, colNames);
        JScrollPane tableContainer = new JScrollPane(table);
        tableContainer.setBounds(10, 10, result.getWidth()-20, result.getHeight()-80);
        
        result.add(tableContainer);
        
        JButton backButton = new JButton("Clear Table"); 
        backButton.setBounds(10, result.getHeight() - 60, 100, 50);
		result.add(backButton);
		
		JButton confirmButton = new JButton("Confirm Changes");
		confirmButton.setBounds(result.getWidth() - 160, result.getHeight() - 60, 150, 50);
		result.add(confirmButton);
		
		result.repaint();
		
		backButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				result.removeAll();
				result.repaint();
				inputTable.removeAll();
				inputTable.repaint();
				inputDomain.removeAll();
				inputDomain.repaint();
				renderDomainPanel();
				
			}
		});
		
		
	}
	
	public void populateResult() {
		
		try {
			con.generateFullTable();
			
			fullTable = con.getFullTable();
			colNames = con.getColNames();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		renderResultPanel();
		
	}
	
	public void populateDomains() {
		
		try {
			domainList = new String[con.getDomains().size()];
			
		} catch (NullPointerException e){
			
			JOptionPane.showMessageDialog(mainPanel, "Invalid Login Credentials");
			loginScreen();
			
		}
		
		int indx = 0;
		for (String domain : con.getDomains()) {
			domainList[indx] = domain;
			indx++;
		}
		
	}
	
	public void populateTables(String s) {
		
		try {
			con.selectDomain(s);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		tableList = new String[con.getTables().size()];
		int indx = 0;
		for (String table : con.getTables()) {
			tableList[indx] = table;
			indx++;
		}
						
	}
	
	public void loginScreen() {
		
		JLabel hostLabel = new JLabel("Host:");
		JTextField hostField = new JTextField("localhost");
		
		JLabel portLabel = new JLabel("Port:");
		JTextField portField = new JTextField("");
		
		JLabel usernameLabel = new JLabel("Username:");
		JTextField usernameField = new JTextField("root");
		
		JLabel passwordLabel = new JLabel("Password:");
		JPasswordField passwordField = new JPasswordField("");
		
		String[] loginOptions = { "Login", "Cancel" };
		
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new GridLayout(0, 2));
		
		loginPanel.add(hostLabel);
		loginPanel.add(hostField);
		loginPanel.add(portLabel);
		loginPanel.add(portField);
		loginPanel.add(usernameLabel);
		loginPanel.add(usernameField);
		loginPanel.add(passwordLabel);
		loginPanel.add(passwordField);
		
		if (JOptionPane.showOptionDialog(null, loginPanel, "Login", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, loginOptions, loginOptions[0]) != 0) {
			System.exit(0);
		}

        // Connect to the Database
		con = new MysqlCon(hostField.getText(), portField.getText(), usernameField.getText(), String.valueOf(passwordField.getPassword()));
		
	}
	
	public static void initiateFrame(String name) throws IOException {
		
		JFrame f = new JFrame();
		f.setName(name);
		f.setSize(screenX, screenY);
		f.setResizable(true);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		graphicsRunner app = new graphicsRunner();
		app.addContents(f.getContentPane());
		f.setVisible(true);		
		
	}
	
	
	public static void main(String[] args) throws IOException {
		
		initiateFrame("DB Visual");
		
	}
	
}