package de.pascalbe.searchrequests.applicants.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Applicant {

    @Id
    private String id;

    private String email;

    private String firstName;

    private String lastName;

    private String comment;
}
