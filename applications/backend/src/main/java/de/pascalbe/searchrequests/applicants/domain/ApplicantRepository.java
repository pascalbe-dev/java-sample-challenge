package de.pascalbe.searchrequests.applicants.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicantRepository extends JpaRepository<Applicant, String> {
}
