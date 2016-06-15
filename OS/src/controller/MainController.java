package controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import model.Customer;

@Controller
@RequestMapping("/")
public class MainController {
	//@Autowired
	//@Qualifier("studentService")
	//StudentService service;
	@RequestMapping(value = {"/", "/login" }, method = RequestMethod.GET)
	public String login(ModelMap model) {
		Customer customer = new Customer();
		model.addAttribute("loginForm", customer);

		return "login";
	}

	/*@RequestMapping(value = {"/login" }, method = RequestMethod.POST)
	public String authenticate(@ModelAttribute("loginForm") Student user, ModelMap model, HttpSession session) {
		List<Student> students = service.findAllStudents();
		int student_id = 0;
		boolean flag = false;
		for(Student student : students){
			System.out.println("user.username=" + user.getUsername());
			System.out.println("user.password=" + user.getPassword());
			System.out.println("student.username=" + student.getUsername());
			System.out.println("student.password=" + student.getPassword());
			if (student.getUsername().equals(user.getUsername())
					&& student.getPassword().equals(user.getPassword())){
				session.setAttribute("user", student);
				session.setAttribute("isLogin", true);
				student_id = student.getId();
				flag = true; 
				break;
			} 
		}*/
		
		/*if (flag){
			model.addAttribute("student_id", student_id);
			//return "forward:student/info";
			return "redirect:student/info";
		} else {
			return "login";
		}
	}
	
	@RequestMapping(value = {"/logout" }, method = RequestMethod.GET)
	public String authenticate(@RequestParam("userId") int id, ModelMap model, HttpSession session) {

		session.setAttribute("isLogin", false);
		return "redirect:/";
		
	}*/
}
