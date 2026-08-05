# Requirement Fonctionnel 5.4 : Stratégie de Sauvegarde

Ce document définit les critères SMART pour la protection contre la perte de données.

---

### 5.4.1 Sauvegarde Physique Atomique (Spécifique)
*   **Requirement** : L'utilisateur doit pouvoir exporter la base de données SQLite complète vers un fichier externe sélectionnable.
*   **Critère de succès** : Le fichier `.db` généré doit être lisible par un explorateur SQLite standard et contenir l'intégralité des tables (`collection_items`, `locations`, etc.).
*   **Action Test** : Test unitaire sur `BackupViewModel.backupDatabase` simulant une écriture vers un URI simulé.

### 5.4.2 Restauration avec Intégrité (Mesurable)
*   **Requirement** : La restauration d'un fichier de sauvegarde doit remplacer la base actuelle sans créer de doublons ou de conflits.
*   **Critère de succès** : Le système doit fermer toutes les instances Room (`AppDatabase.closeInstance()`) avant de remplacer le fichier physique.
*   **Action Test** : Test instrumenté vérifiant qu'after restauration, l'application se relance avec les nouvelles données.
