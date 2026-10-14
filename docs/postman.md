# Test Postman

[Retour au sommaire](index.md)

Ce guide décrit un scénario de test complet de l'API, avec les réponses attendues.

**Prérequis** : l'application tourne (`docker compose up -d`) et répond sur `http://localhost:8081`. Les données de test
sont créées automatiquement au premier démarrage : `email1@socialapi.com` à `email10@socialapi.com`, mot de passe `password`
pour tous.

## Sommaire
- [Préparer Postman](#préparer-postman)
- [Authentification](#authentification)
- [Posts](#posts)
- [Commentaires](#commentaires)
- [Likes](#likes)
- [Cas d'erreur](#cas-derreur)
- [Import automatique](#import-automatique)

---

## Préparer Postman

### Authentification au niveau de la collection

Dans l'onglet **Authorization** de la collection : type `Bearer token`, valeur `{{token}}`. Toutes les requêtes héritent
alors du jeton, il n'y a rien à configurer requête par requête (option *Inherit auth from parent*, active par défaut). Les
routes publiques (lecture) fonctionnent aussi avec un jeton.

### Ordre recommandé

Le scénario ci-dessous est conçu pour être exécuté dans l'ordre : chaque étape s'appuie sur la précédente

| # | Étape             | Résultat attendu       |
|---|-------------------|------------------------|
| 1 | Se connecter      | `200 OK`, jeton stocké |
| 2 | Lister les posts  | `200 OK`               |
| 3 | Créer un post     | `201 Created`          |
| 4 | Commenter ce post | `201 Created`          |
| 5 | Liker ce post     | `201 Created`          |
| 6 | Modifier ce post  | `204 No Content`       |
| 7 | Supprimer ce post | `204 No Content`       |

Modifier et supprimer ne fonctionnent que sur **ses propres** posts : c'est pourquoi on crée d'abord un post avec l'utilisateur
connecté, plutôt que d'utiliser un identifiant de post existant dans les données de test.

---

## Authentification
### Se connecter

```
POST http://localhost:8081/api/login
Content-Type: application/json
{
    "email": "email3@socialapi.com",
    "password": "password"
}
```

**Réponse attendue**  : `200 OK`

```json
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

**Stocker le jeton automatiquement** : onglet **Scripts -> Post-response** de la requête : 
```javascript
pm.test("Connexion réussie", () => pm.response.to.have.status(200));
pm.collectionVariables.set("token", pm.response.json().token);
```
Le jeton est valable 1h. Passé ce délai, les routes protégées répondent `403` : relancer simplement la requête de login.

---

## Posts
### Lister les posts

Route publique, paginée. Paramètres optionnels : `page` (défaut `1') et `limit` (défaut `10`, maximum `50`).

```
GET http://localhost:8081/posts?page=4&limit=1
```
**Réponse attendue** : `200 OK`

```json
{
  "items": [
    {
      "id": 47,
      "content": "content 2",
      "publishedAt": "2026-10-05T10:36:36.818744Z",
      "author": {
        "id": 6,
        "email": "email6@socialapi.com",
        "name": "name6"
      },
      "likedBy": [
        {
          "id": 2,
          "email": "email2@socialapi.com",
          "name": "name2"
        },
        {
          "id": 10,
          "email": "email10@socialapi.com",
          "name": "name10"
        },
        {
          "id": 6,
          "email": "email6@socialapi.com",
          "name": "name6"
        },
        {
          "id": 3,
          "email": "email3@socialapi.com",
          "name": "name3"
        },
        {
          "id": 4,
          "email": "email4@socialapi.com",
          "name": "name4"
        }
      ]
    }
  ],
  "meta": {
    "page": 4,
    "limit": 1,
    "total": 50,
    "totalPages": 50
  }
}
```
Les posts sont triés du plus récent au plus ancien. `meta.totalPages` permet de savoir quand arrêter de paginer.

### Créer un post

```
POST http://localhost:8081/posts
Content-Type: application/json

{ "content": "Création d'un post pour l'API" }
```
**Réponse attendue** : `201 Created`
```json
{
    "id": 52,
    "content": "Création d'un post pour tester l'API",
    "publishedAt": "2026-10-14T13:52:20.016459797Z",
    "author": {
    "id": 3,
    "email": "email3@socialapi.com",
    "name": "name3"
    },
    "likedBy": []
}
```

### Modifier un post
Réserver à l'auteur du post

```
PUT http://localhost:8081/posts/52
Content-Type: application/json

{ "content": "Modification du post nouvellement créé pour tester la route PUT /posts/{id}" }
```
**Réponse attendue** : `204 No Content`, sans corps. Vérifier la modification avec un `GET http://localhost:8081/posts/52`

### Supprimer un post

Réserver à l'auteur du post. Ses commentaires sont aussi supprimés.
```
DELETE http://localhost:8081/posts/52
```
**Réponse attendue** : `204 No Content`, sans corps. Vérifier la suppression avec un `GET http://localhost:8081/posts/52`, vous
devez avoir `404 Not Found` avec dans le corps de la réponse
```json
{
    "error": "POst 52introuvable"
}
```
---

## Commentaires
### Commenter un post

```
POST http://localhost:8081/posts/1/comments
Content-Type: application/json

{ "message": "Commenter un post pour tester la route POST /posts/{postId}/comments" }
```
**Réponse attendue** : `201 Created`
```json
{
  "id": 501,
  "message": "Création d'un commentaire pour tester la route POST /posts/{id}/comments",
  "publishedAt": "2026-10-14T14:23:59.386966849Z",
  "author": {
    "id": 3,
    "email": "email3@socialapi.com",
    "name": "name3"
  }
}
```

### Lister les commentaires d'un post

Route publique

```
GET http://localhost:8081/posts/1/comments
```
**Réponse attendue** : `200 OK`, un tableau de commentaires comme ci-dessus

### Modifier un commentaire

Réserver à l'auteur du commentaire.

```
PUT http://localhost:8081/comments/501
Content-Type: application/json

{ "message": "Modification du commentaire pour tester la route PUT /comments/{id}" }
```
**Résultat attendu** : `204 No Content`

Vérifier la modification en faisant un `GET http://localhost:8081/posts/{id}/comments`

### Supprimer un commentaire

```
DELETE http://localhost:8081/comments/501
```
Réserver à l'auteur du commentaire.

**Réponse attendue** : `204 No Content`

---

## Liker

### Liste des utilisateurs ayant liké

Route publique.

```
GET http://localhost:8081/posts/51/likes
```

**Réponse attendue** : `200 OK`

```json
[
  { 
    "id": 3,
    "email": "email3@socialapi.com",
    "name": "name3"
  }
]
```

### Liker un post

```
POST http://localhost:8081/posts/51/likes
```
**Réponse attendue** : `204 No Content`

Vérifier en faisant un `GET http://localhost:8081/posts/51/likes`

### Retirer un like

```
DELETE http://localhost:8081/posts/51/likes
```
**Réponse attendue** : `204 No Content`

Vérifier en faisant un `GET http://localhost:8081/posts/51/likes`

---

## Cas d'erreur

| Scénario                                | Requête                                                                     | Code attendu                            | Corps                                                 |
|-----------------------------------------|-----------------------------------------------------------------------------|-----------------------------------------|-------------------------------------------------------|
| Identifiants invalides                  | `POST /api/login` avec un mauvais mot de passe                              | `{ "error": "Identifiants invalides" }` |
| Pas de jeton sur une route protégée     | `POST /posts`                                                               | `403 Forbidden`                         | Pas de corps                                          |
| Contenu vide                            | `POST /posts` **et un jeton valide**                                        | `404 Bad Request`                       | `{ "error": "Le champ \"content\" est obligatoire" }` |
| Ressource inexsistante                  | `GET /posts/787878`                                                         | `404 Not Found`                         | `{ "error": "POst 787878introuvable" }`               |
| Modifier le post d'un autre utilisateur | `PUT /posts/{id}` avec **un jeton d'un utilisateur qui n'ets pas l'auteur** | `403 Forbidden`                         | `{ "error": "Vous n'êtes pas l'auteur de ce post" }`  |

**Tester la règle de propriété** : se connecter avec un deuxième utilisateur (ex. `email4@socialapi.com`), puis tenter de modifier ou supprimer le post créé par 
`email3@socialapi.com`.

---

## Import automatique
Dans Postman : **Import**, puis sélectionner [`openapi-java.yaml`](openapi-java.yaml) (présent dans le dossier `doc/`). La
collection est générée à partir des routes et des schémas de l'API ; l'authentification par jeton est défini au début de document.