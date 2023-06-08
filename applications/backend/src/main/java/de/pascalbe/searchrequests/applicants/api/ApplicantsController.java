package de.pascalbe.searchrequests.applicants.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ApplicantsController {

    @PostMapping("/applicants")
    public void storeManualApplicant() {
        log.info("storeManualApplicant was called!");
    }

}
