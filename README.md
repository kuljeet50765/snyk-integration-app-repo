# snyk-integration-app-repo

Dummy Spring Boot application for testing Snyk integration.

## Requirements

- JDK 17+

## Features

- REST API with GET, POST, PUT, PATCH, and DELETE endpoints
- Data persisted into a text file at `data/items.txt`
- JUnit tests for controller and file-backed service behavior

## Endpoints

- `GET /api/items`
- `GET /api/items/{id}`
- `POST /api/items`
- `PUT /api/items/{id}`
- `PATCH /api/items/{id}`
- `DELETE /api/items/{id}`

## Run

```bash
./mvnw spring-boot:run
```

## Test

```bash
./mvnw test
```
