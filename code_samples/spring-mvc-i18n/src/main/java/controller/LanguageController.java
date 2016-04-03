package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LanguageController {

	@RequestMapping(value = "/welcome")
	public String saySomething(ModelMap model){
		
		return "welcome";
	}
}
