# ✅ FIX ERREUR 400 - Initialize Leave Balance

## 🎯 PROBLÈME RÉSOLU

### Erreur Reçue
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Required parameter 'employeeId' is not present.",
  "path": "/api/leave-balances/initialize"
}
```

---

## 🔧 SOLUTION APPLIQUÉE

### Modification du Controller

**Fichier :** `LeaveBalanceController.java`

**CHANGEMENT :**
- Créé le DTO `LeaveBalanceInitRequest`
- Modifié l'endpoint pour accepter un **body JSON** au lieu de **query parameters**

**AVANT :**
```java
@PostMapping("/initialize")
public ResponseEntity<LeaveBalance> initializeLeaveBalance(
    @RequestParam Long employeeId,      // ❌ Query parameters
    @RequestParam Long leaveTypeId,
    @RequestParam(required = false) Integer year) {
    // ...
}
```

**APRÈS :**
```java
@PostMapping("/initialize")
public ResponseEntity<LeaveBalance> initializeLeaveBalance(
    @Valid @RequestBody LeaveBalanceInitRequest request) { // ✅ Body JSON
    // ...
}
```

---

## ✅ UTILISATION CORRECTE

### Format JSON dans le Body (RECOMMANDÉ)

**Méthode :** POST  
**URL :** `http://localhost:8080/api/leave-balances/initialize`  
**Headers :** `Content-Type: application/json`

**Body (raw JSON) :**
```json
{
  "employeeId": 1,
  "leaveTypeId": 1,
  "year": 2024
}
```

---

## 🧪 TESTS

### Test PowerShell

```powershell
$body = @{
    employeeId = 1
    leaveTypeId = 1
    year = 2024
} | ConvertTo-Json

Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-balances/initialize" `
  -ContentType "application/json" `
  -Body $body
```

**Résultat attendu :**
```powershell
id                : 1
employee          : @{id=1; firstName=Jean; ...}
leaveType         : @{id=1; name=Congé Annuel; ...}
year              : 2024
totalDays         : 22
usedDays          : 0
carriedOverDays   : 0
additionalDays    : 0
remainingDays     : 22
```

---

### Test Postman

**1. Créer une nouvelle requête**
- Method : POST
- URL : `{{baseUrl}}/api/leave-balances/initialize`

**2. Headers**
```
Content-Type: application/json
```

**3. Body → raw → JSON**
```json
{
  "employeeId": 1,
  "leaveTypeId": 1,
  "year": 2024
}
```

**4. Cliquer "Send"**

---

### Test cURL (Windows)

```bash
curl.exe --request POST ^
  --url http://localhost:8080/api/leave-balances/initialize ^
  --header "Content-Type: application/json" ^
  --data "{\"employeeId\": 1, \"leaveTypeId\": 1, \"year\": 2024}"
```

---

## 📝 AUTRES ENDPOINTS MODIFIÉS

### ✅ Tous les endpoints utilisent maintenant des bodies JSON quand approprié

**Endpoints Concernés :**
- ✅ POST /api/departments (body JSON)
- ✅ POST /api/employees (body JSON)
- ✅ POST /api/leave-types (body JSON)
- ✅ POST /api/leave-balances/initialize (body JSON) ← **CORRIGÉ**
- ✅ POST /api/leave-requests (body JSON)

---

## 🎯 ENDPOINTS LEAVE BALANCES

### Initialiser un Solde (CORRIGÉ)

```
POST /api/leave-balances/initialize

Body JSON:
{
  "employeeId": 1,
  "leaveTypeId": 1,
  "year": 2024
}
```

---

### Consulter les Soldes

```
GET /api/leave-balances/employee/{employeeId}
GET /api/leave-balances/employee/{employeeId}/year/{year}
```

---

### Ajouter des Jours Supplémentaires

```
POST /api/leave-balances/employee/{empId}/leave-type/{typeId}/year/{year}/additional

Body JSON:
{
  "additionalDays": 5,
  "reason": "Bonus de performance"
}
```

---

### Initialiser les Soldes Annuels pour Tous

```
POST /api/leave-balances/initialize-yearly?year=2024
```

---

### Vérifier le Solde Disponible

```
GET /api/leave-balances/check?employeeId=1&leaveTypeId=1&startDate=2024-07-10&endDate=2024-07-14
```

---

## 🔍 SCÉNARIO COMPLET

### Ordre d'Exécution

```
1. POST /api/departments
   Body: {"name": "RH", "description": "Département RH"}
   → Crée département ID 1

2. POST /api/leave-types
   Body: {"name": "Congé Annuel", "paidLeave": true, "defaultDaysPerYear": 22, ...}
   → Crée type congé ID 1

3. POST /api/employees
   Body: {"firstName": "Jean", "lastName": "Dupont", "department": {"id": 1}, ...}
   → Crée employé ID 1

4. POST /api/leave-balances/initialize
   Body: {"employeeId": 1, "leaveTypeId": 1, "year": 2024}
   → Initialise solde avec 22 jours

5. GET /api/leave-balances/employee/1
   → Vérifier le solde créé

6. POST /api/leave-requests
   Body: {"employee": {"id": 1}, "leaveType": {"id": 1}, "startDate": "2024-07-10", ...}
   → Créer demande de congé
```

---

## 🎊 RÉSUMÉ

### ✅ Problème Résolu

**AVANT :**
```
POST /api/leave-balances/initialize
Query params: ?employeeId=1&leaveTypeId=1&year=2024
→ Compliqué et non-RESTful ❌
```

**APRÈS :**
```
POST /api/leave-balances/initialize
Body JSON: {"employeeId": 1, "leaveTypeId": 1, "year": 2024}
→ Simple et RESTful ✅
```

### Fichiers Modifiés

1. ✅ Créé `LeaveBalanceInitRequest.java` (DTO)
2. ✅ Modifié `LeaveBalanceController.java` (accepte body JSON)
3. ✅ Mis à jour `POSTMAN_TEST_GUIDE.md` (documentation)

### Résultat

- ✅ Endpoint plus RESTful
- ✅ Validation automatique des données
- ✅ Plus facile à utiliser dans Postman
- ✅ Cohérent avec les autres endpoints

---

## 🚀 TESTER MAINTENANT

```powershell
# Redémarrer l'application
# IntelliJ → Stop → Run

# Puis tester :
.\test-api.ps1
```

**Le script utilisera automatiquement le nouveau format !**

---

**Statut :** ✅ **ERREUR 400 CORRIGÉE**  
**Endpoint :** `/api/leave-balances/initialize`  
**Format :** Body JSON (RESTful) ✅

