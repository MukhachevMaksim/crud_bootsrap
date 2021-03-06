package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import web.dao.UserDao;
import web.model.Role;
import web.model.User;
import web.service.RoleService;
import web.service.UserService;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private RoleService roleService;
    private UserService userService;
    private UserDao userDao;

    @Autowired
    public AdminController(RoleService roleService, UserService userService, UserDao userDao) {
        this.roleService = roleService;
        this.userService = userService;
        this.userDao = userDao;
    }

    @GetMapping
    public String getAdmin(Model model) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        User authUser = userDao.getUserByName(a.getName());
        model.addAttribute("authUser", authUser);
        model.addAttribute("listUsers", userService.listUsers());
        model.addAttribute("newUser", new User());
        model.addAttribute("listRoles", roleService.listRoles());
        return "admin";
    }

    @PostMapping
    public String create(@ModelAttribute("user") User user,
                         @RequestParam("select") String[] select) {
        Set<Role> roles = Arrays.stream(select).map(id -> Long.valueOf(id))
                .map(id -> roleService.getRoleById(id))
                .collect(Collectors.toSet());
        user.setRoles(roles);
        userService.add(user);
        return "redirect:/admin";
    }

    @PatchMapping(value = "/{id}")
    public String update(@PathVariable("id") Long id,
                         @RequestParam("select") String[] select,
                         @RequestParam("editFirstName") String editFirstName,
                         @RequestParam("editLastName") String editLastName,
                         @RequestParam("editAge") Long editAge,
                         @RequestParam("editEmail") String editEmail,
                         @RequestParam("editPassword") String editPassword) {
        User editUser = new User();
        editUser.setFirstName(editFirstName);
        editUser.setLastName(editLastName);
        editUser.setAge(editAge);
        editUser.setEmail(editEmail);
        editUser.setPassword(editPassword);
        Set<Role> roles = Arrays.stream(select).map(Long::valueOf)
                .map(x -> roleService.getRoleById(x))
                .collect(Collectors.toSet());
        editUser.setRoles(roles);
        userService.update(id, editUser);
        return "redirect:/admin";
    }

    @DeleteMapping(value = "/{id}")
    public String delete(@PathVariable("id") Long id) {
        userService.removeUserById(id);
        return "redirect:/admin";
    }

}
