# Complete Codebase Overview - Workflow Management System

**Project**: Multi-Tenant Workflow Management Platform  
**Technology Stack**: Spring Boot 3.5.9 + MongoDB + JWT + RestHeart  
**Java Version**: 17  
**Architecture**: Layered MVC with Reactive Components  

---

## 📋 Table of Contents

1. [System Architecture](#system-architecture)
2. [Technology Stack](#technology-stack)
3. [Directory Structure](#directory-structure)
4. [Core Concepts](#core-concepts)
5. [Data Models](#data-models)
6. [API Endpoints](#api-endpoints)
7. [Authentication & Security](#authentication--security)
8. [Service Layer](#service-layer)
9. [Database Design](#database-design)
10. [Configuration](#configuration)
11. [Multi-Tenancy Implementation](#multi-tenancy-implementation)
12. [Common Utilities](#common-utilities)
13. [Error Handling](#error-handling)
14. [How Everything Works Together](#how-everything-works-together)

---

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT (Web/Mobile)                      │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP/REST
                             ↓
┌─────────────────────────────────────────────────────────────────┐
│                    SPRING BOOT BACKEND (Port 8080)              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ WEB LAYER (Controllers)                                  │  │
│  │ - FormsController          (Form management)             │  │
│  │ - WorkflowController       (Workflow management)         │  │
│  │ - ProcessController        (Process management)          │  │
│  │ - LoginController          (Authentication)             │  │
│  │ - RegisterController       (User registration)          │  │
│  │ - TenantController         (Tenant management)          │  │
│  │ - AdminRoleController      (Role management)            │  │
│  └──────────────────────────────────────────────────────────┘  │
│                             │                                    │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ FILTER LAYER (Security)                                  │  │
│  │ - JwtAuthenticationFilter  (JWT validation)             │  │
│  └────────────────��─────────────────────────────────────────┘  │
│                             │                                    │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ SERVICE LAYER (Business Logic)                           │  │
│  │ - AuthenticationService    (Login/Signup/JWT)           │  │
│  │ - FormsService             (CRUD forms)                 │  │
│  │ - WorkflowService          (CRUD workflows)             │  │
│  │ - ProcessService           (CRUD processes)             │  │
│  │ - RoleService              (Role management)            │  │
│  │ - TenantService            (Tenant management)          │  │
│  │ - RestheartService         (MongoDB API calls)          │  │
│  └──────────────────────────────────────────────────────────┘  │
│                             │                                    │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ REPOSITORY LAYER (Data Access - Spring Data MongoDB)    │  │
│  │ - UserRepository                                        │  │
│  │ - FormRepository                                        │  │
│  │ - WorkflowRepository                                    │  │
│  │ - ProcessRepository                                     │  │
│  │ - AuditLogRepository                                    │  │
│  │ - RoleRepository                                        │  │
│  │ - TenantRepository                                      │  │
│  └──────────────────────────────────────────────────────────┘  │
│                             │                                    │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ↓
        ┌────────────────────────────────────────┐
        │     MONGODB DATABASE (Port 27017)      │
        │  Collections:                          │
        │  - users                              │
        │  - forms                              │
        │  - workflows                          │
        │  - process                            │
        │  - workflowInstances                  │
        │  - auditLogs                          │
        │  - roles                              │
        │  - tenants                            │
        └────────────────────────────────────────┘
```

---

## Technology Stack

### Core Framework
- **Spring Boot 3.5.9** - Main framework
- **Spring Security** - Authentication & Authorization
- **Spring Data MongoDB** - MongoDB ORM
- **Spring WebFlux** - Reactive web framework
- **Spring Web** - REST API support

### Authentication & JWT
- **io.jsonwebtoken (JJWT) 0.11.5 - 0.12.5** - JWT generation & validation
- **Spring Security** - Principal & Authentication management

### Database
- **MongoDB** - NoSQL document database
- **Spring Data MongoDB** - Data layer

### Tools & Utilities
- **Lombok** - Boilerplate code reduction (@Getter, @Setter, @Builder, etc.)
- **ModelMapper 3.2.5** - DTO to Entity mapping
- **Jackson** - JSON serialization/deserialization
- **Apache Commons Lang 3** - Utility functions
- **Google Guava 33.4.8** - Common utilities
- **SpringDoc OpenAPI 2.8.5** - Swagger UI & OpenAPI documentation

### Validation
- **Spring Validation** - Input validation
- **Jakarta Validation** - Bean validation annotations

### Build Tools
- **Maven** - Build and dependency management
- **Java 17** - Programming language

---

## Directory Structure

```
src/main/java/com/example/backend/
│
├── BackendApplication.java
│   └─ Main Spring Boot application entry point
│
├── annotations/                          [Custom Annotations]
│   ├── AdminApi.java                     (Marks admin-only APIs)
│   └── SharedApi.java                    (Marks shared APIs)
│
├── common/                               [Shared Utilities]
│   ├── InstantFromMongoDeserializer.java (Custom JSON deserializer for Instant)
│   ├── ObjectIdDeserializer.java         (Custom MongoDB ObjectId deserializer)
│   ├── PagebleObject.java                (Pagination & conversion utilities)
│   ├── ResponseUtil.java                 (Standardized API response wrapper)
│   └── ValidationUtil.java               (Input validation utilities)
│
├── config/                               [Configuration Classes]
│   ├── PasswordEncoderConfig.java        (BCrypt password encoding)
│   ├── WebClientConfig.java              (Reactive HTTP client)
│   ├── jwt/
│   │   ├── SecurityConfig.java           (Spring Security configuration)
│   │   ├── JwtAuthenticationEntryPoint.java (Handles 401 errors)
│   │   └── JwtAccessDeniedHandler.java   (Handles 403 errors)
│   ├── ModelMapper/
│   │   └── MapperConfig.java             (DTO-Entity mapping)
│   ├── Mongo/
│   │   └── MongoConfig.java              (MongoDB configuration)
│   └── OpenApi/
│       ├── OpenApiConfig.java            (Swagger OpenAPI setup)
│       └── SwaggerGroupingConfig.java    (API grouping for docs)
│
├── controller/                           [REST API Controllers]
│   ├── FormsController.java              (Form CRUD endpoints)
│   ├── WorkflowController.java           (Workflow CRUD endpoints)
│   ├── ProcessController.java            (Process CRUD endpoints)
│   ├── LoginController.java              (Authentication endpoint)
│   ├── RegisterController.java           (User registration)
│   ├── TenantController.java             (Tenant management)
│   └── AdminRoleController.java          (Role management)
│
├── dto/                                  [Data Transfer Objects]
│   ├── CreateFormDTO.java                (Form creation request)
│   ├── CreateWorkflowDTO.java            (Workflow creation request)
│   ├── ProcessDTO.java                   (Process request/response)
│   ├── FormsDTO.java                     (Form response)
│   ├── WorkflowDTO.java                  (Workflow response)
│   ├── WorkflowInstanceDTO.java          (Workflow execution instance)
│   ├── SignInRequest.java                (Login request)
│   ├── SignUpRequest.java                (Registration request)
│   ├── TenantDTO.java                    (Tenant info)
│   ├── RoleDTO.java                      (Role info)
│   ├── AuditLogsDTO.java                 (Audit log info)
│   ├── FormFieldsDTO.java                (Form field definition)
│   ├── StepsDTO.java                     (Workflow step definition)
│   ├── WorkflowMetaDataDTO.java          (Workflow metadata)
│   └── UpdateWorkflowDTO.java            (Workflow update request)
│
├── exceptions/                           [Custom Exceptions]
│   ├── RestApiException.java             (Custom exception with HTTP status)
│   └── GlobalExceptionHandler.java       (@ControllerAdvice for error handling)
│
├── filter/                               [HTTP Filters]
│   └── JwtAuthenticationFilter.java      (Validates JWT and sets SecurityContext)
│
├── model/                                [Database Models/Entities]
│   ├── Users.java                        (User entity)
│   ├── Workflow.java                     (Workflow entity)
│   ├── Forms.java                        (Form entity)
│   ├── Process.java                      (Process entity)
│   ├── WorkflowInstance.java             (Workflow execution instance)
│   ├── AuditLogs.java                    (Audit log entity)
│   ├── Role.java                         (Role entity)
│   ├── Tenant.java                       (Tenant entity)
│   ├── FormField.java                    (Form field entity)
│   ├── Steps.java                        (Workflow step entity)
│   ├── Authorities.java                  (Authority/Permission entity)
│   ├── FormMetadata.java                 (Form metadata - timestamps, etc)
│   ├── WorkflowMetadata.java             (Workflow metadata)
│   ├── InstanceMetadata.java             (Instance metadata)
│   ├── TenantConfig.java                 (Tenant configuration)
│   ├── TenantMetadata.java               (Tenant metadata)
│   ├── ContactInfo.java                  (Contact information)
│   └── Property.java                     (Generic property/attribute)
│
├── repository/                           [Data Access Layer - Spring Data MongoDB]
│   ├── UserRepository.java               (User CRUD + custom queries)
│   ├── FormRepository.java               (Form CRUD + custom queries)
│   ├── WorkflowRepository.java           (Workflow CRUD + custom queries)
│   ├── ProcessRepository.java            (Process CRUD + custom queries)
│   ├── WorkflowInstanceRepository.java   (Instance CRUD + custom queries)
│   ├── AuditLogRepository.java           (AuditLog CRUD + custom queries)
│   ├── RoleRepository.java               (Role CRUD + custom queries)
│   └── TenantRepository.java             (Tenant CRUD + custom queries)
│
├── response/                             [API Response Wrappers]
│   └── ApiResponse.java                  (Standardized API response<T>)
│
├── service/                              [Service Interfaces]
│   ├── AuthenticationService.java        (Signup & Login)
│   ├── FormsService.java                 (Form operations)
│   ├── WorkflowService.java              (Workflow operations)
│   ├── ProcessService.java               (Process operations)
│   ├── RoleService.java                  (Role operations)
│   ├── TenantService.java                (Tenant operations)
│   ├── RestheartService.java             (RestHeart HTTP client)
│   └── impl/                             [Service Implementations]
│       ├── AuthenticationServiceImpl.java (Signup & Login implementation)
│       ├── FormsServiceImpl.java          (Form CRUD implementation)
│       ├── WorkflowServiceImpl.java       (Workflow CRUD implementation)
│       ├── ProcessServiceImpl.java        (Process CRUD implementation)
│       ├── RoleServiceImpl.java           (Role operations implementation)
│       ├── TenantServiceImpl.java         (Tenant operations implementation)
│       └── RestheartServiceImpl.java      (RestHeart API wrapper)
│
└── utilService/                          [Utility Services]
    ├── JwtService.java                   (JWT generation & validation)
    ├── UserService.java                  (User details service for Spring)
    ├── SecurityUser.java                 (Custom UserDetails implementation)
    ├── SecurityUtils.java                (Tenant & auth info extraction)
    └── EncryptionUtil.java               (Encryption utilities)
```

---

## Core Concepts

### 1. Multi-Tenancy
Each organization (tenant) has isolated data within the same system.

```
Database Structure:
- All collections have 'tenantId' field
- Every query filters by tenantId
- No cross-tenant data access possible
```

### 2. Workflow Management
A workflow is a series of processes that forms (documents) move through.

```
Workflow Structure:
Workflow
├── workflowId (unique)
├── name (display name)
├── description
├── status (active/inactive)
├── processId[] (list of process IDs)
├── metadata (timestamps, created by, etc)
└── tenantId (tenant isolation)
```

### 3. Forms
Digital forms that users fill out to initiate workflows.

```
Form Structure:
Form
├── formId (unique)
├── name (display name)
├── description
├── fields[] (form field definitions)
├── status (published/draft)
├── metadata (timestamps, versions)
└── tenantId (tenant isolation)
```

### 4. Processes
Steps within a workflow that need to be completed.

```
Process Structure:
Process
├── processId (unique)
├── workflowId (which workflow)
├── processName
├── sequence (order of execution)
├── processType (approval, notification, etc)
├── executionPattern (sequential or parallel)
├── processSteps[] (substeps)
├── assignedRoles[] (who can execute)
├── assignedUsers[] (specific users)
└── tenantId (tenant isolation)
```

### 5. Workflow Instances
Individual executions of a workflow.

```
WorkflowInstance Structure:
Instance
├── instanceId (unique)
├── workflowId (which workflow)
├── properties (data filled in the form)
├── currentStepId (where in the process)
├── status (in-progress, completed, rejected)
├── processId[] (processes involved)
└── tenantId (tenant isolation)
```

### 6. JWT Authentication
Stateless authentication using JSON Web Tokens.

```
JWT Structure:
Header: {alg: HS256}
Payload: {
  sub: "user@company.com",
  tenantId: "bank-abc",
  authorities: ["READ_FORM", "CREATE_FORM"],
  roles: ["ADMIN", "USER"]
}
Signature: HMAC(secret)
```

---

## Data Models

### User Model
```java
@Document("users")
public class Users {
    @Id private String id;
    private String name;
    private String email;
    private String password;         // BCrypt encrypted
    private List<String> roles;      // ["ADMIN", "USER", "APPROVER"]
    private String tenantId;         // For multi-tenancy
}
```

### Workflow Model
```java
@Document("workflows")
public class Workflow {
    @Id private String id;           // MongoDB ID
    @Indexed private String tenantId;
    private String workflowId;       // Business ID
    private String name;
    private String description;
    private String status;
    private String version;
    private List<String> processId;  // References to Process IDs
    private WorkflowMetadata metadata;
}
```

### Form Model
```java
@Document("forms")
public class Forms {
    @Id private String id;           // MongoDB ID
    private String tenantId;         // For tenant isolation
    private String formId;           // Business ID
    private String name;
    private String description;
    private List<FormField> fields;  // Form field definitions
    private String status;           // "published" or "draft"
    private FormMetadata metadata;
}
```

### Process Model
```java
@Document("process")
public class Process {
    @Id private String id;
    @Indexed private String tenantId;
    @Indexed private String processId;
    private String workflowId;
    private String processName;
    private Integer sequence;
    private String processType;
    private String executionPattern;    // "sequential" or "parallel"
    private List<String> assignedRoles;
    private List<String> assignedUsers;
    private List<Steps> processSteps;
}
```

### Workflow Instance Model
```java
@Document("workflowInstances")
public class WorkflowInstance {
    @Id private String id;
    private String instanceId;       // Unique instance ID
    private String workflowId;       // Which workflow
    private String tenantId;         // Tenant isolation
    private Map<String, Object> properties;  // Form data
    private List<String> processId;
    private String status;           // "in-progress", "completed"
    private String currentStepId;
    private InstanceMetadata metadata;
}
```

### User Credentials Flow
```
When User Logs In:
1. System finds user by email in database
2. Compares password with BCrypt hash
3. Extracts user's tenantId from database
4. Creates JWT with tenantId claim
5. JWT is sent to client
6. Client includes JWT in Authorization header for all requests
```

---

## API Endpoints

### Authentication Endpoints

#### POST /api/login
```json
Request:
{
  "email": "user@company.com",
  "password": "hashedPassword123"
}

Response:
{
  "status": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

#### POST /api/register
```json
Request:
{
  "name": "John Doe",
  "email": "john@company.com",
  "password": "securePassword123",
  "tenantId": "bank-abc"
}

Response:
{
  "status": "success",
  "message": "User registered successfully"
}
```

### Forms Endpoints

#### POST /api/forms
```
Headers: Authorization: Bearer JWT_TOKEN
Body: CreateFormDTO

Creates a form for the authenticated user's tenant
```

#### GET /api/forms/{id}
```
Headers: Authorization: Bearer JWT_TOKEN

Returns form only if it belongs to user's tenant
```

#### PUT /api/forms/{id}
```
Headers: Authorization: Bearer JWT_TOKEN
Body: JSON patch payload

Updates form if it belongs to user's tenant
```

#### DELETE /api/forms/{id}
```
Headers: Authorization: Bearer JWT_TOKEN

Deletes form if it belongs to user's tenant
```

### Workflow Endpoints

#### POST /api/workflows
```
Headers: Authorization: Bearer JWT_TOKEN
Body: CreateWorkflowDTO

Creates workflow for authenticated user's tenant
```

#### GET /api/workflows
```
Headers: Authorization: Bearer JWT_TOKEN

Returns workflows for user's tenant only
```

#### PUT /api/workflows/{id}
```
Headers: Authorization: Bearer JWT_TOKEN
Body: WorkflowDTO

Updates workflow if it belongs to user's tenant
```

#### DELETE /api/workflows/{id}
```
Headers: Authorization: Bearer JWT_TOKEN

Deletes workflow if it belongs to user's tenant
```

### Process Endpoints

#### POST /api/processes
```
Headers: Authorization: Bearer JWT_TOKEN
Body: ProcessDTO

Creates process for user's tenant
```

#### GET /api/processes/{id}
```
Headers: Authorization: Bearer JWT_TOKEN

Returns process if it belongs to user's tenant
```

#### PUT /api/processes/{id}
```
Headers: Authorization: Bearer JWT_TOKEN
Body: ProcessDTO

Updates process if it belongs to user's tenant
```

#### DELETE /api/processes/{id}
```
Headers: Authorization: Bearer JWT_TOKEN

Deletes process if it belongs to user's tenant
```

### Tenant Endpoints

#### POST /api/tenants
```
Admin only - Creates new tenant organization
```

#### GET /api/tenants
```
Admin only - Lists all tenants
```

### Role Endpoints

#### POST /api/admin/roles
```
Admin only - Creates new role
```

---

## Authentication & Security

### JWT Authentication Flow

```
1. Client sends credentials to /api/login
   └─ POST /api/login with email & password

2. AuthenticationService validates credentials
   └─ Finds user by email in database
   └─ Compares password with stored hash (BCrypt)
   └─ Extracts user's tenantId

3. JwtService generates JWT
   └─ Includes: email, tenantId, authorities, roles
   └─ Signed with secret key (HS256)

4. JWT returned to client
   └─ Client stores JWT in localStorage or cookies

5. Client includes JWT in Authorization header
   └─ Authorization: Bearer eyJhbGc...

6. JwtAuthenticationFilter processes JWT
   └─ Validates signature
   └─ Checks expiration
   └─ Extracts claims
   └─ Creates SecurityUser object
   └─ Stores in SecurityContext

7. Request reaches controller
   └─ SecurityContext provides authenticated user info
   └─ Tenant automatically isolated

8. Service layer uses SecurityUtils
   └─ Gets tenantId from SecurityContext
   └─ Applies tenant filter to all queries
```

### Security Config (@EnableWebSecurity)

```java
1. CORS Configuration
   └─ Allows cross-origin requests

2. CSRF Protection
   └─ Disabled for API (stateless JWT)

3. Session Management
   └─ STATELESS (no server sessions)

4. Authentication Provider
   └─ DaoAuthenticationProvider
   └─ Uses UserService for user details
   └─ Validates passwords with PasswordEncoder (BCrypt)

5. JWT Filter
   └─ Added before UsernamePasswordAuthenticationFilter
   └─ Processes Authorization header

6. Exception Handlers
   └─ JwtAuthenticationEntryPoint (401)
   └─ JwtAccessDeniedHandler (403)

7. Public Routes (No Auth Required)
   └─ Swagger UI (/swagger-ui/**, /v3/api-docs/**)
   └─ Login endpoint (/api/login)
   └─ Register endpoint (/api/register)

8. Protected Routes (Auth Required)
   └─ All other /api/** endpoints
```

---

## Service Layer

### AuthenticationService
```
Responsibility: Handle user signup & login

Methods:
- signup(SignUpRequest)
  └─ Creates new user account
  └─ Sets tenantId (from request)
  └─ Encrypts password with BCrypt
  └─ Saves to database

- login(SignInRequest)
  └─ Finds user by email
  └─ Validates password
  └─ Generates JWT with tenantId
  └─ Returns token to client
```

### FormsService
```
Responsibility: CRUD operations on Forms

Methods:
- createForms(CreateFormDTO)
  └─ Validates input
  └─ Gets tenantId from SecurityUtils
  └─ Creates Form entity
  └─ Saves to MongoDB via RestHeart

- getFormsByFormId(String formId)
  └─ Gets tenantId from SecurityUtils
  └─ Queries: {tenantId, formId}
  └─ Returns form or 404

- updateForm(String payload, String formId)
  └─ Gets tenantId from SecurityUtils
  └─ Verifies ownership (tenantId matches)
  └─ Applies JSON patch
  └─ Saves changes

- deleteForms(String formId)
  └─ Gets tenantId from SecurityUtils
  └─ Verifies ownership
  └─ Deletes from database

- getAllForms()
  └─ Gets tenantId from SecurityUtils
  └─ Queries: {tenantId}
  └─ Returns all forms for tenant
```

### WorkflowService
```
Responsibility: CRUD operations on Workflows

Methods:
- createWorkflow(CreateWorkflowDTO)
  └─ Gets tenantId from SecurityUtils
  └─ Checks if workflow ID already exists (with tenant filter)
  └─ Creates Workflow entity
  └─ Saves to MongoDB

Similar operations for read, update, delete
```

### ProcessService
```
Responsibility: CRUD operations on Processes

Methods:
- createProcess(ProcessDTO)
  └─ Gets tenantId from SecurityUtils
  └─ Validates steps
  └─ Creates Process entity
  └─ Saves to MongoDB

Similar operations for read, update, delete
```

### RestheartService
```
Responsibility: Communicate with MongoDB via RestHeart

Methods:
- create(collection, entity, entityType)
  └─ HTTP POST to RestHeart
  └─ Creates document in collection

- getWithFilter(collection, filter)
  └─ HTTP GET with filter query
  └─ Returns matching documents

- upsert(collection, id, entity, entityType)
  └─ HTTP PUT
  └─ Updates or creates document

- delete(collection, id)
  └─ HTTP DELETE
  └─ Deletes document

- getAll(collection)
  └─ HTTP GET
  └─ Returns all documents (rarely used - usually filtered)
```

---

## Database Design

### MongoDB Collections

```
1. users
   {
     _id: ObjectId,
     name: String,
     email: String (unique),
     password: String (BCrypt),
     roles: [String],
     tenantId: String (indexed)
   }

2. forms
   {
     _id: ObjectId,
     tenantId: String (indexed),
     formId: String,
     name: String,
     description: String,
     fields: [{fieldName, fieldType, required}],
     status: String,
     metadata: {createdBy, createdAt, updatedAt}
   }

3. workflows
   {
     _id: ObjectId,
     tenantId: String (indexed),
     workflowId: String (indexed),
     name: String,
     description: String,
     status: String,
     processId: [String],
     metadata: {createdBy, createdAt, updatedAt}
   }

4. process
   {
     _id: ObjectId,
     tenantId: String (indexed),
     processId: String (indexed),
     workflowId: String,
     processName: String,
     sequence: Number,
     processType: String,
     executionPattern: String,
     assignedRoles: [String],
     assignedUsers: [String],
     processSteps: [{stepId, stepName, action}]
   }

5. workflowInstances
   {
     _id: ObjectId,
     instanceId: String,
     workflowId: String,
     tenantId: String (indexed),
     properties: {formData},
     processId: [String],
     status: String,
     currentStepId: String
   }

6. auditLogs
   {
     _id: ObjectId,
     tenantId: String (indexed),
     action: String,
     userId: String,
     details: String,
     ipAddress: String,
     timestamp: Date
   }

7. roles
   {
     _id: ObjectId,
     name: String,
     authorities: [String]
   }

8. tenants
   {
     _id: ObjectId,
     tenantId: String (unique),
     name: String,
     domain: String,
     status: String,
     config: {...},
     contactInfo: {...}
   }
```

### Indexing Strategy
```
Primary indexes for performance:
- users.email (for fast user lookups)
- forms.tenantId + formId (for tenant isolation)
- workflows.tenantId + workflowId (for tenant isolation)
- process.tenantId + processId (for tenant isolation)
- workflowInstances.tenantId (for tenant data)
- auditLogs.tenantId + timestamp (for audit trails)
```

---

## Configuration

### Application.yaml
```yaml
Server Configuration:
- port: 8080
- servlet.context-path: /

Spring Data MongoDB:
- host: localhost
- port: 27017
- database: workflow_db
- auto-index-creation: true

JWT Configuration:
- secret.key: (loaded from application.yaml)
- Expiration: 15 minutes
- Algorithm: HS256 (HMAC SHA256)

CORS Configuration:
- allowedOrigins: http://localhost:3000
- allowedMethods: GET, POST, PUT, DELETE
- allowedHeaders: *
- credentials: true

Logging:
- Spring Security: DEBUG
- Application: INFO
```

### ModelMapper Config
```
Purpose: Map DTOs to Entities automatically

Example:
CreateFormDTO → Forms entity
CreateWorkflowDTO → Workflow entity
ProcessDTO → Process entity
```

### MongoConfig
```
Purpose: Configure MongoDB connection

Settings:
- Connection pooling
- Replica set support
- Transaction support
```

### OpenAPI/Swagger Config
```
Purpose: Auto-generate API documentation

Features:
- Groups APIs by controller
- Shows request/response models
- Lists all endpoints
- Interactive "Try it out" feature
- Available at: /swagger-ui.html
```

---

## Multi-Tenancy Implementation

### Core Principle
**TenantId is extracted from JWT token and used to filter every database query.**

### Implementation Details

#### 1. SecurityUtils (Tenant Resolver)
```java
public class SecurityUtils {
    public static String getTenantId() {
        // Gets tenantId from JWT claims in SecurityContext
        // Called by every service method
        // Throws 401 if unauthenticated
    }
}
```

#### 2. Every DTO removes tenantId field
```java
// ❌ Before (UNSAFE - client could spoof)
public class CreateFormDTO {
    private String tenantId;  // Removed!
}

// ✅ After (SECURE - client cannot provide)
public class CreateFormDTO {
    private String formId;    // Only business fields
}
```

#### 3. Service layer enforces isolation
```java
public ApiResponse<String> createForms(CreateFormDTO formsDTO) {
    // ✅ Get tenant from JWT, not from DTO
    String tenantId = SecurityUtils.getTenantId();
    
    Forms form = Forms.builder()
        .tenantId(tenantId)  // Set from JWT
        .formId(formsDTO.getFormId())
        .build();
}
```

#### 4. Database queries include tenant filter
```java
private FormsDTO fetchFormDTO(String formId, String tenantId) {
    // ✅ Query includes BOTH tenant AND business ID
    Map<String, Object> filter = new HashMap<>();
    filter.put("tenantId", tenantId);     // Tenant isolation
    filter.put("formId", formId);         // Business logic
    
    // MongoDB: db.forms.findOne({tenantId, formId})
}
```

### Four Security Layers
```
Layer 1: JWT → TenantId cryptographically signed
Layer 2: SecurityContext → TenantId immutable per request
Layer 3: SecurityUtils → Fail-fast if unauthenticated
Layer 4: Database Query → {tenantId, businessId} filter
```

---

## Common Utilities

### ResponseUtil
```java
Purpose: Standardized API response wrapper

Methods:
- getResponse(data, message)
  └─ Returns: {status: "success", data, message}

- getResponseMessage(message)
  └─ Returns: {status: "success", message}

- getError(exception)
  └─ Returns: {status: "error", message, status}
```

### PagebleObject
```java
Purpose: DTO-Entity conversion & pagination

Methods:
- map(source, targetType)
  └─ Converts DTO to Entity or vice versa

- mapList(sourceList, targetType)
  └─ Converts list of DTOs to Entities

- convertValue(value, targetType)
  └─ Generic object conversion

- getJsonNode(payload)
  └─ Parses JSON string to JsonNode
```

### ValidationUtil
```java
Purpose: Input validation

Methods:
- validate(object)
  └─ Validates bean constraints
  └─ Throws ConstraintViolationException if invalid

- validateFields(object)
  └─ Validates individual fields
```

### SecurityUser (Custom UserDetails)
```java
Extends: org.springframework.security.core.userdetails.UserDetails

Fields:
- user: Users entity
- authorities: List<GrantedAuthority>
- tenantId: String (extracted from user)

Purpose: Store authenticated user info + tenant in SecurityContext
```

### EncryptionUtil
```java
Purpose: Password encryption & verification

Methods:
- encode(password)
  └─ BCrypt hashing

- matches(rawPassword, encodedPassword)
  └─ Compare for password verification
```

---

## Error Handling

### Custom Exception: RestApiException
```java
public class RestApiException extends RuntimeException {
    private HttpStatus status;
    private String message;
    
    Constructor:
    RestApiException(String message, HttpStatus status)
}
```

### Global Exception Handler: GlobalExceptionHandler
```java
@ControllerAdvice
Purpose: Centralized error handling

Methods:
- handleRestApiException()
  └─ Catches RestApiException
  └─ Returns: {status: "error", message, statusCode}

- handleConstraintViolationException()
  └─ Catches validation errors
  └─ Returns: {status: "error", violations}

- handleGenericException()
  └─ Catches all other exceptions
  └─ Returns: {status: "error", message}
```

### HTTP Status Codes Used
```
200 OK                → Successful GET/PUT/DELETE
201 Created           → Successful POST (resource created)
400 Bad Request       → Invalid input (validation error)
401 Unauthorized      → Missing or invalid JWT
403 Forbidden         → User lacks required authority
404 Not Found         → Resource doesn't exist (or cross-tenant)
500 Internal Server Error → Unexpected server error
```

---

## How Everything Works Together

### Complete Request-Response Cycle

#### Scenario: User Creates a Form

```
1. CLIENT
   └─ Sends HTTP request:
      POST /api/forms
      Headers: Authorization: Bearer eyJhbGc...
      Body: {formId: "loan-app", name: "Loan Application"}

2. TOMCAT/SERVLET LAYER
   └─ Receives HTTP request
   └─ Routes to FormsController

3. JWT AUTHENTICATION FILTER (JwtAuthenticationFilter)
   ├─ Extracts JWT from Authorization header
   ├─ Validates signature against secret key
   ├─ Checks expiration
   ├─ Extracts claims: {email, tenantId, authorities}
   ├─ Creates SecurityUser object
   │  └─ Contains: email, tenantId, authorities
   ├─ Stores in SecurityContext
   └─ Allows request to proceed

4. SPRING SECURITY
   └─ Checks @PreAuthorize("hasAuthority('CREATE_FORM')")
   └─ Verifies user has required authority
   └─ Allows request if authorized

5. CONTROLLER (FormsController)
   ├─ Receives HTTP request
   ├─ Converts JSON to CreateFormDTO
   ├─ Validates CreateFormDTO (@Valid)
   └─ Calls: formsService.createForms(formsDTO)

6. SERVICE LAYER (FormsServiceImpl)
   ├─ Validates input: validationUtil.validate(formsDTO)
   ├─ Gets tenant from JWT: String tenantId = SecurityUtils.getTenantId()
   │  └─ Reads from SecurityContext (set by filter)
   │  └─ Returns: "bank-abc"
   ├─ Maps FormFieldDTOs to FormField entities
   ├─ Builds Forms entity:
   │  Forms form = Forms.builder()
   │    .tenantId(tenantId)      // ← From JWT, NOT from DTO
   │    .formId(formsDTO.getFormId())
   │    .name(formsDTO.getName())
   │    ...
   │    .build()
   └─ Calls: restHeartService.create(...)

7. RESTEART SERVICE (RestheartService)
   ├─ Builds HTTP POST request to RestHeart API
   ├─ URL: http://localhost:8081/db/workflow_db/forms
   ├─ Body: JSON serialization of Forms entity
   └─ Sends request

8. MONGODB (via RestHeart)
   ├─ Receives document:
   │  {
   │    _id: ObjectId(),
   │    tenantId: "bank-abc",    // ← Automatically set
   │    formId: "loan-app",
   │    name: "Loan Application",
   │    ...
   │  }
   ├─ Validates schema
   ├─ Inserts into "forms" collection
   ├─ Returns success response

9. SERVICE RETURNS (FormsServiceImpl)
   ├─ RestHeartService returns saved form
   ├─ Service returns: ApiResponse("Form created successfully")

10. CONTROLLER RETURNS (FormsController)
    ├─ Returns ResponseEntity with:
    │  - Status: 201 CREATED
    │  - Body: {status: "success", message: "Form created successfully"}

11. RESPONSE SENT TO CLIENT
    ├─ HTTP Status: 201
    ├─ Body: {status: "success", message: "Form created successfully"}
    └─ TenantId automatically set to "bank-abc" in database

RESULT:
✅ Form created with tenantId="bank-abc"
✅ Only user from bank-abc can read it (queries filtered by tenantId)
✅ Complete tenant isolation maintained
```

### Another Scenario: User Attempts Cross-Tenant Read

```
1. CLIENT (from Tenant B)
   └─ Sends: GET /api/forms/loan-app
      Authorization: Bearer JWT_WITH_TENANTID_bank-xyz

2. FILTER & SECURITY
   ├─ Validates JWT
   ├─ Extracts: tenantId = "bank-xyz"
   ├─ Creates SecurityContext with tenantId="bank-xyz"

3. SERVICE LAYER
   ├─ Gets tenantId from SecurityUtils
   │  └─ Returns: "bank-xyz"
   ├─ Calls: fetchFormDTO("loan-app", "bank-xyz")

4. DATABASE QUERY
   ├─ Filter: {tenantId: "bank-xyz", formId: "loan-app"}
   ├─ MongoDB searches for matching document
   ├─ Form with formId="loan-app" has tenantId="bank-abc"
   ├─ Query returns: null (no match)

5. SERVICE RESPONSE
   ├─ Checks if form is null
   ├─ Throws: RestApiException("Form not found", 404)

6. EXCEPTION HANDLER
   ├─ Catches RestApiException
   ├─ Returns: {status: "error", message: "Form not found", statusCode: 404}

RESULT:
✅ Even though form exists in database
✅ User from bank-xyz cannot access it
✅ Gets 404 (same as if form doesn't exist)
✅ No indication that form belongs to different tenant
✅ Complete tenant isolation maintained
```

---

## Database Relationships

```
Users
  ↓ (has)
Roles
  ↓ (has)
Authorities (Permissions)

Tenant
  ↓ (owns)
Users
  ↓ (creates)
Forms
  ↓ (uses)
Workflows
  ↓ (contains)
Processes
  ↓ (executes)
WorkflowInstances

Workflows
  ↓ (contains)
Forms

Forms
  ↓ (has)
FormFields

Processes
  ↓ (has)
Steps

WorkflowInstances
  ↓ (contains)
AuditLogs
```

---

## Key Design Patterns

### 1. Service-Layer Pattern
- Controllers delegate to Services
- Services contain business logic
- Services use Repositories for data access

### 2. DTO Pattern
- Controllers receive DTOs (not entities)
- DTOs only contain necessary fields for API
- Prevents exposing internal structure

### 3. Repository Pattern
- Data access abstraction
- Spring Data MongoDB handles implementation
- Repositories return entities (not DTOs)

### 4. Mapper Pattern
- DTOs ↔ Entities conversion
- ModelMapper handles automatic mapping
- Services use mappers for transformation

### 5. Filter Pattern
- JwtAuthenticationFilter intercepts requests
- Extracts JWT and sets SecurityContext
- Makes authentication transparent to controllers

### 6. Exception Handler Pattern
- @ControllerAdvice for centralized error handling
- Exceptions converted to API responses
- Consistent error format across all endpoints

### 7. Reactive Pattern
- Spring WebFlux for non-blocking I/O
- RestheartService uses WebClient (async HTTP)
- Improves scalability

---

## Security Best Practices Implemented

1. ✅ **Password Encryption** - BCrypt hashing
2. ✅ **JWT Signing** - HS256 signature verification
3. ✅ **Tenant Isolation** - Every query filters by tenantId
4. ✅ **CORS** - Configured for safe cross-origin requests
5. ✅ **CSRF Protection** - Disabled for stateless API (appropriate)
6. ✅ **Authority-Based Access Control** - @PreAuthorize annotations
7. ✅ **Stateless Sessions** - No server-side session storage
8. ✅ **Secure Headers** - HSTS, X-Content-Type-Options, etc.
9. ✅ **Input Validation** - Bean validation on all DTOs
10. ✅ **Error Messages** - Generic messages (don't leak info)

---

## Performance Considerations

1. **Indexing** - Indexed fields for fast queries
2. **Connection Pooling** - MongoDB connection pool
3. **Reactive Programming** - Non-blocking I/O
4. **Caching** - (Can be added with @Cacheable)
5. **Pagination** - (Can be implemented with PagebleObject)
6. **Query Filtering** - Only retrieve necessary fields

---

## Deployment Architecture

```
┌─────────────────┐
│   Load Balancer │
└────────┬────────┘
         │
    ┌────┴─────┐
    │           │
┌───▼──┐    ┌──▼───┐
│ App  │    │ App  │  (Instances)
│ Pod1 │    │ Pod2 │  (Horizontally scalable)
└───┬──┘    └──┬───┘
    │           │
    └─────┬─────┘
          │
    ┌─────▼──────┐
    │  MongoDB   │  (Shared database)
    │  Replica   │  (High availability)
    │  Set       │
    └────────────┘
```

---

## Quick Reference: File Location Purposes

| File | Purpose | When to Edit |
|------|---------|--------------|
| SecurityConfig.java | Authentication setup | Add new auth rules |
| JwtService.java | JWT generation/validation | Adjust expiration |
| FormsServiceImpl.java | Form business logic | Modify form operations |
| FormsController.java | HTTP endpoints | Add new endpoints |
| Forms.java | Form database model | Add form fields |
| CreateFormDTO.java | Form creation request | Add input fields |
| FormRepository.java | Form data access | Add custom queries |
| GlobalExceptionHandler.java | Error responses | Add custom exceptions |
| SecurityUtils.java | Tenant resolution | Modify tenant logic |

---

## Summary

This is a **multi-tenant workflow management system** where:

1. **Each organization (tenant)** is completely isolated
2. **Users authenticate** with email/password → get JWT with tenantId
3. **Every API request** includes JWT in Authorization header
4. **Every database query** is filtered by tenantId automatically
5. **Forms** are digital documents users fill
6. **Workflows** are processes these forms go through
7. **Processes** are steps within workflows
8. **Instances** are individual executions of workflows

**Security is enforced at 4 levels:**
- JWT authentication (cryptographically signed)
- Request context (SecurityContext with tenantId)
- Service layer (SecurityUtils tenant resolution)
- Database queries (tenantId filter on every query)

**Key principle:** *TenantId comes from JWT only, never from client input.*

---

This document provides complete understanding for any AI or developer to work with the codebase effectively.
