package pl.edu.agh.ki.mwo.web.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pl.edu.agh.ki.mwo.model.School;
import pl.edu.agh.ki.mwo.persistence.DatabaseConnector;

@Controller
public class SchoolsController {

    @RequestMapping(value="/Schools")
    public String listSchools(Model model, HttpSession session) {
    	if (session.getAttribute("userLogin") == null)
    		return "redirect:/Login";

    	model.addAttribute("schools", DatabaseConnector.getInstance().getSchools());

        return "schoolsList";
    }

    @RequestMapping(value="/AddSchool")
    public String displayAddSchoolForm(Model model, HttpSession session) {
    	if (session.getAttribute("userLogin") == null)
    		return "redirect:/Login";

        return "schoolForm";
    }

    @RequestMapping(value="/CreateSchool", method=RequestMethod.POST)
    public String createSchool(@RequestParam(value="schoolName", required=false) String name,
    		@RequestParam(value="schoolAddress", required=false) String address,
    		Model model, HttpSession session) {
    	if (session.getAttribute("userLogin") == null)
    		return "redirect:/Login";

    	School school = new School();
    	school.setName(name);
    	school.setAddress(address);

    	DatabaseConnector.getInstance().addSchool(school);
       	model.addAttribute("schools", DatabaseConnector.getInstance().getSchools());
    	model.addAttribute("message", "Nowa szkoła została dodana");

    	return "schoolsList";
    }

    @RequestMapping(value="/DeleteSchool", method=RequestMethod.POST)
    public String deleteSchool(@RequestParam(value="schoolId", required=false) String schoolId,
    		Model model, HttpSession session) {
    	if (session.getAttribute("userLogin") == null)
    		return "redirect:/Login";

    	DatabaseConnector.getInstance().deleteSchool(schoolId);
       	model.addAttribute("schools", DatabaseConnector.getInstance().getSchools());
    	model.addAttribute("message", "Szkoła została usunięta");

    	return "schoolsList";
    }

    @RequestMapping(value = "/UpdateSchool", method = RequestMethod.POST)
	public String editSchool(@RequestParam(value = "schoolId") String schoolID,
			                 @RequestParam(value = "newSchoolName", required = false) String name,
							 @RequestParam(value = "newSchoolAddress", required = false) String address,
							 Model model, HttpSession session) {
		if (session.getAttribute("userLogin") == null)
			return "redirect:/Login";

		DatabaseConnector.getInstance().editSchool(Long.parseLong(schoolID), name, address);
		model.addAttribute("schools", DatabaseConnector.getInstance().getSchools());
		model.addAttribute("message", "Szkoła została zmieniona");

		return "schoolsList";
	}

	@RequestMapping(value = "/EditSchool", method = RequestMethod.POST)
	public String displayEditSchoolForm(Model model,
										@RequestParam(value = "schoolId") String schoolId,
										HttpSession session) {
		if (session.getAttribute("userLogin") == null)
			return "redirect:/Login";

		model.addAttribute("schoolId", schoolId);
		model.addAttribute("school", DatabaseConnector.getInstance().getSchoolById(schoolId));

		return "schoolEdit";
    }
}