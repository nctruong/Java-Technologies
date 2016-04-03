package dao.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionUtil {

	public static Connection getCurrentConnection(){
		
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fresher_training", "root", "root");
			
		} catch (ClassNotFoundException | SQLException e) {
			connection = null;
			
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
		}

		return connection;
	}
	
	public static void cleanup(PreparedStatement preparedStatement, Connection connection){
		if (preparedStatement != null){
			try {
				preparedStatement.close();
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
	
	
	public static void cleanup(ResultSet result, Statement statement, Connection connection){
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
