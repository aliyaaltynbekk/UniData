package org.unidata1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.unidata1.model.Data;
import org.unidata1.model.Data.EducationLevel;
import org.unidata1.model.User;
import org.unidata1.service.DataService;
import org.unidata1.service.UserService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/data")
public class DataController {

    private final DataService dataService;
    private final UserService userService;

    @Autowired
    public DataController(DataService dataService, UserService userService) {
        this.dataService = dataService;
        this.userService = userService;
    }

    @GetMapping("/faculties")
    public String listFaculties(Model model) {
        List<Data> dataList = dataService.getAllData();
        model.addAttribute("faculties", dataList);
        model.addAttribute("pageTitle", "Факультеттер");
        return "data/faculties";
    }

    @GetMapping("/faculty/{id}")
    public String viewFaculty(@PathVariable Long id, Model model) {
        dataService.getDataById(id).ifPresent(data -> {
            model.addAttribute("faculty", data);
            model.addAttribute("pageTitle", data.getFacultyName());
        });
        return "data/faculty-details";
    }

    @GetMapping("/departments")
    public String listDepartments(Model model) {
        List<String> departments = dataService.getAllDepartments();
        model.addAttribute("departments", departments);
        model.addAttribute("pageTitle", "Кафедралар");
        return "data/departments";
    }

    @GetMapping("/specialties")
    public String listSpecialties(Model model) {
        Map<String, String> specialties = dataService.getAllSpecialties();
        model.addAttribute("specialties", specialties);
        model.addAttribute("pageTitle", "Мамандықтар");
        return "data/specialties";
    }

    @GetMapping("/teachers")
    public String listTeachers(Model model) {
        List<User> teachers = userService.getUsersByRole(org.unidata1.model.Role.RoleType.TEACHER);
        model.addAttribute("teachers", teachers);
        model.addAttribute("pageTitle", "Оқытушылар");
        return "data/teachers";
    }

    @GetMapping("/students")
    public String listStudents(Model model) {
        List<User> students = userService.getUsersByRole(org.unidata1.model.Role.RoleType.STUDENT);
        model.addAttribute("students", students);
        model.addAttribute("pageTitle", "Студенттер");
        return "data/students";
    }

    @GetMapping("/faculty/new")
    public String newFaculty(Model model) {
        model.addAttribute("faculty", new Data());
        model.addAttribute("educationLevels", EducationLevel.values());
        model.addAttribute("pageTitle", "Жаңа факультет");
        return "data/faculty-form";
    }

    @PostMapping("/faculty/save")
    public String saveFaculty(@ModelAttribute Data data) {
        if (data.getId() == null) {
            dataService.createData(data);
        } else {
            dataService.updateData(data);
        }
        return "redirect:/data/faculties";
    }

    @GetMapping("/faculty/edit/{id}")
    public String editFaculty(@PathVariable Long id, Model model) {
        dataService.getDataById(id).ifPresent(data -> {
            model.addAttribute("faculty", data);
            model.addAttribute("educationLevels", EducationLevel.values());
            model.addAttribute("pageTitle", "Факультетті өңдеу");
        });
        return "data/faculty-form";
    }

    @GetMapping("/import")
    public String importDataForm(Model model) {
        model.addAttribute("pageTitle", "Деректерді импорттау");
        return "data/import";
    }

    @PostMapping("/import")
    public String importData(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                String filePath = "temp/" + file.getOriginalFilename();
                file.transferTo(new java.io.File(filePath));
                dataService.importDataFromFile(filePath);
                return "redirect:/data/faculties?imported";
            } catch (Exception e) {
                return "redirect:/data/import?error";
            }
        }
        return "redirect:/data/import?empty";
    }

    @GetMapping("/export")
    public String exportData() {
        String filePath = "export/data_" + System.currentTimeMillis() + ".json";
        dataService.exportDataToFile(filePath);
        return "redirect:/data/faculties?exported";
    }
}