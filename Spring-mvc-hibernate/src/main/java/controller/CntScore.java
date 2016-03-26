package controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import model.Score;
import model.Student;
import service.AddressService;
import service.ScoreService;
import service.StudentService;

@Controller
@RequestMapping("student/score")
public class CntScore {
	@Autowired
	@Qualifier("addressService")
	AddressService addressService;
	@Autowired
	@Qualifier("scoreService")
	ScoreService scoreService;
	@Autowired
	@Qualifier("studentService")
	StudentService studentService;

	@RequestMapping(value = { "/new" }, method = RequestMethod.POST)
	public String createScore(@ModelAttribute("scoreForm") Score score, ModelMap model) {
		
		Student student = studentService.findById(score.getStudent_id());

		score.setStudent(student);
		scoreService.saveScore(score);

		//return "redirect:/student/info?student_id=" + student.getId();
		String message = "New Subject and Score were added successfully.";
		model.addAttribute("message", message);
		model.addAttribute("studentId", student.getId());
		
		return "success";

	}
	
	@RequestMapping(value = { "/edit" }, method = RequestMethod.GET)
	public String editScore(@RequestParam("scoreId") int scoreId, @RequestParam("studentId") int studentId, ModelMap model
			, HttpSession session, HttpServletRequest request) {
		//Score scoreForm = new Score();
		Score score = scoreService.findById(scoreId);
		
		model.addAttribute("scoreForm", score);
		
		model.addAttribute("studentId", studentId);

		return "editScore";

	}
	
	@RequestMapping(value = { "/edit" }, method = RequestMethod.POST)
	public String editScore(@ModelAttribute("scoreForm") Score score, ModelMap model
			, HttpSession session, HttpServletRequest request) {
		
		Student student = studentService.findById(score.getStudent_id());
		score.setStudent(student);
		scoreService.updateScore(score);
		
		String message = "Subject and Score were editted successfully";
		model.addAttribute("message", message);
		model.addAttribute("studentId", student.getId());
		
		return "success";

	}
	
	@RequestMapping(value = { "/askDelete" }, method = RequestMethod.GET)
	public String askDeleteScore(@RequestParam("scoreId") int scoreId, @RequestParam("studentId") int studentId, ModelMap model) {

		model.addAttribute("scoreId", scoreId);
		model.addAttribute("studentId", studentId);

		return "confirm";

	}
	
	@RequestMapping(value = { "/delete" }, method = RequestMethod.GET)
	public String deleteScore(@RequestParam("scoreId") int scoreId, @RequestParam("studentId") int studentId, ModelMap model) {

		scoreService.deleteScoreById(scoreId);
		model.addAttribute("message", "Subject and Score were deleted successfully.");
		model.addAttribute("studentId", studentId);

		return "success";

	}
}
