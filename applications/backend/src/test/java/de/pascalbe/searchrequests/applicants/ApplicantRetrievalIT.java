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
        this.givenApplicantIsCreated("Summer", propertyId, SAMPLE_EMAIL_ADDRESS);
        var morty = this.givenApplicantIsCreated("Morty", propertyId, SAMPLE_EMAIL_ADDRESS);
        var rick = this.givenApplicantIsCreated("Rick", propertyId, SAMPLE_EMAIL_ADDRESS);
        this.givenApplicantIsCreated("Greg", UUID.randomUUID(), SAMPLE_EMAIL_ADDRESS);

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
        this.givenApplicantIsCreated("Chiara", propertyId, SAMPLE_EMAIL_ADDRESS);
        var lisa = this.givenApplicantIsCreated("Lisa", propertyId, SAMPLE_EMAIL_ADDRESS);
        var andi = this.givenApplicantIsCreated("Andi", propertyId, SAMPLE_EMAIL_ADDRESS);
        var thorsten = this.givenApplicantIsCreated("Thorsten", propertyId, SAMPLE_EMAIL_ADDRESS);

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
        this.givenApplicantIsCreated("Chiara", propertyId, SAMPLE_EMAIL_ADDRESS);
        this.givenApplicantIsCreated("Thorsten", propertyId, SAMPLE_EMAIL_ADDRESS);
        var andi = this.givenApplicantIsCreated("Andi", propertyId, SAMPLE_EMAIL_ADDRESS);
        var lisa = this.givenApplicantIsCreated("Lisa", propertyId, SAMPLE_EMAIL_ADDRESS);

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
        this.givenApplicantIsCreated("Chris", propertyId, "chris@gmail.com");
        var christina = this.givenApplicantIsCreated("Christina", propertyId, "christina@gmail.com");
        this.givenApplicantIsCreated("Thorsten", propertyId, SAMPLE_EMAIL_ADDRESS);
        this.givenApplicantIsCreated("Andi", propertyId, SAMPLE_EMAIL_ADDRESS);

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
        var teja = this.givenApplicantIsCreated("Teja", propertyId, SAMPLE_EMAIL_ADDRESS);
        var christina = this.givenApplicantIsCreated("Christina", propertyId, SAMPLE_EMAIL_ADDRESS);
        var thorsten = this.givenApplicantIsCreated("Thorsten", propertyId, SAMPLE_EMAIL_ADDRESS);

        givenApplicantSearchesForThisAmountOfPersons(christina, 2);
        givenApplicantSearchesForThisAmountOfPersons(thorsten, 3);
        givenApplicantSearchesForThisAmountOfPersons(teja, 2);

        mockMvc.perform(get(getApplicantsEndpoint(propertyId)).queryParam("numberOfPersons", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Christina"))
                .andExpect(jsonPath("$[1].firstName").value("Teja"));
    }

    @Test
    void shouldBeAbleToRetrieveOnlyApplicantsWithAWbsPresent() throws Exception {
        var propertyId = UUID.randomUUID();
        this.givenApplicantIsCreated("Christina", propertyId, SAMPLE_EMAIL_ADDRESS);
        var thorsten = this.givenApplicantIsCreated("Thorsten", propertyId, SAMPLE_EMAIL_ADDRESS);
        var teja = this.givenApplicantIsCreated("Teja", propertyId, SAMPLE_EMAIL_ADDRESS);

        givenApplicantHasAWbs(teja, true);
        givenApplicantHasAWbs(thorsten, false);

        mockMvc.perform(get(getApplicantsEndpoint(propertyId)).queryParam("wbsPresent", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Teja"));
    }

    @Test
    void shouldBeAbleToFilterByAllParameters() throws Exception {
        var propertyId = UUID.randomUUID();
        var john = this.givenApplicantIsCreated("John", propertyId, SAMPLE_EMAIL_ADDRESS);
        var chris = this.givenApplicantIsCreated("Chris", propertyId, "chris@chris-is-great.de");
        var christian = this.givenApplicantIsCreated("Christian", propertyId, "christian@rocks.com");
        var chiara = this.givenApplicantIsCreated("Chiara", propertyId, "chiara@muybien.es");
        var charly = this.givenApplicantIsCreated("Charly", propertyId, "unser.charly@ard.de");

        givenApplicantHasStatus(john, Status.INVITED);
        givenApplicantHasStatus(chris, Status.INVITED);
        givenApplicantHasStatus(christian, Status.DECLINED);
        givenApplicantHasStatus(chiara, Status.INVITED);
        givenApplicantHasStatus(charly, Status.INVITED);
        givenApplicantHasAWbs(john, true);
        givenApplicantHasAWbs(chris, true);
        givenApplicantHasAWbs(christian, true);
        givenApplicantHasAWbs(chiara, true);
        givenApplicantHasAWbs(charly, false);
        givenApplicantSearchesForThisAmountOfPersons(john, 2);
        givenApplicantSearchesForThisAmountOfPersons(chris, 1);
        givenApplicantSearchesForThisAmountOfPersons(christian, 4);
        givenApplicantSearchesForThisAmountOfPersons(chiara, 3);
        givenApplicantSearchesForThisAmountOfPersons(charly, 4);

        mockMvc.perform(get(getApplicantsEndpoint(propertyId))
                        .queryParam("status", "INVITED")
                        .queryParam("partOfEmail", "ch")
                        .queryParam("numberOfPersons", "3")
                        .queryParam("wbsPresent", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Chiara"));

    }

    @Test
    void shouldRetrieveNewestApplicantsFirstInApplicantRetrieval() throws Exception {
        var propertyId = UUID.randomUUID();
        var john = this.givenApplicantIsCreated("John", propertyId, SAMPLE_EMAIL_ADDRESS);
        var chris = this.givenApplicantIsCreated("Chris", propertyId, SAMPLE_EMAIL_ADDRESS);
        var margit = this.givenApplicantIsCreated("Margit", propertyId, SAMPLE_EMAIL_ADDRESS);
        var irina = this.givenApplicantIsCreated("Irina", propertyId, SAMPLE_EMAIL_ADDRESS);

        givenApplicantHasStatus(john, Status.INVITED);
        givenApplicantHasStatus(chris, Status.DECLINED);
        givenApplicantHasStatus(margit, Status.INVITED);
        givenApplicantHasStatus(irina, Status.INVITED);

        mockMvc.perform(get(getApplicantsEndpoint(propertyId))
                        .queryParam("status", "INVITED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(irina))
                .andExpect(jsonPath("$[1].id").value(margit))
                .andExpect(jsonPath("$[2].id").value(john));
    }

    //  TO NOTE: we could use something like the builder pattern to prepare test data in a simpler way -
    //      that would make tests easier to write and keep them easy to understand.
    private String givenApplicantIsCreated(String name, UUID propertyId, String email) throws Exception {
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

    private void givenApplicantHasAWbs(String applicantId, boolean wbsPresent) {
        var applicant = repository.findById(applicantId).orElseThrow();
        applicant.setWbsPresent(wbsPresent);
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
