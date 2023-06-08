package de.pascalbe.searchrequests.applicants.api;

import de.pascalbe.searchrequests.applicants.domain.Applicant;
import de.pascalbe.searchrequests.applicants.domain.ApplicantRepository;
import de.pascalbe.searchrequests.applicants.domain.CreationSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ApplicantsController {

    private final ApplicantRepository applicantRepository;

    @PostMapping("/applicants")
    public ResponseEntity<?> storeManualApplicant(@Valid @RequestBody ManualApplicant manualApplicant) {
        //  TO NOTE: we could put this logic in the application layer, but since there is no real application logic, we'll leave it here for simplicity reasons
        var applicant = new Applicant();
        applicant.setId(String.valueOf(UUID.randomUUID()));
        applicant.setEmail(manualApplicant.getEmail());
        applicant.setFirstName(manualApplicant.getFirstName());
        applicant.setLastName(manualApplicant.getLastName());
        applicant.setComment(manualApplicant.getComment());
        applicant.setSalutation(manualApplicant.getSalutation());
        applicant.setCreationSource(CreationSource.MANUAL);
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

}
