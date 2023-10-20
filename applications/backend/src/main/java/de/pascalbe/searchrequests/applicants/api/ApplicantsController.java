package de.pascalbe.searchrequests.applicants.api;

import de.pascalbe.searchrequests.applicants.domain.Applicant;
import de.pascalbe.searchrequests.applicants.domain.ApplicantRepository;
import de.pascalbe.searchrequests.applicants.domain.CreationSource;
import de.pascalbe.searchrequests.applicants.domain.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ApplicantsController {

    private final ApplicantRepository applicantRepository;

    @PostMapping("/properties/{propertyId}/applicants")
    public ResponseEntity<?> storeManualApplicant(@PathVariable UUID propertyId,
                                                  @Valid @RequestBody ManualApplicant manualApplicant) {
        //  TO NOTE: we could put this logic in the application layer, but since there is no real application logic,
        //      we'll leave it here for simplicity reasons.
        var applicant = new Applicant();
        //  TO NOTE: this mapping could be extracted into a mapper (e.g. with mapstruct).
        //      It's left for simplicity reasons. Both options would be fine in my opinion in a real app.
        applicant.setId(String.valueOf(UUID.randomUUID()));
        applicant.setEmail(manualApplicant.getEmail());
        applicant.setFirstName(manualApplicant.getFirstName());
        applicant.setLastName(manualApplicant.getLastName());
        applicant.setUserComment(manualApplicant.getUserComment());
        applicant.setSalutation(manualApplicant.getSalutation());
        applicant.setCreationSource(CreationSource.MANUAL);
        applicant.setPropertyId(propertyId);
        applicant.setStatus(Status.CREATED);
        applicantRepository.save(applicant);

        var response = new StoreApplicantResponse();
        response.setId(applicant.getId());
        return ResponseEntity.created(URI.create("/applicants/" + response.getId())).body(response);
    }

    //  TO NOTE: this endpoint should have unit tests for the validations.
    //      Leaving it out to keep it simple.
    @PostMapping("/properties/{propertyId}/external-applicants")
    public ResponseEntity<?> storeExternalApplicant(@PathVariable UUID propertyId,
                                                    @Valid @RequestBody ExternalApplicant externalApplicant) {
        var applicant = new Applicant();
        applicant.setId(String.valueOf(UUID.randomUUID()));
        applicant.setEmail(externalApplicant.getEmail());
        applicant.setFirstName(externalApplicant.getFirstName());
        applicant.setLastName(externalApplicant.getLastName());
        applicant.setApplicantComment(externalApplicant.getApplicantComment());
        applicant.setSalutation(externalApplicant.getSalutation());
        applicant.setCreationSource(CreationSource.PORTAL);
        applicant.setPropertyId(propertyId);
        applicant.setStatus(Status.CREATED);
        applicant.setWbsPresent(externalApplicant.isWbsPresent());
        applicant.setPets(externalApplicant.isPets());
        applicant.setNumberOfPersons(externalApplicant.getNumberOfPersons());
        applicant.setEarliestMoveInDate(externalApplicant.getEarliestMoveInDate());
        applicantRepository.save(applicant);

        var response = new StoreApplicantResponse();
        response.setId(applicant.getId());
        return ResponseEntity.created(URI.create("/applicants/" + response.getId())).body(response);
    }

    @GetMapping("/applicants/{id}")
    public ResponseEntity<Applicant> getApplicantById(@PathVariable String id) {
        var applicant = applicantRepository.findById(id);

        return applicant.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/properties/{propertyId}/applicants")
    public ResponseEntity<List<Applicant>> getApplicants(@PathVariable UUID propertyId,
                                                         @RequestParam Optional<Status> status,
                                                         @RequestParam Optional<Integer> numberOfPersons,
                                                         @RequestParam Optional<Boolean> wbsPresent,
                                                         @RequestParam Optional<String> partOfEmail) {
        var applicants = applicantRepository.findAllByAttributes(propertyId,
                status.orElse(null),
                numberOfPersons.orElse(null),
                wbsPresent.orElse(null),
                partOfEmail.orElse(null));

        return ResponseEntity.ok(applicants);
    }
}
