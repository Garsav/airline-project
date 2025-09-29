# Java API (Spring Boot + Gradle)

This folder contains a REST API built with **Spring Boot** and **Gradle**.

## Purpose
- Query the `flights` table in Postgres using Spring Data JPA.
- Provide `/health` and `/flights` endpoints.
- Demonstrate both modern JAR deployment and legacy WAR deployment (Tomcat).

## Contents (planned)
- `build.gradle` → Gradle build file.
- `src/main/java/...` → Spring Boot code.
- `Dockerfile` → containerized API.
- Optional WAR deploy to Tomcat (for on-prem simulation).
