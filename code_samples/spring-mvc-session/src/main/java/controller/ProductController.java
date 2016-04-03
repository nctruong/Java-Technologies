package controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import model.Product;

@Controller
@RequestMapping("/")
@SessionAttributes({"cart"})
public class ProductController {

	@RequestMapping(method = RequestMethod.GET)
	public String get(Model model) {
		if (!model.containsAttribute("cart")) {
			model.addAttribute("cart", new ArrayList<Product>());
		}
		
		Product product = new Product();
		model.addAttribute("product", product);
		
		return "home";
	}

	@RequestMapping(value = "addProduct", method = RequestMethod.POST)
	public String addProduct(@ModelAttribute Product product, @ModelAttribute("cart") List<Product> cart) {
		cart.add(product);
		return "redirect:/";
	}
}
