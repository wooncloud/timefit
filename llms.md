# AI Onboarding Document: timefit Project

This document provides a comprehensive overview of the "timefit" project, designed to help an AI quickly understand its architecture, functionalities, and technical stack.

## 1. Project Overview

*   **Purpose:** "timefit" is a Software-as-a-Service (SaaS) application designed to streamline the reservation and communication process between businesses and their customers.
*   **Target Users:**
    *   **Customers:** Individuals looking to book services or appointments.
    *   **Businesses:** Service providers (e.g., salons, clinics, restaurants) needing a system to manage bookings and interact with clients.
*   **Core Value:** To provide an efficient and user-friendly platform for managing reservations and facilitating communication, saving time for both businesses and customers.

## 2. Key Technologies

*   **Backend:**
    *   **Spring Boot (Java):** Framework for building robust and scalable server-side applications.
    *   **PostgreSQL (via Supabase):** Primary relational database for persistent data storage. Supabase provides backend-as-a-service functionalities including authentication and real-time database capabilities.
    *   **Redis:** In-memory data store used for:
        *   Managing JWT Refresh Tokens.
        *   Real-time chat message Pub/Sub.
        *   Distributed locks for concurrent operations (e.g., booking).
        *   Caching frequently accessed data.
*   **Frontend:**
    *   **SvelteKit:** A modern Svelte framework for building fast and interactive user interfaces.
    *   **DaisyUI:** A Tailwind CSS component framework used for styling. It is preferred over custom CSS where possible. AI assistants should prioritize using DaisyUI components and utility classes. For usage and component information, refer to the official documentation: https://daisyui.com/llms.txt
*   **DevOps & Infrastructure:**
    *   **Docker:** For containerizing applications, ensuring consistent development and deployment environments.
    *   **Nginx:** Web server used for:
        *   SSL termination (HTTPS).
        *   Load balancing across multiple backend instances.
        *   Reverse proxy to backend services.
        *   Serving static files.
    *   **GitHub Actions:** CI/CD pipeline for automated testing, building, and deployment.
*   **Monitoring:**
    *   **Prometheus & Grafana:** For collecting metrics and visualizing system performance.

## 3. Core Functionalities

### 3.1. User Management
*   **Registration:** OAuth-based social login (Google, Kakao) and potentially email-based signup.
*   **Authentication:** JWT (Access Token + Refresh Token) mechanism.
*   **Roles:** Distinct roles for "Customers" and "Businesses" with different permissions and features.
*   **Profile Management:** Users can manage their basic information and view their history.

### 3.2. Business Profile Management
*   Businesses can register and manage their profiles, including:
    *   Business name, address, contact information.
    *   Operating hours.
    *   Service descriptions and offerings.

### 3.3. Reservation System
*   **For Customers:**
    *   Browse and search for businesses.
    *   View available time slots.
    *   Book appointments.
    *   View and manage their existing reservations (modify, cancel).
    *   Receive reservation reminders.
*   **For Businesses:**
    *   Define and manage service availability (e.g., time slots, capacity).
    *   View and manage incoming bookings (confirm, decline, modify).
    *   Calendar view for reservation management.

### 3.4. Chat System
*   **Real-time Communication:** Enables 1:1 chat between customers and businesses.
*   **Purpose:** Facilitates discussion about reservation details, inquiries, or other service-related communication.
*   **Integration:** Chat can be linked to specific reservations.

## 4. Architecture Highlights

*   **Authentication:** Uses JWT (JSON Web Tokens) with an Access Token (short-lived) and a Refresh Token (long-lived, stored in Redis) for secure and efficient session management. OAuth 2.0 is integrated for social logins.
*   **Database:** PostgreSQL, managed via Supabase, serves as the primary data store. The schema is detailed in `docs/ERD.md`.
*   **Real-time Features:** Redis Pub/Sub is used for the chat system.
*   **Concurrency Control:** Redis distributed locks are employed to prevent issues with simultaneous booking requests.
*   **Containerization:** All services are containerized using Docker, managed via `docker-compose.yml` for development consistency.
*   **Deployment:** Automated through GitHub Actions, deploying Docker containers to cloud infrastructure (e.g., EC2). Nginx acts as a reverse proxy and load balancer.
*   **Scheduled Tasks:** Spring Batch is used for tasks like cleaning up expired JWTs and archiving logs.

## 5. Data Model Overview

The primary entities in the system include:

*   **USERS:** Stores information about both customers and business owners (differentiated by role).
*   **BUSINESS:** Contains details about registered businesses.
*   **BUSINESS_HOURS:** Defines the operating hours for businesses.
*   **AVAILABLE_SLOTS:** Specifies the time slots open for booking for each business.
*   **RESERVATIONS:** Tracks all booking information, linking users, businesses, and available slots.
*   **CHATS:** Represents chat rooms between a user and a business.
*   **MESSAGES:** Stores individual chat messages within a chat room.
*   **NOTIFICATIONS:** Manages system-generated notifications for users.

Refer to `docs/ERD.md` for a detailed visual representation of the database schema and relationships.

## 6. Development Philosophy

*   **MVP (Minimum Viable Product) Approach:** The project prioritizes delivering core functionalities first and then iteratively adding features based on user feedback.
*   **Phased Development:** Features are planned in stages, as outlined in `docs/기능명세서.md`.

## 7. File Structure Overview

*   **`timefit-back/`:** Contains the Spring Boot backend application.
    *   `src/main/java/`: Java source code.
    *   `build.gradle`: Project build and dependency management file.
*   **`timefit-front/`:** Contains the SvelteKit frontend application.
    *   `src/`: Svelte components, routes, and library code.
    *   `package.json`: Project dependencies and scripts.
*   **`docs/`:** Contains project documentation, including:
    *   `Backend Architecture.md`
    *   `ERD.md` (Entity Relationship Diagram)
    *   `기능명세서.md` (Feature Specifications - in Korean)
    *   `운구름 기능명세서.md` (Additional Feature Specifications - in Korean)
*   **`page/`:** Likely related to documentation or static site generation (uses VitePress).

## 8. Important Documentation

For a deeper understanding of specific aspects of the project, please refer to the following documents located in the `/docs` directory:

*   **`docs/Backend Architecture.md`:** Detailed information on the backend design choices, technologies, and patterns.
*   **`docs/ERD.md`:** The Entity-Relationship Diagram illustrating the database structure.
*   **`docs/기능명세서.md`:** A detailed breakdown of application features, user stories, and constraints (in Korean).
*   **`docs/운구름 기능명세서.md`:** Further details on features and technical specifications (in Korean).

## 9. Project Structure Details

This section provides a more granular look at the typical directory and file organization within the backend and frontend projects.

### 9.1. Backend (`timefit-back`) - Spring Boot / Gradle

The backend is a Spring Boot application built with Gradle. The primary application code resides within the `web` module.

*   **`timefit-back/`**
    *   **`build.gradle`**: Root Gradle build file. May not contain much if it's a multi-project build delegating to subprojects.
    *   **`settings.gradle`**: Defines the project structure, including subprojects (modules). For `timefit`, it declares the `web` module: `include 'web'`.
    *   **`gradlew` / `gradlew.bat`**: Gradle wrapper scripts for consistent builds.
    *   **`gradle/`**: Gradle wrapper JAR and properties.
    *   **`web/`**: The main Spring Boot application module.
        *   **`build.gradle`**: Module-specific Gradle build file. This is where dependencies (Spring Boot starters, database drivers, etc.), plugins, and build tasks for the web application are defined.
        *   **`src/`**: Source code and resources.
            *   **`main/`**: Main application code.
                *   **`java/com/timefit/timefit/`**: (Assuming `com.timefit.timefit` is the base package)
                    *   `TimefitApplication.java`: Main Spring Boot application class with `@SpringBootApplication`.
                    *   **`config/`**: Configuration classes for Spring Security, CORS, Swagger/OpenAPI, Beans, etc.
                        *   Example: `SecurityConfig.java`, `WebConfig.java`, `RedisConfig.java`.
                    *   **`controller/`**: Spring MVC controllers that handle incoming HTTP requests and define API endpoints.
                        *   Example: `UserController.java`, `ReservationController.java`.
                    *   **`dto/`**: Data Transfer Objects used for request and response payloads to decouple API structure from entity structure.
                        *   Example: `UserRegistrationDto.java`, `ReservationRequest.java`.
                    *   **`entity/`**: JPA (Java Persistence API) entities that represent database tables.
                        *   Example: `User.java`, `Business.java`, `Reservation.java`.
                    *   **`repository/`**: Spring Data JPA repositories providing an abstraction layer for database interactions.
                        *   Example: `UserRepository.java`, `ReservationRepository.java`.
                    *   **`service/`**: Contains the business logic of the application. Services orchestrate calls to repositories and other services.
                        *   Example: `UserService.java`, `BookingService.java`.
                    *   **`exception/`**: Custom exception classes and global exception handlers (`@ControllerAdvice`).
                        *   Example: `ResourceNotFoundException.java`, `GlobalExceptionHandler.java`.
                    *   **`util/`**: Utility classes for common tasks (e.g., date manipulation, JWT generation/parsing if not in config).
                        *   Example: `JwtUtil.java`.
                *   **`resources/`**: Application resources.
                    *   `application.yml` (or `application.properties`): Core Spring Boot configuration file for database connections, server port, logging, custom properties, etc. May include profiles like `application-dev.yml`, `application-prod.yml`.
                    *   `static/`: For serving static files directly from Spring Boot (if any, though often Nginx handles this).
                    *   `templates/`: For server-side rendered templates (e.g., Thymeleaf, FreeMarker), if used.
            *   **`test/`**: Test code.
                *   **`java/com/timefit/timefit/`**: Unit and integration tests for controllers, services, repositories, etc.
                    *   Example: `UserServiceTest.java`, `ReservationControllerTest.java`.

### 9.2. Frontend (`timefit-front`) - SvelteKit

The frontend is built using SvelteKit, a framework based on Svelte.

*   **`timefit-front/`**
    *   **`src/`**: Contains all the source code for the SvelteKit application.
        *   **`app.d.ts`**: TypeScript type definitions specific to the SvelteKit app environment (e.g., for page data, locals).
        *   **`app.html`**: The main HTML shell for all pages. SvelteKit injects page content and assets here. Typically includes placeholders like `%sveltekit.head%` and `%sveltekit.body%`.
        *   **`hooks.server.ts`**: Server-side hooks that allow you to intercept and modify requests and responses. Used for things like authentication, authorization, and logging.
        *   **`lib/`**: A crucial directory for reusable Svelte components, utility functions, Svelte stores, and other modules that are not routes.
            *   `assets/` or `images/`: Static assets like images, icons used within components (often better placed in `static/` if globally accessible).
            *   `components/` or `ui/`: Reusable Svelte components (e.g., buttons, modals, layout elements).
                *   Example: `Button.svelte`, `Navbar.svelte`, `ReservationCard.svelte`.
            *   `layout/`: Components related to page layouts.
                *   Example: `Header.svelte`, `Sidebar.svelte`, `Footer.svelte`.
            *   `pages/`: May contain components that represent entire pages, often imported into route files.
            *   `stores/`: Svelte stores (writable, readable, derived) for managing global or shared application state.
                *   Example: `authStore.ts`, `userStore.ts`.
            *   `supabase/`: Code related to Supabase client initialization and interactions (e.g., custom wrappers for Supabase calls).
                *   Example: `supabaseClient.ts`.
            *   `utils/` or `helpers/`: Utility functions (e.g., date formatting, API request helpers).
                *   Example: `dateUtils.ts`, `api.ts`.
            *   `index.ts`: Often used as an entry point for the `lib` directory, exporting key modules for easier import.
        *   **`routes/`**: This directory defines the structure of your application's pages and API endpoints using a file-system based router.
            *   `+page.svelte`: Creates a page. For example, `src/routes/about/+page.svelte` creates the `/about` page.
            *   `+layout.svelte`: Defines a layout that applies to child routes.
            *   `+server.ts`: Creates API endpoints. For example, `src/routes/api/items/+server.ts` could handle requests to `/api/items`.
            *   Subdirectories create nested routes. For example, `src/routes/dashboard/settings/+page.svelte` creates `/dashboard/settings`.
            *   Special syntax for route groups:
                *   **`(pc)/`**: Groups routes for a specific layout (e.g., desktop version) without affecting the URL path.
                *   **`m/`**: Groups routes for a mobile-specific layout.
            *   Example:
                *   `src/routes/(pc)/dashboard/+page.svelte`
                *   `src/routes/m/profile/+page.svelte`
                *   `src/routes/api/auth/login/+server.ts`
        *   **`style.css`**: Global CSS styles. While available for base styling or overrides, the project prefers leveraging **DaisyUI** (a Tailwind CSS component framework) for styling and UI components. AI assistants should maximize the use of DaisyUI's classes and components. Consult the DaisyUI documentation for best practices: https://daisyui.com/llms.txt.
    *   **`static/`**: Contains static assets that are served as-is (e.g., `favicon.png`, `robots.txt`, images, fonts). These are directly accessible via the root path.
    *   **`.gitignore`**: Specifies intentionally untracked files that Git should ignore.
    *   **`.npmrc`**: Configuration file for npm.
    *   **`.prettierignore` / `.prettierrc`**: Configuration for the Prettier code formatter.
    *   **`eslint.config.js`**: Configuration for ESLint, a pluggable linter tool for identifying and reporting on patterns in JavaScript/TypeScript.
    *   **`package.json`**: Node.js project manifest file. Lists project dependencies, scripts (e.g., `dev`, `build`, `preview`, `lint`, `test`), and other metadata.
    *   **`package-lock.json`**: Records the exact versions of dependencies used, ensuring reproducible builds.
    *   **`svelte.config.js`**: SvelteKit specific configuration, including:
        *   **Adapters**: For deploying to different platforms (e.g., `adapter-auto`, `adapter-node`, `adapter-static`).
        *   **Preprocessors**: For processing Svelte components before compilation (e.g., `svelte-preprocess` for TypeScript, SCSS).
        *   **Kit options**: Various SvelteKit specific settings.
    *   **`tsconfig.json`**: TypeScript compiler configuration file, defining how TypeScript code should be transpiled to JavaScript.
    *   **`vite.config.ts`**: Configuration file for Vite, the build tool used by SvelteKit. Allows customization of the build process, plugins, etc.

This detailed structure should give a clearer picture of where to find specific types of code and how the projects are organized.

This document should serve as a solid starting point for any AI to get acquainted with the "timefit" project.
