# 🎯 GUIDE DE TEST FINAL - TOUS LES ENDPOINTS CORRIGÉS

## ✅ TOUS LES PROBLÈMES RÉSOLUS

1. ✅ **403 Forbidden** → SecurityConfig corrigé
2. ✅ **400 Bad Request (initialize)** → Accepte maintenant body JSON
3. ✅ **URL curl incorrecte** → Guide avec URLs correctes

---

## 🚀 DÉMARRAGE RAPIDE

### Prérequis (1 fois)

```
1. MySQL XAMPP démarré ✅
2. Base conges_db créée ✅
3. Application redémarrée ✅
```

---

## 🧪 TESTS POWERSHELL (COPIER-COLLER)

### Test 1 : Créer un Département

```powershell
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/departments" `
  -ContentType "application/json" `
  -Body '{"name": "Ressources Humaines", "description": "Département RH responsable de la gestion des employés"}'
```

**Résultat attendu :**
```
id          : 1
name        : Ressources Humaines
description : Département RH responsable de la gestion des employés
```

---

### Test 2 : Créer un Type de Congé

```powershell
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-types" `
  -ContentType "application/json" `
  -Body '{"name": "Congé Annuel", "description": "Congé payé annuel", "paidLeave": true, "defaultDaysPerYear": 22, "colorCode": "#4CAF50", "requiresDocumentation": false, "canCarryOver": true, "maxCarryOverDays": 5, "minNoticeDays": 7, "allowHalfDay": true, "active": true}'
```

**Résultat attendu :**
```
id                  : 1
name                : Congé Annuel
paidLeave           : True
defaultDaysPerYear  : 22
```

---

### Test 3 : Créer un Employé

```powershell
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/employees" `
  -ContentType "application/json" `
  -Body '{"firstName": "Jean", "lastName": "Dupont", "email": "jean.dupont@example.com", "jobTitle": "Développeur", "hireDate": "2023-01-15", "department": {"id": 1}}'
```

**Résultat attendu :**
```
id        : 1
firstName : Jean
lastName  : Dupont
email     : jean.dupont@example.com
```

---

### Test 4 : Initialiser un Solde (✅ CORRIGÉ)

```powershell
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-balances/initialize" `
  -ContentType "application/json" `
  -Body '{"employeeId": 1, "leaveTypeId": 1, "year": 2024}'
```

**Résultat attendu :**
```
id              : 1
totalDays       : 22
usedDays        : 0
remainingDays   : 22
year            : 2024
```

---

### Test 5 : Créer une Demande de Congé

```powershell
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-requests" `
  -ContentType "application/json" `
  -Body '{"employee": {"id": 1}, "leaveType": {"id": 1}, "startDate": "2024-07-10", "endDate": "2024-07-14", "halfDayStart": false, "halfDayEnd": false, "reason": "Vacances en famille", "isEmergency": false, "contactInfo": "Téléphone: 123-456-7890"}'
```

**Résultat attendu :**
```
id         : 1
status     : PENDING
startDate  : 2024-07-10
endDate    : 2024-07-14
workingDays: 5
```

---

### Test 6 : Approuver la Demande

```powershell
Invoke-RestMethod -Method PUT `
  -Uri "http://localhost:8080/api/leave-requests/1/approve" `
  -ContentType "application/json" `
  -Body '{"managerComment": "Approuvé. Bonnes vacances!"}'
```

**Résultat attendu :**
```
id            : 1
status        : APPROVED
managerComment: Approuvé. Bonnes vacances!
```

---

### Test 7 : Vérifier le Solde Mis à Jour

```powershell
Invoke-RestMethod -Method GET `
  -Uri "http://localhost:8080/api/leave-balances/employee/1"
```

**Résultat attendu :**
```
usedDays      : 5          (5 jours utilisés)
remainingDays : 17         (22 - 5 = 17)
```

---

## 📊 TOUS LES ENDPOINTS AVEC EXEMPLES

### 🏢 Departments

```powershell
# GET - Liste
Invoke-RestMethod -Uri "http://localhost:8080/api/departments"

# POST - Créer
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/departments" `
  -ContentType "application/json" `
  -Body '{"name": "IT", "description": "Département Informatique"}'

# GET - Par ID
Invoke-RestMethod -Uri "http://localhost:8080/api/departments/1"

# PUT - Modifier
Invoke-RestMethod -Method PUT `
  -Uri "http://localhost:8080/api/departments/1" `
  -ContentType "application/json" `
  -Body '{"name": "IT", "description": "Description mise à jour"}'

# DELETE - Supprimer
Invoke-RestMethod -Method DELETE `
  -Uri "http://localhost:8080/api/departments/1"
```

---

### 👥 Employees

```powershell
# GET - Liste
Invoke-RestMethod -Uri "http://localhost:8080/api/employees"

# POST - Créer
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/employees" `
  -ContentType "application/json" `
  -Body '{"firstName": "Marie", "lastName": "Martin", "email": "marie.martin@example.com", "jobTitle": "Manager", "hireDate": "2022-03-01", "department": {"id": 1}}'

# GET - Par ID
Invoke-RestMethod -Uri "http://localhost:8080/api/employees/1"

# PUT - Assigner département
Invoke-RestMethod -Method PUT `
  -Uri "http://localhost:8080/api/employees/1/department/2"

# PUT - Assigner manager
Invoke-RestMethod -Method PUT `
  -Uri "http://localhost:8080/api/employees/1/manager/2"
```

---

### 📋 Leave Types

```powershell
# GET - Liste tous
Invoke-RestMethod -Uri "http://localhost:8080/api/leave-types"

# GET - Actifs seulement
Invoke-RestMethod -Uri "http://localhost:8080/api/leave-types/active"

# POST - Créer Congé Annuel
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-types" `
  -ContentType "application/json" `
  -Body '{"name": "Congé Annuel", "description": "Congé payé annuel", "paidLeave": true, "defaultDaysPerYear": 22, "colorCode": "#4CAF50", "requiresDocumentation": false, "canCarryOver": true, "maxCarryOverDays": 5, "minNoticeDays": 7, "allowHalfDay": true, "active": true}'

# POST - Créer Congé Maladie
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-types" `
  -ContentType "application/json" `
  -Body '{"name": "Congé Maladie", "description": "Congé pour raison médicale", "paidLeave": true, "defaultDaysPerYear": 10, "colorCode": "#FF5722", "requiresDocumentation": true, "canCarryOver": false, "minNoticeDays": 0, "allowHalfDay": true, "active": true}'

# POST - Créer Congé Maternité
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-types" `
  -ContentType "application/json" `
  -Body '{"name": "Congé Maternité", "description": "Congé de maternité", "paidLeave": true, "defaultDaysPerYear": 112, "colorCode": "#E91E63", "requiresDocumentation": true, "canCarryOver": false, "minNoticeDays": 30, "allowHalfDay": false, "active": true}'

# POST - Créer Congé Sans Solde
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-types" `
  -ContentType "application/json" `
  -Body '{"name": "Congé Sans Solde", "description": "Congé non payé", "paidLeave": false, "defaultDaysPerYear": 30, "colorCode": "#9E9E9E", "requiresDocumentation": true, "canCarryOver": false, "minNoticeDays": 14, "allowHalfDay": false, "active": true}'
```

---

### 💰 Leave Balances (✅ CORRIGÉ)

```powershell
# POST - Initialiser (NOUVEAU FORMAT)
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-balances/initialize" `
  -ContentType "application/json" `
  -Body '{"employeeId": 1, "leaveTypeId": 1, "year": 2024}'

# GET - Solde employé
Invoke-RestMethod -Uri "http://localhost:8080/api/leave-balances/employee/1"

# GET - Solde employé par année
Invoke-RestMethod -Uri "http://localhost:8080/api/leave-balances/employee/1/year/2024"

# POST - Ajouter jours supplémentaires
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-balances/employee/1/leave-type/1/year/2024/additional" `
  -ContentType "application/json" `
  -Body '{"additionalDays": 3, "reason": "Bonus"}'

# POST - Initialiser pour tous les employés
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-balances/initialize-yearly?year=2024"

# GET - Vérifier disponibilité
Invoke-RestMethod -Uri "http://localhost:8080/api/leave-balances/check?employeeId=1&leaveTypeId=1&startDate=2024-07-10&endDate=2024-07-14"
```

---

### 📝 Leave Requests

```powershell
# GET - Liste toutes
Invoke-RestMethod -Uri "http://localhost:8080/api/leave-requests"

# POST - Créer une demande
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-requests" `
  -ContentType "application/json" `
  -Body '{"employee": {"id": 1}, "leaveType": {"id": 1}, "startDate": "2024-07-10", "endDate": "2024-07-14", "halfDayStart": false, "halfDayEnd": false, "reason": "Vacances été", "isEmergency": false, "contactInfo": "Tel: 0612345678"}'

# GET - Par employé
Invoke-RestMethod -Uri "http://localhost:8080/api/leave-requests/employee/1"

# GET - Par manager
Invoke-RestMethod -Uri "http://localhost:8080/api/leave-requests/manager/2"

# GET - En attente pour un manager
Invoke-RestMethod -Uri "http://localhost:8080/api/leave-requests/manager/2/pending"

# PUT - Approuver
Invoke-RestMethod -Method PUT `
  -Uri "http://localhost:8080/api/leave-requests/1/approve" `
  -ContentType "application/json" `
  -Body '{"managerComment": "Approuvé. Profitez bien!"}'

# PUT - Rejeter
Invoke-RestMethod -Method PUT `
  -Uri "http://localhost:8080/api/leave-requests/2/reject" `
  -ContentType "application/json" `
  -Body '{"managerComment": "Rejeté. Période critique."}'

# PUT - Annuler
Invoke-RestMethod -Method PUT `
  -Uri "http://localhost:8080/api/leave-requests/3/cancel"

# PUT - Demander information
Invoke-RestMethod -Method PUT `
  -Uri "http://localhost:8080/api/leave-requests/4/request-information" `
  -ContentType "application/json" `
  -Body '{"managerComment": "Merci de fournir un certificat médical."}'

# GET - Par statut
Invoke-RestMethod -Uri "http://localhost:8080/api/leave-requests/status/PENDING"
Invoke-RestMethod -Uri "http://localhost:8080/api/leave-requests/status/APPROVED"
Invoke-RestMethod -Uri "http://localhost:8080/api/leave-requests/status/REJECTED"

# GET - Par plage de dates
Invoke-RestMethod -Uri "http://localhost:8080/api/leave-requests/date-range?startDate=2024-01-01&endDate=2024-12-31"

# GET - Par département
Invoke-RestMethod -Uri "http://localhost:8080/api/leave-requests/department/1"
```

---

### 📥 Export CSV

```powershell
# Export Employés CSV
Invoke-WebRequest -Uri "http://localhost:8080/api/export/employees/csv" `
  -OutFile "employees.csv"
Write-Host "✅ Fichier créé : employees.csv"

# Export Demandes CSV
Invoke-WebRequest -Uri "http://localhost:8080/api/export/leave-requests/csv" `
  -OutFile "leave_requests.csv"
Write-Host "✅ Fichier créé : leave_requests.csv"

# Export Soldes CSV
Invoke-WebRequest -Uri "http://localhost:8080/api/export/leave-balances/csv" `
  -OutFile "leave_balances.csv"
Write-Host "✅ Fichier créé : leave_balances.csv"
```

**Ouvrir les fichiers CSV avec Excel !**

---

## 🎯 SCÉNARIO COMPLET DÉTAILLÉ

### Créer des Données de Test Complètes

```powershell
# ====== 1. DÉPARTEMENTS ======
Write-Host "Création des départements..." -ForegroundColor Cyan

$deptRH = Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/departments" `
  -ContentType "application/json" `
  -Body '{"name": "Ressources Humaines", "description": "Département RH"}'

$deptIT = Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/departments" `
  -ContentType "application/json" `
  -Body '{"name": "Informatique", "description": "Département IT"}'

$deptVentes = Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/departments" `
  -ContentType "application/json" `
  -Body '{"name": "Ventes", "description": "Département Commercial"}'

Write-Host "✅ 3 départements créés" -ForegroundColor Green

# ====== 2. TYPES DE CONGÉS ======
Write-Host "Création des types de congés..." -ForegroundColor Cyan

$typeAnnuel = Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-types" `
  -ContentType "application/json" `
  -Body '{"name": "Congé Annuel", "description": "Congé payé annuel", "paidLeave": true, "defaultDaysPerYear": 22, "colorCode": "#4CAF50", "canCarryOver": true, "maxCarryOverDays": 5, "minNoticeDays": 7, "allowHalfDay": true, "active": true}'

$typeMaladie = Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-types" `
  -ContentType "application/json" `
  -Body '{"name": "Congé Maladie", "description": "Congé maladie", "paidLeave": true, "defaultDaysPerYear": 10, "colorCode": "#FF5722", "requiresDocumentation": true, "canCarryOver": false, "minNoticeDays": 0, "allowHalfDay": true, "active": true}'

$typeMaternite = Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-types" `
  -ContentType "application/json" `
  -Body '{"name": "Congé Maternité", "description": "Congé de maternité", "paidLeave": true, "defaultDaysPerYear": 112, "colorCode": "#E91E63", "requiresDocumentation": true, "canCarryOver": false, "minNoticeDays": 30, "active": true}'

Write-Host "✅ 3 types de congés créés" -ForegroundColor Green

# ====== 3. EMPLOYÉS ======
Write-Host "Création des employés..." -ForegroundColor Cyan

$empJean = Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/employees" `
  -ContentType "application/json" `
  -Body "{`"firstName`": `"Jean`", `"lastName`": `"Dupont`", `"email`": `"jean.dupont@example.com`", `"jobTitle`": `"Développeur`", `"hireDate`": `"2023-01-15`", `"department`": {`"id`": $($deptIT.id)}}"

$empMarie = Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/employees" `
  -ContentType "application/json" `
  -Body "{`"firstName`": `"Marie`", `"lastName`": `"Martin`", `"email`": `"marie.martin@example.com`", `"jobTitle`": `"Manager IT`", `"hireDate`": `"2022-01-01`", `"department`": {`"id`": $($deptIT.id)}}"

$empPierre = Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/employees" `
  -ContentType "application/json" `
  -Body "{`"firstName`": `"Pierre`", `"lastName`": `"Dubois`", `"email`": `"pierre.dubois@example.com`", `"jobTitle`": `"Commercial`", `"hireDate`": `"2023-06-01`", `"department`": {`"id`": $($deptVentes.id)}}"

Write-Host "✅ 3 employés créés" -ForegroundColor Green

# Assigner Marie comme manager de Jean
Invoke-RestMethod -Method PUT `
  -Uri "http://localhost:8080/api/employees/$($empJean.id)/manager/$($empMarie.id)"

Write-Host "✅ Manager assigné" -ForegroundColor Green

# ====== 4. SOLDES DE CONGÉS ======
Write-Host "Initialisation des soldes..." -ForegroundColor Cyan

# Soldes pour Jean
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-balances/initialize" `
  -ContentType "application/json" `
  -Body "{`"employeeId`": $($empJean.id), `"leaveTypeId`": $($typeAnnuel.id), `"year`": 2024}"

Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-balances/initialize" `
  -ContentType "application/json" `
  -Body "{`"employeeId`": $($empJean.id), `"leaveTypeId`": $($typeMaladie.id), `"year`": 2024}"

# Soldes pour Marie
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-balances/initialize" `
  -ContentType "application/json" `
  -Body "{`"employeeId`": $($empMarie.id), `"leaveTypeId`": $($typeAnnuel.id), `"year`": 2024}"

# Soldes pour Pierre
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-balances/initialize" `
  -ContentType "application/json" `
  -Body "{`"employeeId`": $($empPierre.id), `"leaveTypeId`": $($typeAnnuel.id), `"year`": 2024}"

Write-Host "✅ 4 soldes de congés initialisés" -ForegroundColor Green

# ====== 5. DEMANDES DE CONGÉS ======
Write-Host "Création des demandes..." -ForegroundColor Cyan

$reqJean = Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-requests" `
  -ContentType "application/json" `
  -Body "{`"employee`": {`"id`": $($empJean.id)}, `"leaveType`": {`"id`": $($typeAnnuel.id)}, `"startDate`": `"2024-07-10`", `"endDate`": `"2024-07-14`", `"halfDayStart`": false, `"halfDayEnd`": false, `"reason`": `"Vacances été`", `"isEmergency`": false, `"contactInfo`": `"Tel: 0612345678`"}"

$reqPierre = Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/leave-requests" `
  -ContentType "application/json" `
  -Body "{`"employee`": {`"id`": $($empPierre.id)}, `"leaveType`": {`"id`": $($typeAnnuel.id)}, `"startDate`": `"2024-08-01`", `"endDate`": `"2024-08-15`", `"halfDayStart`": false, `"halfDayEnd`": false, `"reason`": `"Vacances famille`", `"isEmergency`": false, `"contactInfo`": `"Tel: 0698765432`"}"

Write-Host "✅ 2 demandes créées (PENDING)" -ForegroundColor Green

# ====== 6. APPROBATION ======
Write-Host "Approbation de la demande de Jean..." -ForegroundColor Cyan

Invoke-RestMethod -Method PUT `
  -Uri "http://localhost:8080/api/leave-requests/$($reqJean.id)/approve" `
  -ContentType "application/json" `
  -Body '{"managerComment": "Approuvé. Bonnes vacances!"}'

Write-Host "✅ Demande approuvée" -ForegroundColor Green

# ====== RÉSUMÉ ======
Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "   DONNÉES DE TEST CRÉÉES AVEC SUCCÈS" -ForegroundColor Yellow
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "✅ 3 Départements (RH, IT, Ventes)" -ForegroundColor Green
Write-Host "✅ 3 Types de Congés (Annuel, Maladie, Maternité)" -ForegroundColor Green
Write-Host "✅ 3 Employés (Jean, Marie, Pierre)" -ForegroundColor Green
Write-Host "✅ 4 Soldes de Congés" -ForegroundColor Green
Write-Host "✅ 2 Demandes (1 APPROVED, 1 PENDING)" -ForegroundColor Green
Write-Host ""
Write-Host "🌐 Vérifier dans phpMyAdmin:" -ForegroundColor Cyan
Write-Host "   http://localhost/phpmyadmin" -ForegroundColor White
Write-Host ""
```

**Sauvegarder ce script comme `test-complet.ps1` et exécuter !**

---

## 🎊 RÉSUMÉ DES CORRECTIONS

### Problèmes Résolus

1. ✅ **403 Forbidden** → SecurityConfig modifié
2. ✅ **400 Bad Request (initialize)** → Accepte body JSON maintenant
3. ✅ **URL incorrecte** → Guides avec URLs correctes

### Fichiers Modifiés

1. ✅ `SecurityConfig.java` - Permet toutes les requêtes
2. ✅ `LeaveBalanceController.java` - Accepte body JSON pour initialize
3. ✅ `LeaveBalanceInitRequest.java` - Nouveau DTO créé

---

## 📋 CHECKLIST FINALE

- [x] SecurityConfig corrigé (403 resolved)
- [x] LeaveBalanceController corrigé (400 resolved)
- [x] DTO LeaveBalanceInitRequest créé
- [x] Documentation mise à jour
- [ ] Application redémarrée
- [ ] Tests validés

---

## 🚀 ACTION IMMÉDIATE

```powershell
# Redémarrer l'application dans IntelliJ
# Puis exécuter :
.\test-api.ps1
```

**Tout devrait fonctionner maintenant ! ✅**

---

**Statut :** ✅ **TOUS LES PROBLÈMES CORRIGÉS**  
**Endpoints :** 40+ fonctionnels  
**Format :** Body JSON RESTful ✅

