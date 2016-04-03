package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCQueryExample {

	public static void main(String[] args) {
		System.out.println("-------- MySQL JDBC Connection Testing ------------");

		try {
			Class.forName("com.mysql.jdbc.Driver");
			
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		}

		System.out.println("MySQL JDBC Driver Registered!");
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fresher_training", "root", "root");
			
			String sql = "SELECT * FROM students";
			 
			statement = connection.createStatement();
			result = statement.executeQuery(sql);
			 
			int count = 0;
			 
			while (result.next()){
			    String fullname = result.getString("fullname");
			    String sex = result.getString("sex");
			    String address = result.getString("address");
			 
			    String output = "User #%d: %s - %s - %s";
			    System.out.println(String.format(output, ++count, fullname, sex, address));
			}

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			
		} finally {
			
			if (result != null){
				try {
					result.close();
				} catch (SQLException e) {
				}
			}
			
			if (statement != null){
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
			
			if (connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

}
