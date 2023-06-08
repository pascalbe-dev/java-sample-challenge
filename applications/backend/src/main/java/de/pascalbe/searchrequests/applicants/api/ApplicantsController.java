package de.pascalbe.searchrequests.applicants.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
@Slf4j
public class ApplicantsController {

    @PostMapping("/applicants")
    public ResponseEntity<?> storeManualApplicant(@Valid @RequestBody ManualApplicant manualApplicant) {
        var response = new StoreApplicantResponse();
        response.setId("275e5104-f939-473d-ad4c-fd432d1d2aea");
        return ResponseEntity.created(URI.create("/applicants/" + response.getId())).body(response);
    }

}
