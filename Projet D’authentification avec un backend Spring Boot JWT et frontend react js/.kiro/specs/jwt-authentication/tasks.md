# Implementation Plan

- [x] 1. Set up Spring Boot project structure and dependencies





  - Create Maven project with pom.xml including Spring Boot Web, Security, Data JPA, MySQL driver, JJWT, and Validation dependencies
  - Configure application.properties with database connection, JWT secret, and server settings
  - Create main application class UserAuthJwtApplication.java with @SpringBootApplication
  - _Requirements: 7.1, 7.5_
-

- [x] 2. Implement User entity and repository



  - [x] 2.1 Create User entity class with JPA annotations


    - Define User.java with id, username, and password fields
    - Add @Entity, @Table, @Id, @GeneratedValue, and @Column annotations
    - Implement constructors, getters, and setters
    - _Requirements: 1.1, 1.5, 7.1_
  - [x] 2.2 Create UserRepository interface


    - Define UserRepository.java extending JpaRepository<User, Long>
    - Add findByUsername() method returning Optional<User>
    - Add existsByUsername() method returning boolean
    - _Requirements: 7.3_

- [x] 3. Create DTO classes for API requests and responses






  - [x] 3.1 Implement SignupRequest DTO

    - Create SignupRequest.java with username, email, and password fields
    - Add @NotBlank validation annotations
    - _Requirements: 1.3, 7.2_

  - [x] 3.2 Implement AuthRequest DTO

    - Create AuthRequest.java with username and password fields
    - Add @NotBlank validation annotations
    - _Requirements: 2.1, 7.2_
  - [x] 3.3 Implement AuthResponse DTO


    - Create AuthResponse.java with token field
    - Add constructor and getter/setter
    - _Requirements: 2.5, 7.2_
-

- [x] 4. Implement JWT service for token generation and validation




  - [x] 4.1 Create JwtService class


    - Implement generateToken() method to create JWT with username, issued date, and expiration
    - Implement extractUsername() method to parse token and extract subject
    - Implement isTokenValid() method to validate token signature and expiration
    - Implement parseClaims() private method using JJWT library
    - Configure HMAC-SHA256 signing with secret key from application.properties
    - _Requirements: 2.1, 2.4, 3.1, 3.2, 3.3, 7.5_


- [x] 5. Implement user authentication service



  - [x] 5.1 Create CustomUserDetailsService


    - Implement UserDetailsService interface
    - Override loadUserByUsername() to load user from database
    - Convert User entity to Spring Security UserDetails
    - Throw UsernameNotFoundException if user not found
    - _Requirements: 2.2, 3.4_
  - [x] 5.2 Create UserService for user management


    - Implement register() method to create new users
    - Check username uniqueness using UserRepository.existsByUsername()
    - Encrypt password using PasswordEncoder (BCrypt)
    - Save user to database via UserRepository
    - Throw RuntimeException if username already exists
    - _Requirements: 1.1, 1.2, 1.5_

- [x] 6. Implement JWT authentication filter





  - Create JwtAuthenticationFilter extending OncePerRequestFilter
  - Extract Authorization header and parse Bearer token
  - Validate token using JwtService
  - Load user details using CustomUserDetailsService
  - Set authentication in SecurityContext
  - Handle exceptions gracefully and continue filter chain
  - _Requirements: 3.1, 3.2, 3.3, 3.4_
-

- [x] 7. Configure Spring Security




  - [x] 7.1 Create SecurityConfig class


    - Define SecurityFilterChain bean with HTTP security configuration
    - Disable CSRF for stateless JWT authentication
    - Configure session management as stateless
    - Permit all requests to /auth/** endpoints
    - Require authentication for all other endpoints
    - Add JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter
    - _Requirements: 6.2, 6.3, 6.4, 6.5, 7.1_

  - [x] 7.2 Configure CORS

    - Add CORS configuration to allow frontend origin (http://localhost:3000)
    - Allow all HTTP methods and headers
    - Enable credentials support
    - _Requirements: 6.1_

  - [x] 7.3 Define PasswordEncoder bean

    - Create BCryptPasswordEncoder bean for password encryption
    - _Requirements: 1.5_
-

- [x] 8. Implement authentication controller





  - [x] 8.1 Create AuthController with signup endpoint

    - Implement POST /auth/signup endpoint
    - Validate SignupRequest using @Valid annotation
    - Check username uniqueness
    - Call UserService.register() to create user
    - Return success message or error response
    - _Requirements: 1.1, 1.2, 1.3, 1.4_

  - [x] 8.2 Implement login endpoint


    - Implement POST /auth/login endpoint
    - Validate AuthRequest using @Valid annotation
    - Find user by username using UserRepository
    - Verify password using PasswordEncoder.matches()
    - Generate JWT token using JwtService
    - Return AuthResponse with token or 401 error
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

- [x] 9. Create protected test endpoint





  - Implement TestController with GET /api/secure endpoint
  - Return a simple message to verify JWT authentication works
  - Endpoint should be accessible only with valid JWT token
  - _Requirements: 3.5_
-

- [x] 10. Set up React frontend project





  - [x] 10.1 Initialize React application

    - Create React app using create-react-app
    - Install axios dependency for HTTP requests
    - _Requirements: 4.1, 7.4_

  - [x] 10.2 Create Axios API configuration

    - Create api.js with axios instance configured with backend baseURL
    - Implement request interceptor to automatically add JWT token from localStorage
    - _Requirements: 4.4, 7.4_
-

- [x] 11. Implement React authentication components



  - [x] 11.1 Create Signup component


    - Build signup form with username, email, and password inputs
    - Implement signup() function to POST /auth/signup
    - Display success or error messages using alerts
    - _Requirements: 4.1, 4.5_
  - [x] 11.2 Create Login component


    - Build login form with email and password inputs
    - Implement login() function to POST /auth/login
    - Store JWT token in localStorage on success
    - Call onLoginSuccess callback to update app state
    - Display error messages on authentication failure
    - _Requirements: 4.2, 4.3, 4.5_
  - [x] 11.3 Create Home component for protected content


    - Implement getSecureData() function to call GET /api/secure
    - Display response message or error
    - Token automatically included via axios interceptor
    - _Requirements: 4.4, 5.3_

- [x] 12. Implement main App component with routing logic





  - Create App.js with authentication state management
  - Track isLogged state based on localStorage token presence
  - Conditionally render Signup/Login or Home/Logout based on authentication
  - Implement logout function to remove token and update state
  - _Requirements: 4.3, 5.1, 5.2, 5.3_
-

- [x] 13. Write backend unit tests




  - [x] 13.1 Test UserService


    - Test user registration with password encryption
    - Test username duplicate detection
    - _Requirements: 1.1, 1.2, 1.5_
  - [x] 13.2 Test JwtService


    - Test token generation with correct claims
    - Test token validation for valid tokens
    - Test rejection of expired tokens
    - Test rejection of tokens with invalid signature
    - _Requirements: 2.1, 2.4, 3.1, 3.2, 3.3_
  - [x] 13.3 Test CustomUserDetailsService


    - Test loading existing user
    - Test exception for non-existent user
    - _Requirements: 2.2, 3.4_

- [x] 14. Write backend integration tests





  - [x] 14.1 Test AuthController endpoints


    - Test signup with valid data
    - Test signup with duplicate username
    - Test login with valid credentials
    - Test login with invalid credentials
    - _Requirements: 1.1, 1.2, 1.4, 2.1, 2.2, 2.3, 2.5_
  - [x] 14.2 Test security configuration


    - Test public access to /auth/** endpoints
    - Test protected access to /api/** endpoints
    - Test JWT validation on protected endpoints
    - _Requirements: 3.1, 3.2, 3.3, 6.4, 6.5_
- [ ] 15. Write frontend component tests


















- [ ] 15. Write frontend component tests

  - Test Login component form submission and token storage
  - Test Signup component form submission and error handling
  - Test Home component API call with token
  - Test App component conditional rendering based on authentication
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 5.1, 5.2, 5.3_
