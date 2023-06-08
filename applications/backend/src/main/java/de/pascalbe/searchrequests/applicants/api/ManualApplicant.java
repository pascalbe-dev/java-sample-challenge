package de.pascalbe.searchrequests.applicants.api;

import de.pascalbe.searchrequests.applicants.domain.Salutation;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ManualApplicant {

    //  TO NOTE: using RFC 5322 regex for email validation (see: https://www.baeldung.com/java-email-validation-regex#regular-expression-by-rfc-5322-for-email-validation)
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotBlank
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String comment;

    private Salutation salutation;

}
