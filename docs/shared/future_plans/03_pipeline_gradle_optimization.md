# Plan 3 : Optimisation du Pipeline CI/CD et Gradle

## Objectif
Fiabiliser l'exécution automatisée sans saturer les ressources de l'émulateur.

## Étapes Précises
1. **Parallélisation Intelligente** : Configurer Gradle pour diviser les tests instrumentés en "shards" exécutables en séquence ou sur plusieurs instances si possible.
2. **Gestion de l'État de l'Émulateur** : Enrichir la tâche `checkEmulatorStatus` pour forcer un nettoyage du cache et un redémarrage des services de rendu (GLSurfaceView) entre les grosses suites de tests.
3. **Orchestration Jacoco** : Optimiser la tâche `generatePipelineReport` pour qu'elle fusionne les résultats de manière incrémentale, évitant ainsi les pics de consommation mémoire.
4. **Alerting** : Ajouter une notification de dépassement de ressources (timeout) spécifique au pipeline.
