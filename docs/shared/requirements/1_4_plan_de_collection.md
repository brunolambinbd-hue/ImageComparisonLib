# Requirements Fonctionnels : 1.4 Plan de Collection

Ce document définit les critères SMART pour le tableau de bord de pilotage de la collection et l'analyse des taux de complétion.

---

### 1.4.1 Analyse Granulaire de Complétion (Spécifique)
*   **Requirement** : Le Plan de Collection doit afficher pour chaque couple (Super-Catégorie, Catégorie) le nombre d'objets possédés par rapport au nombre total d'objets existants.
*   **Critère de succès** : L'affichage doit suivre le format "Nom (Possédés/Total)" (ex: "Sérigraphies (12/45)").
*   **Action Test** : Test unitaire sur `CollectionPlanViewModel` vérifiant la structure de la liste `FullHierarchyItem` renvoyée par le repository.

### 1.4.2 Indicateurs Visuels de Progression (Mesurable)
*   **Requirement** : Chaque segment de collection doit disposer d'une jauge de progression visuelle dont la couleur change selon le pourcentage de complétion.
*   **Critère de succès** :
    - < 25% : Rouge (Début de collection)
    - 25% à 75% : Orange/Jaune (En cours)
    - > 75% : Vert (Collection presque complète)
*   **Action Test** : Test unitaire sur `CollectionRepository.getStatusColor` vérifiant que le calcul retourne la ressource couleur appropriée selon les seuils définis.

### 1.4.3 Navigation Directe vers la Prospection (Atteignable)
*   **Requirement** : Cliquer sur un segment incomplet dans le Plan de Collection doit mener l'utilisateur directement à la liste des objets manquants de ce segment.
*   **Critère de succès** : Le clic sur un badge de catégorie doit ouvrir `ItemListActivity` filtrée sur `isPossessed = false` pour la catégorie sélectionnée.
*   **Action Test** : Test UI Espresso vérifiant que le clic sur une ligne du plan déclenche un Intent vers `ItemListActivity` avec le bon type de liste.

### 1.4.4 Synthèse Multi-Niveaux (Pertinent)
*   **Requirement** : Le plan doit être capable de regrouper les statistiques par Super-Catégorie pour offrir une vision macroscopique du patrimoine.
*   **Critère de succès** : Les catégories doivent être visuellement groupées sous leur en-tête de Super-Catégorie respectif dans le `CollectionPlanActivity`.
*   **Action Test** : Test instrumenté vérifiant le bon fonctionnement du regroupement dans le `CollectionPlanAdapter`.

### 1.4.5 Performance de l'Agrégation (Temporel)
*   **Requirement** : Le calcul complexe d'agrégation SQL (comptage croisé sur des milliers de lignes) ne doit pas geler l'interface utilisateur.
*   **Critère de succès** : Le temps entre la requête et l'affichage des données ne doit pas excéder 500ms pour une collection de 5 000 objets.
*   **Action Test** : Test de performance instrumenté (`CollectionPlanPerformanceTest.kt`) validant que la méthode `getFullHierarchy` respecte le seuil de 500ms.

---

### Détails techniques

#### Analyse et Pilotage
*   **Logique Source** : Agrégation complexe (SUM/GROUP BY) pour calculer les taux de complétion. Algorithme de coloration dynamique.
*   **Validation** : Stress-test sur 5 000 objets pour garantir une fluidité totale de l'interface.
