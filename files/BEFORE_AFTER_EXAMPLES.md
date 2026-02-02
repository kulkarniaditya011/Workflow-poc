# Multi-Tenancy Implementation - Before & After Examples

## Example 1: Creating a Form

### ❌ BEFORE (UNSAFE)

```java
// Controller
@PostMapping
public ResponseEntity<ApiResponse<String>> createForm(@Valid @RequestBody CreateFormDTO formsDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(formsService.createForms(formsDTO));
}

// DTO (UNSAFE - CLIENT CAN SPOOF TENANT)
public class CreateFormDTO {
    @NotEmpty(message = "Tenant id is required")
    private String tenantId;  // ❌ Client provides this!
    @NotEmpty(message = "form-id cannot be null")
    private String formId;
    // ... other fields
}

// Service (UNSAFE - TRUSTS CLIENT)
public ApiResponse<String> createForms(CreateFormDTO formsDTO) {
    // ❌ Takes tenantId directly from DTO
    Forms form = Forms.builder()
        .tenantId(formsDTO.getTenantId())  // SECURITY HOLE!
        .formId(formsDTO.getFormId())
        .build();
}

// Database Query (UNSAFE - NO TENANT FILTER)
filter = Map.of("formId", formId);  // ❌ Cross-tenant leakage possible
```

**Vulnerability**: 
- User A can send: `{ "tenantId": "tenant-b", "formId": "loan-form" }`
- System creates/updates form in Tenant B's collection
- **CRITICAL SECURITY BREACH**

---

### ✅ AFTER (SECURE)

```java
// Controller (NO CHANGE - Still same endpoint)
@PostMapping
public ResponseEntity<ApiResponse<String>> createForm(@Valid @RequestBody CreateFormDTO formsDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(formsService.createForms(formsDTO));
}

// DTO (SECURE - NO TENANT FIELD)
public class CreateFormDTO {
    @NotEmpty(message = "form-id cannot be null")
    private String formId;  // ✅ No tenantId field
    // ... other fields
}

// Service (SECURE - GETS TENANT FROM JWT)
public ApiResponse<String> createForms(CreateFormDTO formsDTO) {
    // ✅ TenantId comes from JWT only
    String tenantId = SecurityUtils.getTenantId();  // From JWT
    
    Forms form = Forms.builder()
        .tenantId(tenantId)  // ✅ Immutable per request
        .formId(formsDTO.getFormId())
        .build();
}

// Database Query (SECURE - INCLUDES TENANT FILTER)
filter = new HashMap<>();
filter.put("tenantId", tenantId);  // ✅ Always included
filter.put("formId", formId);
```

**Security**:
- User A cannot override tenant
- TenantId is locked to JWT claims
- Database query always filtered by tenant
- **SECURE**

---

## Example 2: Retrieving a Form

### ❌ BEFORE (UNSAFE)

```java
// Service
public ApiResponse<FormsDTO> getFormsByFormId(String formId) {
    // ❌ Query only by formId - ANY TENANT CAN READ!
    Map<String, Object> filter = Map.of("formId", formId);
    
    FormsDTO form = restHeartService
        .getWithFilter(FORMS_COLLECTION, filter)
        .map(map -> pagebleObject.convertValue(map, Forms.class))
        .map(entity -> pagebleObject.map(entity, FormsDTO.class))
        .blockFirst();
}
```

**Vulnerability**:
- Tenant A reads formId = "loan-form"
- Query returns Tenant B's "loan-form" if it exists
- **DATA LEAKAGE**

---

### ✅ AFTER (SECURE)

```java
// Service
public ApiResponse<FormsDTO> getFormsByFormId(String formId) {
    // ✅ Get tenant from JWT
    String tenantId = SecurityUtils.getTenantId();
    
    // ✅ Query includes BOTH tenantId AND formId
    FormsDTO form = fetchFormDTO(formId, tenantId);
}

// Helper
private FormsDTO fetchFormDTO(String formId, String tenantId) {
    // ✅ Composite filter - only returns form if BOTH match
    Map<String, Object> filter = createFormFilter(formId, tenantId);
    
    FormsDTO form = restHeartService
        .getWithFilter(FORMS_COLLECTION, filter)
        .map(map -> pagebleObject.convertValue(map, Forms.class))
        .map(entity -> pagebleObject.map(entity, FormsDTO.class))
        .blockFirst();
}

// Filter helper
private Map<String, Object> createFormFilter(String formId, String tenantId) {
    Map<String, Object> filter = new HashMap<>();
    filter.put("tenantId", tenantId);  // ✅ Tenant isolation
    filter.put("formId", formId);      // ✅ Business ID
    return filter;
}
```

**Security**:
- Tenant A queries "loan-form"
- Database only returns results where tenantId = "tenant-a" AND formId = "loan-form"
- If no match, returns null and throws 404
- **SECURE**

---

## Example 3: Updating a Form

### ❌ BEFORE (UNSAFE)

```java
// Service - NO TENANT VERIFICATION
public ApiResponse<String> updateForm(String payload, String formId) {
    // ❌ Finds form WITHOUT checking tenant
    Forms existingForm = findFormByIdOrThrow(formId);  // Could be Tenant B's!
    
    // ❌ Updates form from ANY tenant
    applyPatchToForm(updatePayload, fieldUpdaters);
}

// Helper - NO TENANT FILTER
private Forms findFormByIdOrThrow(String formId) {
    Map<String, Object> filter = Map.of("formId", formId);  // ❌ No tenant check
    Forms form = restHeartService
        .getWithFilter(FORMS_COLLECTION, filter)
        .blockFirst();
}
```

**Vulnerability**:
- Tenant A updates formId = "secret-form"
- System updates Tenant B's "secret-form" (if it exists)
- **DATA CORRUPTION + BREACH**

---

### ✅ AFTER (SECURE)

```java
// Service - WITH TENANT VERIFICATION
public ApiResponse<String> updateForm(String payload, String formId) {
    // ✅ Get authenticated user's tenant
    String tenantId = SecurityUtils.getTenantId();
    
    // ✅ Finds form ONLY if it belongs to current tenant
    Forms existingForm = findFormByIdOrThrow(formId, tenantId);
    
    // ✅ Update succeeds only if form is current tenant's
    applyPatchToForm(updatePayload, fieldUpdaters);
}

// Helper - WITH TENANT FILTER
private Forms findFormByIdOrThrow(String formId, String tenantId) {
    // ✅ Composite filter - requires BOTH tenant AND form match
    Map<String, Object> filter = createFormFilter(formId, tenantId);
    
    Forms form = restHeartService
        .getWithFilter(FORMS_COLLECTION, filter)
        .blockFirst();
    
    if (form == null) {
        throw new RestApiException(
            "Form not found",
            HttpStatus.NOT_FOUND  // Even if form exists in other tenant, return 404
        );
    }
}
```

**Security**:
- Tenant A attempts to update "secret-form"
- Database query filters: tenantId="tenant-a" AND formId="secret-form"
- If no match: throws 404 (same response whether form doesn't exist or belongs to different tenant)
- If match: updates only current tenant's form
- **SECURE & CONSISTENT**

---

## Example 4: Deleting a Form

### ❌ BEFORE (UNSAFE)

```java
// Service - NO TENANT CHECK
public ApiResponse<String> deleteForms(String formId) {
    // ❌ Deletes ANY form with this ID (cross-tenant!)
    Forms existingForm = findFormByIdOrThrow(formId);
    
    restHeartService.delete(FORMS_COLLECTION, existingForm.getId()).block();
}
```

**Vulnerability**:
- Tenant A calls DELETE /api/forms/critical-report
- System deletes Tenant B's "critical-report"
- **DATA LOSS + SABOTAGE**

---

### ✅ AFTER (SECURE)

```java
// Service - WITH TENANT CHECK
public ApiResponse<String> deleteForms(String formId) {
    // ✅ Get authenticated user's tenant
    String tenantId = SecurityUtils.getTenantId();
    
    // ✅ Finds form ONLY for current tenant
    Forms existingForm = findFormByIdOrThrow(formId, tenantId);
    
    // ✅ Deletes only current tenant's form
    restHeartService.delete(FORMS_COLLECTION, existingForm.getId()).block();
}
```

**Security**:
- Tenant A attempts to delete "critical-report"
- System first verifies it belongs to Tenant A
- Delete succeeds only if ownership confirmed
- **SECURE**

---

## Example 5: Getting All Forms (Pagination)

### ❌ BEFORE (UNSAFE)

```java
// Service - RETURNS ALL FORMS
public ApiResponse<List<FormsDTO>> getAllForms() {
    // ❌ Returns ALL forms from ALL tenants!
    List<FormsDTO> forms = restHeartService
        .getAll(FORMS_COLLECTION)  // NO FILTER
        .collectList()
        .block();
}
```

**Vulnerability**:
- Tenant A calls GET /api/forms
- Response includes forms from Tenant B, C, D, etc.
- **MASSIVE DATA BREACH**

---

### ✅ AFTER (SECURE)

```java
// Service - RETURNS ONLY TENANT'S FORMS
public ApiResponse<List<FormsDTO>> getAllForms() {
    // ✅ Get authenticated user's tenant
    String tenantId = SecurityUtils.getTenantId();
    
    // ✅ Query ONLY forms belonging to current tenant
    Map<String, Object> filter = createFormTenantFilter(tenantId);
    
    List<FormsDTO> forms = restHeartService
        .getWithFilter(FORMS_COLLECTION, filter)  // ✅ WITH FILTER
        .collectList()
        .block();
}

// Filter helper
private Map<String, Object> createFormTenantFilter(String tenantId) {
    // ✅ Single filter for tenant - returns all forms for this tenant
    return Map.of("tenantId", tenantId);
}
```

**Security**:
- Tenant A calls GET /api/forms
- Response includes ONLY Tenant A's forms
- If Tenant A has 5 forms, gets 5
- If Tenant B has 50 forms, still gets 5 (not 50)
- **SECURE & ISOLATED**

---

## Security Pattern Summary

### The Golden Rule: **ALWAYS FILTER BY TENANT**

Every database query must follow this pattern:

```
Query Filter = {
    tenantId: SecurityUtils.getTenantId(),  // ← Immutable from JWT
    ... other criteria ...                   // ← Business logic
}
```

### Never:
- ❌ Accept tenantId from request body
- ❌ Query without tenantId in filter
- ❌ Use ThreadLocal for tenant isolation
- ❌ Skip tenant check "for now"

### Always:
- ✅ Get tenantId from SecurityUtils
- ✅ Include tenantId in ALL queries
- ✅ Pass tenantId through method parameters
- ✅ Set tenantId explicitly when creating entities

---

## Testing the Security

### Test Case 1: Tenant Isolation Read

```bash
# Tenant A's JWT
curl -H "Authorization: Bearer $TOKEN_A" \
     GET /api/forms/secret-form

# Should return:
# - 200 + form if owned by Tenant A
# - 404 if owned by Tenant B (same response)
# - 401 if no valid JWT
```

### Test Case 2: Tenant Isolation Write

```bash
# Attempt to create form for another tenant
curl -H "Authorization: Bearer $TOKEN_A" \
     POST /api/forms \
     -d '{
       "formId": "loan-form",
       "name": "Loan Application"
     }'

# Should create with tenantId="tenant-a" (from JWT)
# Not with spoofed tenantId in request
```

### Test Case 3: Cross-Tenant Attack (Should Fail)

```bash
# Even if old code accepted tenantId in body
curl -H "Authorization: Bearer $TOKEN_A" \
     POST /api/forms \
     -d '{
       "tenantId": "tenant-b",  # Spoofed!
       "formId": "stolen-form"
     }'

# NEW CODE: Ignores tenantId in body
# Creates with tenantId="tenant-a" (from JWT)
# ✅ Attack blocked
```

---

## Database Indexes for Performance

With tenant-scoped queries, ensure proper indexing:

```javascript
// MongoDB indexes
db.forms.createIndex({ tenantId: 1, formId: 1 })
db.workflows.createIndex({ tenantId: 1, workflowId: 1 })
db.processes.createIndex({ tenantId: 1, processId: 1 })
db.auditLogs.createIndex({ tenantId: 1, timestamp: -1 })
```

These indexes ensure:
- Fast lookups by tenant + business_id
- Efficient tenant-only scans
- Good pagination performance

---

## Summary

| Aspect | Before | After |
|--------|--------|-------|
| **TenantId Source** | Request body (unsafe) | JWT (secure) |
| **Spoofing Risk** | HIGH - Client controls | NONE - JWT signed |
| **Database Queries** | No tenant filter | Always filtered by tenant |
| **Cross-tenant Risk** | CRITICAL | ELIMINATED |
| **ThreadLocal** | Used (not async-safe) | Removed |
| **Audit Trail** | Limited | Enhanced |
| **Compliance** | Non-compliant | Compliant |

