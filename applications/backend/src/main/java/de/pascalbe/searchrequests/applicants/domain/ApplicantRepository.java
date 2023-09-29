package de.pascalbe.searchrequests.applicants.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicantRepository extends JpaRepository<Applicant, String> {
    List<Applicant> findAllByPropertyId(UUID propertyId);
}
