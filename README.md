TEST CHANGE
# airline-project
Cloud data pipeline + containerized APIs (Python/Flask &amp; Spring/Gradle), deployed on Kubernetes with Jenkins CI/CD. Covers ETL, DB integration, CI/CD automation, and legacy Tomcat concepts.


# Airline Flight Schedule Integration Pipeline

A production-style capstone project that mimics real enterprise workflows:

- **ETL (Python)**: Ingests provider flight data (CSV → Parquet), validates, and UPSERTs into Postgres.
- **API (Flask)**: Containerized REST service to query flight schedules.
- **API (Spring Boot)**: Java/Gradle REST service (runs as JAR or WAR on Tomcat).
- **CI/CD (Jenkins)**: Automated build → push → deploy to Kubernetes.
- **Orchestration (Kubernetes / OpenShift)**: Health probes, services, CronJob for nightly ETL.
- **On-Prem Concepts**: Tomcat, Gradle, Spring Boot (WebSphere-analog).

## Architecture
```mermaid
flowchart LR
  A[CSV/JSON Provider Data] -->|ETL (Python)| B[(Postgres DB)]
  B --> C[Flask API (Docker)]
  B --> D[Spring Boot API (Gradle/Tomcat)]
  C -->|Kubernetes Service| E[Internal Apps]
  D -->|Kubernetes Service| E
  F[Jenkins CI/CD] -->|Build & Deploy| C
  F -->|Build & Deploy| D
  G[CronJob] -->|Nightly ETL| A
