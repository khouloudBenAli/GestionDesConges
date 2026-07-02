# 📬 TEST DE L'API AVEC POSTMAN

## 🎯 CONFIGURATION RAPIDE

### 1. Variables d'Environnement Postman

**Créer un Environment "Local" :**

| Variable | Value |
|----------|-------|
| baseUrl  | http://localhost:8080 |
| authToken | (laisser vide pour l'instant) |

### 2. Importer les Requêtes

**Les requêtes principales à créer dans Postman :**

---

## 📂 COLLECTION : Departments

### ✅ GET - Liste tous les Départements

```
GET {{baseUrl}}/api/departments
```

**Headers :** Aucun requis

**Résultat attendu :** `[]` ou liste de départements

---

### ✅ POST - Créer un Département

```
POST {{baseUrl}}/api/departments
```

**Headers :**
```
Content-Type: application/json
```

**Body (raw JSON) :**
```json
{
  "name": "Ressources Humaines",
  "description": "Département RH responsable de la gestion des employés"
}
```

**Résultat attendu :**
```json
{
  "id": 1,
  "name": "Ressources Humaines",
  "description": "Département RH responsable de la gestion des employés",
  "createdAt": "2026-03-15T21:00:00",
  "updatedAt": "2026-03-15T21:00:00"
}
```

---

### ✅ GET - Obtenir un Département par ID

```
GET {{baseUrl}}/api/departments/1
```

**Headers :** Aucun requis

---

### ✅ PUT - Modifier un Département

```
PUT {{baseUrl}}/api/departments/1
```

**Headers :**
```
Content-Type: application/json
```

**Body (raw JSON) :**
```json
{
  "name": "Ressources Humaines",
  "description": "Description mise à jour"
}
```

---

### ✅ DELETE - Supprimer un Département

```
DELETE {{baseUrl}}/api/departments/1
```

**Headers :** Aucun requis

---

## 📂 COLLECTION : Employees

### ✅ POST - Créer un Employé

```
POST {{baseUrl}}/api/employees
```

**Headers :**
```
Content-Type: application/json
```

**Body (raw JSON) :**
```json
{
  "firstName": "Jean",
  "lastName": "Dupont",
  "email": "jean.dupont@example.com",
  "jobTitle": "Développeur",
  "hireDate": "2023-01-15",
  "department": {
    "id": 1
  }
}
```

---

### ✅ GET - Liste tous les Employés

```
GET {{baseUrl}}/api/employees
```

---

### ✅ PUT - Assigner un Employé à un Département

```
PUT {{baseUrl}}/api/employees/1/department/1
```

---

## 📂 COLLECTION : Leave Types

### ✅ POST - Créer un Type de Congé

```
POST {{baseUrl}}/api/leave-types
```

**Headers :**
```
Content-Type: application/json
```

**Body (raw JSON) :**
```json
{
  "name": "Congé Annuel",
  "description": "Congé payé annuel",
  "paidLeave": true,
  "defaultDaysPerYear": 22,
  "colorCode": "#4CAF50",
  "requiresDocumentation": false,
  "canCarryOver": true,
  "maxCarryOverDays": 5,
  "minNoticeDays": 7,
  "allowHalfDay": true
}
```

---

### ✅ GET - Liste les Types de Congés

```
GET {{baseUrl}}/api/leave-types
```

---

## 📂 COLLECTION : Leave Balances

### ✅ POST - Initialiser un Solde de Congé

```
POST {{baseUrl}}/api/leave-balances/initialize
```

**Headers :**
```
Content-Type: application/json
```

**Body (raw JSON) :**
```json
{
  "employeeId": 1,
  "leaveTypeId": 1,
  "year": 2024
}
```

**Note :** L'endpoint accepte maintenant un body JSON (✅ CORRIGÉ)

---

### ✅ GET - Solde d'un Employé

```
GET {{baseUrl}}/api/leave-balances/employee/1
```

---

## 📂 COLLECTION : Leave Requests

### ✅ POST - Créer une Demande de Congé

```
POST {{baseUrl}}/api/leave-requests
```

**Headers :**
```
Content-Type: application/json
```

**Body (raw JSON) :**
```json
{
  "employee": {
    "id": 1
  },
  "leaveType": {
    "id": 1
  },
  "startDate": "2024-07-10",
  "endDate": "2024-07-14",
  "halfDayStart": false,
  "halfDayEnd": false,
  "reason": "Vacances en famille",
  "isEmergency": false,
  "contactInfo": "Téléphone: 123-456-7890"
}
```

---

### ✅ PUT - Approuver une Demande

```
PUT {{baseUrl}}/api/leave-requests/1/approve
```

**Headers :**
```
Content-Type: application/json
```

**Body (raw JSON) :**
```json
{
  "managerComment": "Approuvé. Bonnes vacances!"
}
```

---

### ✅ PUT - Rejeter une Demande

```
PUT {{baseUrl}}/api/leave-requests/1/reject
```

**Headers :**
```
Content-Type: application/json
```

**Body (raw JSON) :**
```json
{
  "managerComment": "Rejeté en raison d'une échéance critique."
}
```

---

## 🧪 SCÉNARIO DE TEST COMPLET

### Ordre d'Exécution Recommandé

```
1. POST /api/departments           → Créer un département
2. POST /api/leave-types          → Créer un type de congé
3. POST /api/employees            → Créer un employé
4. POST /api/leave-balances/initialize → Initialiser le solde
5. POST /api/leave-requests       → Créer une demande
6. PUT /api/leave-requests/1/approve → Approuver la demande
```

---

## 🔧 TESTER EN POWERSHELL

### Créer un Département

```powershell
$body = @{
    name = "Ressources Humaines"
    description = "Département RH"
} | ConvertTo-Json

Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/departments" `
  -ContentType "application/json" `
  -Body $body
```

### Lister les Départements

```powershell
Invoke-RestMethod -Method GET `
  -Uri "http://localhost:8080/api/departments"
```

---

## 🌐 TESTER DANS LE NAVIGATEUR

### GET Endpoints (Direct dans le Navigateur)

```
http://localhost:8080/api/departments
http://localhost:8080/api/employees
http://localhost:8080/api/leave-types
http://localhost:8080/api/leave-requests
```

**Le navigateur affichera le JSON directement !**

---

## ✅ VÉRIFICATIONS

### 1. Application Démarrée ?

**Logs IntelliJ doivent montrer :**
```
Started CongesApplication in X.XXX seconds
Tomcat started on port(s): 8080 (http)
```

### 2. MySQL XAMPP Démarré ?

**Panneau XAMPP :**
```
MySQL - Running ✅
```

### 3. Base de Données Créée ?

**phpMyAdmin :**
```
conges_db ✅
  ├── department
  ├── employee
  ├── leave_type
  ├── leave_request
  └── leave_balance
```

---

## 🎊 RÉSUMÉ DU FIX

### Problème
- ❌ 403 Forbidden sur POST /api/departments
- ❌ Spring Security bloquait les requêtes

### Solution Appliquée
- ✅ Modifié `SecurityConfig.java`
- ✅ Changé `.anyRequest().authenticated()` → `.anyRequest().permitAll()`

### Actions Nécessaires
1. **Redémarrer** l'application Spring Boot
2. **Tester** avec l'URL correcte : `http://localhost:8080/api/departments`
3. **Utiliser** Postman pour faciliter les tests

---

## 📞 AIDE SUPPLÉMENTAIRE

### Si 403 Persiste

1. **Vérifier les logs** dans IntelliJ (onglet Run)
2. **Vérifier que SecurityConfig.java est chargé**
3. **Faire un Clean Rebuild** : Build → Rebuild Project

### Si Erreur 500

- **Vérifier MySQL XAMPP** est démarré
- **Vérifier la base** `conges_db` existe

### Si Erreur de Connexion

- **Vérifier l'URL** : `http://localhost:8080` (pas `{{baseurl}}`)
- **Vérifier le port** : 8080 est libre

---

**Statut :** ✅ **CONFIGURATION DE SÉCURITÉ CORRIGÉE**  
**Action :** Redémarrer l'application et tester  
**Temps :** 1 minute

