package de.pascalbe.searchrequests.applicants.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicantsController.class)
class ApplicantsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldFailToCreateApplicantWithInvalidEmail() throws Exception {
        var requestBody = "{\"email\": \"invalid-email\", \"firstName\": \"John\", \"lastName\": \"Doe\"}";
        this.mockMvc.perform(post("/applicants", requestBody)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailToCreateApplicantWithoutEmail() throws Exception {
        var requestBody = "{\"firstName\": \"John\", \"lastName\": \"Doe\"}";
        this.mockMvc.perform(post("/applicants", requestBody)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailToCreateApplicantWithoutFirstName() throws Exception {
        var requestBody = "{\"email\": \"john.doe@example.com\", \"lastName\": \"Doe\"}";
        this.mockMvc.perform(post("/applicants", requestBody)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailToCreateApplicantWithoutLastName() throws Exception {
        var requestBody = "{\"email\": \"john.doe@example.com\", \"firstName\": \"John\"}";
        this.mockMvc.perform(post("/applicants", requestBody)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldAllowToCreateApplicantWithoutComment() throws Exception {
        var requestBody = "{\"email\": \"john.doe@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\"}";
        this.mockMvc.perform(post("/applicants", requestBody)).andExpect(status().isCreated());
    }

    @Test
    void shouldAllowToCreateApplicantWithComment() throws Exception {
        var requestBody = "{\"email\": \"john.doe@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"comment\": \"I am a comment\"}";
        this.mockMvc.perform(post("/applicants", requestBody)).andExpect(status().isCreated());
    }
}
