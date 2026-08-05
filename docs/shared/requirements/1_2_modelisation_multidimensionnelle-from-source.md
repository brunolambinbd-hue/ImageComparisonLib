# Documentation Technique 1.1 (from-source) : Modélisation Multidimensionnelle

Ce document détaille l'implémentation réelle de la gestion des métadonnées dans le code source et sa validation par les tests.

---

### 1. Spécification de l'Entité de Données
Le cœur de la modélisation repose sur la classe Kotlin `CollectionItem`, qui fait office d'entité unique pour SQLite (via Room).

**Champs implémentés (Source: `CollectionItem.kt`) :**
*   **Identité :** `id` (Clé primaire), `remoteId` (Index unique pour la réconciliation).
*   **Métadonnées Littérales :** `titre`, `editeur`, `description`.
*   **Métadonnées Temporelles :** `annee`, `mois`, `updatedAt` (Timestamp système).
*   **Classification Hiérarchique :** `superCategorie`, `categorie`.
*   **Caractéristiques Physiques :** `materiau`, `tirage`, `dimensions`.
*   **Données Économiques :** `prixAchat`, `valeurEstimee`, `lieuAchat`.
*   **Données Binaires / Média :** `imageUri`, `imageEmbedding` (Signature IA de type BLOB).
*   **Logistique :** `locationId` (Lien vers la hiérarchie spatiale), `isPossessed`.

### 2. Interface de Saisie et Ergonomie
L'interface utilisateur (`EditItemActivity`) expose ces champs de manière structurée via le layout `activity_edit_item.xml`.

**Composants validés :**
*   **TextInputLayouts :** Utilisation systématique de `OutlinedBox` pour la lisibilité.
*   **Entrées spécialisées :** `numberDecimal` pour les prix et `number` pour les dates, garantissant l'intégrité des types.
*   **Saisie assistée :** `AutoCompleteTextView` pour les catégories et les localisations.

### 3. État de la Validation (Preuve par les tests)
L'implémentation source est couverte par trois niveaux de tests automatisés :

| Requirement SMART | Test Source | Description de la validation |
| :--- | :--- | :--- |
| **Point 1 : Exhaustivité** | `EditItemFieldsTest.kt` | Vérifie via Espresso que les 14 champs de saisie sont présents et visibles pour l'utilisateur. |
| **Point 3 : Persistance** | `MetadataPersistenceTest.kt` | Garantit que le cycle "Insertion -> Récupération" ne subit aucune perte de données sur l'intégralité des métadonnées. |
| **Point 5 : Horodatage** | `CollectionRepositoryTimestampTest.kt` | Confirme que toute modification via le `CollectionRepository` incrémente automatiquement le timestamp `updatedAt`. |

### 4. Conclusion Technique
La modélisation est jugée **conforme et robuste**. Elle supporte non seulement les exigences métier initiales (Titres, Éditeurs, Tirages, etc.), mais intègre nativement les dimensions logistiques et technologiques (localisations et signatures IA) indispensables à la vision produit globale.
