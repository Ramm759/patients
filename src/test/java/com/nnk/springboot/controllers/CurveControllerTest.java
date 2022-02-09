package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
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
public class CurveControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CurvePointRepository curvePointRepository;

    @Test
    @WithMockUser(authorities = "USER")
    public void homeWithUserProfileTest() throws Exception {
        mockMvc.perform(get("/curvePoint/list")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void homeWithAdminProfileTest() throws Exception {
        mockMvc.perform(get("/curvePoint/list")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "USER")
    public void addWithUserProfileTest() throws Exception {
        mockMvc.perform(get("/curvePoint/add")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void addWithAdminProfileTest() throws Exception {
        mockMvc.perform(get("/curvePoint/add")).andExpect(status().isOk());
    }

    @WithMockUser(authorities = "ADMIN") // User ne peut pas insérer
    @Test
    public void validateCurveWithAdminProfileTest() throws Exception {
        mockMvc.perform(post("/curvePoint/validate")
                .param("curveId", "10")
                .param("term", "20D")
                .param("value", "5D")
                .with(csrf()) // Contournement attaque
        ).andExpect(redirectedUrl("/curvePoint/list"));
    }

    @WithMockUser(authorities = "ADMIN") // User ne peut pas insérer
    @Test
    public void validateCurveWithAdminProfileAndErrorTest() throws Exception {
        mockMvc.perform(post("/curvePoint/validate")
                .param("curveId", "10")
                .param("term", "20D")
                .param("value", "aaa")
                .with(csrf()) // Contournement attaque
        ).andExpect(model().hasErrors());
    }

    @WithMockUser(authorities = "USER")
    @Test
    public void validateCurveWithUserProfileTest() throws Exception {
        this.mockMvc.perform(post("/curvePoint/validate")
                .param("curveId", "10")
                .param("term", "20D")
                .param("value", "5D")
                .with(csrf())
        ).andExpect(status().isForbidden());
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    public void showUpdateCurveWithAdminProfileTest() throws Exception {
        CurvePoint curve = curvePointRepository.save(new CurvePoint(10, 20D, 5D));

        mockMvc.perform(get("/curvePoint/update/" + curve.getId()))
                .andExpect(model().attribute("curvePoint", Matchers.hasProperty("curveId", Matchers.equalTo(10))))
                .andExpect(model().attribute("curvePoint", Matchers.hasProperty("term", Matchers.equalTo(20D))))
                .andExpect(model().attribute("curvePoint", Matchers.hasProperty("value", Matchers.equalTo(5D))));
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    public void updateCurveWithAdminProfileTest() throws Exception {
        CurvePoint curve = curvePointRepository.save(new CurvePoint(10, 20D, 5D));
        this.mockMvc.perform(post("/curvePoint/update/" + curve.getCurveId())
                .param("curveId", "10")
                .param("term", "20D")
                .param("value", "5D")
                .with(csrf())
        ).andExpect(redirectedUrl("/curvePoint/list"));
    }

    @WithMockUser(authorities = "USER")
    @Test
    public void showUpdateCurveWithUserProfileTest() throws Exception {
        CurvePoint curve = curvePointRepository.save(new CurvePoint(10, 20D, 5D));

        this.mockMvc.perform(get("/curvePoint/update/" + curve.getCurveId())).andExpect(status().isForbidden());
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    public void updateCurveWithAdminProfileAndErrorTest() throws Exception {
        CurvePoint curve = curvePointRepository.save(new CurvePoint(10, 20D, 5D));
        this.mockMvc.perform(post("/curvePoint/update/" + curve.getId())
                .param("curveId", "10")
                .param("term", "20D")
                .param("value", "5dddD")
                .with(csrf())
        ).andExpect(model().hasErrors());
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    public void deleteCurveWithAdminProfileTest() throws Exception {
        CurvePoint curve = curvePointRepository.save(new CurvePoint(10, 20D, 5D));

        this.mockMvc.perform(get("/curvePoint/delete/" + curve.getId())).andExpect(status().isFound()).andReturn();
    }
}
