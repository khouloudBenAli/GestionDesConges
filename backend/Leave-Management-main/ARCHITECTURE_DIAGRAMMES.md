# 🏗️ Architecture et Diagrammes du Projet Congés

## 📐 Diagramme 1 : Architecture en Couches (MVC)

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                         │
│  (Postman, Browser, Mobile App, Angular/React Frontend)    │
└─────────────────────────────────────────────────────────────┘
                              ↕ HTTP (JSON)
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                      │
│                      (@RestController)                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  Department  │  │   Employee   │  │ LeaveRequest │     │
│  │  Controller  │  │  Controller  │  │  Controller  │ ... │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
│  • Reçoit les requêtes HTTP                                 │
│  • Valide les données d'entrée                              │
│  • Retourne les réponses HTTP                               │
└─────────────────────────────────────────────────────────────┘
                              ↕
┌─────────────────────────────────────────────────────────────┐
│                      BUSINESS LAYER                         │
│                        (@Service)                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  Department  │  │   Employee   │  │ LeaveRequest │     │
│  │   Service    │  │   Service    │  │   Service    │ ... │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
│  • Logique métier                                           │
│  • Règles de gestion                                        │
│  • Validations complexes                                    │
│  • Transactions                                             │
└─────────────────────────────────────────────────────────────┘
                              ↕
┌─────────────────────────────────────────────────────────────┐
│                     PERSISTENCE LAYER                       │
│                       (@Repository)                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  Department  │  │   Employee   │  │ LeaveRequest │     │
│  │  Repository  │  │  Repository  │  │  Repository  │ ... │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
│  • Accès aux données                                        │
│  • Requêtes SQL (générées automatiquement)                  │
│  • Mapping objet-relationnel                                │
└─────────────────────────────────────────────────────────────┘
                              ↕ JDBC
┌─────────────────────────────────────────────────────────────┐
│                        DATA LAYER                           │
│                    (@Entity + Database)                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ departments  │  │  employees   │  │leave_requests│     │
│  │    TABLE     │  │    TABLE     │  │    TABLE     │ ... │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
│                  MySQL / H2 Database                        │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Diagramme 2 : Flux d'une Requête GET

```
┌──────────┐
│  Client  │  GET /api/departments/1
└──────────┘
     │
     ↓
┌─────────────────────────────────────────────────┐
│            Spring Boot Application              │
│                                                  │
│  1️⃣ DispatcherServlet                          │
│     • Reçoit la requête                         │
│     • Trouve le Controller approprié            │
│     └─→ @GetMapping("/{id}")                    │
│                                                  │
│  2️⃣ DepartmentController                       │
│     • getDepartmentById(1)                      │
│     • Extrait l'ID de l'URL                     │
│     │                                            │
│     ↓ departmentService.getDepartmentById(1)    │
│                                                  │
│  3️⃣ DepartmentService                          │
│     • Applique la logique métier                │
│     • Vérifie les règles de gestion             │
│     │                                            │
│     ↓ departmentRepository.findById(1)          │
│                                                  │
│  4️⃣ DepartmentRepository (Interface)           │
│     • Spring Data JPA génère l'implémentation   │
│     │                                            │
│     ↓                                            │
│                                                  │
│  5️⃣ Hibernate                                   │
│     • Génère : SELECT * FROM departments        │
│     •          WHERE id = 1                     │
│     │                                            │
│     ↓ JDBC                                       │
└─────────────────────────────────────────────────┘
     │
     ↓
┌──────────────────┐
│  MySQL Database  │
│                  │
│  departments     │
│  ┌──┬────────┐  │
│  │1 │RH Dept │ ←─ Ligne trouvée
│  └──┴────────┘  │
└──────────────────┘
     │
     ↓ Résultat
┌─────────────────────────────────────────────────┐
│            Spring Boot Application              │
│                                                  │
│  6️⃣ Hibernate                                   │
│     • Mappe les colonnes SQL → objet Java       │
│     • Department dept = new Department()        │
│     • dept.setId(1), dept.setName("RH Dept")   │
│     │                                            │
│     ↓ Optional<Department>                      │
│                                                  │
│  7️⃣ DepartmentService                          │
│     • .orElseThrow() → Extrait le Department    │
│     │                                            │
│     ↓ Department                                 │
│                                                  │
│  8️⃣ DepartmentController                       │
│     • new ResponseEntity<>(dept, OK)            │
│     │                                            │
│     ↓                                            │
│                                                  │
│  9️⃣ Jackson (Sérialisation JSON)               │
│     • Convertit l'objet Java en JSON            │
│     • { "id": 1, "name": "RH Dept" }           │
└─────────────────────────────────────────────────┘
     │
     ↓ HTTP 200 OK + JSON
┌──────────┐
│  Client  │  Reçoit : {"id":1,"name":"RH Dept"}
└──────────┘
```

---

## 🗂️ Diagramme 3 : Mapping Entité ↔ Table

```
┌───────────────────────────────────────────────────┐
│          Java Entity (Department.java)            │
├───────────────────────────────────────────────────┤
│  @Entity                                          │
│  @Table(name = "departments")                     │
│  public class Department {                        │
│                                                   │
│    @Id                                            │
│    @GeneratedValue(strategy = IDENTITY)           │
│    private Long id; ─────────────────────┐       │
│                                           │       │
│    @Column(nullable = false, unique = true)       │
│    private String name; ──────────────────┼───┐  │
│                                           │   │  │
│    private String description; ───────────┼───┼┐ │
│                                           │   ││ │
│    @ManyToOne                             │   ││ │
│    @JoinColumn(name = "manager_id")       │   ││ │
│    private Employee manager; ─────────────┼───┼┼┐│
│  }                                        │   │││││
└───────────────────────────────────────────┼───┼┼┼┘
                                            │   │││
                    Hibernate fait le Mapping│   │││
                                            ↓   ↓↓↓
┌───────────────────────────────────────────────────┐
│        MySQL Table (departments)                  │
├──────────┬─────────────┬──────────┬──────────────┤
│   id     │    name     │description│  manager_id  │
│ (BIGINT) │ (VARCHAR)   │(VARCHAR) │   (BIGINT)   │
│  PK      │  NOT NULL   │ NULLABLE │  FK→employees│
│  AUTO    │  UNIQUE     │          │              │
├──────────┼─────────────┼──────────┼──────────────┤
│    1     │ RH Dept     │ Gère RH  │    NULL      │
│    2     │ IT Dept     │ Info     │      5       │
│    3     │ Sales       │ Ventes   │      7       │
└──────────┴─────────────┴──────────┴──────────────┘
```

---

## 🔗 Diagramme 4 : Relations entre Entités

```
┌──────────────────┐
│   Department     │
│                  │
│  - id            │
│  - name          │
│  - description   │
└──────────────────┘
        │ 1
        │ manager
        │
        │ N
        ↓
┌──────────────────┐
│    Employee      │◄───────────┐
│                  │            │
│  - id            │            │
│  - firstName     │            │
│  - lastName      │            │
│  - email         │            │
│  - jobTitle      │            │
│  - hireDate      │            │
└──────────────────┘            │
        │ 1                     │
        │ employee              │ 1
        │                       │
        │ N                     │
        ↓                       │
┌──────────────────┐            │
│  LeaveRequest    │            │
│                  │            │
│  - id            │            │
│  - startDate     │            │
│  - endDate       │            │
│  - status        │ approvedBy │
│  - reason        │────────────┘
└──────────────────┘
        │ N
        │
        │ 1
        ↓
┌──────────────────┐
│    LeaveType     │
│                  │
│  - id            │
│  - name          │
│  - paidLeave     │
│  - defaultDays   │
└──────────────────┘


Légende :
1 = One (Un)
N = Many (Plusieurs)
@ManyToOne = Plusieurs entités pointent vers une
@OneToMany = Une entité a plusieurs références
```

---

## 🎯 Diagramme 5 : Flux POST (Création)

```
Client envoie :
POST /api/departments
Content-Type: application/json
{
  "name": "Finance",
  "description": "Département financier"
}
     │
     ↓
┌─────────────────────────────────────────┐
│  1. Controller                          │
│  @PostMapping                           │
│  createDepartment(@RequestBody dept)    │
│  • Spring convertit JSON → objet Java   │
└─────────────────────────────────────────┘
     │
     ↓ Department object
┌─────────────────────────────────────────┐
│  2. Service                             │
│  saveDepartment(dept)                   │
│  • Vérifie que le nom n'existe pas      │
│  • Applique les règles métier           │
└─────────────────────────────────────────┘
     │
     ↓ Validated Department
┌─────────────────────────────────────────┐
│  3. Repository                          │
│  save(dept)                             │
│  • Spring Data JPA génère INSERT        │
└─────────────────────────────────────────┘
     │
     ↓ SQL généré par Hibernate
┌─────────────────────────────────────────┐
│  INSERT INTO departments                │
│  (name, description)                    │
│  VALUES                                 │
│  ('Finance', 'Département financier')   │
└─────────────────────────────────────────┘
     │
     ↓ Exécution dans MySQL
┌─────────────────────────────────────────┐
│  MySQL retourne :                       │
│  • ID généré = 4                        │
│  • Nombre de lignes affectées = 1       │
└─────────────────────────────────────────┘
     │
     ↓ Hibernate met à jour l'objet
┌─────────────────────────────────────────┐
│  Department dept                        │
│  - id = 4  (maintenant rempli)         │
│  - name = "Finance"                     │
│  - description = "Département financier"│
└─────────────────────────────────────────┘
     │
     ↓ Retour au client
HTTP 201 CREATED
{
  "id": 4,
  "name": "Finance",
  "description": "Département financier"
}
```

---

## ⚙️ Diagramme 6 : Injection de Dépendances

```
┌────────────────────────────────────────┐
│      Spring IoC Container              │
│   (Inversion of Control Container)     │
│                                        │
│  Au démarrage, Spring :                │
│  1. Scan tous les packages             │
│  2. Trouve les @Component, @Service... │
│  3. Crée des instances (beans)         │
│  4. Les stocke dans le Container       │
└────────────────────────────────────────┘
              │
              ↓ Crée et gère
┌─────────────────────────────────────────┐
│  Bean 1: DepartmentController           │
│  @RestController                        │
│                                         │
│  @Autowired                             │
│  private DepartmentService service; ←───┐
└─────────────────────────────────────────┘│
                                           │
┌─────────────────────────────────────────┐│
│  Bean 2: DepartmentService              ││
│  @Service                               ││
│                                         ││
│  @Autowired                             ││ Spring injecte
│  private DepartmentRepository repo; ←───┼─ automatiquement
└─────────────────────────────────────────┘│
                                           │
┌─────────────────────────────────────────┐│
│  Bean 3: DepartmentRepositoryImpl       ││
│  (généré par Spring Data JPA)           ││
│  implements DepartmentRepository        ││
└─────────────────────────────────────────┘

Avantage :
• Vous n'appelez JAMAIS "new DepartmentService()"
• Spring gère tout automatiquement
• Facilite les tests (mock injection)
```

---

## 🔐 Diagramme 7 : Sécurité Spring Security

```
Client fait : GET /api/departments
     │
     ↓
┌──────────────────────────────────────────┐
│  Spring Security Filter Chain            │
│                                          │
│  1️⃣ DisableEncodeUrlFilter              │
│     ↓                                     │
│  2️⃣ WebAsyncManagerIntegrationFilter    │
│     ↓                                     │
│  3️⃣ SecurityContextHolderFilter         │
│     ↓                                     │
│  4️⃣ HeaderWriterFilter                  │
│     ↓                                     │
│  5️⃣ CsrfFilter ───→ Si CSRF activé      │
│     ↓                                     │
│  6️⃣ LogoutFilter                        │
│     ↓                                     │
│  7️⃣ AuthenticationFilter (si JWT)       │
│     • Vérifie le token                   │
│     • Extrait l'utilisateur              │
│     ↓                                     │
│  8️⃣ AuthorizationFilter                 │
│     • Vérifie les permissions            │
│     • Autorise ou refuse l'accès         │
│     ↓                                     │
│  ✅ Accès autorisé                       │
└──────────────────────────────────────────┘
     │
     ↓
Controller est appelé
```

**Configuration dans `SecurityConfig.java` :**

```java
http.authorizeRequests()
    .antMatchers("/api/departments/**").authenticated()  // Authentifié
    .antMatchers("/api/auth/**").permitAll()             // Public
    .anyRequest().authenticated();
```

---

## 📦 Diagramme 8 : Structure des Packages

```
com.rh.conges
│
├── 📁 config/              (Configuration)
│   ├── SecurityConfig.java
│   ├── WebMvcConfig.java
│   └── SwaggerConfig.java
│
├── 📁 controller/          (Présentation)
│   ├── DepartmentController.java
│   ├── EmployeeController.java
│   └── LeaveRequestController.java
│
├── 📁 service/             (Logique métier)
│   ├── DepartmentService.java
│   ├── EmployeeService.java
│   └── LeaveRequestService.java
│
├── 📁 repository/          (Accès aux données)
│   ├── DepartmentRepository.java
│   ├── EmployeeRepository.java
│   └── LeaveRequestRepository.java
│
├── 📁 model/               (Entités)
│   ├── Department.java
│   ├── Employee.java
│   ├── LeaveRequest.java
│   └── LeaveStatus.java (enum)
│
├── 📁 dto/                 (Data Transfer Objects)
│   ├── LeaveRequestDTO.java
│   └── EmployeeDTO.java
│
├── 📁 exception/           (Gestion des erreurs)
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
│
└── CongesApplication.java  (Point d'entrée)
```

**Principe de séparation :**
- Chaque package a une responsabilité unique
- Facilite la maintenance
- Améliore la testabilité

---

## 🧪 Diagramme 9 : Cycle de Vie d'une Entité JPA

```
┌─────────────┐
│   NEW       │  Department dept = new Department();
│  (Transient)│  dept.setName("Finance");
└─────────────┘  // L'objet existe en mémoire uniquement
      │
      ↓ repository.save(dept)
┌─────────────┐
│  MANAGED    │  Spring track les modifications
│  (Persistent)│  Changements automatiquement synchronisés
└─────────────┘
      │
      ↓ Transaction commit
┌─────────────┐
│  DETACHED   │  entityManager.detach(dept)
│             │  Plus suivi par Hibernate
└─────────────┘
      │
      ↓ repository.delete(dept)
┌─────────────┐
│  REMOVED    │  Marqué pour suppression
│             │  DELETE SQL lors du commit
└─────────────┘
```

---

## 📊 Diagramme 10 : Transactions

```
Service Method avec @Transactional
     │
     ↓ Début de transaction
┌──────────────────────────────────┐
│  1. Open Transaction             │
│     BEGIN                        │
└──────────────────────────────────┘
     │
     ↓
┌──────────────────────────────────┐
│  2. Execute Business Logic       │
│     • dept.setName("New Name")   │
│     • repo.save(dept)            │
│     • employee.setDept(dept)     │
│     • empRepo.save(employee)     │
└──────────────────────────────────┘
     │
     ↓ Tout réussit ?
┌──────────────────────────────────┐
│  3a. ✅ COMMIT                   │
│      Toutes les modifications    │
│      sont sauvegardées           │
└──────────────────────────────────┘

     OU

┌──────────────────────────────────┐
│  3b. ❌ ROLLBACK                 │
│      Si une erreur survient,     │
│      TOUTES les modifications    │
│      sont annulées               │
└──────────────────────────────────┘
```

**Exemple de code :**

```java
@Transactional
public void transferEmployee(Long empId, Long newDeptId) {
    // Tout dans cette méthode = 1 transaction
    Employee emp = employeeRepo.findById(empId).get();
    Department newDept = deptRepo.findById(newDeptId).get();
    
    emp.setDepartment(newDept);
    employeeRepo.save(emp);
    
    // Si erreur ici ↓ , TOUT est annulé
    if (newDept.getEmployees().size() > 100) {
        throw new RuntimeException("Department full!");
        // ROLLBACK automatique
    }
    
    // Si on arrive ici, COMMIT automatique
}
```

---

## 🎓 Résumé Visuel

```
┌───────────────────────────────────────────────────────────┐
│                   VOTRE APPLICATION                       │
│                                                           │
│  HTTP Request → Controller → Service → Repository        │
│       ↓             ↓          ↓          ↓              │
│    JSON         Routing   Business    SQL Query          │
│                             Logic                         │
│                                                           │
│  HTTP Response ← JSON ← Java Object ← Database Result    │
└───────────────────────────────────────────────────────────┘

Tout est automatisé par Spring Boot ! 🚀
```

---

**Créé le 15 Mars 2026 - Diagrammes du projet de gestion des congés**

