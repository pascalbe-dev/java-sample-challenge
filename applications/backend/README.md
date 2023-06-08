# backend

## Local development

**Building the application**

```bash
mvn verify
```

**Running the application**

```bash
mvn spring-boot:run
```

**Running local manual tests**

```bash
# to create a manual applicant
curl -X POST -H "Content-Type: application/json" -d @dummy-data/request-payload/create-manual-applicant.json http://localhost:8081/applicants
```

```bash
# to create a manual applicant with comment
curl -X POST -H "Content-Type: application/json" -d @dummy-data/request-payload/create-manual-applicant-with-comment.json http://localhost:8081/applicants
```

```bash
# to fetch an applicant by ID (you need to set the env var "APPLICANT_ID" to the ID of the applicant you want to fetch)
curl -X GET http://localhost:8081/applicants/$APPLICANT_ID
```
