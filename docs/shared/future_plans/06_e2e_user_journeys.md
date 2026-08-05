# Plan 6 : Scénarios UI End-to-End Réalistes

## Objectif
Valider la robustesse des parcours utilisateurs complets (User Journeys).

## Étapes Précises
1. **Définition des Parcours Critiques** :
    - *Parcours A* : Acquisition complète (Prendre photo -> Mesure AR -> Enregistrement -> Backup).
    - *Parcours B* : Audit et Correction (Import CSV -> Détection anomalies via Audit -> Correction manuelle).
    - *Parcours C* : Recherche Multimodale intensive.
2. **Implémentation Espresso/UIAutomator** : Créer des classes de tests dédiées à ces parcours, simulant des délais et des interactions réalistes.
3. **Injection de Fautes** : Simuler des interruptions pendant ces parcours (ex: appel entrant fictif, rotation d'écran, perte de connexion pendant un backup).
4. **Reporting Visuel** : Configurer la capture de screenshots systématique en cas d'échec dans ces scénarios critiques.
