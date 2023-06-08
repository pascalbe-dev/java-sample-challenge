package de.pascalbe.searchrequests.applicants.api;

import de.pascalbe.searchrequests.applicants.domain.ApplicantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicantsController.class)
class ApplicantsControllerTest {

    private static final String VALID_REQUEST_BODY = "{\"email\": \"john.doe@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"comment\": \"This is a comment\", \"salutation\": \"MR\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicantRepository repository;

    @Test
    void shouldFailToCreateApplicantWithInvalidEmail() throws Exception {
        var requestBody = "{\"email\": \"invalid-email\", \"firstName\": \"John\", \"lastName\": \"Doe\"}";
        this.mockMvc.perform(post("/applicants").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailToCreateApplicantWithoutEmail() throws Exception {
        var requestBody = "{\"firstName\": \"John\", \"lastName\": \"Doe\"}";
        this.mockMvc.perform(post("/applicants").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailToCreateApplicantWithoutFirstName() throws Exception {
        var requestBody = "{\"email\": \"john.doe@example.com\", \"lastName\": \"Doe\"}";
        this.mockMvc.perform(post("/applicants").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailToCreateApplicantWithoutLastName() throws Exception {
        var requestBody = "{\"email\": \"john.doe@example.com\", \"firstName\": \"John\"}";
        this.mockMvc.perform(post("/applicants").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailToCreateApplicantWithInvalidSalutation() throws Exception {
        var requestBody = "{\"email\": \"john.doe@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"salutation\": \"MX\"}";
        this.mockMvc.perform(post("/applicants").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldAllowToCreateApplicantWithoutComment() throws Exception {
        var requestBody = "{\"email\": \"john.doe@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"salutation\": \"MRS\"}";
        this.mockMvc.perform(post("/applicants").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isCreated());
    }

    @Test
    void shouldAllowToCreateApplicantWithoutSalutation() throws Exception {
        var requestBody = "{\"email\": \"john.doe@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"comment\": \"Sample comment\"}";
        this.mockMvc.perform(post("/applicants").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isCreated());
    }

    @Test
    void shouldAllowToCreateApplicantWithAllData() throws Exception {
        this.mockMvc.perform(post("/applicants").contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST_BODY)).andExpect(status().isCreated());
    }

    @Test
    void shouldReturnTheCreatedApplicantsId() throws Exception {
        this.mockMvc.perform(post("/applicants").contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isString());
    }
}
