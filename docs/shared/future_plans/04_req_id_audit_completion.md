# Plan 4 : Audit et Complétion des Req IDs dans les Tests

## Objectif
Atteindre 100% de traçabilité documentaire pour la suite de tests.

## Étapes Précises
1. **Utilisation de `validateTestTraceability`** : Lancer la tâche existante pour lister tous les tests orphelins (environ 1000+ tests à vérifier).
2. **Campagne d'Annotation Massive** : Parcourir les fichiers de tests (ViewModels, Repositories, Utils) et rattacher chaque méthode `@Test` à sa fonction source.
3. **Validation Croisée** : S'assurer qu'un test annoté `CoveredRequirement("X")` correspond bien à une méthode source annotée `SatisfiesRequirement("X")`.
4. **Verrouillage du Pipeline** : Faire échouer le build si un nouveau test est ajouté sans annotation (en rendant `validateTestTraceability` bloquante).
