package de.pascalbe.searchrequests.applicants;

import com.jayway.jsonpath.JsonPath;
import de.pascalbe.searchrequests.applicants.domain.ApplicantRepository;
import de.pascalbe.searchrequests.applicants.domain.Status;
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
    private static final String VALID_REQUEST_BODY = "{\"email\": \"john.doe@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"comment\": \"I am a comment\", \"salutation\": \"MRS\"}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicantRepository repository;

    @Test
    void shouldBeAbleToRetrieveAllApplicantsForOneProperty() throws Exception {
        var propertyId = UUID.randomUUID();
        this.givenApplicantIsCreated("Rick", propertyId);
        this.givenApplicantIsCreated("Morty", propertyId);
        this.givenApplicantIsCreated("Summer", propertyId);
        this.givenApplicantIsCreated("Greg", UUID.randomUUID());


        mockMvc.perform(get(getApplicantsEndpoint(propertyId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].firstName").value("Rick"))
                .andExpect(jsonPath("$[1].firstName").value("Morty"))
                .andExpect(jsonPath("$[2].firstName").value("Summer"));
    }

    @Test
    void shouldBeAbleToRetrieveOnlyInvitedApplicants() throws Exception {
        var propertyId = UUID.randomUUID();
        this.givenApplicantIsCreated("Chiara", propertyId);
        var thorsten = this.givenApplicantIsCreated("Thorsten", propertyId);
        var andi = this.givenApplicantIsCreated("Andi", propertyId);
        var lisa = this.givenApplicantIsCreated("Lisa", propertyId);

        givenApplicantHasStatus(thorsten, Status.INVITED);
        givenApplicantHasStatus(lisa, Status.INVITED);
        givenApplicantHasStatus(andi, Status.DECLINED);

        mockMvc.perform(get(getApplicantsEndpoint(propertyId)).queryParam("status", "INVITED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Thorsten"))
                .andExpect(jsonPath("$[1].firstName").value("Lisa"));
    }

    private String givenApplicantIsCreated(String name, UUID propertyId) throws Exception {
        var body = VALID_REQUEST_BODY.replace("John", name);
        var response = mockMvc.perform(post(getApplicantsEndpoint(propertyId))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        var applicantId = JsonPath.read(response.getResponse().getContentAsString(), "$.id");
        return (String) applicantId;
    }

    private void givenApplicantHasStatus(String applicantId, Status status) {
        var applicant = repository.findById(applicantId).orElseThrow();
        applicant.setStatus(status);
        repository.save(applicant);
    }

    private String getApplicantsEndpoint(UUID propertyId) {
        return "/properties/" + propertyId + "/applicants";
    }
}
