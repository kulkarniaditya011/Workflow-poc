# Multi-Tenancy Quick Reference Guide

## TL;DR - The Golden Rule

**Every database query must filter by tenant.**

```java
String tenantId = SecurityUtils.getTenantId();  // Get from JWT
Map<String, Object> filter = new HashMap<>();
filter.put("tenantId", tenantId);      // ALWAYS include this
filter.put("businessId", someId);      // THEN add business logic
```

---

## Copy-Paste Template for Any Service

### 1. Import SecurityUtils
```java
import com.example.backend.utilService.SecurityUtils;
```

### 2. For CREATE operations
```java
public ApiResponse<String> createEntity(CreateEntityDTO dto) {
    validationUtil.validate(dto);
    
    String tenantId = SecurityUtils.getTenantId();  // ← Get tenant
    
    Entity entity = Entity.builder()
        .tenantId(tenantId)              // ← Set tenant
        .businessId(dto.getBusinessId())
        .build();
    
    // Save entity
    return ResponseUtil.getResponseMessage("Created");
}
```

### 3. For READ operations
```java
public ApiResponse<EntityDTO> getById(String id) {
    String tenantId = SecurityUtils.getTenantId();  // ← Get tenant
    
    Entity entity = findByIdOrThrow(id, tenantId);  // ← Pass tenant
    
    return ResponseUtil.getResponse(entity, "Found");
}

// Helper method
private Entity findByIdOrThrow(String id, String tenantId) {
    Map<String, Object> filter = new HashMap<>();
    filter.put("tenantId", tenantId);  // ← Include tenant
    filter.put("id", id);
    
    Entity entity = service.getWithFilter(COLLECTION, filter).blockFirst();
    
    if (entity == null) {
        throw new RestApiException("Not found", HttpStatus.NOT_FOUND);
    }
    return entity;
}
```

### 4. For UPDATE operations
```java
public ApiResponse<String> update(String id, UpdateEntityDTO dto) {
    String tenantId = SecurityUtils.getTenantId();  // ← Get tenant
    
    Entity entity = findByIdOrThrow(id, tenantId);  // ← Verify tenant
    
    entity.setName(dto.getName());
    
    // Save changes
    return ResponseUtil.getResponseMessage("Updated");
}
```

### 5. For DELETE operations
```java
public ApiResponse<String> delete(String id) {
    String tenantId = SecurityUtils.getTenantId();  // ← Get tenant
    
    Entity entity = findByIdOrThrow(id, tenantId);  // ← Verify tenant
    
    // Delete entity
    return ResponseUtil.getResponseMessage("Deleted");
}
```

---

## DTO Checklist

### Request DTOs (@RequestBody)
```java
public class CreateFormDTO {
    // ❌ NO tenantId field
    
    @NotEmpty
    private String formId;  // ✅ Business ID only
    
    private String name;
    // ... other fields
}
```

### Response DTOs (returned to client)
```java
public class FormResponseDTO {
    // ✅ Include tenantId (for reference)
    private String tenantId;
    
    private String formId;
    private String name;
    // ... other fields
}
```

---

## Working Examples (Real from Codebase)

### FormsServiceImpl - Full Example
```java
@Service
public class FormsServiceImpl implements FormsService {
    
    @Override
    public ApiResponse<String> createForms(CreateFormDTO formsDTO) {
        validationUtil.validate(formsDTO);
        
        String tenantId = SecurityUtils.getTenantId();  // ✅ Get tenant
        
        Forms form = buildFormFromDTO(formsDTO, tenantId, validatedFields);
        
        restHeartService.create(FORMS_COLLECTION, form, Forms.class).block();
        
        return ResponseUtil.getResponseMessage("Form created successfully");
    }
    
    @Override
    public ApiResponse<FormsDTO> getFormsByFormId(String formId) {
        String tenantId = SecurityUtils.getTenantId();  // ✅ Get tenant
        
        FormsDTO form = fetchFormDTO(formId, tenantId);  // ✅ Pass tenant
        
        return ResponseUtil.getResponse(form, "Form retrieved");
    }
    
    private FormsDTO fetchFormDTO(String formId, String tenantId) {
        Map<String, Object> filter = createFormFilter(formId, tenantId);  // ✅ Composite filter
        
        FormsDTO form = restHeartService
            .getWithFilter(FORMS_COLLECTION, filter)
            .blockFirst();
        
        if (form == null) {
            throw new RestApiException("Form not found", HttpStatus.NOT_FOUND);
        }
        return form;
    }
    
    private Map<String, Object> createFormFilter(String formId, String tenantId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("tenantId", tenantId);  // ✅ Always first
        filter.put("formId", formId);      // ✅ Then business logic
        return filter;
    }
}
```

---

## Anti-Patterns (DON'T DO THIS)

### ❌ WRONG: Query without tenant
```java
// NEVER DO THIS
Map<String, Object> filter = Map.of("formId", formId);
```

### ❌ WRONG: Get tenant from DTO
```java
// NEVER DO THIS
.tenantId(dto.getTenantId())  // Client could spoof!
```

### ❌ WRONG: Accept tenantId in request
```java
// NEVER DO THIS
public class CreateFormDTO {
    private String tenantId;  // Remove this field!
}
```

### ❌ WRONG: Use ThreadLocal
```java
// NEVER DO THIS
TenantContext.getTenantId()  // Delete this class!
```

### ❌ WRONG: Skip tenant in helper
```java
// NEVER DO THIS
private Entity findByIdOrThrow(String id) {  // Missing tenantId param!
    Map<String, Object> filter = Map.of("id", id);  // No tenant filter!
}
```

---

## Testing Checklist

Before committing code:

```java
// Test 1: Create with correct tenant
@Test
public void testCreateIncludesCorrectTenant() {
    mockStatic(SecurityUtils.class);
    when(SecurityUtils.getTenantId()).thenReturn("tenant-a");
    
    formsService.createForms(dto);
    
    // Verify tenantId was set to "tenant-a"
}

// Test 2: Cannot read other tenant's data
@Test
public void testCannotReadOtherTenantData() {
    mockStatic(SecurityUtils.class);
    when(SecurityUtils.getTenantId()).thenReturn("tenant-a");
    
    // Mock service to return null for tenant-a's query
    when(service.getWithFilter(...)).thenReturn(Mono.empty());
    
    assertThrows(RestApiException.class, 
        () -> formsService.getFormsByFormId("secret-form"));
}

// Test 3: Verify filter includes tenant
@Test
public void testFilterIncludesTenant() {
    ArgumentCaptor<Map<String, Object>> captor = 
        ArgumentCaptor.forClass(Map.class);
    
    formsService.getFormsByFormId("form-1");
    
    verify(service).getWithFilter(eq("forms"), captor.capture());
    
    Map<String, Object> filter = captor.getValue();
    assertThat(filter).containsEntry("tenantId", "tenant-a");
    assertThat(filter).containsEntry("formId", "form-1");
}
```

---

## Common Pitfalls & Fixes

### Pitfall 1: Accepting tenantId in DTO

**Wrong:**
```java
public class CreateFormDTO {
    private String tenantId;  // ❌
}
```

**Right:**
```java
public class CreateFormDTO {
    // No tenantId field ✅
}
```

**Fix**: Remove tenantId from request DTOs

---

### Pitfall 2: Setting tenantId from DTO

**Wrong:**
```java
private Forms buildFormFromDTO(CreateFormDTO dto) {
    return Forms.builder()
        .tenantId(dto.getTenantId())  // ❌ From untrusted source
        .build();
}
```

**Right:**
```java
private Forms buildFormFromDTO(CreateFormDTO dto, String tenantId) {
    return Forms.builder()
        .tenantId(tenantId)  // ✅ From SecurityUtils parameter
        .build();
}
```

**Fix**: Pass tenantId as method parameter, set from SecurityUtils

---

### Pitfall 3: Query without tenant filter

**Wrong:**
```java
Map<String, Object> filter = Map.of("formId", formId);  // ❌
```

**Right:**
```java
Map<String, Object> filter = new HashMap<>();
filter.put("tenantId", tenantId);  // ✅
filter.put("formId", formId);
```

**Fix**: Always include tenantId in filter Map

---

### Pitfall 4: Calling SecurityUtils in helper method

**Wrong:**
```java
private Entity findByIdOrThrow(String id) {
    String tenantId = SecurityUtils.getTenantId();  // ❌ Called twice!
}

// Called from service:
String tenantId = SecurityUtils.getTenantId();
Entity entity = findByIdOrThrow(id);  // SecurityUtils called again
```

**Right:**
```java
private Entity findByIdOrThrow(String id, String tenantId) {
    // tenantId passed as parameter ✅
}

// Called from service:
String tenantId = SecurityUtils.getTenantId();  // Called once ✅
Entity entity = findByIdOrThrow(id, tenantId);  // Pass it
```

**Fix**: Call SecurityUtils in service, pass to helpers

---

## Database Indexes

For performance, create indexes on frequently queried fields:

```javascript
// MongoDB
db.forms.createIndex({ tenantId: 1, formId: 1 })
db.workflows.createIndex({ tenantId: 1, workflowId: 1 })
db.processes.createIndex({ tenantId: 1, processId: 1 })
db.auditLogs.createIndex({ tenantId: 1, timestamp: -1 })
```

---

## Quick Debugging

### How to verify tenant isolation is working:

**1. Check JWT claims**
```bash
# Decode JWT to see tenantId
echo $JWT_TOKEN | cut -d'.' -f2 | base64 -d | jq
# Should show: "tenantId": "your-tenant"
```

**2. Check SecurityUtils is being called**
```java
// Add logging
log.info("Accessing tenant: {}", SecurityUtils.getTenantId());
```

**3. Check database queries**
```javascript
// Monitor MongoDB
db.setProfilingLevel(1, {slowms: 100})
db.system.profile.find().sort({ts: -1}).limit(5).pretty()
// Look for tenantId in query filter
```

**4. Test cross-tenant access**
```bash
# Create form with tenant A
curl -H "Authorization: Bearer $TOKEN_A" POST /api/forms -d '{"formId": "test"}'

# Try to read with tenant B
curl -H "Authorization: Bearer $TOKEN_B" GET /api/forms/test
# Should get 404 (not 200 with form)
```

---

## Reference Documentation

1. **IMPLEMENTATION_COMPLETE.md** - Full status report
2. **BEFORE_AFTER_EXAMPLES.md** - Code examples and patterns
3. **REFACTORING_GUIDELINES.md** - Step-by-step guide
4. **FormsServiceImpl.java** - Reference implementation

---

## Key Files

| File | Purpose |
|------|---------|
| SecurityUtils.java | Tenant resolution utility |
| CreateFormDTO.java | Example: DTO without tenantId |
| FormsServiceImpl.java | Example: Fully refactored service |
| WorkflowServiceImpl.java | Example: Fully refactored service |
| ProcessServiceImpl.java | Example: Fully refactored service |

---

## Questions?

**Q: Where do I get tenantId?**  
A: `String tenantId = SecurityUtils.getTenantId();`

**Q: Where do I put tenantId in queries?**  
A: Always first in filter: `filter.put("tenantId", tenantId);`

**Q: Can users send tenantId in request?**  
A: No, remove it from DTOs. TenantId comes from JWT only.

**Q: What if user is not authenticated?**  
A: SecurityUtils throws 401 RestApiException automatically.

**Q: How do I test this?**  
A: See "Testing Checklist" section above.

---

**Last Updated**: January 23, 2026  
**Status**: ✅ Ready to use
