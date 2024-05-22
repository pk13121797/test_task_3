package by.pavvel.controller;

import by.pavvel.utils.FileValidator;
import by.pavvel.model.Employee;
import by.pavvel.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(path = "/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    private final FileValidator fileValidator;

    public EmployeeController(EmployeeService employeeService, FileValidator fileValidator) {
        this.employeeService = employeeService;
        this.fileValidator = fileValidator;
    }

    @GetMapping
    public String employeesHomePage(@RequestParam(value = "q",required = false) String q, Model model){
        return getEmployees(1,"id","asc",q, model);
    }

    @GetMapping("/page/{pageNo}")
    public String getEmployees(@PathVariable("pageNo") int pageNo,
                               @RequestParam("sortField") String field,
                               @RequestParam("sortDirection") String direction,
                               @RequestParam(value = "q",required = false) String q,
                               Model model){

        Page<Employee> page = employeeService.showEmployees(pageNo,field,direction,q);
        model.addAttribute("query",q);
        model.addAttribute("page",page);
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("employees",page.getContent());
        model.addAttribute("sortField",field);
        model.addAttribute("sortDirection",direction);
        model.addAttribute("sortReverse",direction.equals("asc") ? "desc" : "asc");
        return "employees/employees";
    }

    @GetMapping("/new")
    public String showEmployeeForm(@ModelAttribute("employee") Employee employee){
        return "employees/add_form";
    }

    @PostMapping
    public String addEmployee(@ModelAttribute("employee") @Valid Employee employee,
                              BindingResult bindingResult,
                              Model model,
                              @RequestParam(value = "files", required = false) MultipartFile... files) {
        fileValidator.validate(files, bindingResult);
        if (bindingResult.hasErrors()){
            model.addAttribute("employees", employeeService.getEmployees());
            return "employees/add_form";
        }
        employeeService.addEmployee(employee, files);
        return "redirect:/employees";
    }

    @GetMapping("/edit/{employeeId}")
    public String showUpdateEmployeeForm(@PathVariable("employeeId") Long employeeId, Model model) {
        model.addAttribute("id",employeeId);
        model.addAttribute("employee",employeeService.getEmployee(employeeId));
        return "employees/edit_form";
    }

    @PutMapping
    public String updateEmployee(@ModelAttribute("employee") @Valid Employee employee,
                                 BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()){
            model.addAttribute("id",employee.getId());
            model.addAttribute("employee",employee);
            return "employees/edit_form";
        }
        employeeService.updateEmployee(employee);
        return "redirect:/employees";
    }

    @DeleteMapping("/{employeeId}")
    public String deleteEmployee(@PathVariable("employeeId") Long employeeId){
        employeeService.deleteEmployee(employeeId);
        return "redirect:/employees";
    }
}
