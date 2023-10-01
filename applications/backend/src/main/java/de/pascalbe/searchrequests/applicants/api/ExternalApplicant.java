package de.pascalbe.searchrequests.applicants.api;

import de.pascalbe.searchrequests.applicants.domain.Salutation;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ExternalApplicant {
    //  TO NOTE: using RFC 5322 regex for email validation (see: https://www.baeldung.com/java-email-validation-regex#regular-expression-by-rfc-5322-for-email-validation)
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotBlank
    private String email;

    private String firstName;

    @NotBlank
    private String lastName;

    private String applicantComment;

    private Salutation salutation;

    private boolean wbsPresent;

    private boolean pets;

    private int numberOfPersons;

    //  TODO: determine which type makes sense and adjust it
    private String earliestMoveInDate;
}
