package by.pavvel.project.controller;

import by.pavvel.project.entity.Employee;
import by.pavvel.project.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(path = "/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String employeesHomePage(Model model){
        return getEmployees(1,"id","asc",model);
    }

    @GetMapping("/page/{pageNo}")
    public String getEmployees(@PathVariable("pageNo") int pageNo,
                               @RequestParam("sortField") String field,
                               @RequestParam("sortDirection") String direction,
                               Model model){

        int pageSize = 5;
        Page<Employee> page = employeeService.showEmployees(pageNo,pageSize,field,direction);
        List<Employee> employees = page.getContent();

        model.addAttribute("page",page);
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalProjects",page.getTotalElements());
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("employees",employees);

        model.addAttribute("sortField",field);
        model.addAttribute("sortDirection",direction);
        model.addAttribute("sortReverse",direction.equals("asc") ? "desc" : "asc");
        return "employee";
    }

    @GetMapping("/new")
    public String showEmployeeForm(@ModelAttribute("employee") Employee employee,
                                   Model model){
        model.addAttribute("employees", employeeService.getEmployees());
        return "employee_form";
    }

    @PostMapping("/add")
    public String addEmployee(@ModelAttribute("employee") @Valid Employee employee,
                              BindingResult bindingResult,
                              Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("employees", employeeService.getEmployees());
            return "employee_form";
        }
        employeeService.addEmployee(employee);
        return "redirect:/employee";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateEmployeeForm(@PathVariable("id") Long id,
                                         Model model) {

        model.addAttribute("id",id);
        model.addAttribute("employee",employeeService.getEmployee(id));
        return "employee_update";
    }

    @PostMapping("/update")
    public String updateEmployee(@ModelAttribute("employee") @Valid Employee employee,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()){
            model.addAttribute("id",employee.getId());
            model.addAttribute("employee",employee);
            return "employee_update";
        }
        employeeService.updateEmployee(employee);
        return "redirect:/employee";
    }

    @GetMapping("/remove/{id}")
    public String deleteEmployee(@PathVariable("id") Long id){
        employeeService.deleteEmployee(id);
        return "redirect:/employee";
    }
}
