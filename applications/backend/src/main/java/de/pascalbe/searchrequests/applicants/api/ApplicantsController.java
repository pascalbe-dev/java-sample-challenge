package de.pascalbe.searchrequests.applicants.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
public class ApplicantsController {

    @PostMapping("/applicants")
    public ResponseEntity<?> storeManualApplicant(@Valid @RequestBody ManualApplicant manualApplicant) {
        log.info("storeManualApplicant was called!");
        //  TO NOTE: this should be 201 CREATED, but we have no location to return yet
        return ResponseEntity.ok().build();
    }

}
