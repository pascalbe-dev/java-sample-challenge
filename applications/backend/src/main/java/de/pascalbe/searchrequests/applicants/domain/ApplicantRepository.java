package de.pascalbe.searchrequests.applicants.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ApplicantRepository extends JpaRepository<Applicant, String> {
    @Query("SELECT a FROM Applicant a " +
            "WHERE a.propertyId = :propertyId " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:numberOfPersons IS NULL OR a.numberOfPersons = :numberOfPersons) " +
            "AND (:wbsPresent IS NULL OR a.wbsPresent = :wbsPresent) " +
            "AND (:email IS NULL OR a.email LIKE %:email%)")
    List<Applicant> findAllByAttributes(
            @Param("propertyId") UUID propertyId,
            @Param("status") Status status,
            @Param("numberOfPersons") Integer numberOfPersons,
            @Param("wbsPresent") Boolean wbsPresent,
            @Param("email") String email);
}
