# Documentation

[Retour](../README.md)

---

## Sommaire

- [Prérequis](#prérequis)
- [Lancement avec Docker](#lancement-avec-docker)
- [Configuration](#configuration)
- [Modèle de données](#modèle-de-données)
- [Routes API](#routes-de-lapi)
- [Documentation Swagger UI](#documentation-swagger-ui)
- [Tester avec Postman](#tester-avec-postman)
- [Structure du projet](#structure-du-projet)

---

## Prérequis

- Docker
- docker compose

## Lancement avec Docker

```bash
docker compose up -d --build
```

| Service    | Accès                                            |
|------------|--------------------------------------------------|
| API        | `http://localhost:8081`                          |
| Swagger UI | `http://localhost:8081/swagger`                  |
| MySQL      | `localhost:3307` (utile pour client SQL externe) |

Le profile Spring `dev` est actif dans le conteneur : `DataSeeder` s'exécute au premier démarrage et crée 10 utilisateurs
5 posts par utilisateur (chacun liké par 5 autres), 10 commentaires par post.

**Comptes de test** : `email1@socialapi.com` à `email10@socialapi.com`, mot de passe `password` pour tous les comptes.

### Commandes utiles
```bash
# arrêt des conteneurs
docker compose down

# arrêt + reset complet des données
docker compose down -v

# suivi de logs
docker compose logs -f app

# état des conteneurs
docker compose ps
```

### Dépannage

| Symptôme                                            | Cause probable                                                                                                       |
|-----------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| `app` redémarre en boucle (`up less than a second`) | consulter les logs et vérifiez que `SPRING_DATASOURCE_URL` utilise bien le nom du service `mysql` et non `localhost` |
| `failed to bind host port 0.0.0.0:3306`             | un MySQL local occupe déjà surement le port 3306, changez le port hôte dans `docker-compose.yaml`                    |

---

## Configuration

Créer un fichier `.env` à la racine du projet :
```env
MYSQL_ROOT_PASSWORD=root
MYSQL_USER=socialapi
MYSQL_PASSWORD=socialapi
JWT_SECRET=à-générer
JWT_EXPIRATION_MS=3600000
```

Générer une clé JWT :
```bash
openssl rand -hex 32
```

---

## Modèle de données

| Entité    | Champs principaux                                   |
|-----------|-----------------------------------------------------|
| `User`    | `id`, `email`, `password`, `name`                   |
| `Post`    | `id`, `content`, `publishedAt`, `author`, `likedBy` |
| `Comment` | `id`, `message`, `publishedAt`, `author`, `post`    |

---

## Routes de l'API

| Méthode | URL                        | Description                      | Accès               |
|---------|----------------------------|----------------------------------|---------------------|
| POST    | `/api/login`               | Authentification, renvoie un JWT | Public              |
| GET     | `/posts?page=&limit=`      | Liste paginée des posts          | Public              |
| GET     | `/posts/{id}`              | Détail d'un post                 | Public              |
| POST    | `/posts`                   | Crée un post                     | Authentifé          |
| PUT     | `/posts/{id}`              | Modifie un post                  | Authentifé + auteur |
| DELETE  | `/posts/{id}`              | Supprime un post                 | Authentifé + auteur |
| GET     | `/posts/{postId}/comments` | Commentaires d'un post           | Public              |
| GET     | `/comments/{id}`           | Détail d'un commentaire          | Public              |
| POST    | `/posts/{postId}/comments` | Ajoute un commentaire            | Authentifé          |
| PUT     | `/comments/{id}`           | Modifie un commentaire           | Authentifé + auteur |
| DELETE  | `/comments/{id}`           | Suppirme un commentaire          | Authentifé + auteur |
| GET     | `/posts/{postId}/likes`    | Utilisateurs ayant liké          | Public              |
| POST    | `/posts/{postId}/likes`    | Like un post                     | Authentifé          |
| DELETE  | `/posts/{postId}/likes`    | Retire son like                  | Authentifé          |

Pagination : `page` (défaut 1) et `limit` (défaut 10, plafonnée à 50)

## Documentation Swagger UI

La documentation Swagger UI est disponible :

```bash
http://localhost:8081/swagger
```

Routes groupées par tag (**Auth**, **Posts**, **Comments**, **Likes**). Le bouton **Authorize** permet de coller un jeton
JWT une seule fois pour tester toutes les routes protégées depuis l'interface.

---

## Tester avec Postman

Les tests avec Postman sont disponibles [ici](postman.md).

---

## Structure du projet

```
src/main/java/com/socialapi/socialapi/
├── config/ # DataSeeder, OpenApiConfig
├── controller/ # Contrôleurs REST
├── dto/ # Objets exposés par l'API
├── entity/ # User, Post, Comment (JPA)
├── exception/ # Exceptions métier + gestion centralisée
├── mapper/ # DtoMapper : entité → DTO
├── repository/ # Spring Data JPA
├── security/ # JWT (génération, filtre, UserDetailsService)
└── service/ # Logique métier, contrôle de propriété des ressources
```