# Conduit - Spring Boot & Vaadin Implementation

This is an implementation of the RealWorld "Conduit" application, built using **Spring Boot**, **Vaadin**, and **MongoDB**. 
The application demonstrates essential features like authentication, article management, comments, and user profiles, 
following the RealWorld specification.

## Table of Contents
- [Project Description](#project-description)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation and Running Locally](#installation-and-running-locally)
- [Application Configuration](#application-configuration)
- [Testing](#testing)
- [License](#license)
- [Issues](#issues)

## Project Description
Conduit is a web application inspired by the RealWorld "Conduit" project. It serves as a practical example for implementing 
a feature-rich application using modern Java technologies. The application includes features such as user authentication, 
article creation, and a dynamic UI, showcasing a clean integration of Spring Boot and Vaadin with MongoDB as the database.

## Features
- **Authentication**: Login/Signup with JWT.
- **User Management**: CRU (Create, Read, Update) for users.
- **Articles**: Full CRUD operations for articles.
- **Comments**: Create and Read comments on articles.
- **Article Feed**: Paginated global feed, user-specific feed, and filtered feeds by tags.
- **Social Features**: Favorite articles and follow/unfollow users.

## Tech Stack
- **Backend**: Spring Boot (with Spring Security and Spring Data MongoDB).
- **Frontend**: Vaadin (for a modern web UI).
- **Database**: MongoDB.

## Prerequisites
Ensure the following are installed:
- **Java 21** or higher
- **Maven**
- **MongoDB**

## Installation and Running Locally
1. **Clone the repository**:
   ```bash
   git clone https://github.com/markomilunovic/conduit-springboot-vaadin.git
   cd conduit-springboot-vaadin
   ```

2. **Configure MongoDB**: Ensure MongoDB is running locally and create a database named `conduitdb`.

3. **Build the application**:
   ```bash
   mvn clean install
   ```

4. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**: Visit [http://localhost:8080](http://localhost:8080).

## Application Configuration
Configurations are defined in `application.properties`:

- **MongoDB URI**: `spring.data.mongodb.uri=mongodb://localhost:27017/conduitdb`
- **JWT Secrets**: Configured for access and refresh tokens.
- **Token Expirations**:
    - Access Token: 15 minutes
    - Refresh Token: 7 days

## Testing
Run unit tests with:
```bash
mvn test
```

## License
This project is licensed under the MIT License. See the `LICENSE` file for details.

## Issues
The "Issues" section is open. Report bugs or share feedback.
