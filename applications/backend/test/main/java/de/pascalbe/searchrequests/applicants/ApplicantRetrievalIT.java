package de.pascalbe.searchrequests.applicants;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ApplicantRetrievalIT {
    private static final UUID SAMPLE_PROPERTY_ID = UUID.fromString("6c54590a-04d4-46e6-b383-d1bc8be8e530");
    private static final String VALID_REQUEST_BODY = "{\"email\": \"john.doe@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"comment\": \"I am a comment\", \"salutation\": \"MRS\"}";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldBeAbleToRetrieveAllApplicantsForOneProperty() throws Exception {
        this.saveApplicant("Rick", SAMPLE_PROPERTY_ID);
        this.saveApplicant("Morty", SAMPLE_PROPERTY_ID);
        this.saveApplicant("Summer", SAMPLE_PROPERTY_ID);
        this.saveApplicant("Greg", UUID.randomUUID());


        mockMvc.perform(get(getApplicantsEndpoint(SAMPLE_PROPERTY_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].firstName").value("Rick"))
                .andExpect(jsonPath("$[1].firstName").value("Morty"))
                .andExpect(jsonPath("$[2].firstName").value("Summer"));
    }

    private void saveApplicant(String name, UUID propertyId) throws Exception {
        var body = VALID_REQUEST_BODY.replace("John", name);
        mockMvc.perform(post(getApplicantsEndpoint(propertyId))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated());
    }

    private String getApplicantsEndpoint(UUID propertyId) {
        return "/properties/" + propertyId + "/applicants";
    }
}
