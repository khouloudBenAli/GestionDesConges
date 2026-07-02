# 🚀 DÉMARRAGE RAPIDE - Leave Management System

## ❌ Problème Actuel

**Erreur :** `Access denied for user 'root'@'localhost' (using password: NO)`

---

## ✅ SOLUTION RAPIDE (3 méthodes)

### Méthode 1 : Configurer le Mot de Passe MySQL ⚡ (2 minutes)

**Si vous connaissez votre mot de passe MySQL :**

1. **Ouvrir** `src/main/resources/application.properties`

2. **Modifier la ligne 11** :
   ```properties
   spring.datasource.password=VOTRE_MOT_DE_PASSE
   ```

3. **Sauvegarder** le fichier

4. **Redémarrer** l'application dans IntelliJ (bouton Stop puis Run)

✅ **TERMINÉ !**

---

### Méthode 2 : Créer la Base de Données MySQL ⚡ (3 minutes)

**Si MySQL n'a PAS de mot de passe :**

1. **Ouvrir PowerShell/Command Prompt**

2. **Se connecter à MySQL** :
   ```bash
   mysql -u root
   ```

3. **Créer la base de données** :
   ```sql
   CREATE DATABASE conges_db;
   EXIT;
   ```

4. **Redémarrer** l'application Spring Boot

✅ **TERMINÉ !**

---

### Méthode 3 : Utiliser H2 (Sans MySQL) ⚡⚡⚡ (1 minute)

**La plus RAPIDE pour tester immédiatement :**

1. **Ouvrir** `src/main/resources/application.properties`

2. **Remplacer TOUT le contenu** par :
   ```properties
   # H2 Database (En mémoire)
   spring.datasource.url=jdbc:h2:mem:conges_db
   spring.datasource.driver-class-name=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   
   # Console H2
   spring.h2.console.enabled=true
   spring.h2.console.path=/h2-console
   
   # JPA
   spring.jpa.hibernate.ddl-auto=create-drop
   spring.jpa.show-sql=true
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   
   # Server
   server.port=8080
   ```

3. **Sauvegarder** et **Redémarrer** l'application

4. **Accéder à** : http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:conges_db`
   - User: `sa`
   - Password: (vide)

✅ **L'APPLICATION DÉMARRE IMMÉDIATEMENT !**

---

## 🎯 Quelle Méthode Choisir ?

| Méthode | Temps | Persistance | Recommandé pour |
|---------|-------|-------------|-----------------|
| **MySQL avec mot de passe** | 2 min | ✅ Oui | Production |
| **MySQL sans mot de passe** | 3 min | ✅ Oui | Développement |
| **H2 en mémoire** | 1 min | ❌ Non | Tests rapides |

---

## 📋 Vérification du Démarrage

**L'application a démarré avec succès si vous voyez :**

```
Started CongesApplication in X.XXX seconds
Tomcat started on port 8080
```

**Tester les endpoints :**
```
http://localhost:8080/api/departments
http://localhost:8080/api/employees
http://localhost:8080/h2-console (si H2)
```

---

## 🔧 Après le Démarrage

### Créer des données de test

**Via Postman ou curl :**

```bash
# Créer un département
curl -X POST http://localhost:8080/api/departments \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"IT\",\"description\":\"Département Informatique\"}"

# Créer un employé
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d "{\"firstName\":\"Jean\",\"lastName\":\"Dupont\",\"email\":\"jean@example.com\",\"jobTitle\":\"Dev\",\"hireDate\":\"2024-01-01\",\"department\":{\"id\":1}}"

# Créer un type de congé
curl -X POST http://localhost:8080/api/leave-types \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Congé Annuel\",\"paidLeave\":true,\"defaultDaysPerYear\":25,\"active\":true}"
```

---

## 💡 Astuce

**Utiliser H2 pour le développement et MySQL pour la production :**

Créer deux profils :
- `application.properties` → H2 par défaut
- `application-prod.properties` → MySQL

Lancer avec profil :
```bash
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

---

## 🎉 Résumé

### Pour démarrer MAINTENANT :

1. ✅ **Choisir une méthode** (recommandé : H2 pour tester rapidement)
2. ✅ **Modifier** `application.properties`
3. ✅ **Redémarrer** l'application
4. ✅ **Vérifier** que ça fonctionne
5. ✅ **Tester** les endpoints API

---

**⚡ SOLUTION LA PLUS RAPIDE : Utiliser H2 (Méthode 3) - 1 minute !**

**Date :** 2026-03-15  
**Problème :** Connexion MySQL  
**Solutions :** 3 méthodes disponibles  
**Temps :** 1-3 minutes

