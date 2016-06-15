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

import model.Customer;
import service.CustomerService;

@Controller
@RequestMapping("/")
public class MainController {
	@Autowired @Qualifier("CustomerService") private CustomerService service;
	
	@RequestMapping(value = {"/", "/login" }, method = RequestMethod.GET)
	public String login(ModelMap model) {
		Customer customer = new Customer();
		model.addAttribute("loginForm", customer);

		return "login";
	}

	@RequestMapping(value = {"/login" }, method = RequestMethod.POST)
	public String authenticate(@ModelAttribute("loginForm") Customer user, ModelMap model, HttpSession session) {
		List customers = (List) service.findAllCustomers();
		int i;
		int Customer_id = 0;
		boolean flag = false;
		for(i=0;i<customers.size();i++){
			System.out.println("customer.username=" + user.getUsername());
			System.out.println("customer.password=" + user.getPassword());
			int    id = ((Customer)customers.get(i)).getId();
			String nm = ((Customer)customers.get(i)).getUsername();
			String pw = ((Customer)customers.get(i)).getPassword();
			if (nm.equals(user.getUsername())
					&& pw.equals(user.getPassword())){
				session.setAttribute("customer", customers.get(i));
				session.setAttribute("isLogin", true);
				Customer_id = id;
				flag = true; 
				break;
			} 
		}
		
		if (flag){
			model.addAttribute("Customer_id", Customer_id);
			//return "forward:Customer/info";
			return "redirect:Customer/info";
		} else {
			return "login";
		}
	}
	
	/*@RequestMapping(value = {"/logout" }, method = RequestMethod.GET)
	public String authenticate(@RequestParam("customerId") int id, ModelMap model, HttpSession session) {

		session.setAttribute("isLogin", false);
		return "redirect:/";
		
	}*/
}
