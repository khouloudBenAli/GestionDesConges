# 💻 Exemples de Code Annotés - Comprendre Chaque Ligne

## 📝 Exemple 1 : Controller Complet Annoté

```java
// Package = organisation logique du code
package com.rh.conges.controller;

// Imports nécessaires
import com.rh.conges.model.Department;
import com.rh.conges.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController = Cette classe gère les requêtes HTTP REST
// Spring crée automatiquement une instance de cette classe au démarrage
@RestController

// @RequestMapping = Toutes les routes de ce controller commencent par "/api/departments"
@RequestMapping("/api/departments")
public class DepartmentController {

    // @Autowired = Spring injecte automatiquement une instance de DepartmentService
    // Vous n'avez pas besoin d'écrire : service = new DepartmentService()
    @Autowired
    private DepartmentService departmentService;

    /**
     * EXEMPLE 1 : GET /api/departments
     * Récupère TOUS les départements
     */
    // @GetMapping = Cette méthode gère les requêtes GET sur /api/departments
    @GetMapping
    // ResponseEntity<List<Department>> = Réponse HTTP contenant une liste de départements
    public ResponseEntity<List<Department>> getAllDepartments() {
        
        // 1. Appelle le service pour récupérer tous les départements
        List<Department> departments = departmentService.getAllDepartments();
        
        // 2. Crée une réponse HTTP 200 OK contenant la liste
        return new ResponseEntity<>(departments, HttpStatus.OK);
        
        // Spring Boot convertit automatiquement la liste en JSON :
        // [{"id":1,"name":"RH"}, {"id":2,"name":"IT"}]
    }

    /**
     * EXEMPLE 2 : GET /api/departments/1
     * Récupère UN département par son ID
     */
    // @GetMapping("/{id}") = URL variable, {id} sera extrait par Spring
    @GetMapping("/{id}")
    // @PathVariable Long id = Spring extrait l'ID de l'URL et le convertit en Long
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        
        try {
            // 1. Cherche le département
            Department department = departmentService.getDepartmentById(id);
            
            // 2. Si trouvé, retourne 200 OK avec le département
            return new ResponseEntity<>(department, HttpStatus.OK);
            
        } catch (RuntimeException e) {
            // 3. Si non trouvé (exception lancée), retourne 404 NOT FOUND
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * EXEMPLE 3 : POST /api/departments
     * Crée un NOUVEAU département
     */
    // @PostMapping = Cette méthode gère les requêtes POST
    @PostMapping
    // @RequestBody = Spring convertit automatiquement le JSON reçu en objet Department
    // Exemple JSON : {"name":"Finance","description":"Dept finances"}
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        
        // À ce stade, 'department' est déjà un objet Java créé par Spring
        // department.getName() retourne "Finance"
        
        // 1. Sauvegarde le département (génère un INSERT SQL)
        Department savedDepartment = departmentService.saveDepartment(department);
        
        // 2. Retourne 201 CREATED avec le département créé (contenant l'ID généré)
        return new ResponseEntity<>(savedDepartment, HttpStatus.CREATED);
        
        // Spring convertit savedDepartment en JSON :
        // {"id":4,"name":"Finance","description":"Dept finances"}
    }

    /**
     * EXEMPLE 4 : PUT /api/departments/1
     * Met à JOUR un département existant
     */
    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(
            @PathVariable Long id,              // ID extrait de l'URL
            @RequestBody Department department  // Nouvelles données en JSON
    ) {
        try {
            // 1. Vérifie que le département existe
            departmentService.getDepartmentById(id);
            
            // 2. Assure que l'ID est correct (évite de créer un nouveau département)
            department.setId(id);
            
            // 3. Sauvegarde les modifications (génère un UPDATE SQL)
            Department updatedDepartment = departmentService.saveDepartment(department);
            
            // 4. Retourne 200 OK avec le département mis à jour
            return new ResponseEntity<>(updatedDepartment, HttpStatus.OK);
            
        } catch (RuntimeException e) {
            // Si le département n'existe pas, retourne 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * EXEMPLE 5 : DELETE /api/departments/1
     * SUPPRIME un département
     */
    @DeleteMapping("/{id}")
    // ResponseEntity<Void> = Pas de corps de réponse, juste un code HTTP
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        try {
            // 1. Vérifie que le département existe
            departmentService.getDepartmentById(id);
            
            // 2. Supprime le département (génère un DELETE SQL)
            departmentService.deleteDepartment(id);
            
            // 3. Retourne 204 NO CONTENT (succès sans contenu)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            
        } catch (RuntimeException e) {
            // Si erreur (département non trouvé ou a des employés), retourne 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
```

**Résumé du Controller :**
- ✅ Gère les requêtes HTTP
- ✅ Extrait les paramètres (URL, JSON)
- ✅ Appelle le service
- ✅ Retourne les réponses HTTP
- ❌ NE contient PAS de logique métier
- ❌ NE fait PAS d'accès direct à la base de données

---

## 🧠 Exemple 2 : Service Complet Annoté

```java
package com.rh.conges.service;

import com.rh.conges.model.Department;
import com.rh.conges.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @Service = Spring gère cette classe comme un service
// Une seule instance est créée au démarrage (singleton)
@Service
public class DepartmentService {

    // Injection du repository
    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * EXEMPLE 1 : Récupérer tous les départements
     * Méthode simple sans logique métier complexe
     */
    public List<Department> getAllDepartments() {
        // Appelle directement le repository
        // Le repository génère : SELECT * FROM departments
        return departmentRepository.findAll();
    }

    /**
     * EXEMPLE 2 : Récupérer un département par ID
     * Avec gestion d'erreur
     */
    public Department getDepartmentById(Long id) {
        // findById() retourne un Optional<Department>
        // Optional = peut contenir un département OU être vide
        
        return departmentRepository.findById(id)
                // .orElseThrow() = Si vide, lance une exception
                .orElseThrow(() -> 
                    new RuntimeException("Department not found with ID: " + id)
                );
        
        // Équivalent à :
        // Optional<Department> optional = departmentRepository.findById(id);
        // if (optional.isPresent()) {
        //     return optional.get();
        // } else {
        //     throw new RuntimeException("Not found");
        // }
    }

    /**
     * EXEMPLE 3 : Sauvegarder un département
     * AVEC logique métier (validation)
     */
    // @Transactional = Tout dans cette méthode est une transaction
    // Si une erreur survient, TOUT est annulé (rollback)
    @Transactional
    public Department saveDepartment(Department department) {
        
        // LOGIQUE MÉTIER 1 : Vérifier que le nom n'existe pas déjà
        if (department.getName() != null) {
            
            // Cherche un département avec le même nom
            Optional<Department> existing = departmentRepository.findByName(department.getName());
            
            if (existing.isPresent()) {
                // Si trouvé ET que c'est un département différent
                if (department.getId() == null || 
                    !existing.get().getId().equals(department.getId())) {
                    
                    // Lance une exception = arrête la méthode
                    throw new RuntimeException(
                        "Department with name '" + department.getName() + "' already exists"
                    );
                }
            }
        }
        
        // LOGIQUE MÉTIER 2 : On pourrait ajouter d'autres validations
        // Par exemple :
        // - Vérifier la longueur du nom
        // - Normaliser le texte (trim, lowercase)
        // - Valider le format
        // - Envoyer une notification
        // - Logger l'action
        
        // Si tout est OK, sauvegarde
        // save() génère soit un INSERT (si id = null) soit un UPDATE
        return departmentRepository.save(department);
    }

    /**
     * EXEMPLE 4 : Supprimer un département
     * AVEC vérifications métier
     */
    @Transactional
    public void deleteDepartment(Long id) {
        
        // 1. Récupère le département (lance une exception si non trouvé)
        Department department = getDepartmentById(id);
        
        // 2. RÈGLE MÉTIER : Ne pas supprimer un département qui a des employés
        if (!department.getEmployees().isEmpty()) {
            throw new RuntimeException(
                "Cannot delete department with employees. Reassign employees first."
            );
        }
        
        // 3. RÈGLE MÉTIER : On pourrait vérifier d'autres choses
        // - Le département est-il utilisé dans des rapports ?
        // - Y a-t-il des congés en attente pour ce département ?
        // - Est-ce un département système non supprimable ?
        
        // 4. Si tout est OK, supprime
        // deleteById() génère : DELETE FROM departments WHERE id = ?
        departmentRepository.deleteById(id);
        
        // 5. On pourrait aussi faire des actions post-suppression
        // - Logger l'action
        // - Notifier les administrateurs
        // - Archiver les données
    }

    /**
     * EXEMPLE 5 : Méthode avec logique métier complexe
     */
    @Transactional
    public void assignManager(Long departmentId, Long managerId) {
        
        // 1. Récupère le département
        Department department = getDepartmentById(departmentId);
        
        // 2. Récupère le manager (suppose qu'on a un EmployeeService)
        // Employee manager = employeeService.getEmployeeById(managerId);
        
        // 3. RÈGLE MÉTIER : Le manager doit être du même département ?
        // if (!manager.getDepartment().equals(department)) {
        //     throw new RuntimeException("Manager must be from the same department");
        // }
        
        // 4. RÈGLE MÉTIER : Le manager doit avoir un certain niveau ?
        // if (!manager.getJobTitle().contains("Manager")) {
        //     throw new RuntimeException("Employee must have Manager title");
        // }
        
        // 5. Assigne le manager
        // department.setManager(manager);
        
        // 6. Sauvegarde
        departmentRepository.save(department);
        
        // 7. Notification ?
        // notificationService.notifyNewManager(manager, department);
    }

    /**
     * EXEMPLE 6 : Méthode utilisant une requête personnalisée
     */
    public List<Department> getDepartmentsWithoutEmployees() {
        // Appelle une méthode personnalisée du repository
        // Celle-ci utilise une @Query JPQL
        return departmentRepository.findDepartmentsWithoutEmployees();
    }

    /**
     * EXEMPLE 7 : Méthode avec calculs métier
     */
    public long countEmployeesInDepartment(Long departmentId) {
        // Utilise une requête personnalisée pour compter
        return departmentRepository.countEmployeesByDepartmentId(departmentId);
    }
}
```

**Résumé du Service :**
- ✅ Contient la logique métier
- ✅ Fait des validations
- ✅ Gère les transactions
- ✅ Orchestre plusieurs appels repository
- ❌ NE gère PAS les requêtes HTTP
- ❌ NE génère PAS de SQL directement

---

## 💾 Exemple 3 : Repository Annoté

```java
package com.rh.conges.repository;

import com.rh.conges.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// @Repository = Spring gère cette interface comme un repository
@Repository
// JpaRepository<Department, Long> :
// - Department = Type d'entité gérée
// - Long = Type de la clé primaire (ID)
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES AUTOMATIQUES (héritées de JpaRepository)
    // Vous n'avez RIEN à écrire, Spring les génère automatiquement !
    // ═══════════════════════════════════════════════════════════
    
    // findAll() → SELECT * FROM departments
    // findById(Long id) → SELECT * FROM departments WHERE id = ?
    // save(Department d) → INSERT ou UPDATE
    // deleteById(Long id) → DELETE FROM departments WHERE id = ?
    // count() → SELECT COUNT(*) FROM departments
    // existsById(Long id) → SELECT EXISTS(SELECT 1 FROM departments WHERE id = ?)

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES PAR CONVENTION DE NOMMAGE
    // Spring génère le SQL basé sur le nom de la méthode !
    // ═══════════════════════════════════════════════════════════

    /**
     * EXEMPLE 1 : Recherche par nom exact
     * Nom de méthode : findByName
     * Spring génère : SELECT * FROM departments WHERE name = ?
     */
    Optional<Department> findByName(String name);

    /**
     * EXEMPLE 2 : Vérifier l'existence par nom
     * Nom de méthode : existsByName
     * Spring génère : SELECT EXISTS(SELECT 1 FROM departments WHERE name = ?)
     */
    boolean existsByName(String name);

    /**
     * EXEMPLE 3 : Recherche par manager
     * Nom de méthode : findByManagerId
     * Spring génère : SELECT * FROM departments WHERE manager_id = ?
     */
    List<Department> findByManagerId(Long managerId);

    /**
     * EXEMPLE 4 : Recherche avec LIKE (insensible à la casse)
     * Nom de méthode : findByNameContainingIgnoreCase
     * Spring génère : SELECT * FROM departments WHERE LOWER(name) LIKE LOWER(?)
     */
    List<Department> findByNameContainingIgnoreCase(String namePattern);

    /**
     * EXEMPLE 5 : Recherche avec plusieurs critères
     * Nom de méthode : findByNameAndManagerId
     * Spring génère : SELECT * FROM departments WHERE name = ? AND manager_id = ?
     */
    List<Department> findByNameAndManagerId(String name, Long managerId);

    // ═══════════════════════════════════════════════════════════
    // REQUÊTES PERSONNALISÉES AVEC @Query
    // Pour des requêtes complexes
    // ═══════════════════════════════════════════════════════════

    /**
     * EXEMPLE 6 : Requête JPQL personnalisée
     * JPQL = Java Persistence Query Language (orienté objet, pas SQL)
     */
    // @Query = Requête personnalisée
    @Query("SELECT DISTINCT d FROM Department d JOIN d.employees e")
    // "d" = alias pour Department
    // "JOIN d.employees" = jointure avec la liste employees de l'entité
    List<Department> findDepartmentsWithEmployees();

    /**
     * EXEMPLE 7 : Requête JPQL avec condition
     */
    @Query("SELECT d FROM Department d WHERE d.employees IS EMPTY")
    // "IS EMPTY" = condition JPQL pour vérifier qu'une collection est vide
    List<Department> findDepartmentsWithoutEmployees();

    /**
     * EXEMPLE 8 : Requête avec paramètre nommé
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    // :departmentId = paramètre nommé
    // @Param("departmentId") = lie le paramètre Java au paramètre JPQL
    long countEmployeesByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * EXEMPLE 9 : Requête native SQL
     * Quand JPQL n'est pas suffisant
     */
    @Query(
        value = "SELECT * FROM departments d " +
                "WHERE YEAR(d.created_date) = :year",
        nativeQuery = true  // Indique que c'est du SQL natif, pas JPQL
    )
    List<Department> findDepartmentsCreatedInYear(@Param("year") int year);

    /**
     * EXEMPLE 10 : Requête complexe avec jointures
     */
    @Query(
        "SELECT d.id, d.name, COUNT(e) " +
        "FROM Department d LEFT JOIN d.employees e " +
        "GROUP BY d.id, d.name"
    )
    List<Object[]> findDepartmentsWithEmployeeCount();
    // Object[] car le résultat contient plusieurs colonnes de types différents
    // [0] = Long (id), [1] = String (name), [2] = Long (count)
}

// ═══════════════════════════════════════════════════════════
// CONVENTION DE NOMMAGE SPRING DATA JPA
// ═══════════════════════════════════════════════════════════
/*
 * Préfixes :
 * - find...By → SELECT
 * - count...By → SELECT COUNT(*)
 * - exists...By → SELECT EXISTS
 * - delete...By → DELETE
 * 
 * Mots-clés :
 * - And → WHERE ... AND ...
 * - Or → WHERE ... OR ...
 * - Between → WHERE ... BETWEEN ... AND ...
 * - LessThan → WHERE ... < ...
 * - GreaterThan → WHERE ... > ...
 * - Like → WHERE ... LIKE ...
 * - Containing → WHERE ... LIKE %...%
 * - StartingWith → WHERE ... LIKE ...%
 * - EndingWith → WHERE ... LIKE %...
 * - IgnoreCase → LOWER(...) = LOWER(...)
 * - OrderBy → ORDER BY ...
 * 
 * Exemples :
 * findByNameAndDescriptionContaining(String name, String desc)
 * → WHERE name = ? AND description LIKE %?%
 * 
 * findByCreatedDateBetweenOrderByNameAsc(Date start, Date end)
 * → WHERE created_date BETWEEN ? AND ? ORDER BY name ASC
 */
```

**Résumé du Repository :**
- ✅ Interface seulement (Spring génère l'implémentation)
- ✅ Méthodes automatiques via héritage de JpaRepository
- ✅ Méthodes par convention de nommage
- ✅ Requêtes personnalisées avec @Query
- ❌ NE contient PAS de logique métier
- ❌ NE contient PAS de code d'implémentation

---

## 🏛️ Exemple 4 : Entity Annotée

```java
package com.rh.conges.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

// @Entity = Cette classe représente une table dans la base de données
@Entity

// @Table = Configuration de la table
@Table(name = "departments")  // Nom de la table dans la DB
// On peut ajouter des contraintes :
// @Table(name = "departments", 
//        uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))

// @Data = Lombok génère automatiquement :
// - getters pour tous les champs
// - setters pour tous les champs
// - toString()
// - equals() et hashCode()
@Data

// @NoArgsConstructor = Lombok génère un constructeur sans paramètres
// Department dept = new Department();
@NoArgsConstructor

// @AllArgsConstructor = Lombok génère un constructeur avec tous les paramètres
// Department dept = new Department(1L, "Finance", "Desc", null, new ArrayList<>());
@AllArgsConstructor
public class Department {

    // ═══════════════════════════════════════════════════════════
    // CLÉ PRIMAIRE
    // ═══════════════════════════════════════════════════════════

    // @Id = Clé primaire de la table
    @Id
    
    // @GeneratedValue = Valeur générée automatiquement
    // IDENTITY = Auto-increment dans la base de données
    // MySQL génère : id BIGINT AUTO_INCREMENT PRIMARY KEY
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ═══════════════════════════════════════════════════════════
    // COLONNES SIMPLES
    // ═══════════════════════════════════════════════════════════

    // @NotBlank = Validation Jakarta : ne peut pas être null ou vide
    @NotBlank(message = "Department name is required")
    
    // @Size = Validation de la taille
    @Size(min = 2, max = 100, message = "Department name must be between 2 and 100 characters")
    
    // @Column = Configuration de la colonne
    @Column(
        nullable = false,  // NOT NULL dans la DB
        unique = true      // UNIQUE dans la DB
    )
    private String name;
    // MySQL génère : name VARCHAR(100) NOT NULL UNIQUE

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    // MySQL génère : description VARCHAR(500)

    // ═══════════════════════════════════════════════════════════
    // RELATIONS
    // ═══════════════════════════════════════════════════════════

    /**
     * RELATION MANY-TO-ONE
     * Plusieurs départements peuvent avoir le même manager
     */
    // @ManyToOne = Relation plusieurs-à-un
    @ManyToOne(
        fetch = FetchType.LAZY  // Chargement paresseux (charge seulement si nécessaire)
        // FetchType.EAGER = Chargement immédiat (charge toujours)
    )
    
    // @JoinColumn = Clé étrangère dans la table
    @JoinColumn(name = "manager_id")  // Nom de la colonne FK dans la DB
    private Employee manager;
    // MySQL génère : manager_id BIGINT, FOREIGN KEY (manager_id) REFERENCES employees(id)

    /**
     * RELATION ONE-TO-MANY
     * Un département a plusieurs employés
     */
    // @OneToMany = Relation un-à-plusieurs
    @OneToMany(
        mappedBy = "department",  // Nom du champ dans Employee qui référence Department
        cascade = CascadeType.ALL,  // Cascade toutes les opérations (save, delete, etc.)
        fetch = FetchType.LAZY  // Chargement paresseux
    )
    
    // @JsonIgnore = Ne pas sérialiser ce champ en JSON
    // Évite les boucles infinies : Department → Employee → Department → ...
    @JsonIgnore
    private List<Employee> employees = new ArrayList<>();
    // Pas de colonne dans la table departments
    // La relation est gérée par la colonne department_id dans la table employees

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES UTILITAIRES
    // ═══════════════════════════════════════════════════════════

    /**
     * Ajoute un employé à ce département
     * Gère la relation bidirectionnelle
     */
    public void addEmployee(Employee employee) {
        employees.add(employee);        // Ajoute à la liste
        employee.setDepartment(this);   // Met à jour la référence inverse
    }

    /**
     * Retire un employé de ce département
     */
    public void removeEmployee(Employee employee) {
        employees.remove(employee);     // Retire de la liste
        employee.setDepartment(null);   // Supprime la référence inverse
    }

    // ═══════════════════════════════════════════════════════════
    // CALLBACKS JPA (optionnels)
    // ═══════════════════════════════════════════════════════════

    /**
     * Appelé avant la première sauvegarde (INSERT)
     */
    @PrePersist
    public void prePersist() {
        System.out.println("Avant INSERT : " + this.name);
        // On peut initialiser des valeurs par défaut ici
        // if (this.createdDate == null) {
        //     this.createdDate = LocalDateTime.now();
        // }
    }

    /**
     * Appelé avant chaque mise à jour (UPDATE)
     */
    @PreUpdate
    public void preUpdate() {
        System.out.println("Avant UPDATE : " + this.name);
        // On peut mettre à jour des timestamps
        // this.updatedDate = LocalDateTime.now();
    }

    /**
     * Appelé avant la suppression (DELETE)
     */
    @PreRemove
    public void preRemove() {
        System.out.println("Avant DELETE : " + this.name);
        // On peut vérifier des conditions avant suppression
    }
}

// ═══════════════════════════════════════════════════════════
// RÉSUMÉ DES ANNOTATIONS JPA
// ═══════════════════════════════════════════════════════════
/*
 * CLASSE :
 * @Entity → Entité JPA
 * @Table → Configuration de la table
 * 
 * CHAMPS :
 * @Id → Clé primaire
 * @GeneratedValue → Valeur auto-générée
 * @Column → Configuration de la colonne
 * 
 * RELATIONS :
 * @OneToOne → 1 à 1
 * @OneToMany → 1 à plusieurs
 * @ManyToOne → Plusieurs à 1
 * @ManyToMany → Plusieurs à plusieurs
 * @JoinColumn → Clé étrangère
 * 
 * CHARGEMENT :
 * FetchType.LAZY → Chargement paresseux (recommandé)
 * FetchType.EAGER → Chargement immédiat
 * 
 * CASCADE :
 * CascadeType.ALL → Tout cascader
 * CascadeType.PERSIST → Cascader save()
 * CascadeType.REMOVE → Cascader delete()
 * 
 * VALIDATION :
 * @NotNull → Ne peut pas être null
 * @NotBlank → Ne peut pas être null ou vide
 * @Size → Taille min/max
 * @Min, @Max → Valeur min/max
 * @Email → Format email
 * @Pattern → Expression régulière
 */
```

---

## 🎯 Exemple 5 : Application Properties Annoté

```properties
# ═══════════════════════════════════════════════════════════
# CONFIGURATION DU SERVEUR
# ═══════════════════════════════════════════════════════════

# Port sur lequel le serveur écoute
server.port=8080

# Contexte de l'application (URL de base)
# Avec ceci, les URLs commencent par http://localhost:8080/conges/...
# server.servlet.context-path=/conges

# ═══════════════════════════════════════════════════════════
# CONFIGURATION DE LA BASE DE DONNÉES
# ═══════════════════════════════════════════════════════════

# URL de connexion MySQL
# Format : jdbc:mysql://hôte:port/nom_base_de_données
spring.datasource.url=jdbc:mysql://localhost:3306/conges_db

# Nom d'utilisateur MySQL
spring.datasource.username=root

# Mot de passe MySQL
spring.datasource.password=votre_mot_de_passe

# Driver JDBC (classe Java qui communique avec MySQL)
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ═══════════════════════════════════════════════════════════
# CONFIGURATION HIBERNATE / JPA
# ═══════════════════════════════════════════════════════════

# Stratégie de gestion du schéma de base de données
# - none : Ne fait rien
# - validate : Vérifie que le schéma correspond aux entités
# - update : Met à jour le schéma si nécessaire (RECOMMANDÉ en dev)
# - create : Supprime et recrée le schéma à chaque démarrage
# - create-drop : Supprime le schéma à l'arrêt de l'application
spring.jpa.hibernate.ddl-auto=update

# Afficher les requêtes SQL dans la console
spring.jpa.show-sql=true

# Formater les requêtes SQL pour qu'elles soient lisibles
spring.jpa.properties.hibernate.format_sql=true

# Dialecte Hibernate (adapte le SQL à MySQL)
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Afficher les valeurs des paramètres SQL (utile pour le debug)
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# ═══════════════════════════════════════════════════════════
# CONFIGURATION DU POOL DE CONNEXIONS
# ═══════════════════════════════════════════════════════════

# Nombre minimum de connexions dans le pool
spring.datasource.hikari.minimum-idle=5

# Nombre maximum de connexions dans le pool
spring.datasource.hikari.maximum-pool-size=20

# Timeout de connexion (en millisecondes)
spring.datasource.hikari.connection-timeout=30000

# ═══════════════════════════════════════════════════════════
# CONFIGURATION DES LOGS
# ═══════════════════════════════════════════════════════════

# Niveau de log général
logging.level.root=INFO

# Niveau de log pour votre application
logging.level.com.rh.conges=DEBUG

# Niveau de log pour Spring
logging.level.org.springframework.web=DEBUG

# Niveau de log pour Hibernate
logging.level.org.hibernate.SQL=DEBUG

# ═══════════════════════════════════════════════════════════
# CONFIGURATION JACKSON (JSON)
# ═══════════════════════════════════════════════════════════

# Format des dates en JSON
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss

# Fuseau horaire
spring.jackson.time-zone=UTC

# Ne pas échouer sur des propriétés inconnues
spring.jackson.deserialization.fail-on-unknown-properties=false

# ═══════════════════════════════════════════════════════════
# CONFIGURATION DEVTOOLS (rechargement automatique en dev)
# ═══════════════════════════════════════════════════════════

# Activer le rechargement automatique
spring.devtools.restart.enabled=true

# Fichiers à exclure du rechargement
spring.devtools.restart.exclude=static/**,public/**
```

---

**Créé le 15 Mars 2026 - Exemples de code annotés pour comprendre chaque ligne**

