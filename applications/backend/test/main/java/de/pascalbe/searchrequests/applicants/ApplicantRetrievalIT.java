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
    private static final String SAMPLE_EMAIL_ADDRESS = "john.doe@example.com";
    private static final String VALID_REQUEST_BODY = "{" +
            "\"email\": \"john.doe@example.com\", " +
            "\"firstName\": \"John\", " +
            "\"lastName\": \"Doe\", " +
            "\"userComment\": \"I am a comment\", " +
            "\"salutation\": \"MRS\"" +
            "}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicantRepository repository;

    @Test
    void shouldBeAbleToRetrieveAllApplicantsForOneProperty() throws Exception {
        var propertyId = UUID.randomUUID();
        var rick = this.givenManualApplicantIsCreated("Rick", propertyId, SAMPLE_EMAIL_ADDRESS);
        var morty = this.givenManualApplicantIsCreated("Morty", propertyId, SAMPLE_EMAIL_ADDRESS);
        this.givenManualApplicantIsCreated("Summer", propertyId, SAMPLE_EMAIL_ADDRESS);
        this.givenManualApplicantIsCreated("Greg", UUID.randomUUID(), SAMPLE_EMAIL_ADDRESS);

        givenApplicantHasStatus(rick, Status.INVITED);
        givenApplicantHasStatus(morty, Status.DECLINED);


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
        this.givenManualApplicantIsCreated("Chiara", propertyId, SAMPLE_EMAIL_ADDRESS);
        var thorsten = this.givenManualApplicantIsCreated("Thorsten", propertyId, SAMPLE_EMAIL_ADDRESS);
        var andi = this.givenManualApplicantIsCreated("Andi", propertyId, SAMPLE_EMAIL_ADDRESS);
        var lisa = this.givenManualApplicantIsCreated("Lisa", propertyId, SAMPLE_EMAIL_ADDRESS);

        givenApplicantHasStatus(thorsten, Status.INVITED);
        givenApplicantHasStatus(lisa, Status.INVITED);
        givenApplicantHasStatus(andi, Status.DECLINED);

        mockMvc.perform(get(getApplicantsEndpoint(propertyId)).queryParam("status", "INVITED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Thorsten"))
                .andExpect(jsonPath("$[1].firstName").value("Lisa"));
    }

    @Test
    void shouldBeAbleToRetrieveOnlyDeclinedApplicants() throws Exception {
        var propertyId = UUID.randomUUID();
        this.givenManualApplicantIsCreated("Chiara", propertyId, SAMPLE_EMAIL_ADDRESS);
        this.givenManualApplicantIsCreated("Thorsten", propertyId, SAMPLE_EMAIL_ADDRESS);
        var andi = this.givenManualApplicantIsCreated("Andi", propertyId, SAMPLE_EMAIL_ADDRESS);
        var lisa = this.givenManualApplicantIsCreated("Lisa", propertyId, SAMPLE_EMAIL_ADDRESS);

        givenApplicantHasStatus(lisa, Status.INVITED);
        givenApplicantHasStatus(andi, Status.DECLINED);

        mockMvc.perform(get(getApplicantsEndpoint(propertyId)).queryParam("status", "DECLINED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Andi"));
    }

    @Test
    void shouldBeAbleToRetrieveApplicantsBasedOnPartsOfTheirEmail() throws Exception {
        var propertyId = UUID.randomUUID();
        var christina = this.givenManualApplicantIsCreated("Christina", propertyId, "christina@gmail.com");
        this.givenManualApplicantIsCreated("Thorsten", propertyId, SAMPLE_EMAIL_ADDRESS);
        this.givenManualApplicantIsCreated("Andi", propertyId, SAMPLE_EMAIL_ADDRESS);
        this.givenManualApplicantIsCreated("Chris", propertyId, "chris@gmail.com");

        givenApplicantHasStatus(christina, Status.INVITED);

        mockMvc.perform(get(getApplicantsEndpoint(propertyId)).queryParam("partOfEmail", "chris"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Christina"))
                .andExpect(jsonPath("$[1].firstName").value("Chris"));
    }

    @Test
    void shouldBeAbleToRetrieveOnlyApplicantsWithASpecificNumberOfPersons() throws Exception {
        var propertyId = UUID.randomUUID();
        var christina = this.givenManualApplicantIsCreated("Christina", propertyId, SAMPLE_EMAIL_ADDRESS);
        var thorsten = this.givenManualApplicantIsCreated("Thorsten", propertyId, SAMPLE_EMAIL_ADDRESS);
        var teja = this.givenManualApplicantIsCreated("Teja", propertyId, SAMPLE_EMAIL_ADDRESS);

        givenApplicantSearchesForThisAmountOfPersons(christina, 2);
        givenApplicantSearchesForThisAmountOfPersons(thorsten, 3);
        givenApplicantSearchesForThisAmountOfPersons(teja, 2);

        mockMvc.perform(get(getApplicantsEndpoint(propertyId)).queryParam("numberOfPersons", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Christina"))
                .andExpect(jsonPath("$[1].firstName").value("Teja"));
    }

    private String givenManualApplicantIsCreated(String name, UUID propertyId, String email) throws Exception {
        var body = VALID_REQUEST_BODY.replace("John", name).replace("john.doe@example.com", email);
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

    private void givenApplicantSearchesForThisAmountOfPersons(String applicantId, int numberOfPersons) {
        var applicant = repository.findById(applicantId).orElseThrow();
        applicant.setNumberOfPersons(numberOfPersons);
        repository.save(applicant);
    }

    private String getApplicantsEndpoint(UUID propertyId) {
        return "/properties/" + propertyId + "/applicants";
    }
}
