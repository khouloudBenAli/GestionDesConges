# 📚 Explication du Flux API vers Base de Données

## 🔄 Vue d'Ensemble du Flux

Voici comment une requête API se transforme en opération de base de données dans votre application Spring Boot :

```
Client (Postman/Browser)
         ↓
    [HTTP Request]
         ↓
    🌐 Controller (DepartmentController)
         ↓
    🧠 Service (DepartmentService)
         ↓
    💾 Repository (DepartmentRepository)
         ↓
    🗄️ Base de Données (MySQL/H2)
```

---

## 📋 Exemple Concret : GET /api/departments/{id}

### 1️⃣ **Étape 1 : Le Client fait une Requête**

```http
GET http://localhost:8080/api/departments/1
```

### 2️⃣ **Étape 2 : Le Controller Reçoit la Requête**

**Fichier : `DepartmentController.java`**

```java
@RestController
@RequestMapping("/api/departments")  // ← Toutes les routes commencent par /api/departments
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;  // ← Injection du Service

    /**
     * Cette méthode intercepte la requête GET /api/departments/{id}
     */
    @GetMapping("/{id}")  // ← Spring mappe automatiquement cette route
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        try {
            // 🎯 ÉTAPE 2A : Le controller appelle le service
            Department department = departmentService.getDepartmentById(id);
            
            // 🎯 ÉTAPE 2B : Retourne la réponse HTTP avec le département trouvé
            return new ResponseEntity<>(department, HttpStatus.OK);
        } catch (RuntimeException e) {
            // 🎯 ÉTAPE 2C : Si non trouvé, retourne 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
```

**Ce qui se passe ici :**
- `@RestController` : Indique que c'est un contrôleur REST
- `@GetMapping("/{id}")` : Spring intercepte les requêtes GET avec un paramètre id
- `@PathVariable Long id` : Spring extrait automatiquement l'ID de l'URL
- Le contrôleur **ne contient AUCUNE logique métier**, il délègue tout au service

---

### 3️⃣ **Étape 3 : Le Service Traite la Logique Métier**

**Fichier : `DepartmentService.java`**

```java
@Service  // ← Indique que c'est un service Spring
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;  // ← Injection du Repository

    /**
     * Cette méthode contient la logique métier
     */
    public Department getDepartmentById(Long id) {
        // 🎯 ÉTAPE 3A : Le service appelle le repository
        return departmentRepository.findById(id)
                // 🎯 ÉTAPE 3B : Si trouvé, retourne le département
                .orElseThrow(() -> 
                    // 🎯 ÉTAPE 3C : Si non trouvé, lance une exception
                    new RuntimeException("Department not found with ID: " + id)
                );
    }
}
```

**Ce qui se passe ici :**
- `@Service` : Spring gère ce composant comme un service
- Le service contient la **logique métier** (validation, calculs, règles)
- Il appelle le repository pour accéder à la base de données
- Il peut transformer les données avant de les retourner

---

### 4️⃣ **Étape 4 : Le Repository Accède à la Base de Données**

**Fichier : `DepartmentRepository.java`**

```java
@Repository  // ← Indique que c'est un repository Spring Data JPA
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // 🎯 Cette méthode est AUTOMATIQUEMENT implémentée par Spring Data JPA
    // Pas besoin d'écrire le code SQL !
    Optional<Department> findById(Long id);
    
    // 🎯 Autres méthodes automatiques :
    List<Department> findAll();           // SELECT * FROM departments
    Department save(Department dept);      // INSERT ou UPDATE
    void deleteById(Long id);              // DELETE FROM departments WHERE id = ?
    
    // 🎯 Méthodes personnalisées - Spring génère le SQL automatiquement
    Optional<Department> findByName(String name);  
    // → Spring génère : SELECT * FROM departments WHERE name = ?
    
    // 🎯 Requêtes personnalisées avec @Query
    @Query("SELECT d FROM Department d WHERE d.employees IS EMPTY")
    List<Department> findDepartmentsWithoutEmployees();
}
```

**Ce qui se passe ici :**
- `JpaRepository<Department, Long>` : 
  - `Department` = Type d'entité
  - `Long` = Type de l'ID
- Spring Data JPA **génère automatiquement** l'implémentation
- **Vous n'écrivez PAS le code SQL** pour les opérations standard
- Spring traduit les méthodes en requêtes SQL

---

### 5️⃣ **Étape 5 : L'Entité Mappe la Table de Base de Données**

**Fichier : `Department.java`**

```java
@Entity  // ← Indique que c'est une entité JPA
@Table(name = "departments")  // ← Nom de la table dans la DB
@Data  // ← Lombok génère getters, setters, toString, etc.
public class Department {

    @Id  // ← Clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← Auto-incrémentée
    private Long id;

    @Column(nullable = false, unique = true)  // ← Colonne NOT NULL et UNIQUE
    private String name;

    private String description;

    @ManyToOne  // ← Relation plusieurs-à-un avec Employee
    @JoinColumn(name = "manager_id")  // ← Clé étrangère dans la table
    private Employee manager;

    @OneToMany(mappedBy = "department")  // ← Relation un-à-plusieurs
    @JsonIgnore  // ← Ne pas sérialiser en JSON (évite les boucles infinies)
    private List<Employee> employees = new ArrayList<>();
}
```

**Mapping avec la Base de Données :**

```sql
-- Table créée automatiquement par Hibernate
CREATE TABLE departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    manager_id BIGINT,
    FOREIGN KEY (manager_id) REFERENCES employees(id)
);
```

---

## 🔍 Qu'est-ce qui se Passe en Coulisses ?

### Étape 4A : Spring Data JPA Génère le SQL

Quand vous appelez `departmentRepository.findById(1)`, Spring Data JPA :

1. **Génère automatiquement la requête SQL :**
   ```sql
   SELECT 
       d.id, 
       d.name, 
       d.description, 
       d.manager_id
   FROM departments d
   WHERE d.id = 1
   ```

2. **Exécute la requête via JDBC**

3. **Mappe les résultats vers l'objet Java :**
   ```java
   Department dept = new Department();
   dept.setId(resultSet.getLong("id"));
   dept.setName(resultSet.getString("name"));
   dept.setDescription(resultSet.getString("description"));
   // etc.
   ```

4. **Retourne l'objet `Optional<Department>`**

---

## 📊 Exemple Complet avec Logs

Quand vous faites `GET http://localhost:8080/api/departments/1` :

```
2026-03-15 10:30:45 - Requête HTTP reçue : GET /api/departments/1
2026-03-15 10:30:45 - DepartmentController.getDepartmentById(1) appelé
2026-03-15 10:30:45 - DepartmentService.getDepartmentById(1) appelé
2026-03-15 10:30:45 - DepartmentRepository.findById(1) appelé
2026-03-15 10:30:45 - Hibernate: select d1_0.id, d1_0.name, d1_0.description, d1_0.manager_id from departments d1_0 where d1_0.id=?
2026-03-15 10:30:45 - Department trouvé : {id=1, name="Ressources Humaines"}
2026-03-15 10:30:45 - Réponse HTTP 200 OK envoyée
```

---

## 🎯 Autres Exemples d'Opérations

### POST - Créer un Département

```java
// Controller
@PostMapping
public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
    Department saved = departmentService.saveDepartment(department);
    return new ResponseEntity<>(saved, HttpStatus.CREATED);
}

// Service
public Department saveDepartment(Department department) {
    return departmentRepository.save(department);  // INSERT INTO departments...
}
```

**SQL Généré :**
```sql
INSERT INTO departments (name, description, manager_id) 
VALUES ('IT Department', 'Département informatique', NULL);
```

---

### PUT - Mettre à Jour un Département

```java
// Controller
@PutMapping("/{id}")
public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
    Department existing = departmentService.getDepartmentById(id);  // SELECT
    department.setId(id);
    Department updated = departmentService.saveDepartment(department);  // UPDATE
    return ResponseEntity.ok(updated);
}
```

**SQL Généré :**
```sql
-- 1. Vérification
SELECT * FROM departments WHERE id = 1;

-- 2. Mise à jour
UPDATE departments 
SET name = 'IT Department Updated', 
    description = 'Nouveau description', 
    manager_id = 5
WHERE id = 1;
```

---

### DELETE - Supprimer un Département

```java
// Controller
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
    departmentService.deleteDepartment(id);
    return ResponseEntity.noContent().build();
}

// Service
public void deleteDepartment(Long id) {
    Department dept = getDepartmentById(id);  // SELECT
    if (!dept.getEmployees().isEmpty()) {
        throw new RuntimeException("Department has employees!");
    }
    departmentRepository.deleteById(id);  // DELETE
}
```

**SQL Généré :**
```sql
-- 1. Vérification
SELECT * FROM departments WHERE id = 1;

-- 2. Suppression
DELETE FROM departments WHERE id = 1;
```

---

## 🔧 Configuration de la Base de Données

**Fichier : `application.properties`**

```properties
# Configuration MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/conges_db
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuration JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update  # Crée/met à jour les tables automatiquement
spring.jpa.show-sql=true              # Affiche les requêtes SQL dans les logs
spring.jpa.properties.hibernate.format_sql=true  # Formate les requêtes
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

---

## 📝 Résumé du Flux

```
1. CLIENT envoie : GET /api/departments/1
                    ↓
2. CONTROLLER reçoit la requête et extrait l'ID (1)
                    ↓
3. SERVICE applique la logique métier
                    ↓
4. REPOSITORY génère et exécute le SQL
                    ↓
5. BASE DE DONNÉES retourne les données
                    ↓
6. HIBERNATE mappe les données vers l'objet Java
                    ↓
7. CONTROLLER retourne la réponse JSON au client
```

---

## 💡 Points Clés à Retenir

1. **Architecture en Couches** : Controller → Service → Repository → Database
2. **Séparation des Responsabilités** :
   - Controller = Gestion HTTP
   - Service = Logique métier
   - Repository = Accès aux données
   - Entity = Représentation des données

3. **Spring Data JPA** génère automatiquement le SQL
4. **Hibernate** mappe les objets Java vers les tables SQL
5. **@Annotations** configurent tout le comportement

---

## 🎓 Pour Aller Plus Loin

### Méthodes de Recherche Personnalisées

```java
// Spring génère automatiquement le SQL basé sur le nom de la méthode !
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    // WHERE name = ?
    List<Department> findByName(String name);
    
    // WHERE name LIKE %?%
    List<Department> findByNameContaining(String keyword);
    
    // WHERE manager_id = ?
    List<Department> findByManagerId(Long managerId);
    
    // WHERE name = ? AND manager_id = ?
    List<Department> findByNameAndManagerId(String name, Long managerId);
    
    // COUNT(*) WHERE ...
    long countByManagerId(Long managerId);
    
    // EXISTS WHERE name = ?
    boolean existsByName(String name);
}
```

### Requêtes Complexes avec @Query

```java
@Query("SELECT d FROM Department d LEFT JOIN d.employees e WHERE SIZE(d.employees) > :minSize")
List<Department> findDepartmentsWithMinEmployees(@Param("minSize") int minSize);

@Query(value = "SELECT * FROM departments WHERE YEAR(created_date) = ?1", nativeQuery = true)
List<Department> findDepartmentsCreatedInYear(int year);
```

---

## ✅ Checklist de Compréhension

- [ ] Je comprends le rôle de chaque couche (Controller, Service, Repository)
- [ ] Je sais comment Spring mappe les URLs vers les méthodes du Controller
- [ ] Je comprends comment JPA génère automatiquement le SQL
- [ ] Je sais ce que font les annotations @Entity, @Table, @Column, etc.
- [ ] Je comprends la différence entre findById(), save(), et delete()
- [ ] Je peux créer mes propres méthodes de recherche dans le Repository

---

**Créé le 15 Mars 2026 pour le projet de gestion des congés**

