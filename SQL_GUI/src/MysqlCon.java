// Waiz Khan

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MysqlCon {
	
	private Connection con;
	
	private List<String> domainNames;
	private String selectedDomain;
	private List<String> tableNames;
	private String selectedTable;
	
	private String tablePath;
	
	private String[][] fullTable;
	private String[] colNames;

	
	public MysqlCon(String host, String port, String user, String pass) {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/common", user, pass);

			fillDomains(con);
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	public void fillDomains(Connection con) throws SQLException {
		
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select distinct(DomainName) from common.TableofTables where ReferenceFlag = 1;");
		
		domainNames = new ArrayList<>();
		while (rs.next())
			domainNames.add(rs.getString(1));
				
	}
	
	public void fillTables(Connection con) throws SQLException {
		
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select TableName from common.TableofTables where "
				+ "ReferenceFlag = 1 and DomainName = '" + selectedDomain + "';");
		
		tableNames = new ArrayList<>();
		while (rs.next())
			tableNames.add(rs.getString(1));
				
	}
	
	public void selectDomain(String s) throws SQLException {
		selectedDomain = s;
		fillTables(con);
		
		System.out.println("Domain: " + selectedDomain);
	}
	
	public void selectTable(String t) {
		selectedTable = t;
		System.out.println("Table: " + selectedTable);

	}
	
	public List<String> getDomains() {
		return domainNames;
	}
	
	public List<String> getTables() {
		return tableNames;
	}
	
	public void generateFullTable() throws SQLException {
		
		// Get original table and metadata
		tablePath = selectedDomain + "." + selectedTable;
		
		Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = stmt.executeQuery("select * from "+ tablePath + ";");
		
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();
		
		if (cols > 4) {
			cols -= 4;
		}
		else {
			throw new SQLException();
		}
		
		rs.last();
		int rows = rs.getRow();
		rs.beforeFirst();
		
		colNames = new String[cols];
		
		for(int indx = 0; indx < cols; indx++)
			colNames[indx] = rsmd.getColumnName(indx + 1);
	
		System.out.println(Arrays.toString(colNames));
		
		/*
		// Filter table and get new metadata
		ResultSet updatedRS = filterTable();
		ResultSetMetaData updatedRSMD = updatedRS.getMetaData();
		cols = updatedRSMD.getColumnCount();
		
		colNames = new String[cols];
		for(int indx = 0; indx < cols; indx++)
			colNames[indx] = updatedRSMD.getColumnName(indx + 1);
	
		System.out.println(Arrays.toString(colNames));
		
		updatedRS.last();
		rows = updatedRS.getRow();
		updatedRS.beforeFirst();
		*/
		
		fullTable = new String[rows + 1][cols];
		
		int r = 0;		
		while(rs.next())  {
			
			for(int c = 0; c < cols; c ++) {
				
				String val = rs.getString(c + 1);
				fullTable[r][c] = val;
				
			}
			r++;
			
		}
		
		for(String[] col : fullTable) {
			
			for(String data : col)
				System.out.print(data + "\t");
			
			System.out.println();
			
		}
		//stmt.executeUpdate("DROP TABLE new_tbl;");
		
	}
	
	public ResultSet filterTable() throws SQLException {
		
		Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		stmt.executeUpdate("CREATE TEMPORARY TABLE new_tbl SELECT * FROM " + tablePath + ";");
		
		for(int x = colNames.length - 1; x > -1; x--) {
			
			if(colNames[x].equals("DateTimeModified") || colNames[x].equals("ModifiedBy") || colNames[x].equals("DateTimeAdded") || colNames[x].equals("AddedBy"))
				stmt.executeUpdate("ALTER TABLE new_tbl DROP COLUMN " + colNames[x] + ";");
			
		}
		
		return stmt.executeQuery("SELECT * FROM new_tbl;");
		
		
		// check if audit columns are missing
	}

	
	public String[][] getFullTable() {
		return fullTable;
	}

	public String[] getColNames() {
		return colNames;
	}

}