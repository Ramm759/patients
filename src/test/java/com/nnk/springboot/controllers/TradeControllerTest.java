package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
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
public class TradeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    TradeRepository tradeRepository;

    @Test
    @WithMockUser(authorities = "USER")
    public void homeWithUserProfileTest() throws Exception {
        mockMvc.perform(get("/trade/list")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void homeWithAdminProfileTest() throws Exception {
        mockMvc.perform(get("/trade/list")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "USER")
    public void addWithUserProfileTest() throws Exception {
        mockMvc.perform(get("/trade/add")).andExpect(status().isForbidden());
    }

    // Requete Admin
    @Test
    @WithMockUser(authorities = "ADMIN")
    public void addWithAdminProfileTest() throws Exception {
        mockMvc.perform(get("/trade/add")).andExpect(status().isOk());
    }

    @WithMockUser(authorities = "ADMIN") // User ne peut pas insérer
    @Test
    public void validateTradeWithAdminProfileTest() throws Exception {

        mockMvc.perform(post("/trade/validate")
                .param("account", "Account")
                .param("type", "Type")
                .with(csrf()) // Contournement attaque
        ).andExpect(redirectedUrl("/trade/list"));
    }

    @WithMockUser(authorities = "ADMIN") // User ne peut pas insérer
    @Test
    public void validateTradeWithAdminProfileAndErrorTest() throws Exception {
        mockMvc.perform(post("/trade/validate")
                .param("account", "") // account not Blank
                .param("type", "Type")
                .with(csrf()) // Contournement attaque
        ).andExpect(model().hasErrors());
    }

    @WithMockUser(authorities = "USER")
    @Test
    public void validateTradeWithUserProfileTest() throws Exception {
        this.mockMvc.perform(post("/trade/validate")
                .param("account", "Account")
                .param("type", "Type")
                .with(csrf()) // Contournement attaque
        ).andExpect(status().isForbidden());
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    public void showUpdateTradeWithAdminProfileTest() throws Exception {
        Trade trade = tradeRepository.save(new Trade("Account", "Type"));

        mockMvc.perform(get("/trade/update/" + trade.getTradeId()))
                .andExpect(model().attribute("trade", Matchers.hasProperty("account", Matchers.equalTo("Account"))))
                .andExpect(model().attribute("trade", Matchers.hasProperty("type", Matchers.equalTo("Type"))));
    }

    @WithMockUser(authorities = "USER")
    @Test
    public void showUpdateTradeWithUserProfileTest() throws Exception {
        Trade trade = tradeRepository.save(new Trade("Account", "Type"));

        this.mockMvc.perform(get("/trade/update/" + trade.getTradeId())).andExpect(status().isForbidden());
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    public void updateTradeWithAdminProfileTest() throws Exception {
        Trade trade = tradeRepository.save(new Trade("Account", "Type"));
        this.mockMvc.perform(post("/trade/update/" + trade.getTradeId())
                .param("account", "Account")
                .param("type", "Type")
                .with(csrf())
        ).andExpect(redirectedUrl("/trade/list"));
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    public void updateTradeWithAdminProfileAndErrorTest() throws Exception {
        Trade trade = tradeRepository.save(new Trade("Account", "Type"));
        this.mockMvc.perform(post("/trade/update/" + trade.getTradeId())
                .param("account", "") // account not Blank
                .param("type", "Type")
                .with(csrf())
        ).andExpect(model().hasErrors());
    }

    @WithMockUser(authorities = "ADMIN")
    @Test
    public void deleteCurveWithAdminProfileTest() throws Exception {
        Trade trade = tradeRepository.save(new Trade("Account", "Type"));

        this.mockMvc.perform(get("/trade/delete/" + trade.getTradeId())).andExpect(status().isFound()).andReturn();
    }
}
