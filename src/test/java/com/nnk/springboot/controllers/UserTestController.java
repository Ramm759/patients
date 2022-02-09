package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringJUnit4ClassRunner.class)
public class UserTestController {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Test
    @WithMockUser(authorities = "USER")
    public void homeWithUserProfileTest() throws Exception {
        mockMvc.perform(get("/user/list")).andExpect(status().isOk());
    }

    // Requete Admin
    @Test
    @WithMockUser(authorities = "ADMIN")
    public void homeWithAdminProfileTest() throws Exception {
        mockMvc.perform(get("/user/list")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "USER")
    public void addWithUserProfileTest() throws Exception {
        mockMvc.perform(get("/user/add")).andExpect(status().isOk()); // Permission pour le profil User
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void addWithAdminProfileTest() throws Exception {
        mockMvc.perform(get("/user/add")).andExpect(status().isOk());
    }

    @WithMockUser(authorities = "ADMIN") // User ne peut pas insérer
    @Test
    public void validateUserWithAdminProfileTest() throws Exception {
        mockMvc.perform(post("/user/validate")
                .param("username", "Username")
                .param("password", "Password@1")
                .param("fullname", "Fullname")
                .param("role", "Role")
                .with(csrf()) // Contournement attaque
        ).andExpect(redirectedUrl("/user/list"));
    }

    @WithMockUser(authorities = "ADMIN") // User ne peut pas insérer
    @Test
    public void validateUserWithAdminProfileAndErrorTest() throws Exception {
        mockMvc.perform(post("/user/validate")
                .param("username", "Username")
                .param("password", "Password")
                .param("fullname", "Fullname")
                .param("role", "Role")
                .with(csrf()) // Contournement attaque
        ).andExpect(model().hasErrors());
    }

    @WithMockUser(authorities = "USER")
    @Test
    public void validateUserWithUserProfileTest() throws Exception {
        mockMvc.perform(post("/user/validate")
                .param("username", "Username")
                .param("password", "Password")
                .param("fullname", "Fullname")
                .param("role", "Role")
                .with(csrf()) // Contournement attaque
        ).andExpect(status().isOk()); // Permision pour le profil User
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    public void showUpdateUserWithAdminProfileTest() throws Exception {
        User user = userRepository.save(new User("Username", "Password@1", "Fullname", "Role"));

        mockMvc.perform(get("/user/update/" + user.getId()))
                .andExpect(model().attribute("user", Matchers.hasProperty("username", Matchers.equalTo("Username"))))
                .andExpect(model().attribute("user", Matchers.hasProperty("password", Matchers.equalTo("")))) // A corriger
                .andExpect(model().attribute("user", Matchers.hasProperty("fullname", Matchers.equalTo("Fullname"))))
                .andExpect(model().attribute("user", Matchers.hasProperty("role", Matchers.equalTo("Role"))));
    }

    @WithMockUser(authorities = "USER")
    @Test
    public void showUpdateUserWithUserProfileTest() throws Exception {
        User user = userRepository.save(new User("Username", "Password@1", "Fullname", "Role"));

        this.mockMvc.perform(get("/user/update/" + user.getId())).andExpect(status().isForbidden());
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    public void updateUserWithAdminProfileTest() throws Exception {
        User user = userRepository.save(new User("Username", "Password@1", "Fullname", "Role"));

        this.mockMvc.perform(post("/user/update/" + user.getId())
                .param("username", "Username")
                .param("password", "Password@1")
                .param("fullname", "Fullname")
                .param("role", "Role")
                .with(csrf()) // Contournement attaque
        ).andExpect(redirectedUrl("/user/list"));
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    public void updateUserWithAdminProfileAndErrorTest() throws Exception {
        User user = userRepository.save(new User("Username", "Password@1", "Fullname", "Role"));

        this.mockMvc.perform(post("/user/update/" + user.getId())
                .param("username", "Username")
                .param("password", "Pa")  // Erreur validation
                .param("fullname", "Fullname")
                .param("role", "Role")
                .with(csrf())
        ).andExpect(model().hasErrors());
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    public void deleteUserWithAdminProfileTest() throws Exception {
        User user = userRepository.save(new User("Username", "Password@1", "Fullname", "Role"));

        this.mockMvc.perform(get("/user/delete/" + user.getId())).andExpect(status().isFound()).andReturn();
    }
}
