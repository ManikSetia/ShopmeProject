package com.shopme.admin.user;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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

        //Display the first page for /users
//        Page<User> page = userService.listByPage(1);
//        List<User> userList = page.getContent();
//
//        model.addAttribute("userList", userList);
//
//        return "users";

        //code reusability
        return listByPage(1, model, "firstName", "asc", null);
    }

    //For pagination
    @GetMapping("/users/page/{pageNumber}")
    public String listByPage(@PathVariable("pageNumber") int pageNumber, Model model,
                             @Param("sortField") String sortField, @Param("sortDirection") String sortDirection, @Param("keyword") String keyword){

//        System.out.println("sortField: "+sortField);
//        System.out.println("sortDirection: "+sortDirection);

        Page<User> page = userService.listByPage(pageNumber, sortField, sortDirection, keyword);
        List<User> userList = page.getContent();

//        System.out.println("Total elements: "+page.getTotalElements());
//        System.out.println("Total pages: "+page.getTotalPages());

        long startCount=(pageNumber-1)*userService.USERS_PER_PAGE+1;
        long endCount=startCount+userService.USERS_PER_PAGE-1;

        if(endCount>page.getTotalElements()) {
            endCount=page.getTotalElements();
        }

//        System.out.println("Start count: "+startCount);
//        System.out.println("End count: "+endCount);

        String reverseSortDirection=(sortDirection.equals("asc"))?"desc":"asc";
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("lastPage", page.getTotalPages());
        model.addAttribute("userList", userList);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", reverseSortDirection);
        model.addAttribute("keyword", keyword);

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
//        return "redirect:/users";//if redirect is not used, then we'll have to refresh the page after saving the object
        return getRedirectURLToAffectedUsers(user);
    }

    //code improvement so that if any user is updated, the user will be redirected to the updation page.
    private String getRedirectURLToAffectedUsers(User user) {
        String firstPartOfEmail= user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDirection=asc&keyword=" + firstPartOfEmail;
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
