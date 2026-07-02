# Guide de lancement des services backend

Ce projet est composé de 4 services Spring Boot (Eureka, Employee, Leave, API Gateway),
d'une infrastructure Docker (Postgres x2, Kafka, Zookeeper, Kafka UI) et d'un frontend React (Vite).

## Prérequis

- Java 17
- Maven (`mvn -v`)
- Docker Desktop (doit être démarré)

## Étape 1 — Démarrer l'infrastructure (Postgres + Kafka)

Depuis la racine du projet (`leave-management-microservices/`) :

```bash
docker compose up -d
```

Cela démarre 5 conteneurs :

| Conteneur | Rôle | Port |
|---|---|---|
| `employee-db` | PostgreSQL — base `employee_db` | 5432 |
| `leave-db` | PostgreSQL — base `leave_db` | 5433 |
| `zookeeper` | requis par Kafka | 2181 |
| `kafka` | broker Kafka | 9092 |
| `kafka-ui` | interface web pour Kafka | 8090 |

Vérifier que tout est démarré :

```bash
docker compose ps
```

Attendre que `employee-db`, `leave-db` et `kafka` soient `healthy` avant de lancer les services Java
(sinon ils échoueront à se connecter).

## Étape 2 — Démarrer les services Spring Boot (dans cet ordre)

Chaque service doit être lancé dans son propre terminal, **dans l'ordre suivant**, car chacun
s'enregistre auprès d'Eureka et le gateway route vers les autres services via Eureka.

### 2.1 Eureka Server (registre de services)

```bash
cd eureka-server
mvn spring-boot:run
```

Attendre que `http://localhost:8761` réponde (dashboard Eureka) avant de continuer.

### 2.2 Employee Service

```bash
cd employee-service
mvn spring-boot:run
```

- Port : `8081`
- Base de données : `employee_db` (port 5432)

### 2.3 Leave Service

```bash
cd leave-service
mvn spring-boot:run
```

- Port : `8082`
- Base de données : `leave_db` (port 5433)

(2.2 et 2.3 peuvent être démarrés en parallèle.)

### 2.4 API Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

- Port : `8085`
- Route `/api/auth/**` (login/register/me/logout) directement
- Route `/api/employees/**`, `/api/departments/**` → `employee-service` via Eureka
- Route `/api/leave-types/**`, `/api/leave-requests/**`, `/api/leave-balances/**` → `leave-service` via Eureka

À démarrer **en dernier**, une fois que employee-service et leave-service sont enregistrés dans Eureka.

## Étape 3 — Démarrer le frontend

```bash
cd Leave-Management-frontend
npm run dev
```

- Port : `3000`
- Le proxy Vite (`vite.config.ts`) redirige `/api/*` vers les services backend correspondants.

## Vérifications rapides

| URL | Attendu |
|---|---|
| `http://localhost:8761` | Dashboard Eureka listant `EMPLOYEE-SERVICE`, `LEAVE-SERVICE`, `API-GATEWAY` |
| `http://localhost:8085/actuator/health` | `200` (ou `401` si authentification requise — signe que le service tourne) |
| `http://localhost:8090` | Kafka UI |
| `http://localhost:3000` | Page de connexion |

## Comptes par défaut

| Utilisateur | Mot de passe | Rôle |
|---|---|---|
| `admin` | `admin123` | ADMIN |
| `manager` | `manager123` | MANAGER |
| `employee` | `employee123` | EMPLOYEE |

## Problèmes fréquents

- **502 / ECONNREFUSED sur le frontend** : le service backend ciblé par le proxy n'est pas démarré (vérifier l'ordre de démarrage).
- **"Port 8085 already in use"** : un ancien processus `java` tourne encore (le wrapper `mvn` a été arrêté mais pas le processus Java enfant). Trouver le PID avec `Get-NetTCPConnection -LocalPort 8085` puis `Stop-Process -Id <PID> -Force`.
- **Création d'employé très lente (~60s) ou en erreur 500** : Kafka n'est pas démarré ou pas encore prêt — vérifier `docker compose ps` et que `kafka` est `healthy`.
