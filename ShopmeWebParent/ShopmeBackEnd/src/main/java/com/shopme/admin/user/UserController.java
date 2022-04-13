package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listAllUsers(Model model){
        List<User> userList=userService.listAllUsers();
        model.addAttribute("userList", userList);

        return "users";
    }

    //This api is for form page
    @GetMapping("/users/new")
    public String newUser(Model model){
        User user=new User();
        user.setEnabled(true);//setting it because by default it should be true

        List<Role> roles=userService.listRoles();
        //This is passed to map the values of form attributes to our instance variables of User class.
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("pageTitle", "Create New User");
        return "user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes){
        //User object is passed because we want to map the values coming from form to the instance variables of User class
        //RedirectAttributes is used to display the successful message, i.e. we're sending this attribute while redirecting the page.
        //We can send message while redirecting.
//        System.out.println(user);

        userService.save(user);

        redirectAttributes.addFlashAttribute("saveMessage", "The user has been added successfully.");
        return "redirect:/users";//if redirect is not used, then we'll have to refresh the page after saving the object
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes){
        try{
            User user=userService.get(id);
            List<Role> roles=userService.listRoles();
            model.addAttribute(roles);
            model.addAttribute(user);
            model.addAttribute("roles", roles);
            model.addAttribute("pageTitle", "Edit User (ID: "+id+")");
//            model.addAttribute("enabled", user.isEnabled());
            return "user_form";
        }
        catch (UserNotFoundException ex){
            redirectAttributes.addFlashAttribute("userNotFoundMessage", ex.getMessage());
            return "redirect:/users";
        }

    }

}
