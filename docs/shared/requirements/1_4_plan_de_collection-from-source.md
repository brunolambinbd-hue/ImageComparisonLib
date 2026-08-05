# Documentation Technique 1.3 (from-source) : Plan de Collection

Ce document détaille l'implémentation du tableau de bord de pilotage et les algorithmes d'analyse de complétion.

---

### 1. Architecture de l'Analyse en Temps Réel
Le Plan de Collection est une vue agrégée construite via une requête SQL complexe définie dans `CollectionDao.kt`.

**Traitement des données (Source: `CollectionRepository.kt`) :**
*   **Agrégation récursive :** Calcul du ratio (Possédés / Total) pour chaque branche de l'arborescence.
*   **Logique de Coloration :** Algorithme `getStatusColor` qui mappe le pourcentage de complétion vers des ressources couleurs (R.color.status_ok/warning/error).

### 2. Interface de Pilotage Haute Densité
L'affichage utilise un `CollectionPlanAdapter` capable de grouper visuellement les données sous forme de cartes thématiques.

**Fonctionnalités validées :**
*   **Badges Dynamiques :** Affichage compact des statistiques par catégorie.
*   **Navigation Contextuelle :** Lien direct entre un badge incomplet et la liste des objets à rechercher (`isPossessed = false`).

### 3. État de la Validation (Preuve par les tests)

| Requirement SMART | Test Source | Description de la validation |
| :--- | :--- | :--- |
| **Point 2 : Indicateurs Visuels** | `CollectionRepositoryColorTest.kt` | Valide les seuils mathématiques (25% et 75%) déclenchant les changements de couleurs du tableau de bord. |
| **Point 3 : Navigation** | `CollectionPlanNavigationTest.kt` | Vérifie que le clic sur un segment déclenche bien le filtre correct pour la recherche d'objets manquants. |
| **Point 5 : Performance** | `CollectionPlanPerformanceTest.kt` | Stress-test sur 5 000 objets garantissant un temps de calcul SQL inférieur à 500ms. |

### 4. Conclusion Technique
Le Plan de Collection transforme une base de données brute en un **outil de décision stratégique**. Grâce à une optimisation SQL rigoureuse et une validation par benchmarks, il reste fluide et précis quelle que soit la taille du patrimoine géré.
