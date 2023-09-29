package de.pascalbe.searchrequests.applicants;

import com.jayway.jsonpath.JsonPath;
import de.pascalbe.searchrequests.applicants.domain.CreationSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AutomatedApplicantCreationIT {
    private static final String CREATE_APPLICANT_ENDPOINT = "/properties/6c54590a-04d4-46e6-b383-d1bc8be8e530/external-applicants";
    private static final String VALID_REQUEST_BODY = "{" +
            "\"email\": \"john.doe@example.com\", " +
            "\"firstName\": \"John\", " +
            "\"lastName\": \"Doe\", " +
            "\"applicantComment\": \"Applicant comment\", " +
            "\"salutation\": \"MRS\", " +
            "\"wbsPresent\": true, " +
            "\"pets\": false, " +
            "\"numberOfPersons\": 3, " +
            "\"earliestMoveInDate\": \"2024-01-01\"" +
            "}";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldBeAbleToStoreAndFetchDataAboutAutomatedApplicants() throws Exception {
        var requestResult = mockMvc.perform(post(CREATE_APPLICANT_ENDPOINT)
                        .contentType("application/json")
                        .content(VALID_REQUEST_BODY))
                .andExpect(status().isCreated())
                .andReturn();

        var applicantId = JsonPath.read(requestResult.getResponse().getContentAsString(), "$.id");
        assertThat((String) applicantId).isNotBlank();

        mockMvc.perform(get("/applicants/" + applicantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.applicantComment").value("Applicant comment"))
                .andExpect(jsonPath("$.salutation").value("MRS"))
                .andExpect(jsonPath("$.wbsPresent").value(true))
                .andExpect(jsonPath("$.pets").value(false))
                .andExpect(jsonPath("$.numberOfPersons").value(3))
                .andExpect(jsonPath("$.earliestMoveInDate").value("2024-01-01"));
    }

    @Test
    void shouldMarkApplicantsAsPortalApplicants() throws Exception {
        var requestResult = mockMvc.perform(post(CREATE_APPLICANT_ENDPOINT)
                        .contentType("application/json")
                        .content(VALID_REQUEST_BODY))
                .andExpect(status().isCreated())
                .andReturn();

        var applicantId = JsonPath.read(requestResult.getResponse().getContentAsString(), "$.id");
        assertThat((String) applicantId).isNotBlank();

        mockMvc.perform(get("/applicants/" + applicantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creationSource").value(CreationSource.PORTAL.toString()));
    }

    @Test
    void shouldStoreCreationTimestampForApplicant() throws Exception {
        var requestResult = mockMvc.perform(post(CREATE_APPLICANT_ENDPOINT)
                        .contentType("application/json")
                        .content(VALID_REQUEST_BODY))
                .andExpect(status().isCreated())
                .andReturn();

        var applicantId = JsonPath.read(requestResult.getResponse().getContentAsString(), "$.id");
        assertThat((String) applicantId).isNotBlank();

        var timestampRegex = "^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{6}Z$";
        mockMvc.perform(get("/applicants/" + applicantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creationTimestamp").isNotEmpty())
                .andExpect(jsonPath("$.creationTimestamp", matchesPattern(timestampRegex)));
    }

    @Test
    void shouldMarkNewlyCreatedAutomatedApplicantsWithStatusCreated() throws Exception {
        var requestResult = mockMvc.perform(post(CREATE_APPLICANT_ENDPOINT)
                        .contentType("application/json")
                        .content(VALID_REQUEST_BODY))
                .andExpect(status().isCreated())
                .andReturn();

        var applicantId = JsonPath.read(requestResult.getResponse().getContentAsString(), "$.id");
        assertThat((String) applicantId).isNotBlank();

        mockMvc.perform(get("/applicants/" + applicantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREATED"));
    }
}
