package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest //to leverage the data jpa test of spring data jpa
@AutoConfigureTestDatabase(replace = Replace.NONE) //used to test against real database, by default test occurs for in-memory database.
@Rollback(value = false)
class RoleRepositoryTests {

    @Autowired
    private RoleRepository repo;

    @Test
    public void testCreateFirstRole(){
        Role roleAdmin=new Role("Admin", "manages everything");
        Role savedRole=repo.save(roleAdmin);
        assertThat(savedRole.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateRestRoles(){
        Role roleSalesperson=new Role("Salesperson", "manage product price, customers, shipping, orders, and sales report");
        Role roleEditor=new Role("Editor", "manages categories, brands, products, articles, and menus");
        Role roleShipper=new Role("Shipper", "view products, view orders, and updates order status");
        Role roleAssistant=new Role("Assistant", "manage questions, and reviews");

        repo.saveAll(List.of(roleSalesperson, roleEditor, roleShipper, roleAssistant));
        //of method returns an immutable list containing the specified object. So, we're not using assert method for this, it will succeed by default.
    }

}