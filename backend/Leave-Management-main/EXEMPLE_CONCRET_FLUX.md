# 🎯 Exemple Concret : Traçage Complet d'un Appel API

## 📍 Scénario : Récupérer le Département avec ID = 1

---

## 🔹 ÉTAPE 1 : Requête HTTP du Client

```http
GET http://localhost:8080/api/departments/1
Accept: application/json
```

**Ce qui se passe :**
- Le client (Postman, navigateur, application mobile) envoie une requête HTTP
- Méthode : GET
- URL : `/api/departments/1`
- Le serveur Spring Boot écoute sur le port 8080

---

## 🔹 ÉTAPE 2 : Spring Boot Reçoit la Requête

```
┌─────────────────────────────────────────┐
│    Spring Boot Embedded Tomcat          │
│    (Serveur Web intégré)                │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│    DispatcherServlet                    │
│    (Routeur principal de Spring)        │
│    - Analyse l'URL                      │
│    - Trouve le bon Controller           │
└─────────────────────────────────────────┘
                  ↓
         Recherche du Controller
         correspondant à /api/departments
```

---

## 🔹 ÉTAPE 3 : Le Controller est Appelé

**Fichier : `DepartmentController.java` (ligne 34-41)**

```java
@GetMapping("/{id}")  // ← Cette annotation dit : "Je gère GET /api/departments/{id}"
public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
    // @PathVariable extrait "1" de l'URL et le convertit en Long
    
    System.out.println("🎯 Controller : Demande reçue pour le département ID = " + id);
    
    try {
        // Appel du service
        Department department = departmentService.getDepartmentById(id);
        
        System.out.println("✅ Controller : Département trouvé, envoi de la réponse");
        return new ResponseEntity<>(department, HttpStatus.OK);
    } catch (RuntimeException e) {
        System.out.println("❌ Controller : Département non trouvé");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
```

**Variables à ce moment :**
```java
id = 1L  // Type Long, valeur 1
```

---

## 🔹 ÉTAPE 4 : Le Service Traite la Demande

**Fichier : `DepartmentService.java` (ligne 40-44)**

```java
public Department getDepartmentById(Long id) {
    System.out.println("🧠 Service : Recherche du département ID = " + id);
    System.out.println("🧠 Service : Appel du repository...");
    
    return departmentRepository.findById(id)
            .orElseThrow(() -> {
                System.out.println("❌ Service : Département non trouvé dans la DB");
                return new RuntimeException("Department not found with ID: " + id);
            });
}
```

**Ce que fait `.orElseThrow()` :**
- Si `findById()` trouve le département → le retourne
- Si `findById()` ne trouve rien → lance une exception

---

## 🔹 ÉTAPE 5 : Le Repository Accède à la BD

**Fichier : `DepartmentRepository.java`**

```java
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // findById() est automatiquement fournie par JpaRepository
    // Vous n'avez PAS besoin de l'écrire !
}
```

**En coulisses, Spring Data JPA fait ceci :**

```java
// Code généré automatiquement par Spring Data JPA (vous ne le voyez pas)
public Optional<Department> findById(Long id) {
    System.out.println("💾 Repository : Préparation de la requête SQL");
    
    // 1. Création de la requête SQL
    String sql = "SELECT d.id, d.name, d.description, d.manager_id " +
                 "FROM departments d WHERE d.id = ?";
    
    System.out.println("💾 Repository : Exécution de : " + sql);
    System.out.println("💾 Repository : Avec paramètre : id = 1");
    
    // 2. Exécution de la requête via JDBC
    ResultSet rs = jdbcConnection.executeQuery(sql, id);
    
    // 3. Vérification du résultat
    if (rs.next()) {
        System.out.println("✅ Repository : Ligne trouvée dans la base de données");
        
        // 4. Création de l'objet Department
        Department dept = new Department();
        dept.setId(rs.getLong("id"));
        dept.setName(rs.getString("name"));
        dept.setDescription(rs.getString("description"));
        
        System.out.println("✅ Repository : Objet Department créé : " + dept);
        
        return Optional.of(dept);
    } else {
        System.out.println("❌ Repository : Aucune ligne trouvée");
        return Optional.empty();
    }
}
```

---

## 🔹 ÉTAPE 6 : Hibernate Exécute le SQL

**Log Hibernate (dans la console) :**

```sql
Hibernate: 
    select
        d1_0.id,
        d1_0.name,
        d1_0.description,
        d1_0.manager_id 
    from
        departments d1_0 
    where
        d1_0.id=?

-- Paramètre : 1
```

**Résultat de la base de données :**

```
+----+-----------------------+----------------------------------+------------+
| id | name                  | description                      | manager_id |
+----+-----------------------+----------------------------------+------------+
|  1 | Ressources Humaines   | Département RH responsable...    |       NULL |
+----+-----------------------+----------------------------------+------------+
```

---

## 🔹 ÉTAPE 7 : Mapping des Données vers l'Objet Java

**Hibernate crée l'objet Java :**

```java
Department department = new Department();
department.setId(1L);
department.setName("Ressources Humaines");
department.setDescription("Département RH responsable de la gestion des employés");
department.setManager(null);
department.setEmployees(new ArrayList<>());
```

**État de l'objet en mémoire :**

```
Department {
    id: 1
    name: "Ressources Humaines"
    description: "Département RH responsable de la gestion des employés"
    manager: null
    employees: []
}
```

---

## 🔹 ÉTAPE 8 : Remontée de l'Objet dans les Couches

```
Repository (retourne Optional<Department>)
    ↓
Service (extrait le Department de l'Optional)
    ↓
Controller (reçoit le Department)
```

---

## 🔹 ÉTAPE 9 : Sérialisation en JSON

**Le Controller fait :**

```java
return new ResponseEntity<>(department, HttpStatus.OK);
```

**Spring Boot utilise Jackson pour convertir l'objet Java en JSON :**

```java
// Jackson (bibliothèque de sérialisation JSON)
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(department);
```

**JSON généré :**

```json
{
  "id": 1,
  "name": "Ressources Humaines",
  "description": "Département RH responsable de la gestion des employés",
  "manager": null
}
```

**Notes :**
- `employees` n'apparaît PAS car il a `@JsonIgnore`
- `null` est inclus pour `manager`

---

## 🔹 ÉTAPE 10 : Envoi de la Réponse HTTP

**Réponse complète envoyée au client :**

```http
HTTP/1.1 200 OK
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 15 Mar 2026 10:30:45 GMT

{
  "id": 1,
  "name": "Ressources Humaines",
  "description": "Département RH responsable de la gestion des employés",
  "manager": null
}
```

---

## 📊 Chronologie Complète (Timeline)

```
Temps | Couche        | Action
------|---------------|--------------------------------------------------
0ms   | Client        | Envoie GET /api/departments/1
2ms   | Tomcat        | Reçoit la requête HTTP
3ms   | Dispatcher    | Route vers DepartmentController
4ms   | Controller    | Appelle departmentService.getDepartmentById(1)
5ms   | Service       | Appelle departmentRepository.findById(1)
6ms   | Repository    | Spring Data JPA génère le SQL
7ms   | Hibernate     | Prépare la requête JDBC
8ms   | JDBC          | Envoie SELECT * FROM departments WHERE id=1
10ms  | MySQL         | Exécute la requête et retourne le résultat
12ms  | Hibernate     | Mappe les données vers l'objet Department
13ms  | Repository    | Retourne Optional<Department>
14ms  | Service       | Extrait le Department
15ms  | Controller    | Reçoit le Department
16ms  | Jackson       | Sérialise en JSON
17ms  | Tomcat        | Envoie la réponse HTTP 200 OK
18ms  | Client        | Reçoit le JSON
```

**Temps total : ~18ms**

---

## 🎭 Analogie avec le Monde Réel

Imaginez que vous commandez un livre sur Amazon :

1. **Client (Vous)** : "Je veux le livre avec ID 1"
   → *Requête HTTP*

2. **Service Client (Controller)** : "D'accord, je vérifie avec l'entrepôt"
   → *Appel du Service*

3. **Manager d'Entrepôt (Service)** : "Je demande au magasinier de chercher"
   → *Appel du Repository*

4. **Magasinier (Repository)** : "Je cherche dans les rayons..."
   → *Requête SQL*

5. **Rayon (Base de Données)** : "Voici le livre !"
   → *Résultat SQL*

6. **Magasinier (Repository)** : "Je l'emballe et le donne au manager"
   → *Mapping vers objet Java*

7. **Manager (Service)** : "Je le donne au service client"
   → *Retour au Controller*

8. **Service Client (Controller)** : "Je vous l'envoie !"
   → *Réponse JSON*

---

## 💡 Points Clés

### ✅ Ce qui est AUTOMATIQUE (Spring fait le travail) :

1. **Routing** : Spring mappe automatiquement `/api/departments/1` vers le bon contrôleur
2. **Extraction des paramètres** : `@PathVariable` extrait automatiquement l'ID
3. **Génération SQL** : Spring Data JPA génère automatiquement le `SELECT`
4. **Mapping objet-relationnel** : Hibernate mappe automatiquement les colonnes vers les champs
5. **Sérialisation JSON** : Jackson convertit automatiquement l'objet Java en JSON
6. **Gestion des transactions** : Spring gère automatiquement les transactions DB

### ⚙️ Ce que VOUS devez faire :

1. **Créer le Controller** avec les annotations `@GetMapping`, etc.
2. **Créer le Service** avec la logique métier
3. **Créer le Repository** (interface seulement !)
4. **Créer l'Entity** avec les annotations JPA
5. **Configurer la base de données** dans `application.properties`

---

## 🔍 Comment Voir ce qui se Passe ?

### Activer les Logs SQL

**Dans `application.properties` :**

```properties
# Afficher les requêtes SQL
spring.jpa.show-sql=true

# Formater les requêtes SQL
spring.jpa.properties.hibernate.format_sql=true

# Afficher les paramètres SQL
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Logs Spring
logging.level.org.springframework.web=DEBUG
```

**Vous verrez alors dans la console :**

```
2026-03-15 10:30:45.123 DEBUG [http-nio-8080-exec-1] o.s.web.servlet.DispatcherServlet : GET "/api/departments/1"
2026-03-15 10:30:45.125 DEBUG [http-nio-8080-exec-1] o.s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to com.rh.conges.controller.DepartmentController#getDepartmentById(Long)
Hibernate: 
    select
        d1_0.id,
        d1_0.name,
        d1_0.description,
        d1_0.manager_id 
    from
        departments d1_0 
    where
        d1_0.id=?
2026-03-15 10:30:45.140 TRACE [http-nio-8080-exec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [1] as [BIGINT] - [1]
2026-03-15 10:30:45.145 DEBUG [http-nio-8080-exec-1] o.s.web.servlet.DispatcherServlet : Completed 200 OK
```

---

## 🎓 Quiz de Compréhension

**Question 1 :** Quelle couche est responsable de générer le SQL ?
<details>
<summary>Réponse</summary>
Le Repository (via Spring Data JPA et Hibernate)
</details>

**Question 2 :** Où se trouve la logique métier ?
<details>
<summary>Réponse</summary>
Dans le Service
</details>

**Question 3 :** Qui convertit l'objet Java en JSON ?
<details>
<summary>Réponse</summary>
Jackson (bibliothèque de sérialisation)
</details>

**Question 4 :** Pourquoi le Repository est une interface et non une classe ?
<details>
<summary>Réponse</summary>
Spring Data JPA génère automatiquement l'implémentation à partir de l'interface
</details>

---

**Créé le 15 Mars 2026 - Guide de compréhension du flux API**

