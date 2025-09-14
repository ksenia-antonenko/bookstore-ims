# Bookstore Inventory Management System

The Bookstore Inventory Management System is a web application (REST API only) that allows bookstore owners to manage
their inventory

## Table of Contents

<!-- TOC -->
* [Bookstore Inventory Management System](#bookstore-inventory-management-system)
  * [Table of Contents](#table-of-contents)
  * [Local development](#local-development)
  * [Resources](#resources)
  * [Build](#build)
  * [Usage](#usage)
    * [Configuration properties](#configuration-properties)
    * [Run application](#run-application)
    * [Run application in Docker](#run-application-in-docker)
  * [Tests](#tests)
    * [Test description](#test-description)
    * [Test types](#test-types)
    * [Test containers (aka. testcontainers)](#test-containers-aka-testcontainers)
    * [Steps to run integration tests on Windows](#steps-to-run-integration-tests-on-windows)
  * [Swagger](#swagger)
  * [Implementation details](#implementation-details)
    * [Health check and management endpoints](#health-check-and-management-endpoints)
    * [Security](#security)
      * [Authentication](#authentication)
      * [Authorization](#authorization)
    * [Exception handling](#exception-handling)
      * [Exception handling description](#exception-handling-description)
      * [Exception handling class locations](#exception-handling-class-locations)
  * [Further steps](#further-steps)
    * [Testing](#testing)
    * [CI/CD](#cicd)
    * [Authentication & Authorization](#authentication--authorization)
    * [Extended functionality](#extended-functionality)
    * [Observability](#observability)
    * [Scalability & Best Practices](#scalability--best-practices)
<!-- TOC -->

## Local development

In order to set up a local development environment, several steps need to be done:

1. To start required resources run docker and wait until docker image started
   using [file](docker/docker-compose.yml) ```docker-compose -f docker/docker-compose.yml up -d db```
2. start app in order to init the database
3. execute sql [script](docker/init-sql.sql) in order to create entities to work with
   (it creates several authors, and genres, and links between them, since Authors and Genres management is not in scope of this task)

## Build

Gradle is used as a build tool to assemble and validate source code.
There is no need to have Gradle installed.
Source code is shipped with Gradle binaries.
You can invoke any Gradle command by using *gradlew* wrapper.
To build application and get a jar artifact please use the following command:

```
./gradlew build
```

## Usage

### Configuration properties

Available properties are the following:
<details>
<summary>Basic (no classification applied) properties</summary>

[TBD]

</details>

### Run application

Run application using Gradle:

```shell
./gradlew bootRun
```

Run as a standalone application:

```shell
java -jar build/libs/bookstore*.jar
```

Application will be bind to `8080` port by default, but you can override it by specifying `-Dserver.port=YOUR_PORT`.

## Tests

### Test description

To maintain high code quality combination of different test types is used, as well as multiple
code quality control tools, such as and [Checkstyle](https://checkstyle.sourceforge.io/).
[Sonar](https://www.sonarqube.org/) can be set up later.
All new code must be covered with tests and no critical issues should be present.

Integration tests should extend [AbstractIT](src/test/java/org/example/bookstore/AbstractIT.java) or one of its
successors.
Contract tests should
extend [AbstractControllerContractIT](src/test/java/org/example/bookstore/contract/AbstractControllerContractIT.java) or
one of its successors - [TBD].
Repository tests should
extend [AbstractRepositoryIT](src/test/java/org/example/bookstore/repository/AbstractRepositoryIT.java) or one of its
successors.

### Test types

* Unit tests - testing class logic in isolation from external dependencies.
  Most of the tests are unit tests.
* Integration tests - tests from endpoints level to repository level.
  Located in [Controller package](src/test/java/org/example/bookstore/controller).
* Contract tests - tests that endpoint behavior has not changed and gives expected responses.
  Located in [Contract package](src/test/java/org/example/bookstore/contract).
* Property tests - tests that properties are populated as expected.
  Located in [Config package](src/test/java/org/example/bookstore/config).
* Repository tests - tests on repository level.
  Located in [Repository package](src/test/java/org/example/bookstore/repository).

### Test containers (a.k.a. testcontainers)

To implement better integration tests, test containers are used for:

1. Postgres database - SharedPostgresqlContainer.java

### Steps to run integration tests on Windows

To run Integration tests you need to have Docker installed.
Since Docker Desktop needs a commercial licence to use, the simplest solution is to run Docker inside WSL.
To make testcontainers work with Docker installed in WSL you need to follow this steps:

1. Create file `/etc/docker/daemon.json` in WSL with content

```json 
{
    "hosts": [
        "tcp://0.0.0.0:2375",
        "unix:///var/run/docker.sock"
    ]
}
``` 

2. Restart docker
3. In Windows cmd run `wsl hostname -I`
4. Take the first ip returned by the command and create environment variable `DOCKER_HOST = tcp://172.23.212.242:2375`
   either as system environment variable, or just IT run configuration.
   Be mindful that value need to be updated every WSL restart.
5. Run IT

## Swagger

When application is running and property `springdoc.swagger-ui.enabled` is `true`, you can access swagger ui at address
`{base_url}/api/swagger/ui`
[TBD]

## Implementation details

### Health check and management endpoints

Health check and management endpoints are provided by Spring Boot Actuator auto-configuration
and available under default REST API endpoints:

* `/actuator/health/liveness`
* `/actuator/health/readiness`.

### Security

#### Authentication

For now, only Basic authentication is implemented.
In future iterations OAuth2 authentication with JWT should be implemented.
[AWS Cognito](https://aws.amazon.com/cognito/) can be used for user authentication.

#### Authorization

Access to endpoints is regulated
by [security configuration](src/test/java/org/example/bookstore/config/SecurityConfig.java).

### Exception handling

#### Exception handling description

In case if bookstore service needs to throw an exception in code - it should
be [BookstoreRuntimeException](src/test/java/org/example/bookstore/exception/BookstoreRuntimeException.java).
All exceptions are mapped to appropriate codes in configuration.

## Further steps

The current implementation is functional but should be extended and hardened for production use. The following
improvements are recommended:

### Testing

- Cover all **services, mappers, and specifications** with unit tests.
- Add **repository tests** if custom/native JPQL/SQL queries are introduced.
- Create **contract controller tests** (with mocked services) to validate request/response schemas and validation errors
  quickly.
- Keep **end-to-end tests** only for critical flows (CRUD, associations).

### CI/CD

- Tune the **Jenkins pipeline** (Jenkinsfile) with stages: build → unit tests → integration tests → static analysis (
  Jacoco, Checkstyle, Sonar) → Docker build & push → Kubernetes deployment.

### Authentication & Authorization

- Integrate with **AWS Cognito** for OAuth2/JWT-based authentication.
- Map Cognito groups/claims to fine-grained authorities (e.g. `BOOK_WRITE`, `AUTHOR_WRITE`, `READ_ONLY`).
- Keep method-level authorization with `@PreAuthorize`.

### Extended functionality

- Implement **CRUD + search** for Authors and Genres.
- Add **image upload** to **S3** with pre-signed URLs and metadata stored in DB.
- Make search more flexible e.g. with **RSQL** + parser.
- **Auditing**: enable Spring Data auditing and Hibernate Envers to keep history of changes.
- Introduce API-first approach with OpenApi.

### Observability

- Integrate with Prometheus and Grafana. Expose **metrics** related to business logic. Create Grafana dashboards.
- Add **structured logging** with correlation IDs.
- Configure **error codes** consistently and document in OpenAPI/Swagger.

### Scalability & Best Practices

- Add indexes for frequent queries (title, author, genre).
- Use **Redis cache** for frequent read queries (e.g. book search by title/author/genre).
- Add retries/circuit breakers for external integrations (S3, Cognito).
- Manage secrets via a vault (e.g. AWS Secrets Manager).  