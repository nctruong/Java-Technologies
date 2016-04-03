package dao.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dao.model.Student;
import dao.util.ConnectionUtil;

public class StudentDAOImpl implements StudentDAO {

	@Override
	public void create(String fullName, String sex, String address) {
		PreparedStatement preparedStatement = null;
		Connection connection = ConnectionUtil.getCurrentConnection();
		
		try {
			String sql = "INSERT INTO students (fullname, sex, address) VALUES (?, ?, ?)";
			 
			preparedStatement = connection.prepareStatement(sql);
			
			preparedStatement.setString(1, fullName);
			preparedStatement.setString(2, sex);
			preparedStatement.setString(3, address);
			 
			int rowsInserted = preparedStatement.executeUpdate();
			if (rowsInserted > 0) {
			    System.out.println("A new student was inserted successfully!");
			}

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			
		} finally {
			ConnectionUtil.cleanup(preparedStatement, connection);
		}
		
	}

	@Override
	public List<Student> listStudents() {
		List<Student> list = new ArrayList<Student>();
		
		Connection connection = ConnectionUtil.getCurrentConnection();
		Statement statement = null;
		ResultSet result = null;
		
		try {
			String sql = "SELECT * FROM students";
			 
			statement = connection.createStatement();
			result = statement.executeQuery(sql);
			
			Student student = null;
			while (result.next()){
				student = new Student();
				
				student.setId(result.getInt("student_id"));
			    student.setFullName(result.getString("fullname"));
			    student.setSex(result.getString("sex"));
			    student.setAddress(result.getString("address"));
			    
			    list.add(student);
			}

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			
		} finally {
			ConnectionUtil.cleanup(result, statement, connection);
		}
		
		return list;
	}

}
