# backend

## Local development

**Building the application (with automated tests)**

```bash
mvn clean verify
```

**Running the application**

```bash
mvn spring-boot:run
```

**Running local manual tests**

- start up the database via `docker-compose up -d` (in the database folder)
- start up the application (see above)
- import the [postman collection](manual-testing/postman-collection.json) into postman
- run the different requests
  - you can enable/change filters for the GET endpoint
  - you can change the request body (feel free to use the sample JSON files in [manual-testing](manual-testing/request-payload))
