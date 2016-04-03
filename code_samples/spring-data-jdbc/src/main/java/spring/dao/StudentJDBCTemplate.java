package spring.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import spring.model.Student;

public class StudentJDBCTemplate implements StudentDAO {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void create(String fullName, String sex, String address) {

		String sql = " INSERT INTO students (fullname, sex, address) VALUES (?, ?, ?) ";

		jdbcTemplate.update(sql, fullName, sex, address);

		System.out.println("A new student was inserted successfully!");

		return;
	}

	@Override
	public Student getStudent(Integer id) {
		String sql = " SELECT * FROM students WHERE student_id = ? ";
		Student student = jdbcTemplate.queryForObject(sql, new Object[] { id }, new StudentMapper());
		return student;
	}

	@Override
	public List<Student> listStudents() {
		String SQL = " SELECT * FROM students ";
		List<Student> students = jdbcTemplate.query(SQL, new StudentMapper());
		return students;
	}

	@Override
	public void delete(Integer id) {
		String SQL = " DELETE FROM students WHERE student_id = ? ";
		jdbcTemplate.update(SQL, id);
		System.out.println("Deleted Record with ID = " + id);
		return;
	}

	@Override
	public void update(Integer id, String address) {
		String SQL = " UPDATE students SET address = ? WHERE student_id = ? ";
		jdbcTemplate.update(SQL, address, id);
		System.out.println("Updated Record with ID = " + id);
		return;

	}

}
