package controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import model.Score;
import model.Student;
import service.StudentService;


@Controller
@RequestMapping("student")
public class CntStudent {
	
	@Autowired
	@Qualifier("studentService")
	StudentService service;

	@RequestMapping(value = { "/info" }, method = RequestMethod.GET)
	public String studentInfo(@RequestParam("student_id") int student_id, ModelMap model){
		// scoreForm
		Score scoreForm	= new Score();
		model.put("scoreForm", scoreForm);
		
		Student student = service.findById(student_id);
		model.addAttribute("student", student);
		model.addAttribute("address", student.getAddress());
				
		
		List<Score> scores  = student.getScores();
		List<Score> modelScores	= new ArrayList<Score>(); 
		for(Score score:scores){
			if (score != null){
				modelScores.add(score);
			}
		}
		model.addAttribute("scores", modelScores);
		model.addAttribute("studentId", student_id);
		
		return "studentInfo";
	}
	
	@RequestMapping(value = { "/logout" }, method = RequestMethod.GET)
	public String studentInfo(@RequestParam("userId") int userId, ModelMap model
			,HttpSession session, HttpServletRequest request){
		
		session.setAttribute("isLogin", false);
		
		return "login";
	}
}
