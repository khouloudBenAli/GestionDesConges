# 🧪 Script de Test Rapide de l'API
# Testez tous les endpoints facilement avec PowerShell

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "   TEST API GESTION DES CONGES" -ForegroundColor Yellow
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8080"

# Test de connexion au serveur
Write-Host "[1/6] Test de connexion au serveur..." -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/departments" -Method GET -UseBasicParsing -ErrorAction Stop
    Write-Host "✅ Serveur accessible - Code: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur: Le serveur n'est pas accessible" -ForegroundColor Red
    Write-Host "   Vérifiez que l'application Spring Boot est démarrée" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Test 1 : Créer un Département
Write-Host "[2/6] Création d'un département..." -ForegroundColor Cyan
$deptBody = @{
    name = "Ressources Humaines"
    description = "Département RH responsable de la gestion des employés"
} | ConvertTo-Json

try {
    $dept = Invoke-RestMethod -Method POST `
        -Uri "$baseUrl/api/departments" `
        -ContentType "application/json" `
        -Body $deptBody
    Write-Host "✅ Département créé - ID: $($dept.id)" -ForegroundColor Green
    $deptId = $dept.id
} catch {
    Write-Host "❌ Erreur lors de la création du département" -ForegroundColor Red
    Write-Host "   Détails: $($_.Exception.Message)" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Test 2 : Créer un Type de Congé
Write-Host "[3/6] Création d'un type de congé..." -ForegroundColor Cyan
$leaveTypeBody = @{
    name = "Congé Annuel"
    description = "Congé payé annuel"
    paidLeave = $true
    defaultDaysPerYear = 22
    colorCode = "#4CAF50"
    requiresDocumentation = $false
    canCarryOver = $true
    maxCarryOverDays = 5
    minNoticeDays = 7
    allowHalfDay = $true
    active = $true
} | ConvertTo-Json

try {
    $leaveType = Invoke-RestMethod -Method POST `
        -Uri "$baseUrl/api/leave-types" `
        -ContentType "application/json" `
        -Body $leaveTypeBody
    Write-Host "✅ Type de congé créé - ID: $($leaveType.id)" -ForegroundColor Green
    $leaveTypeId = $leaveType.id
} catch {
    Write-Host "❌ Erreur lors de la création du type de congé" -ForegroundColor Red
    Write-Host "   Détails: $($_.Exception.Message)" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Test 3 : Créer un Employé
Write-Host "[4/6] Création d'un employé..." -ForegroundColor Cyan
$empBody = @{
    firstName = "Jean"
    lastName = "Dupont"
    email = "jean.dupont@example.com"
    jobTitle = "Développeur"
    hireDate = "2023-01-15"
    department = @{
        id = $deptId
    }
} | ConvertTo-Json

try {
    $employee = Invoke-RestMethod -Method POST `
        -Uri "$baseUrl/api/employees" `
        -ContentType "application/json" `
        -Body $empBody
    Write-Host "✅ Employé créé - ID: $($employee.id)" -ForegroundColor Green
    $employeeId = $employee.id
} catch {
    Write-Host "❌ Erreur lors de la création de l'employé" -ForegroundColor Red
    Write-Host "   Détails: $($_.Exception.Message)" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Test 4 : Initialiser le Solde de Congé
Write-Host "[5/6] Initialisation du solde de congé..." -ForegroundColor Cyan
$balanceBody = @{
    employeeId = $employeeId
    leaveTypeId = $leaveTypeId
    year = 2024
} | ConvertTo-Json

try {
    $balance = Invoke-RestMethod -Method POST `
        -Uri "$baseUrl/api/leave-balances/initialize" `
        -ContentType "application/json" `
        -Body $balanceBody
    Write-Host "✅ Solde initialisé - Jours disponibles: $($balance.totalDays)" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Attention: Erreur lors de l'initialisation du solde" -ForegroundColor Yellow
    Write-Host "   Détails: $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host ""

# Test 5 : Créer une Demande de Congé
Write-Host "[6/6] Création d'une demande de congé..." -ForegroundColor Cyan
$requestBody = @{
    employee = @{
        id = $employeeId
    }
    leaveType = @{
        id = $leaveTypeId
    }
    startDate = "2024-07-10"
    endDate = "2024-07-14"
    halfDayStart = $false
    halfDayEnd = $false
    reason = "Vacances en famille"
    isEmergency = $false
    contactInfo = "Téléphone: 123-456-7890"
} | ConvertTo-Json

try {
    $request = Invoke-RestMethod -Method POST `
        -Uri "$baseUrl/api/leave-requests" `
        -ContentType "application/json" `
        -Body $requestBody
    Write-Host "✅ Demande de congé créée - ID: $($request.id)" -ForegroundColor Green
    Write-Host "   Statut: $($request.status)" -ForegroundColor Yellow
} catch {
    Write-Host "⚠️ Attention: Erreur lors de la création de la demande" -ForegroundColor Yellow
    Write-Host "   Détails: $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "   TESTS TERMINÉS" -ForegroundColor Yellow
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "🎉 Vérifiez les résultats dans phpMyAdmin:" -ForegroundColor Green
Write-Host "   http://localhost/phpmyadmin" -ForegroundColor Cyan
Write-Host ""
Write-Host "📊 Données créées:" -ForegroundColor Green
Write-Host "   - 1 Département (Ressources Humaines)" -ForegroundColor White
Write-Host "   - 1 Type de Congé (Congé Annuel)" -ForegroundColor White
Write-Host "   - 1 Employé (Jean Dupont)" -ForegroundColor White
Write-Host "   - 1 Solde de Congé (22 jours)" -ForegroundColor White
Write-Host "   - 1 Demande de Congé (PENDING)" -ForegroundColor White
Write-Host ""

