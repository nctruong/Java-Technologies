package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUpdateExample {
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
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fresher_training", "root", "root");
			
			String sql = "UPDATE students SET fullname = 'Nguyen Van A' WHERE student_id = 1";
			 
			statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql);
			
			System.out.println(String.format("There are %d records is updated", rowAffected));

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			
		} finally {
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
