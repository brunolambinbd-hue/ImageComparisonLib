# Requirements Fonctionnels : 1.1 Patrimoine vs Prospection

Ce document définit les critères de succès pour la gestion de l'inventaire physique (Patrimoine) et de la liste de recherche (Prospection).

---

### User Stories Globales

#### US.1 Navigation et Dashboard
*   **Requirement** : L'utilisateur doit pouvoir naviguer entre les différentes sections de l'application (Recherche, Inventaire, Plan) via un menu latéral et un tableau de bord central.
*   **Critère de succès** : Toutes les activités principales sont accessibles depuis `MainActivity` et respectent le thème visuel global.
*   **Action Test** : Test de navigation complet (`MainSearchEditFlowTest.kt`).

---

### US.Workflow Intégrité du Flux Métier (Transverse)
*   **Requirement** : Le système doit garantir la cohérence des données lors d'un cycle complet (Acquisition -> Recherche -> Inventaire).
*   **Critère de succès** : Un objet recherché peut être transformé en objet possédé sans perte de métadonnées.
*   **Action Test** : Test de bout-en-bout (`ParaBDWorkflowExhaustiveTest`).

---

### 1.1.1 Distinction du Statut de Possession (Spécifique)
*   **Requirement** : L'application doit permettre de distinguer les objets possédés de ceux recherchés via le flag `isPossessed`.
*   **Critère de succès** : Le tableau de bord affiche les deux compteurs séparément.
*   **Action Test** : Un test unitaire sur `CollectionItem` doit valider que l'état par défaut est cohérent et qu'aucune valeur nulle n'est possible.

### 1.1.2 Exactitude des Compteurs Dynamiques (Mesurable)
*   **Description** : Les compteurs UI (Toolbar et Dashboard) doivent refléter le nombre exact d'entrées en base de données.
*   **Action Test** : Un test instrumenté (`.11`) doit vérifier que `PossessedCounter` == `SELECT COUNT(*) WHERE isPossessed = 1`.

### 1.1.3 Workflow de Transition d'État (Atteignable)
*   **Description** : Le passage d'un objet de "Prospection" vers "Patrimoine" (Achat) doit permettre l'assignation immédiate d'un emplacement (`locationId`).
*   **Action Test** : Vérifier dans `BatchPossessionViewModel` que la mise à jour du statut déclenche bien la possibilité de choisir une localisation.

### 1.1.4 Visibilité Conditionnelle des Métadonnées (Pertinent)
*   **Description** : Les champs liés à l'acquisition (Prix d'achat, Date d'achat, Lieu d'achat) ne doivent être visibles et éditables que si `isPossessed` est vrai.
*   **Action Test** : Test instrumenté (`.11`) vérifiant la propriété `visibility` des champs de prix dans `EditItemActivity` selon l'état du switch possession.

### 1.1.5 Réactivité des Statistiques de Complétion (Temporel)
*   **Description** : Le calcul des statistiques du "Plan de Collection" doit être effectué en moins de 200ms pour garantir une navigation fluide.
*   **Action Test** : Test de performance mesurant le temps d'exécution de `CollectionRepository.getSuperCategoryInfo`.

---

### Détails techniques

#### Distinction Patrimoine / Prospection
*   **Logique Source** : Repose sur le flag `isPossessed` dans l'entité. Les compteurs sont pilotés par des requêtes `COUNT` réactives.
*   **Validation** : Vérifie que le basculement d'un état à l'autre déclenche les mises à jour UI et DB sans latence.
