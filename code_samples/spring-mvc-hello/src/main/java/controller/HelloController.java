package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {

	@RequestMapping("/welcome")
	public String sayHello(ModelMap model){
		
		
		String message = "Welcome to Spring MVC.!";
		
		model.addAttribute("message", message);
		
		return "welcome";
	}
}
