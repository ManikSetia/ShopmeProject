package com.shopme.admin.user;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController {

    @Autowired
    private UserService userService;

    @GetMapping("/account")
    public String viewDetails(@AuthenticationPrincipal ShopmeUserDetails loggedUser, Model model){
        String email=loggedUser.getUsername();
        User user=userService.getUserByEmail(email);

        model.addAttribute("user", user);
        return "account_form";
    }

    @PostMapping("/account/update")
    public String updateAccount(User user,
                                @AuthenticationPrincipal ShopmeUserDetails loggedInUser,
                                RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile) throws IOException {
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
            User savedUser=userService.updateAccount(user);
            String uploadDir="user-photos/"+savedUser.getId();

            //cleanup before saving
            FileUploadUtil.cleanup(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
        else{
            //photo is not uploaded
            if(user.getPhotos().isEmpty()) user.setPhotos(null);
            userService.updateAccount(user);
        }

//        userService.save(user);

        loggedInUser.setFirstName(user.getFirstName());
        loggedInUser.setLastName(user.getLastName());

        redirectAttributes.addFlashAttribute("alertMessage", "Your account has been updated.");
//        return "redirect:/users";//if redirect is not used, then we'll have to refresh the page after saving the object
        return "redirect:/account";
    }
}
