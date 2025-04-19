# ClaimPilot 🛠️

**ClaimPilot** is a microservice-based backend system for managing auto insurance claims, built with Java and Spring Boot. Designed to showcase real-world service-oriented architecture and backend optimization. Live demo: https://claim-pilot-frontend.vercel.app

---

## 🚀 Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **JUnit & Mockito** (for testing)
- **GitHub Actions** (CI/CD)
- **AWS EC2**
- **Docker**
- **Agile/Scrum** methodology
- **RESTful APIs** with Swagger

---

## 🛠️ Getting Started

### Prerequisites

- Java 17+
- Maven
- PostgreSQL (v14+)
- Git

## API Documentation

- Swagger UI:
  https://claimpilot.duckdns.org/swagger-ui/index.html

## Run the Backend Locally

Prerequisites
• Java 17+
• Maven
• PostgreSQL
• Git

1. Clone the Repository

git clone https://github.com/freeheart110/ClaimPilot.git
cd ClaimPilot

2. Set Up Your Local Database

createdb claimpilot_local

Update your src/main/resources/application.properties (or use application-local.properties) with your local DB credentials:

spring.datasource.url=jdbc:postgresql://localhost:5432/claimpilot_local
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.profiles.active=local

3. Build and Run

./mvnw clean package -DskipTests
java -jar target/claimpilot-0.0.1-SNAPSHOT.jar

## ☁️ Cloud Deployment

- Frontend (Vercel):

https://claim-pilot-frontend.vercel.app

- Backend (EC2 + RDS)
