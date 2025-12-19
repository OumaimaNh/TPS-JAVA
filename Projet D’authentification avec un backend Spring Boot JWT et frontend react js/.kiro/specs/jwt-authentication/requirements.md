# Requirements Document

## Introduction

Ce document définit les exigences pour un système d'authentification complet utilisant JWT (JSON Web Token) avec un backend Spring Boot et un frontend React. Le système permettra aux utilisateurs de s'inscrire, se connecter, et accéder à des ressources protégées via des tokens JWT sécurisés.

## Glossary

- **JWT System**: Le système complet d'authentification basé sur JSON Web Token
- **Backend API**: L'application Spring Boot qui gère l'authentification et les endpoints sécurisés
- **Frontend Client**: L'application React qui interagit avec le Backend API
- **User**: Un utilisateur enregistré dans le système avec username et password
- **JWT Token**: Un token signé contenant les informations d'authentification de l'utilisateur
- **Protected Endpoint**: Un endpoint API accessible uniquement avec un JWT Token valide
- **Authentication Filter**: Le filtre Spring Security qui valide les JWT Tokens sur chaque requête

## Requirements

### Requirement 1

**User Story:** En tant qu'utilisateur, je veux créer un compte avec un username et un mot de passe, afin de pouvoir accéder au système.

#### Acceptance Criteria

1. WHEN a user submits valid signup credentials, THE Backend API SHALL create a new user account with encrypted password
2. IF a username already exists in the database, THEN THE Backend API SHALL return an error message indicating the username is taken
3. THE Backend API SHALL validate that username and password fields are not empty before creating an account
4. WHEN a user account is created successfully, THE Backend API SHALL return a success confirmation message
5. THE Backend API SHALL store the password using BCrypt encryption algorithm

### Requirement 2

**User Story:** En tant qu'utilisateur enregistré, je veux me connecter avec mes identifiants, afin de recevoir un token d'authentification.

#### Acceptance Criteria

1. WHEN a user submits valid login credentials, THE Backend API SHALL generate a JWT Token signed with HMAC-SHA256
2. IF the username does not exist in the database, THEN THE Backend API SHALL return an authentication error with status code 401
3. IF the password does not match the stored encrypted password, THEN THE Backend API SHALL return an authentication error with status code 401
4. THE JWT Token SHALL contain the username as subject and expiration time of 24 hours
5. WHEN authentication succeeds, THE Backend API SHALL return the JWT Token in the response body

### Requirement 3

**User Story:** En tant qu'utilisateur authentifié, je veux accéder aux ressources protégées en utilisant mon token, afin d'utiliser les fonctionnalités sécurisées.

#### Acceptance Criteria

1. WHEN a request includes a valid JWT Token in the Authorization header, THE Authentication Filter SHALL extract and validate the token
2. IF the JWT Token is expired, THEN THE Authentication Filter SHALL reject the request with status code 401
3. IF the JWT Token signature is invalid, THEN THE Authentication Filter SHALL reject the request with status code 401
4. WHEN the JWT Token is valid, THE Authentication Filter SHALL load user details and set authentication context
5. THE Protected Endpoint SHALL be accessible only when authentication context contains valid user details

### Requirement 4

**User Story:** En tant qu'utilisateur du frontend, je veux une interface pour m'inscrire et me connecter, afin d'interagir facilement avec le système.

#### Acceptance Criteria

1. THE Frontend Client SHALL provide a signup form with fields for username, email, and password
2. THE Frontend Client SHALL provide a login form with fields for email and password
3. WHEN login succeeds, THE Frontend Client SHALL store the JWT Token in browser localStorage
4. THE Frontend Client SHALL automatically include the JWT Token in the Authorization header for all API requests
5. THE Frontend Client SHALL display appropriate error messages when authentication fails

### Requirement 5

**User Story:** En tant qu'utilisateur connecté, je veux pouvoir me déconnecter, afin de sécuriser mon compte.

#### Acceptance Criteria

1. WHEN a user clicks the logout button, THE Frontend Client SHALL remove the JWT Token from localStorage
2. WHEN the JWT Token is removed, THE Frontend Client SHALL redirect the user to the login page
3. THE Frontend Client SHALL prevent access to protected pages when no JWT Token exists in localStorage

### Requirement 6

**User Story:** En tant qu'administrateur système, je veux que les communications entre frontend et backend soient sécurisées, afin de protéger les données sensibles.

#### Acceptance Criteria

1. THE Backend API SHALL configure CORS to accept requests from the Frontend Client origin
2. THE Backend API SHALL disable CSRF protection for stateless JWT authentication
3. THE Backend API SHALL configure session management as stateless
4. THE Backend API SHALL expose public endpoints for signup and login without authentication
5. THE Backend API SHALL require JWT authentication for all endpoints except those under /auth/** path

### Requirement 7

**User Story:** En tant que développeur, je veux une architecture modulaire et maintenable, afin de faciliter les évolutions futures.

#### Acceptance Criteria

1. THE Backend API SHALL separate concerns using controller, service, repository, and security layers
2. THE Backend API SHALL use DTOs for request and response data transfer
3. THE Backend API SHALL implement JPA repository pattern for database access
4. THE Frontend Client SHALL use Axios interceptors for centralized token management
5. THE Backend API SHALL externalize JWT secret and expiration configuration in application.properties
