package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> listAllUsers(){
        return (List<User>) userRepository.findAll();
    }

    //used to get all roles from the db so that we can pass to the form (of creating new user)
    public List<Role>  listRoles(){
        return (List<Role>) roleRepository.findAll();
    }

    public void save(User user){
        boolean isUpdatingUser= (user.getId() != null);

        if(isUpdatingUser){

            //There can be 2 cases:
                //One is user wants to change the password
                //Second is user doesn't wants to change the password
            User existingUser=userRepository.findById(user.getId()).get();
            if(user.getPassword().isEmpty()){
                user.setPassword(existingUser.getPassword());
            }
            else{
                encodePassword(user);
            }
        }
        else {
            //new user
            encodePassword(user);
        }

        userRepository.save(user);
    }

    private void encodePassword(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    public boolean isEmailUnique(Integer id, String email){
        User userByEmail=userRepository.getUserByEmail(email);

        if(userByEmail == null) return true;

        boolean isCreatingNew= (id==null);

        if(isCreatingNew){
            if(userByEmail != null) return false;
        }
        else{
            if(userByEmail.getId() != id) return false;
        }

        return true;
    }

    public User get(Integer id) throws UserNotFoundException {
        try{
            return userRepository.findById(id).get();
        }
        catch(NoSuchElementException ex){
            throw new UserNotFoundException("Could not find any user with ID: "+id);
        }
    }
}