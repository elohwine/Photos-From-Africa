# Copilot Instructions for Infinity-PhotoGallery

## Project Overview
- **Infinity-PhotoGallery** is a Java Spring Boot web application for an online photo gallery, supporting three user roles: admin, artist, and user.
- The platform enables browsing, buying, and selling photos, as well as registering for and organizing workshops and photoshoots with scheduling capabilities.
- The codebase is located in `infinity/` with main logic under `src/main/java/com/ken/infinity/` and resources in `src/main/resources/`.

## Architecture & Key Components
- **Controllers**: Handle HTTP requests for features like adding photos, managing photoshoots, workshops, user authentication, etc. (`controllers/`)
- **Models**: Define domain entities such as Photo, User, Photoshoot, Workshop, etc. (`models/`)
- **Repository**: Spring Data JPA repositories for database access (`repository/`)
- **Services**: Business logic and cross-controller operations (`services/`)
- **Configurations**: Security and other app-wide settings (`configurations/`)
- **Templates**: Thymeleaf HTML templates for UI (`resources/templates/`)
- **Static Assets**: CSS, JS, images, and Bootstrap resources (`resources/static/`)

## Developer Workflows
- **Build**: Use `./mvnw clean package` from the `infinity/` directory to build the project.
- **Run**: Start the app with `./mvnw spring-boot:run` (default port: 8080).
- **Test**: Run tests with `./mvnw test`.
- **Database**: Uses SQL schema in `resources/Schema.sql` and properties in `application.properties`.
- **Static/Template Changes**: Edit files in `resources/static/` or `resources/templates/` for UI updates.

## Project-Specific Patterns & Conventions
- **User Roles**: Role-based access is enforced in `WebSecurityConfiguration.java`.
- **Photo Status**: Photos have labels: `Verifying`, `Unsold`, `Sold` (see model and controller logic).
- **Email Notifications**: Order and registration actions trigger email confirmations (see relevant controllers/services).
- **Form Handling**: Most user actions (buy, register, submit photo) are handled via form POSTs to controller endpoints.
- **Approval Workflow**: Admins approve/decline submitted photos before public display.
- **Photoshoot Scheduling**: Photoshoots include start/end times, duration, photographer assignment, and equipment requirements.

## Integration & External Dependencies
- **Spring Boot**: Main framework for backend and MVC.
- **Spring Data JPA**: ORM for database operations (migrated from JDBC).
- **Thymeleaf**: Server-side rendering for HTML templates.
- **Bootstrap**: Frontend styling (see `static/bootstrap/`).
- **Email**: Outbound email for confirmations (configured in properties and used in services).

## Examples
- To add a new photo category, update the Photo model, controller, and relevant template(s).
- To add a new user role or permission, update `WebSecurityConfiguration.java` and related logic.
- For new UI pages, add a template in `resources/templates/` and link via a controller.
- To add scheduling features to photoshoots, extend the Photoshoot model and update the PhotoshootController.

## References
- Main entry: `InfinityApplication.java`
- Security: `configurations/WebSecurityConfiguration.java`
- Example controller: `controllers/AddPhotoController.java`
- Example template: `resources/templates/addPhoto.html`

---

For questions or unclear conventions, review the README or inspect the relevant controller/service for examples.
