package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class UserRepositoryTests {

    @Autowired
    private UserRepository repo;

    //It is used to get role from the database so that we can assign it to the user
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateUserWithSingleRole(){
        Role roleAdmin=entityManager.find(Role.class, 1);
        User userManik=new User("manik","setia", "manik.setia001@gmail.com", "password");
        userManik.addRole(roleAdmin);

        User savedUser=repo.save(userManik);//save method returns a persisted object
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateUserWithTwoRoles(){
        User userManu=new User("manu", "setia", "manusetia@gmail.com","pass");

        Role roleEditor=new Role(2);
        Role roleAssistant=new Role(4);

        userManu.addRole(roleEditor);
        userManu.addRole(roleAssistant);

        User savedUser=repo.save(userManu);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllUsers(){
        Iterable<User> usersList=repo.findAll();
        usersList.forEach(user -> System.out.println(user));
    }

    @Test
    public void testGetUserById(){
        User user=repo.findById(1).get();
        System.out.println(user);
        assertThat(user).isNotNull();
    }

    @Test
    public void testUpdateUserDetails(){
        User user=repo.findById(1).get();
        user.setEnabled(true);
        user.setEmail("manik.setia002@gmail.com");

        repo.save(user);
    }

    @Test
    public void testUpdateUserRoles(){
        User user=repo.findById(2).get();
        Role roleShipper=new Role(4);
        user.getRoles().remove(roleShipper);//this will not be deleted if equals and hashCode methods are not there in Role class.
        user.addRole(new Role(5));//Assistant

        repo.save(user);
    }

    @Test
    public void testDeleteUser(){
        repo.deleteById(2);
    }

    @Test
    public void testGetUserByEmail(){
        String email="manik.setia002@gmail.com";
        User user=repo.getUserByEmail(email);
        assertThat(user).isNotNull();
    }

    @Test
    public void testCountById(){
//        Integer id=100;
        Integer id=1;
        Long countById=repo.countById(id);

        assertThat(countById).isNotNull().isGreaterThan(0);
    }
}