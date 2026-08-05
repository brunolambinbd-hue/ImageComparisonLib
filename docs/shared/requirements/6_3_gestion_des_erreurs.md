# Requirement Fonctionnel 6.3 : Gestion des Erreurs

Ce document définit les critères SMART pour la capture et le rapport des crashs.

---

### 6.3.1 Interception des Crashs (Spécifique)
*   **Requirement** : L'application ne doit jamais "disparaître" brusquement lors d'une exception non gérée.
*   **Critère de succès** : Le `GlobalExceptionHandler` doit intercepter tout crash et rediriger l'utilisateur vers la `CrashActivity`.
*   **Action Test** : Test instrumenté provoquant une exception volontaire et vérifiant que la `CrashActivity` est affichée.

### 6.3.2 Rapport Technique d'Audit (Mesurable)
*   **Requirement** : Chaque crash doit générer un log textuel complet (StackTrace).
*   **Critère de succès** : Le log doit être enregistré dans le stockage privé de l'app (`crash_log.txt`) et être copiable dans le presse-papier par l'utilisateur.
*   **Action Test** : Test unitaire sur `GlobalExceptionHandler.writeErrorToLog` vérifiant la création du fichier après une exception.
