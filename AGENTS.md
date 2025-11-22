# TFG_UNIR-backend Agent Guide

## Project Overview
This is the backend service for the TFG UNIR project, built with Spring Boot. It provides REST APIs for the application.

## Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Build Tool**: Maven
- **Database**: PostgreSQL
- **Containerization**: Docker

## Quick Start
### Prerequisites
- Java 17
- Maven
- Docker (for database)

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

### Tests
```bash
mvn test
```

## Architecture
The project follows a standard layered architecture:
- **Controller**: REST endpoints (`src/main/java/.../controller`)
- **Service**: Business logic (`src/main/java/.../service`)
- **Repository**: Data access (`src/main/java/.../repository`)
- **Model**: JPA entities (`src/main/java/.../model`)

## Conventions
- **Code Style**: Standard Java conventions.
- **Git**: Commit messages should follow Conventional Commits (e.g., `feat:`, `fix:`, `chore:`).
- **Testing**: Unit tests for services, integration tests for controllers.
