package com.nnk.springboot.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringJUnit4ClassRunner.class)
public class HomeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    // Test pour User et Admin
    public void homeTest() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    public void AdminHomeWithAdminProfileTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/admin/home")
                .with(csrf())
        ).andExpect(redirectedUrl("/bidList/list"));
    }

    @WithMockUser(authorities = "USER")
    @Test
    public void AdminHomeWhithUserProfileTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/admin/home")
                .with(csrf())
        ).andExpect(status().isForbidden());
    }
}
