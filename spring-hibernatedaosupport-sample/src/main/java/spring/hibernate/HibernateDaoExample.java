package spring.hibernate;

import java.io.IOException;
import java.util.List;

import org.hibernate.MappingException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import spring.hibernate.dao.EmployeeDao;
import spring.hibernate.model.Employee;


public class HibernateDaoExample {

	private EmployeeDao empDao;
	
    public static void main(String[] args) throws MappingException, IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        HibernateDaoExample hibernateDao = (HibernateDaoExample) context.getBean("hibernateDaoExample");
        hibernateDao.execute();
    }
    
    @Transactional(readOnly=false)
    public void execute() {
        List<?> empList = empDao.findEmployees();
        empDao.deleteEmployees(empList);
        empDao.createEmployee("Joe");
        Employee empSam = new Employee();
        empSam.setName("Sam");
        empDao.saveEmployee(empSam);
        System.out.println("List of employees: " + empDao.findEmployees());
    }

    public void setEmployeeDao(EmployeeDao empDao) {
        this.empDao = empDao;
    }     
}
