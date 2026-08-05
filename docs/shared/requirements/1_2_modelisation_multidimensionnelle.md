# Requirements Fonctionnels : 1.2 Modélisation Multidimensionnelle

Ce document définit les critères SMART pour la gestion exhaustive des métadonnées des objets de collection (titres, éditeurs, tirages, matériaux, dimensions physiques).

---

### 1.2.1 Exhaustivité des Champs de Saisie (Spécifique)
*   **Requirement** : L'interface de création/édition doit permettre la saisie de l'intégralité des métadonnées métier : Titre (obligatoire), Éditeur, Année, Mois, Catégorie, Super-Catégorie, Matériau, Tirage, Dimensions, Prix d'achat, Valeur estimée et Lieu d'achat.
*   **Critère de succès** : Tous ces champs sont présents dans `activity_edit_item.xml` et mappés vers l'entité `CollectionItem`.
*   **Action Test** : Test instrumenté (`.11`) vérifiant la présence et l'accessibilité de chaque champ de saisie dans `EditItemActivity`.

### 1.2.2 Intégrité des Types de Données (Mesurable)
*   **Requirement** : Les métadonnées numériques (Année, Mois, Prix, Valeur) doivent être validées avant enregistrement pour éviter les corruptions de base de données.
*   **Critère de succès** : Les champs "Année" et "Mois" n'acceptent que des entiers. Les champs "Prix" et "Valeur" acceptent des décimaux. Toute saisie textuelle dans un champ numérique doit être ignorée ou corrigée (`null`).
*   **Action Test** : Test unitaire sur `EditItemViewModel` ou `CollectionItem` vérifiant que l'insertion de valeurs malformées (ex: "abc" dans Prix) ne provoque pas de crash et est traitée comme `null` ou `0.0`.

### 1.2.3 Persistance et Rétention (Atteignable)
*   **Requirement** : Chaque modification d'une métadonnée doit être persistée de manière atomique dans la base de données Room.
*   **Critère de succès** : Après fermeture et réouverture de l'application, les données modifiées doivent être strictement identiques à la saisie précédente.
*   **Action Test** : Test d'intégration (Room DAO test) effectuant un cycle "Update -> Fetch -> Compare" pour chaque champ de métadonnée.

### 1.2.4 Rendu Visuel Multidimensionnel (Pertinent)
*   **Requirement** : L'affichage en plein écran (`FullScreenImageActivity`) doit présenter une synthèse lisible des métadonnées pour faciliter l'identification rapide de l'objet.
*   **Critère de succès** : Si une métadonnée est présente, elle s'affiche avec son libellé (ex: "Éditeur : Dupuis"). Si elle est absente, le champ n'occupe pas d'espace visuel (Visibility = GONE).
*   **Action Test** : Test instrumenté (`.11`) vérifiant que `image_info_layout` adapte sa hauteur dynamiquement selon le nombre de champs remplis.

### 1.2.5 Horodatage des Modifications (Temporel)
*   **Requirement** : Toute modification d'une métadonnée doit automatiquement mettre à jour le champ `updatedAt` de l'objet.
*   **Critère de succès** : Le champ `updatedAt` doit refléter l'heure système au moment de l'enregistrement de l'objet, permettant un tri chronologique précis.
*   **Action Test** : Test unitaire vérifiant que la valeur de `updatedAt` augmente après une opération d'édition sur un objet existant.

---

### Détails techniques

#### Métadonnées Métier
*   **Logique Source** : Gestion de 14 champs métier spécifiques. Utilisation de Room pour la persistence BLOB (signatures) et REAL (prix).
*   **Validation** : Garantit l'absence de perte de données (Data Integrity) et l'exhaustivité des formulaires de saisie.
