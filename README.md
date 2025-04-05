# ClaimPilot 🛠️

**ClaimPilot** is a microservice-based backend system for managing auto insurance claims, built with Java and Spring Boot. Designed to showcase real-world service-oriented architecture and backend optimization, the project reflects my experience as a tradesman turned developer and my goal to work in the insurance software space.

---

## 🚀 Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **JUnit & Mockito** (for testing)
- **GitHub Actions** (CI/CD)
- **AWS EC2 / Lambda**
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

### Database Setup

EC2_PUBLIC_IP: 35.93.224.186
endpoint test curl:
curl http://35.93.224.186:8080/api/claims

packaging without testing:
./mvnw clean package -DskipTests

upload to EC2 instance:
scp -i ~/.ssh/ec2-key-claimpilot.pem target/claimpilot-0.0.1-SNAPSHOT.jar ec2-user@35.93.224.186:~/

SSH into EC2 instance:
ssh -i ~/.ssh/ec2-key-claimpilot.pem ec2-user@35.93.224.186

Run the Spring Boot JAR in ec2 console:
java -jar claimpilot-0.0.1-SNAPSHOT.jar

Connect to RDS database from EC2:

psql -h database-claimpilot.c524qeq804s9.us-west-2.rds.amazonaws.com \
 -U claimpilotDB \
 -d postgres
