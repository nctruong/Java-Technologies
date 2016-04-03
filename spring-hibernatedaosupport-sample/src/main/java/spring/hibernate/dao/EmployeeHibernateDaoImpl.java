package spring.hibernate.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import spring.hibernate.model.Employee;


public class EmployeeHibernateDaoImpl extends HibernateDaoSupport implements EmployeeDao {

	@Transactional(readOnly = true)
	public List<?> findEmployees() {
		List<?> empList = getHibernateTemplate().find("from Employee where name in (?, ?)", "Joe", "Sam");
		System.out.println("Employees found: " + empList.size());
		return empList;
	}

	@Transactional(readOnly = false)
	public void deleteEmployees(List<?> empList) {
		if (!empList.isEmpty()) {
			getHibernateTemplate().deleteAll(empList);
			System.out.println("Employees deleted");
		}
	}

	@Transactional(readOnly = false)
	public void createEmployee(final String Name) {
		System.out.println("Create new employee " + Name);
		Employee emp = getHibernateTemplate().execute(new HibernateCallback<Employee>() {

			public Employee doInHibernate(Session session) throws HibernateException {
				Employee emp = new Employee();
				emp.setName(Name);
				session.saveOrUpdate(emp);
				return emp;
			}
		});
		System.out.println("Employee created " + emp);
	}

	@Transactional(readOnly = false)
	public void saveEmployee(Employee emp) {
		System.out.println("Create new employee " + emp);
		getHibernateTemplate().save(emp);
		System.out.println("Employee created " + emp);
	}
}