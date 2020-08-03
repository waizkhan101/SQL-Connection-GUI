// Waiz Khan
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MysqlCon {
	
	private String username;
	private String password;
	private Connection con;
	
	private List<String> domainNames;
	private String selectedDomain;
	private List<String> tableNames;
	private String selectedTable;
	
	private String[][] fullTable;
	private String[] colNames;

	
	public MysqlCon(String user, String pass) {
		
		this.username = user;
		this.password = pass;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/common", username, password);

			fillDomains(con);
			
			//selectedDomain = "ABC";
			
			//fillTables(con);
			
			//selectedTable = "Test1";
			
			//System.out.println(domainNames);
			//System.out.println(tableNames);
						
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	public void fillDomains(Connection con) throws SQLException {
		
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select distinct(DomainName) from tableoftables where ReferenceFlag = 1;");
		
		domainNames = new ArrayList<>();
		while (rs.next())
			domainNames.add(rs.getString(1));
				
	}
	
	public void fillTables(Connection con) throws SQLException {
		
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select TableName from tableoftables where "
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
		
		Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = stmt.executeQuery("select * from "+ selectedDomain + "." + selectedTable + ";");
		
		rs.last();
		int rows = rs.getRow();
		rs.beforeFirst();
		
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();
		
		colNames = new String[cols];
		
		int indx = 0;
		while(rs.next()) {
			colNames[indx] = rsmd.getColumnName(indx + 1);
			indx++;
		}
		rs.beforeFirst();
		System.out.println(Arrays.toString(colNames));
		
		fullTable = new String[rows][cols];
		
		int r = 0;		
		while(rs.next())  {
			
			for(int c = 0; c < cols; c ++) {
				
				String val = rs.getString(c + 1);
				
				if(rs.wasNull())
					fullTable[r][c] = "null";
				
				fullTable[r][c] = val;
			}
			
			r++;
			
		}
		
		for(String[] col : fullTable) {
			
			for(String data : col)
				System.out.print(data + "\t");
			
			System.out.println();
			
		}
		
	}
	
	public String[][] getFullTable() {
		return fullTable;
	}

	public String[] getColNames() {
		return colNames;
	}

	
	
	public static void main(String args[]) {
		
		MysqlCon conn = new MysqlCon("root", "blueBlanket!2");
		
	}

}
