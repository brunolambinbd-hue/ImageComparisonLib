# Requirements Fonctionnels : 1.3 Structuration Hiérarchique

Ce document définit les critères SMART pour l'organisation automatique de la collection via le moteur de mapping métier (Super-Catégories).

---

### 1.3.1 Centralisation des Règles de Métier (Spécifique)
*   **Requirement** : Toutes les règles de correspondance entre une "Catégorie" (ex: Sérigraphie) et une "Super-Catégorie" (ex: Image) doivent être centralisées dans un moteur de mapping unique.
*   **Critère de succès** : L'objet `CategoryMapper` doit être la seule source de vérité pour le dictionnaire de catégories. Toute modification du dictionnaire doit se répercuter instantanément dans toute l'application.
*   **Action Test** : Test unitaire sur `CategoryMapper` vérifiant que chaque catégorie d'entrée renvoie la super-catégorie attendue (ex: "Affiches" -> "Image").

### 1.3.2 Automatisation du Mapping (Mesurable)
*   **Requirement** : Lors de la création d'un objet ou d'un import CSV, la super-catégorie doit être déduite automatiquement si elle n'est pas fournie.
*   **Critère de succès** : 100% des objets ayant une catégorie reconnue doivent disposer d'une super-catégorie valide en base de données.
*   **Action Test** : Test d'intégration simulant l'ajout d'un objet avec seulement "categorie = 'Albums'" et vérifiant que le champ "superCategorie" est rempli par "Album" avant la persistence.

### 1.3.3 Outil de Réparation des Incohérences (Atteignable)
*   **Requirement** : Le système doit proposer un outil pour identifier et corriger les objets dont la super-catégorie est manquante ou obsolète (ex: après un changement de règles métier).
*   **Critère de succès** : L'écran "Audit des Catégories" doit lister le nombre exact d'anomalies et permettre leur correction en un clic.
*   **Action Test** : Test unitaire sur `CategoryAuditViewModel` vérifiant qu'un objet avec "superCategorie = 'N/D'" est correctement identifié comme "à réparer".

### 1.3.4 Navigation Hiérarchique Cohérente (Pertinent)
*   **Requirement** : L'utilisateur doit pouvoir naviguer dans sa collection de manière descendante (Super-Catégorie > Catégorie > Items).
*   **Critère de succès** : Le filtrage par super-catégorie dans `CategoryListActivity` ne doit jamais afficher de liste vide si le compteur global indique la présence d'objets.
*   **Action Test** : Test instrumenté (`.11`) vérifiant que cliquer sur "Image" affiche bien la liste des sous-catégories correspondantes (Sérigraphies, Offsets, etc.).

### 1.3.5 Intégrité lors de la Modification Manuelle (Temporel)
*   **Requirement** : Si un utilisateur change manuellement la catégorie d'un objet dans l'écran d'édition, la super-catégorie doit être suggérée ou mise à jour immédiatement.
*   **Critère de succès** : La mise à jour du champ `etCategory` doit déclencher une vérification de cohérence en temps réel sur l'interface.
*   **Action Test** : Test instrumenté (`.11`) vérifiant que sélectionner une catégorie dans le spinner de `EditItemActivity` pré-remplit correctement le champ `etSuperCategory`.

### 1.3.6 Mise à jour de Possession par Lot (Atteignable)
*   **Requirement** : L'application doit permettre de marquer comme "possédés" plusieurs volumes d'une série en une seule opération (Batch Update).
*   **Critère de succès** : L'utilisateur peut saisir un nom de série et une plage numérique (ex: "Tintin", vol 1 à 24) pour identifier et mettre à jour tous les objets correspondants.
*   **Action Test** : Test instrumenté (`.11`) validant la regex d'extraction des numéros et l'application du flag `isPossessed` sur la liste filtrée.

---

### Détails techniques

#### Moteur de Classification
*   **Logique Source** : `CategoryMapper` agit comme source unique de vérité. L'audit de données répare les erreurs d'imports CSV.
*   **Validation** : Vérifie la robustesse face aux entrées malformées ("N/D") et l'assistance à la saisie (Auto-fill).
