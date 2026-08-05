# Requirement Fonctionnel 2.4 : Logique de "Fallback"

Ce document définit les critères SMART pour la présentation des résultats en cas de faible certitude.

---

### 2.4.1 Seuil de Confiance Strict (Spécifique)
*   **Requirement** : Le système doit distinguer les résultats "certains" des résultats "probables".
*   **Critère de succès** : Le seuil de haute confiance est fixé à 65% (0.65).
*   **Action Test** : Test unitaire sur le filtrage des résultats vérifiant que les items < 0.65 sont marqués comme incertains.

### 2.4.2 Affichage des Meilleures Chances (Mesurable)
*   **Requirement** : Si aucun item ne dépasse le seuil de 65%, le système doit proposer les 15 meilleurs résultats triés par score décroissant.
*   **Critère de succès** : L'utilisateur ne doit jamais être confronté à une page "Aucun résultat" si des items avec une similarité > 0.1 existent en base.
*   **Action Test** : Test UI Espresso vérifiant que la liste est peuplée même pour une recherche floue.

### 2.4.3 Signalement de l'Incertitude (Pertinent)
*   **Requirement** : L'interface doit avertir explicitement l'utilisateur lorsque les résultats ne sont pas garantis.
*   **Critère de succès** : Affichage du message `search_fallback_results` dans le bandeau de résumé si le flag `isFallback` du résultat est vrai.
*   **Action Test** : Test UI vérifiant la visibilité du texte d'avertissement en mode Fallback.
