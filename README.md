# Faculty Notes Publishing System

DevOps project, Vidyalankar Institute of Technology.

A small web application where faculty create notes, send them for review and publish them for students.
The project also builds a full CI/CD pipeline around the application: Git and GitHub, Jenkins, Selenium,
Docker, and Ansible.

**Author:** Chirayu Pritmani (Roll No 23102C0085)

## Features
- Dashboard with note counts by status (Draft, Under Review, Published)
- Notes list with search by title, subject or keyword
- Create note (saved as Draft)
- Health endpoint at `/actuator/health`
- Planned: view and edit (Task 5), login, roles and status workflow (Task 6)

## Technology
| Area | Choice |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5, Thymeleaf, Spring Data JPA |
| Database | H2 (in-memory) |
| Build | Maven 3.9 or newer, WAR packaging |
| Deployment | Tomcat 10.1, or Docker |
| Tests | JUnit 5, MockMvc, Selenium (later) |
| CI/CD | Jenkins, Docker Hub, Ansible |

## Getting started
Requirements: JDK 21 and Maven installed, and `JAVA_HOME` set.

```
mvn clean package
mvn spring-boot:run
```

Open:
- http://localhost:8082/  (dashboard)
- http://localhost:8082/notes  (list and search)
- http://localhost:8082/actuator/health  (health check)

If port 8082 is busy, set another port first, for example `$env:PORT=8090` in PowerShell.

### Deploy to Tomcat 10.1
Copy `target/faculty-notes.war` to the Tomcat `webapps` folder, start Tomcat, and open
`http://localhost:8081/faculty-notes/` (Tomcat configured on port 8081).

## Ports
| Service | Port |
|---|---|
| Jenkins | 8080 |
| Tomcat | 8081 |
| Application (`spring-boot:run`) | 8082 |

## Repository layout
```
.github/            issue templates and pull request template
docs/               project documents and the branching policy
src/main/java/      application code (model, repository, web)
src/main/resources/ templates, CSS and configuration
src/test/java/      unit and web tests
pom.xml             Maven build
```

## Contributing
- Branching model, naming rules and commit conventions: see [docs/BRANCHING.md](docs/BRANCHING.md).
- Work from an issue, on a `feature/<issue>-<name>` branch, and open a pull request into `develop`.

## Project status
| Task | Topic | Status |
|---|---|---|
| 1 | Problem definition and scope | Done |
| 2 | Agile planning and DevOps workflow | Done |
| 3 | Requirements, architecture and setup | Done |
| 4 | Git and GitHub initialisation | In progress |
| 5 onwards | Features, Jenkins, Selenium, Docker, Ansible | Planned |
