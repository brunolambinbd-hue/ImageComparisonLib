# Requirement Fonctionnel 3.3 : Scanner d'Inventaire

Ce document définit les critères SMART pour le flux d'assignation rapide des objets à un emplacement.

---

### 3.3.1 Identification à la Volée (Spécifique)
*   **Requirement** : L'utilisateur doit pouvoir scanner une série d'objets physiquement présents pour les assigner instantanément à l'emplacement sélectionné.
*   **Critère de succès** : After chaque scan photo, le système propose les 3 objets les plus probables de la collection.
*   **Action Test** : Test instrumenté (`.11`) dans `InventoryScannerActivity` simulant une capture et vérifiant l'ouverture automatique de `InventoryResultActivity`.

### 3.3.2 Validation d'Assignation (Mesurable)
*   **Requirement** : Une confirmation d'assignation doit mettre à jour simultanément le statut de possession et l'ID de localisation.
*   **Critère de succès** : Le clic sur un résultat de scan doit déclencher un `UPDATE` en base de données avec `isPossessed = true` et le `locationId` du scanner.
*   **Action Test** : Test d'intégration sur `InventoryViewModel` vérifiant l'appel à `repository.update` avec les bons paramètres après sélection d'un item.
