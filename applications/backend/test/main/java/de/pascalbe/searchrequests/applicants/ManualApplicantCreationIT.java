package de.pascalbe.searchrequests.applicants;

import com.jayway.jsonpath.JsonPath;
import de.pascalbe.searchrequests.applicants.api.ApplicantsController;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.CapturingMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ManualApplicantCreationIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldBeAbleToStoreAndFetchDataAboutManualApplicants() throws Exception {
        var requestBody = "{\"email\": \"john.doe@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"comment\": \"I am a comment\"}";
        var requestResult = mockMvc.perform(post("/applicants")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        var applicantId = JsonPath.read(requestResult.getResponse().getContentAsString(), "$.id");
        assertThat((String) applicantId).isNotBlank();

        mockMvc.perform(get("/applicants/" + applicantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.comment").value("I am a comment"));
    }
}
