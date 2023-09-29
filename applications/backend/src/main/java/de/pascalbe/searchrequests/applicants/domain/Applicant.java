package de.pascalbe.searchrequests.applicants.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
public class Applicant {

    @Id
    private String id;

    private UUID propertyId;

    private String email;

    private String firstName;

    private String lastName;

    private String comment;

    private Salutation salutation;

    private CreationSource creationSource;

    @CreationTimestamp
    private Instant creationTimestamp;
}
