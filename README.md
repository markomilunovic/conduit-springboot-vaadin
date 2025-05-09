# Conduit - Spring Boot + Vaadin

A full-stack RealWorld (Conduit) implementation built with **Spring Boot 3.4.0**, **Vaadin 24**, and **MongoDB**.

This project provides a Medium.com–style blogging platform with user authentication, article management, comments, favoriting, following users, and tag-based article filtering — built according to the [RealWorld API specs](https://github.com/gothinkster/realworld).

Currently focused on **completing backend features** and a **basic frontend**.

---

## Features

**Backend (Spring Boot + MongoDB)**

- JWT Authentication (login, registration, token validation)
- CRUD Articles (create, read, update, delete)
- CRD Comments on articles
- Favorite and unfavorite articles
- Follow and unfollow users
- Pagination support for articles and feed
- Tag listing
- Centralized error handling
- Clean DTO → Entity mapping (via Mapper classes)
- OpenAPI (Swagger) documentation

**Frontend (Vaadin 24)**

- Load and display articles
- Load and display tags
- Basic interaction between article/tag listings
- Notification system for errors

---

## Project Structure

```bash
conduit-springboot-vaadin
├── src/main/java/com/example/conduit_springboot_vaadin/
│   ├── backend/
│   │   ├── common/        # Utility classes, global error handling
│   │   ├── config/        # Security configuration
│   │   ├── controller/    # REST API endpoints (Articles, Users, Tags, Profiles)
│   │   ├── dto/           # Data Transfer Objects
│   │   ├── exception/     # Custom exceptions
│   │   ├── mapper/        # Entity <-> DTO mappers
│   │   ├── model/         # MongoDB domain models
│   │   ├── repository/    # Spring Data MongoDB repositories
│   │   ├── security/      # CustomUserDetails and Authentication logic
│   │   └── service/       # Business logic (ArticleService, UserService, etc.)
│   ├── frontend/
│   │   ├── config/        # Frontend configuration (RestTemplate)
│   │   ├── service/       # Frontend service calls (Articles, Tags)
│   │   └── view/          # Vaadin UI (MainView)
├── src/main/resources/
│   ├── application.properties # App configuration (MongoDB URI, JWT secrets, logging)
├── pom.xml                     # Maven project configuration
└── README.md                    # (You're here!)
```

---

## Technologies Used
- **Java 21**
- **Spring Boot 3.4.0**
- **Vaadin 24**
- **MongoDB**
- **Spring Security (JWT authentication)**
- **Spring Data MongoDB**
- **Lombok**
- **Javadoc** (method documentation)
- **Swagger / OpenAPI** (API documentation)
- **Jakarta Validation**

---

## Database
Using MongoDB (non-relational)

Default connection string in `application.properties`:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/conduitdb
```

Collections:
- users
- articles
- comments
- access_token
- refresh_token

---

## How to Run the Project

1.  **Clone the Repository**

    ```bash
    git clone https://github.com/yourusername/conduit-springboot-vaadin.git
    cd conduit-springboot-vaadin
    ```

2. **Set Up MongoDB**

   Make sure MongoDB is running locally at `localhost:27017`.

   If not installed, install MongoDB first: [MongoDB Download](https://www.mongodb.com/try/download/community)

3. **Run the Application**

    ```bash
    ./mvnw spring-boot:run
    ```
   The server will start on [http://localhost:8080/](http://localhost:8080/).

4. **Access Swagger UI**

   View API documentation at:
    ```bash
    http://localhost:8080/swagger-ui/index.html
    ```

5. **Access the Frontend (Vaadin)**

   Visit:
    ```bash
    http://localhost:8080/
    ```
   MainView will load with options to display articles and tags.

---

## Frontend Status
This project implements a full-stack architecture with a Spring Boot backend and a Vaadin-based frontend.

- The backend implements approximately 85% of the RealWorld API specification.
- The frontend currently supports basic article and tag browsing.
- Features like login, article editor, profile pages, favoriting, following, and commenting will be added in future iterations.

---

## Authentication
Login via `/api/users/login` to receive an **Access Token** and **Refresh Token**.

Use the Access Token in the `Authorization` header for protected API calls:
```http
Authorization: Token jwt.token.here
```

---

## API Endpoints Overview

| Feature             | Endpoint                                  | Access        |
|---------------------|-------------------------------------------|---------------|
| User Registration   | POST /api/users                           | Public        |
| User Login          | POST /api/users/login                     | Public        |
| Get Current User    | GET /api/user                             | Authenticated |
| Update User         | PUT /api/user                             | Authenticated |
| Get Profile         | GET /api/profiles/{username}              | Public        |
| Follow User         | POST /api/profiles/{username}/follow      | Authenticated |
| Unfollow User       | DELETE /api/profiles/{username}/follow    | Authenticated |
| List Articles       | GET /api/articles                         | Public        |
| List Feed           | GET /api/articles/feed                    | Authenticated |
| Get Article         | GET /api/articles/{slug}                  | Public        |
| Create Article      | POST /api/articles                        | Authenticated |
| Update Article      | PUT /api/articles/{slug}                  | Authenticated |
| Delete Article      | DELETE /api/articles/{slug}               | Authenticated |
| Add Comment         | POST /api/articles/{slug}/comments        | Authenticated |
| Get Comments        | GET /api/articles/{slug}/comments         | Public        |
| Delete Comment      | DELETE /api/articles/{slug}/comments/{id} | Authenticated |
| Favorite Article    | POST /api/articles/{slug}/favorite        | Authenticated |
| Unfavorite Article  | DELETE /api/articles/{slug}/favorite      | Authenticated |
| Get Tags            | GET /api/tags                             | Public        |

Full API docs available at Swagger UI.

---

## Error Handling
All errors are returned in a standardized format:
```json
{
  "status": "error",
  "message": "Resource not found",
  "timestamp": "2025-04-28T14:23:12.345Z"
}
```

Custom Exceptions include:
- `UserAlreadyExistsException`
- `InvalidCredentialsException`
- `ArticleNotFoundException`
- `CommentNotFoundException`
- `AccessDeniedException`

---

## Future Improvements
- Expand Vaadin frontend: login, register, settings, profile, editor pages
- Add markdown support for article body (frontend rendering)
- Implement token refresh endpoint
- Add tests (unit/integration)
- Add pagination controls on frontend
- Dockerize the application
- Deploy to cloud (AWS, Heroku, Render, etc.)

---

## Author
**Marko Milunović**  
[LinkedIn Profile](https://www.linkedin.com/in/marko-milunović-946428267)

---

## License
This project is licensed under the MIT License.

Feel free to use it for learning, building projects, or as a base for further improvements.