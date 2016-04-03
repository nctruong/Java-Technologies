package spring.hibernate.dao;

import java.util.List;

import spring.hibernate.model.Employee;


public interface EmployeeDao {
	List<?> findEmployees();

    void deleteEmployees(List<?> empList);

    void createEmployee(String Name);

    void saveEmployee(Employee employee);
}
