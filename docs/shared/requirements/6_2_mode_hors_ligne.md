# Requirement Fonctionnel 6.2 : Mode Hors-ligne

Ce document définit les critères SMART pour le fonctionnement autonome de l'application.

---

### 6.2.1 Autonomie de l'IA (Spécifique)
*   **Requirement** : La reconnaissance visuelle doit fonctionner sans aucune connexion Internet.
*   **Critère de succès** : Le modèle de reconnaissance d'image MediaPipe doit être embarqué localement dans le dossier `assets`.
*   **Action Test** : Test instrumenté vérifiant que `imageEmbedderHelper` peut s'initialiser et produire un result alors que le mode avion est activé.

### 6.2.2 Persistance Locale Prioritaire (Mesurable)
*   **Requirement** : Toutes les données saisies doivent être accessibles instantanément même sans réseau.
*   **Critère de succès** : L'application doit utiliser exclusivement SQLite via Room comme stockage primaire.
*   **Action Test** : Test unitaire vérifiant que la recherche simple retourne des résultats depuis la base locale.
