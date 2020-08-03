import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
	
	MysqlCon con;
	
	private String[] domainList;
	private String[] tableList;
		
	private GridBagLayout gridbag;
	private GridBagConstraints c;

	private JPanel mainPanel;
	
	private JPanel inputDomain;
	private JPanel inputTable;
	private JPanel result;
	
	private String[][] fullTable;
	private String[] colNames;

	
	public void addContents(Container pane) {
		
		mainPanel = new JPanel();
		
				       
		gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        
        mainPanel.setLayout(null);
        
        c.insets = new Insets(30, 30, 30, 30);
		
        // Connect to the Database
        con = new MysqlCon("root", "blueBlanket!2");
        
        // Populate domainList with Domains from the Database
        populateDomains();		
		
        Border b = BorderFactory.createLineBorder(Color.BLACK, 2);
        
        // Create Left Panel
        inputDomain = new JPanel();
        inputDomain.setLayout(null);
        //inputDomain.setBorder(b);
        
        // Create Right Panel
        inputTable = new JPanel();  
        inputTable.setLayout(null);
       // inputTable.setBorder(b);
        
        // Create Result Panel
        result = new JPanel();
        result.setBorder(b);
        
        // Add three panels
        inputDomain.setBounds(30, 30, (screenX-60)/2, 50);
        mainPanel.add(inputDomain);

        inputTable.setBounds(30 + (screenX-60)/2, 30, (screenX-60)/2, 50);
        mainPanel.add(inputTable);

        result.setBounds(30, 100, (screenX-60), 700);
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
		inputDomain.add(domainDropdown);
		
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
		inputTable.add(tableDropdown);
		
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
        tableContainer.setBounds(10, 10, result.getWidth()-20, result.getHeight()-100);
        
        Border b1 = BorderFactory.createLineBorder(Color.BLUE, 3);
        //tableContainer.setBorder(b1);
        result.add(tableContainer);
        
        JButton backButton = new JButton("Clear Table");
        backButton.setBackground(Color.RED); 
        backButton.setBounds(10, result.getHeight() - 80, 100, 50);
		result.add(backButton, c);
		
		result.repaint();
		
		backButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				result.removeAll();
				result.repaint();
				inputTable.removeAll();
				inputTable.repaint();
				renderDomainPanel();
				
			}});
		
		
	}
	
	public void populateResult() {
		
		try {
			con.generateFullTable();
			
			fullTable = con.getFullTable();
			colNames = con.getColNames();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		renderResultPanel();
		
	}
	
	public void populateDomains() {
		
		domainList = new String[con.getDomains().size()];
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		tableList = new String[con.getTables().size()];
		int indx = 0;
		for (String table : con.getTables()) {
			tableList[indx] = table;
			indx++;
		}
						
	}
	
	public static void initiateFrame(String name) throws IOException {
		
		JFrame f = new JFrame();
		f.setName(name);
		f.setSize(screenX, screenY);
		f.setResizable(false);
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