package pl.edu.agh.ki.mwo.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pl.edu.agh.ki.mwo.model.Student;
import pl.edu.agh.ki.mwo.persistence.DatabaseConnector;

import javax.servlet.http.HttpSession;


@Controller
public class StudentController {

    @RequestMapping(value = "/Students")
    public String listStudents(Model model, HttpSession session) {
        if (session.getAttribute("userLogin") == null)
            return "redirect:/Login";

        model.addAttribute("students", DatabaseConnector.getInstance().getStudents());

        return "studentsList";
    }

    @RequestMapping(value = "/DeleteStudent", method = RequestMethod.POST)
    public String deleteStudent(@RequestParam(value = "studentId", required = false) String studentId,
                                Model model, HttpSession session) {
        if (session.getAttribute("userLogin") == null)
            return "redirect:/Login";

        DatabaseConnector.getInstance().deleteStudent(studentId);
        model.addAttribute("students", DatabaseConnector.getInstance().getStudents());
        model.addAttribute("message", "Uczeń został usunięty");

        return "studentsList";
    }

    @RequestMapping(value = "/AddStudent")
    public String displayAddStudentForm(Model model, HttpSession session) {
        if (session.getAttribute("userLogin") == null)
            return "redirect:/Login";

        model.addAttribute("schoolClasses", DatabaseConnector.getInstance().getSchoolClasses());

        return "studentForm";
    }

    @RequestMapping(value = "/CreateStudent", method = RequestMethod.POST)
    public String createStudent(@RequestParam(value = "studentName", required = false) String name,
                                @RequestParam(value = "studentSurname", required = false) String surname,
                                @RequestParam(value = "studentPesel", required = false) String pesel,
                                @RequestParam(value = "studentSchoolClass", required = false) String schoolClassId,
                                Model model, HttpSession session) {
        if (session.getAttribute("userLogin") == null)
            return "redirect:/Login";

        Student student = new Student();
        student.setName(name);
        student.setSurname(surname);
        student.setPesel(pesel);

        DatabaseConnector.getInstance().addStudent(student, schoolClassId);
        model.addAttribute("students", DatabaseConnector.getInstance().getStudents());
        model.addAttribute("message", "Nowy uczeń został dodany");

        return "studentsList";
    }

    @RequestMapping(value = "/UpdateStudent", method = RequestMethod.POST)
    public String editStudent(@RequestParam(value = "studentId") String studentId,
                              @RequestParam(value = "newStudentName", required = false) String name,
                              @RequestParam(value = "newStudentSurname", required = false) String surname,
                              @RequestParam(value = "newStudentPesel", required = false) String pesel,
                              @RequestParam(value = "newStudentSchoolClass", required = false) String schoolClassId,
                              Model model, HttpSession session) {
        if (session.getAttribute("userLogin") == null)
            return "redirect:/Login";

        DatabaseConnector.getInstance().editStudent(Long.parseLong(studentId), name, surname,
                pesel, schoolClassId);

        model.addAttribute("students", DatabaseConnector.getInstance().getStudents());
        model.addAttribute("message", "Dane ucznia zostały zmienione");

        return "studentsList";
    }

    @RequestMapping(value = "/EditStudent", method = RequestMethod.POST)
    public String displayEditStudentForm(Model model, HttpSession session,
                                         @RequestParam(value = "studentId") String studentId) {
        if (session.getAttribute("userLogin") == null)
            return "redirect:/Login";

        model.addAttribute("schoolClasses", DatabaseConnector.getInstance().getSchoolClasses());
        model.addAttribute("studentId", studentId);
        model.addAttribute("student", DatabaseConnector.getInstance().getStudentById(studentId));

        return "studentEdit";
    }
}
