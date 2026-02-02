# Multi-Tenancy Implementation - Guidelines for Remaining Services

## Quick Refactoring Checklist for Any Service

Use this checklist when refactoring any remaining service to ensure tenant isolation.

### Step 1: Add Import
```java
import com.example.backend.utilService.SecurityUtils;
```

### Step 2: Identify All Public Methods
For each public method that modifies or reads data:
```java
// GET - Single entity
public ApiResponse<FormsDTO> getFormsByFormId(String formId) { }

// GET - All entities
public ApiResponse<List<FormsDTO>> getAllForms() { }

// POST - Create
public ApiResponse<String> createForms(CreateFormDTO formsDTO) { }

// PUT - Update
public ApiResponse<String> updateForm(String payload, String formId) { }

// DELETE - Delete
public ApiResponse<String> deleteForms(String formId) { }
```

### Step 3: For Each Public Method

#### 3a. Get Tenant ID
```java
String tenantId = SecurityUtils.getTenantId();
```

#### 3b. Update Method Signature of Helper Methods
Add `tenantId` parameter:
```java
// Before
private Forms findFormByIdOrThrow(String formId)

// After  
private Forms findFormByIdOrThrow(String formId, String tenantId)
```

#### 3c. Update Filter Creation
```java
// Before - UNSAFE
private Map<String, Object> createFormIdFilter(String formId) {
    return Map.of(FORM_ID_FIELD, formId);
}

// After - SECURE
private Map<String, Object> createFormFilter(String formId, String tenantId) {
    Map<String, Object> filter = new HashMap<>();
    filter.put("tenantId", tenantId);
    filter.put(FORM_ID_FIELD, formId);
    return filter;
}
```

#### 3d. Update Entity Builders
```java
// Before - UNSAFE (gets from DTO)
private Forms buildFormFromDTO(CreateFormDTO dto, List<FormField> validatedFields) {
    return Forms.builder()
        .tenantId(dto.getTenantId())  // ❌
        ...
}

// After - SECURE (gets from parameter)
private Forms buildFormFromDTO(CreateFormDTO dto, String tenantId, List<FormField> validatedFields) {
    return Forms.builder()
        .tenantId(tenantId)  // ✅ From SecurityUtils
        ...
}
```

### Step 4: Remove TenantId from Request DTOs

For any DTO used in @RequestBody:

```java
// Before - UNSAFE
public class CreateFormDTO {
    @NotEmpty(message = "Tenant id is required")
    private String tenantId;  // ❌ DELETE THIS
    
    @NotEmpty(message = "form-id cannot be null")
    private String formId;
    ...
}

// After - SECURE
public class CreateFormDTO {
    @NotEmpty(message = "form-id cannot be null")
    private String formId;  // ✅ Keep business ID
    ...
}
```

**Note**: Keep tenantId in Response DTOs - they return data to client.

---

## Service-by-Service Guide

### RoleServiceImpl (Global Roles - May Not Need Refactoring)

**Assessment**: Roles appear to be global, not tenant-specific.

**Decision**: 
- If roles are global: No tenant filtering needed
- If roles should be tenant-specific: Add tenantId to Role model and refactor

**Current Code**:
```java
public ApiResponse<String> createRole(RoleDTO roleDTO) {
    Map<String, Object> filter = Map.of("name", roleDTO.getName());
    // Checks if roles with same name exists globally (correct for shared roles)
}
```

**Recommendation**: Leave as-is for now (roles are global)

---

### TenantServiceImpl (Admin Tenant Management)

**Assessment**: Manages tenant registrations, typically admin-only.

**Current Code**:
```java
@Override
public ApiResponse<String> createTenant(TenantDTO requestTenantDTO) {
    Tenant tenant = pagebleObject.map(requestTenantDTO, Tenant.class);
    tenantRepository.save(tenant);
    return ResponseUtil.getResponseMessage("Tenant created");
}
```

**Recommendation**: 
- Keep as-is (tenant creation is admin-only, outside normal multi-tenant scope)
- Consider adding audit logging

---

### AuthenticationServiceImpl (Login/Signup)

**Assessment**: Handles JWT generation, must ensure tenant is embedded in token.

**What to Verify**:
```java
// In signup
@Override
public ApiResponse<String> signup(SignUpRequest signUpRequest) {
    Users user = pagebleObject.map(signUpRequest, Users.class);
    
    // ✅ MUST SET TENANT HERE
    user.setTenantId(SecurityUtils.getTenantId());  // Should be set during registration
    
    userRepository.save(user);
}

// In login
@Override
public ApiResponse<Map<String, String>> login(SignInRequest signInRequest) {
    Users user = userRepository.findByEmail(signInRequest.getEmail());
    
    if (user == null || !passwordEncoder.matches(...)) {
        throw new BadCredentialsException("Invalid credentials");
    }
    
    // ✅ JWT must include tenant
    SecurityUser securityUser = new SecurityUser(user, user.getRoles());
    String token = jwtService.generateToken(securityUser);  // Includes tenantId
    
    return ResponseUtil.getResponse(
        Map.of("token", token),
        "Login successful"
    );
}
```

**Current State**: Verify that JwtService includes tenantId in token (should already be there)

---

### Any Other Services (If They Exist)

#### WorkflowInstanceServiceImpl
- ✅ Should filter by tenantId
- Queries: workflow instance by ID, all instances for tenant
- Updates: validate tenant ownership

#### AuditLogServiceImpl (If Exists)
- ✅ Should filter by tenantId
- Queries: logs for current tenant only
- Creation: automatically set tenantId from SecurityUtils

#### Pattern for Any Service:

```java
@Service
public class AnyServiceImpl implements AnyService {
    
    private final RestheartService restheartService;
    
    // ✅ GET single item
    public ApiResponse<AnyDTO> getById(String id) {
        String tenantId = SecurityUtils.getTenantId();
        return fetch(id, tenantId);
    }
    
    // ✅ GET all items
    public ApiResponse<List<AnyDTO>> getAll() {
        String tenantId = SecurityUtils.getTenantId();
        Map<String, Object> filter = Map.of("tenantId", tenantId);
        // Query with filter
    }
    
    // ✅ CREATE
    public ApiResponse<String> create(CreateAnyDTO dto) {
        String tenantId = SecurityUtils.getTenantId();
        Any entity = buildEntity(dto, tenantId);  // Pass tenantId
        // Save entity
    }
    
    // ✅ UPDATE
    public ApiResponse<String> update(String id, UpdateAnyDTO dto) {
        String tenantId = SecurityUtils.getTenantId();
        Any entity = findByIdOrThrow(id, tenantId);  // Verify ownership
        // Update entity
    }
    
    // ✅ DELETE
    public ApiResponse<String> delete(String id) {
        String tenantId = SecurityUtils.getTenantId();
        Any entity = findByIdOrThrow(id, tenantId);  // Verify ownership
        // Delete entity
    }
    
    // ✅ HELPER: Find with tenant check
    private Any findByIdOrThrow(String id, String tenantId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("tenantId", tenantId);
        filter.put("id", id);
        
        Any entity = restheartService
            .getWithFilter(COLLECTION, filter)
            .blockFirst();
        
        if (entity == null) {
            throw new RestApiException("Not found", HttpStatus.NOT_FOUND);
        }
        return entity;
    }
}
```

---

## Testing Your Changes

### Unit Test Template

```java
@Test
public void testCreateFormIncludesTenantId() {
    // Setup
    String tenantId = "test-tenant";
    CreateFormDTO dto = new CreateFormDTO();
    dto.setFormId("test-form");
    
    // Mock SecurityUtils
    mockStatic(SecurityUtils.class);
    when(SecurityUtils.getTenantId()).thenReturn(tenantId);
    
    // Execute
    ApiResponse<String> response = formsService.createForms(dto);
    
    // Verify tenantId was set
    ArgumentCaptor<Forms> captor = ArgumentCaptor.forClass(Forms.class);
    verify(restHeartService).create(eq("forms"), captor.capture(), eq(Forms.class));
    
    Forms savedForm = captor.getValue();
    assertThat(savedForm.getTenantId()).isEqualTo(tenantId);
}

@Test
public void testCannotAccessOtherTenantForm() {
    // Setup
    String myTenant = "tenant-a";
    String otherTenant = "tenant-b";
    
    // Mock SecurityUtils
    mockStatic(SecurityUtils.class);
    when(SecurityUtils.getTenantId()).thenReturn(myTenant);
    
    // Create form in other tenant
    Forms form = Forms.builder()
        .id("form-1")
        .tenantId(otherTenant)
        .formId("secret-form")
        .build();
    
    // Mock RestHeart to return null (form not found for my tenant)
    when(restHeartService.getWithFilter(
        eq("forms"),
        argThat(filter -> 
            filter.containsKey("tenantId") && 
            filter.get("tenantId").equals(myTenant)
        )
    )).thenReturn(Mono.empty());
    
    // Execute & Verify
    assertThrows(RestApiException.class, 
        () -> formsService.getFormsByFormId("secret-form"));
}
```

### Integration Test (With Real JWT)

```java
@Test
public void testFormCreationWithTenantFromJWT() {
    // Create JWT with tenant A
    String tokenA = jwtService.generateToken(userA);  // tenantId: "tenant-a"
    
    // Create form
    CreateFormDTO dto = new CreateFormDTO();
    dto.setFormId("test-form");
    
    ApiResponse<String> response = restTemplate
        .postForObject(
            "/api/forms",
            new HttpEntity<>(dto, headers(tokenA)),
            ApiResponse.class
        );
    
    // Verify in database
    Forms savedForm = formRepository.findByFormId("test-form");
    assertThat(savedForm.getTenantId()).isEqualTo("tenant-a");
    
    // Try to access with Token B (different tenant)
    ApiResponse response2 = restTemplate.getForObject(
        "/api/forms/test-form",
        ApiResponse.class,
        headers(tokenB)  // tokenB has tenantId: "tenant-b"
    );
    
    assertThat(response2.getStatusCode()).isEqualTo(404);  // Form not found for tenant-b
}
```

---

## Compliance Checklist for Each Service

Before submitting changes:

- [ ] SecurityUtils imported and used for all tenantId retrieval
- [ ] All request DTOs do NOT have tenantId field
- [ ] All database queries include tenantId in filter
- [ ] All entity builders set tenantId from parameter (not DTO)
- [ ] Helper methods accept tenantId as parameter
- [ ] Code compiles without errors
- [ ] Unit tests verify tenant isolation
- [ ] Integration tests verify cross-tenant prevention
- [ ] No ThreadLocal tenant references
- [ ] Documentation updated

---

## Common Mistakes to Avoid

### ❌ Mistake 1: Forgetting Tenant in Query

```java
// WRONG
private Forms findFormByIdOrThrow(String formId) {
    Map<String, Object> filter = Map.of("formId", formId);  // ❌ Missing tenant
}

// RIGHT
private Forms findFormByIdOrThrow(String formId, String tenantId) {
    Map<String, Object> filter = new HashMap<>();
    filter.put("tenantId", tenantId);  // ✅
    filter.put("formId", formId);
}
```

### ❌ Mistake 2: Getting Tenant in Helper Method

```java
// WRONG
private Forms findFormByIdOrThrow(String formId) {
    String tenantId = SecurityUtils.getTenantId();  // ❌ Called twice!
    // ...
}

// RIGHT
private Forms findFormByIdOrThrow(String formId, String tenantId) {
    // tenantId passed as parameter ✅
    // ...
}
```

### ❌ Mistake 3: Forgetting to Update DTO

```java
// WRONG
public class CreateFormDTO {
    private String tenantId;  // ❌ Still here!
}

// RIGHT
public class CreateFormDTO {
    // No tenantId field ✅
}
```

### ❌ Mistake 4: Setting Tenant from DTO

```java
// WRONG
private Forms buildFormFromDTO(CreateFormDTO dto) {
    return Forms.builder()
        .tenantId(dto.getTenantId())  // ❌ From untrusted DTO
        .build();
}

// RIGHT
private Forms buildFormFromDTO(CreateFormDTO dto, String tenantId) {
    return Forms.builder()
        .tenantId(tenantId)  // ✅ From SecurityUtils (via parameter)
        .build();
}
```

---

## Questions?

Refer to:
1. **MULTI_TENANCY_IMPLEMENTATION.md** - Overview of completed work
2. **BEFORE_AFTER_EXAMPLES.md** - Specific code examples
3. **FormsServiceImpl** - Reference implementation (fully refactored)
4. **WorkflowServiceImpl** - Reference implementation (fully refactored)
5. **ProcessServiceImpl** - Reference implementation (fully refactored)
