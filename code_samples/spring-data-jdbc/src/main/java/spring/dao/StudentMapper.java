package spring.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import spring.model.Student;

public class StudentMapper implements RowMapper<Student> {

	@Override
	public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
		Student student = new Student();
		student.setId(rs.getInt("student_id"));
		student.setFullName(rs.getString("fullname"));
		student.setSex(rs.getString("sex"));
		student.setAddress(rs.getString("address"));
		return student;
	}
}
