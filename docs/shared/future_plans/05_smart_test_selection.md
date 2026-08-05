# Plan 5 : Analyse d'Impact et Sélection de Tests (Smart Selection)

## Objectif
Réduire le temps de feedback en ne lançant que les tests impactés par une modification.

## Étapes Précises
1. **Cartographie de Dépendances** : Créer un fichier de mapping (ou une convention de nommage stricte) liant chaque classe source à ses classes de tests (Unitaires et Instrumentés).
2. **Script de Détection de Changement** : Créer un outil (script shell ou plugin Gradle) qui utilise `git diff` pour lister les fichiers modifiés.
3. **Tâche `testImpacted`** : Développer une tâche Gradle personnalisée qui :
    - Lit la liste des fichiers modifiés.
    - Identifie les tests correspondants via la cartographie.
    - Exécute uniquement cette sélection filtrée.
4. **Fallback** : Maintenir la possibilité de lancer `testAll` pour les releases finales.
