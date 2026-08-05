# Plan 2 : Nomenclature du Niveau 4 des Requirements

## Objectif
Affiner la traçabilité en qualifiant la nature de chaque instruction et de chaque test.

## Nomenclature Officielle (Suffixes de niveau 4)
Ces suffixes s'appliquent après le 3ème point (ex: 2.1.1.1).

- **.1 : Mode Nominal / Happy Path**
    - Code de production : Logique principale de succès.
    - Tests : Cas passants nominaux.
- **.2 : Exceptions / Erreurs / Fallbacks**
    - Code de production : Blocs catch, gestion d'erreurs, chemins de repli.
    - Tests : Simulation de pannes, entrées invalides, erreurs réseau/DB.
- **.3 : Sécurité / Intégrité / Validation**
    - Code de production : Validations d'entrées, contraintes, vérification de permissions.
    - Tests : Tests de robustesse des données, refus de permissions.
- **.4 : Cas Limites / Saturation Technique**
    - Code de production : Optimisations, code de transition.
    - Tests : Saturation de branches complexes, volumes extrêmes, timeouts.

## Étapes Précises
1. **Audit de l'existant** : Identifier les Req IDs actuels se terminant par `.9` (placeholders) et les reclassifier.
2. **Élimination des "Ghosts"** : Re-mapper les IDs non documentés (`US.1.9`, etc.) vers cette structure.
3. **Refactorisation du Code Source** : Mettre à jour les annotations `@SatisfiesRequirement`.
4. **Refactorisation des Tests** : Aligner les annotations `@CoveredRequirement`.
5. **Mise à jour du Script de Rapport** : Adapter `generateTraceabilityReport` pour qu'il puisse grouper les statistiques par type de mode.
