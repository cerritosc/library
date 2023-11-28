package com.focusservices.library.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.focusservices.library.config.SecurityUserDetails;
import com.focusservices.library.domain.Loan;
import com.focusservices.library.service.LoanService;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = LoanController.class)
public class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private LoanService loanService;

    private SecurityUserDetails details;

    @BeforeEach
    public void setUp() {
        details = new SecurityUserDetails();

        Mockito.reset(loanService);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    }

    @Test
    @DisplayName("LoanController indexAppUserTest should return string, not empy or null.")
    @WithMockUser
    public void indexAppUserTest() throws Exception {
        this.mockMvc.perform(get("/loan/").with(user(details)))
//                .andDo(print()) //Used to print the mocked response
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyOrNullString())));
    }
}
