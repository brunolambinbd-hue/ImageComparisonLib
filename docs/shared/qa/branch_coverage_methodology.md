# Méthodologie de Couverture de Branches (Jacoco 95%+)

Ce document récapitule la méthode éprouvée pour atteindre et dépasser les 95% de couverture de branches dans un projet Android Kotlin, en ciblant spécifiquement les ViewModels et les Repositories.

---

## 1. Analyse du Rapport Jacoco
Le point de départ est toujours le rapport HTML.
*   **Rouge** : Code non exécuté.
*   **Jaune** : Branchement partiel (bytecode complexe).
*   **Vert** : Couverture totale.

**Note technique** : Une ligne jaune signifie souvent que le compilateur Kotlin a généré des branches invisibles au niveau source (opérateurs Elvis `?:`, Safe Calls `?.`, ou machines à états de Coroutines).

## 2. Stratégie de Refactorisation : "Thérapie par décomposition"
Pour verdir les lignes jaunes, il faut aider Jacoco en simplifiant le bytecode généré.

### A. Extraction des blocs anonymes
Ne laissez pas de logique complexe dans des lambdas de `viewModelScope.launch` ou `withContext`.
*   **Action** : Extraire le contenu dans une fonction `internal suspend` ou une méthode privée.
*   **Bénéfice** : Permet de tester la logique de la coroutine indépendamment de son déclenchement.

### B. Éclatement des expressions complexes
Les chaînes de caractères type `val x = a?.b ?: c` génèrent 3 à 4 branches de bytecode.
*   **Action** : Remplacer par un bloc `if / else` explicite.
*   **Bénéfice** : Jacoco peut valider chaque branche de manière unitaire.

### C. Isolation de la récursivité
Pour les fonctions récursives (ex: parcours de hiérarchie), extraire le corps de la boucle dans une méthode dédiée.

## 3. Techniques de Tests Chirurgicaux ("Saturation")
Une fois le code simplifié, on crée des tests de "saturation" qui ciblent les branches techniques restantes.

### A. Saturation des Seuils (Boundary Testing)
Pour les algorithmes de scoring ou de filtrage :
*   Tester la valeur **exacte** du seuil (ex: `similarity == 0.1f`).
*   Tester `seuil - epsilon` (ex: 0.09f).
*   Tester `seuil + epsilon` (ex: 0.11f).

### B. Saturation des Gardes de Log
Les lignes `if (Log.isLoggable(TAG, Log.DEBUG))` restent souvent jaunes.
*   **Action** : Utiliser `ShadowLog.setLoggable(TAG, Log.DEBUG)` dans le test.
*   **Action** : Créer un second test avec `Log.ASSERT` (logs désactivés) pour couvrir la branche `false`.

### C. Saturation de la Machine à États (Coroutines)
Les blocs `withContext` ou `await()` créent des points de suspension.
*   **Chemin de reprise** : Utiliser `runTest` et `advanceUntilIdle()` pour s'assurer que la coroutine finit son cycle.
*   **Chemin d'annulation** : Lancer la coroutine dans un `job` et appeler `job.cancelAndJoin()` immédiatement pour forcer les branches d'interruption.
*   **Chemin d'exception** : Mocker une dépendance pour qu'elle lève une `Exception` (ou un `Throwable` fatal) à l'intérieur du bloc asynchrone.

### D. Saturation des Objets Système (Android)
Utiliser des `ContextWrapper` ou des mocks de `ContentResolver` pour forcer des cas impossibles (ex: `getDatabasePath` renvoie `null`).

## 4. Check-list de succès
1.  [ ] La méthode est-elle extraite du bloc `launch` ?
2.  [ ] Les opérateurs `?.` et `?:` ont-ils été remplacés par des `if` explicites ?
3.  [ ] Existe-t-il un test forçant une exception brute (`RuntimeException`) ?
4.  [ ] Existe-t-il un test avec les logs désactivés ?
5.  [ ] Les valeurs limites (null, vide, max) sont-elles toutes passées en paramètre ?

---
*Document maintenu par l'équipe QA - ParaBDCollector*
