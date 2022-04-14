package com.shopme.admin.user;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image")MultipartFile multipartFile) throws IOException {
        //User object is passed because we want to map the values coming from form to the instance variables of User class
        //RedirectAttributes is used to display the successful message, i.e. we're sending this attribute while redirecting the page.
        //We can send message while redirecting.
//        System.out.println(user);
//        System.out.println(multipartFile.getOriginalFilename());

        if(!multipartFile.isEmpty()){
            //that means user has uploaded a file

            //It is recommended to use StringUtils to get the file name
            String fileName= StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser=userService.save(user);
            String uploadDir="user-photos/"+savedUser.getId();

            //cleanup before saving
            FileUploadUtil.cleanup(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
        else{
            //photo is not uploaded
            if(user.getPhotos().isEmpty()) user.setPhotos(null);
            userService.save(user);
        }

//        userService.save(user);

        redirectAttributes.addFlashAttribute("alertMessage", "The user has been added successfully.");
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
            redirectAttributes.addFlashAttribute("alertMessage", ex.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes){
        try{
            userService.delete(id);
            redirectAttributes.addFlashAttribute("alertMessage", "The user with ID: "+id+" has been deleted successfully.");
        }
        catch (UserNotFoundException ex){
            redirectAttributes.addFlashAttribute("alertMessage", ex.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id,
                                          @PathVariable("status") boolean enabled,
                                          RedirectAttributes redirectAttributes){
        userService.updateUserEnabledStatus(id, enabled);
        String status=enabled?"enabled":"disabled";
        String message="The user with ID: "+id+" has been "+status;

        redirectAttributes.addFlashAttribute("alertMessage", message);
        return "redirect:/users";
    }

}
