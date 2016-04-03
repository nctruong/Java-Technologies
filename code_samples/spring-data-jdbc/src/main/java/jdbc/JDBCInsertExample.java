package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBCInsertExample {
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
		PreparedStatement preparedStatement = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fresher_training", "root", "root");
			
			String sql = "INSERT INTO students (fullname, sex, address) VALUES (?, ?, ?)";
			 
			preparedStatement = connection.prepareStatement(sql);
			
			preparedStatement.setString(1, "Tran Minh");
			preparedStatement.setString(2, "M");
			preparedStatement.setString(3, "Cong Hoa, Tan Binh");
			 
			int rowsInserted = preparedStatement.executeUpdate();
			if (rowsInserted > 0) {
			    System.out.println("A new student was inserted successfully!");
			}

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			
		} finally {
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
	}
}
