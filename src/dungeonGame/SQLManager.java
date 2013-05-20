package dungeonGame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLManager {
	
	private final String PATH = "C:/Users/Evan/workspace/Dungeon/src/dungeonGame/";
	private static int nextInt;
	
	private Connection connection;
	private String tableName;
	
	public SQLManager(String name) {
		// load the sqlite-JDBC driver using the current class loader
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			System.out.println("ClassNotFoundException");
		}
		
		tableName = name;
		nextInt = 1;
		connection = null;
		
		createTable(tableName);
	}
	
	public void createTable(String name) {
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + PATH + "saves.db");
			
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
			
			statement.executeUpdate("DROP TABLE IF EXISTS " + tableName);
			statement.executeUpdate("CREATE TABLE " + tableName + " (id integer, name string)");
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		finally {
			closeConnection();
		}
	}
	
	public void insertRow(String table, String name) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + PATH + "saves.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
			
			statement.executeUpdate("INSERT INTO " + table + " values(" + nextInt++ + ", '" + name + "')");
		}
		catch(SQLException e) {
			System.err.println(e.getMessage());
		}
		finally {
			closeConnection();
		}
	}
	
	public void updateTable(String table, int rowId, String newName) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + PATH + "saves.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
			String update = "UPDATE " + table + " " +
							"SET name='" + newName + "' " +
							"WHERE id=" + rowId + ";";
			statement.executeUpdate(update);
		}
		catch(SQLException e) {
			System.err.println(e.getMessage());
		}
		finally {
			closeConnection();
		}
	}
	
	public void deleteRow(String table, int rowId) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + PATH + "saves.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
			String update = "DELETE FROM " + table + " " +
							"WHERE id=" + rowId + ";";
			statement.executeUpdate(update);
		}
		catch(SQLException e) {
			System.err.println(e.getMessage());
		}
		finally {
			closeConnection();
		}
	}
	
	public void printTable(String table) {
		System.out.println("Results from table: " + table + "...");
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + PATH + "saves.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
			ResultSet rs = statement.executeQuery("SELECT * FROM " + table);
			while(rs.next()) {
				System.out.println("id: " + rs.getInt("id") + " name: " + rs.getString("name"));
			}
		}
		catch(SQLException e) {
			System.err.println(e.getMessage());
		}
		finally {
			closeConnection();
		}
	}
	
	public void closeConnection() {
		try {
			if(connection != null)
				connection.close();
		}
		catch(SQLException e) {
			System.err.println(e + "failure to close");
		}
	}
	
	public static void main(String[] args) {
		String table = "myTable";
		SQLManager myManager = new SQLManager(table);
		
		System.out.println("## starting run ##");
		
		myManager.insertRow(table, "evan");
		myManager.insertRow(table, "mike");
		myManager.insertRow(table, "troy");
		myManager.printTable(table);
		
		myManager.updateTable(table, 2, "LAMP");
		myManager.printTable(table);
		
		myManager.deleteRow(table, 2);
		myManager.printTable(table);
		
		System.out.println("## ending run ##");
	}

}
