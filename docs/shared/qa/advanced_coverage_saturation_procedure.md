# Procédure de Saturation Chirurgicale de la Couverture (99%+)

Ce document détaille les techniques avancées utilisées pour saturer les branches de bytecode "invisibles" générées par le compilateur Kotlin, permettant d'atteindre une couverture quasi-totale (99%+) sur les ViewModels et classes complexes.

---

## 1. Saturation des Paramètres par Défaut (`$default`)
En Kotlin, une fonction avec des paramètres par défaut (ex: `dispatcher: CoroutineDispatcher = Dispatchers.IO`) génère une méthode synthétique en bytecode contenant un branchement conditionnel.
*   **Symptôme** : La signature de la fonction ou la ligne d'initialisation reste jaune dans JaCoCo.
*   **Technique** : Ajouter un test qui appelle la fonction **sans passer l'argument optionnel**.
*   **Résultat** : Force le bytecode à emprunter la branche "utilisation de la valeur par défaut".

## 2. Saturation des Blocs de Coroutines (`invokeSuspend`)
Les blocs `withContext`, `async` et `launch` génèrent des machines à états complexes. Pour les verdir intégralement :

### A. Branche de "Dispatcher Identity"
Les blocs `withContext(dispatcher)` vérifient si le dispatcher demandé est le même que l'actuel.
*   **Technique** : Créer deux tests. L'un avec le `TestDispatcher` (branche "identité"), l'autre avec un dispatcher réel ou différent comme `Dispatchers.Default` (branche "dispatch réel").

### B. Branche de "Reprise Technique" (Resumption with Failure)
Le bytecode contient du code pour gérer les échecs critiques survenant pendant la suspension.
*   **Technique** : Mocker la fonction suspendue pour qu'elle jette une **`Error`** ou un **`Throwable`** technique (ex: `VerifyError`, `LinkageError`) au lieu d'une `RuntimeException`.
*   **Stabilité** : Capturer ce `Throwable` dans le test avec un bloc `try-catch` autour de `advanceUntilIdle()` pour éviter de faire échouer le build Gradle (évite le code de sortie 10).

### C. Branche d'Annulation (Cancellation)
*   **Technique** : Lancer la coroutine, puis appeler immédiatement `job.cancelAndJoin()` avant de synchroniser le temps virtuel.
*   **Validation** : Vérifier `assertTrue(job.isCancelled)`. Cela sature les branches de nettoyage du bytecode.

## 3. Saturation des Blocs de Ressources (`use`)
Le bloc `.use { ... }` génère un `try-finally` interne pour garantir la fermeture des ressources (comme les `Cursor`).
*   **Technique** : Forcer une exception **à l'intérieur** du bloc `use`.
*   **Résultat** : Cela oblige le bytecode à passer par le chemin `finally` généré par le compilateur pour fermer la ressource en cas d'erreur.

## 4. Gestion des URIs et Données Opaque
Pour les fonctions de traitement de fichiers/URIs :
*   **Technique** : Tester des URIs "opaques" (sans slash, ex: `file:test.csv` ou `mailto:user@ex.com`).
*   **Fallback** : S'assurer que le code utilise `uri.schemeSpecificPart` si `uri.path` est null.
*   **Symptôme** : Couvre les branches de repli (Elvis operator) sur les propriétés de l'URI.

## 5. Saturation des États Système (Shadows)
Utiliser Robolectric pour forcer des états matériels difficiles à reproduire.
*   **Exemple** : `ShadowEnvironment.setExternalStorageState(Environment.MEDIA_REMOVED)`.
*   **Objectif** : Couvrir les branches `else` des vérifications de disponibilité du stockage.

## 6. Stabilité et Diagnostic des Tests
Pour éviter que les tests de saturation technique ne polluent les rapports ou ne crashent le pipeline :
*   **Capture par String** : Utiliser `e.stackTraceToString().contains("Marker")` pour identifier l'erreur injectée.
*   **Mocks de Prudence** : Toujours mocker les `LiveData` retournés par les repositories (ex: `whenever(repo.getAll()).thenReturn(MutableLiveData(emptyList()))`) pour éviter les `NullPointerException` dans les blocs `init` des ViewModels lors des tests de branche.

---
*Document de référence technique - Équipe Performance Couverture (PC)*
