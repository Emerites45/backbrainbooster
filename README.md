# Brain-Booster - Backend API 🚀

Bienvenue sur le dépôt Backend de l'application **Brain-Booster**. Cette API REST est développée avec **Spring Boot** et gère l'authentification sécurisée, la persistance des données et la logique métier de l'application.

---

## 🛠️ Technologies & Outils

* **Framework Principal :** Spring Boot 3.3.1
* **Langage :** Java 17
* **Sécurité :** Spring Security (BCrypt & JWT pour le mode Stateless)
* **Persistance :** Spring Data JPA / Hibernate
* **Base de données :** PostgreSQL 16
* **Gestionnaire de dépendances :** Maven

---

## 🚀 Installation et Démarrage Local

### 1. Prérequis
Assurez-vous d'avoir installé sur votre machine :
* **Java 17** (ou supérieur)
* **PostgreSQL 16**

### 2. Configuration de la Base de Données
1. Ouvrez votre outil de gestion de base de données (pgAdmin, DBeaver, etc.).
2. Créez une nouvelle base de données vide nommée `brainbooster`.
3. Ouvrez le fichier `src/main/resources/application.properties` et configurez vos accès locaux :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/brainbooster
spring.datasource.username=votre_utilisateur_postgres
spring.datasource.password=votre_mot_de_passe_postgres

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

3. Lancement de l'applicationDans votre terminal à la racine du projet, exécutez la commande suivante :Bash./mvnw spring-boot:run
L'application sera accessible par défaut à l'adresse suivante : http://localhost:8080.🔐 Endpoints d'Authentification (API)Les routes suivantes sont publiques et gérées par l'AuthController :MéthodeEndpointDescriptionCorps de la requête (JSON)POST/api/v1/auth/signupInscription d'un nouvel utilisateur{"name": "...", "email": "...", "password": "..."}POST/api/v1/auth/loginConnexion & Génération de Token{"email": "...", "password": "..."}🧪 Comment tester l'API ?Pour valider le fonctionnement des routes sans interface graphique :Installez l'extension Thunder Client (ou utilisez Postman).Configurez une requête POST vers http://localhost:8080/api/v1/auth/signup avec un JSON valide pour créer votre premier compte.Testez ensuite la route de connexion pour récupérer votre jeton d'accès.📝 Normes de Code (Git Flow)Avant d'ouvrir une Pull Request :Assurez-vous que le projet compile sans aucun avertissement ni erreur (0 problems).N'ajoutez jamais vos mots de passe personnels ou configurations locales sensibles dans vos commits.
---

### 🛠️ Comment l'ajouter à ton projet :
1. Crée un fichier nommé `README.md` à la racine de ton projet `backbrainbooster` (s'il n'existe pas déjà).
2. Colle tout le bloc de code ci-dessus à l'intérieur.
3. Sauvegarde.

Une fois que c'est fait, tu pourras faire un petit `git add README.md`, l'ajouter à ton commit actuel ou faire un commit dédié, et ton projet sera une véritable vitrine de pro. 

Est-ce que la structure te convient ou tu veux qu'on y ajoute 