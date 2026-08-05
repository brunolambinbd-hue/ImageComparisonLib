# Taxonomie des Tests Instrumentés (.1x)

Ce document définit les suffixes techniques utilisés dans les annotations `@CoveredRequirement` pour les tests instrumentés (UI Automator / Espresso), permettant de distinguer la nature de la vérification.

---

## .11 : Flux Métier Nominaux (Happy Path)
*   **Objectif** : Valider que la fonctionnalité principale marche comme prévu pour l'utilisateur.
*   **Exemple** : Ajouter un objet, effectuer une recherche par titre, créer une localisation.
*   **Standard** : Doit utiliser UI Automator pour simuler une interaction réelle.

## .13 : Flux d'Erreurs et de Rupture (Edge Cases)
*   **Objectif** : Valider la résilience de l'interface face aux problèmes techniques.
*   **Cibles** :
    *   Erreurs de base de données (DB Error).
    *   Matériel non supporté (ARCore, Caméra).
    *   Annulations de l'utilisateur (Back button dans un Picker).
    *   Fichiers corrompus ou malformés.

## .14 : Navigation et Structure de l'Interface
*   **Objectif** : Valider l'intégrité de la navigation et du rendu visuel.
*   **Cibles** :
    *   Bouton "Home" (Android ID home) dans la Toolbar.
    *   Menu latéral (Navigation Drawer).
    *   Rotation d'écran (Configuration changes).
    *   Visibilité conditionnelle des champs.

## .19 : Saturation Chirurgicale (Coverage Boost)
*   **Objectif** : Atteindre les lignes orphelines identifiées par Jacoco qui ne sont pas couvertes par les flux métier.
*   **Cibles** :
    *   Branches `else` de sécurité.
    *   Toasts de succès ou messages de diagnostic éphémères.
    *   Cas de données "impossibles" injectées via Mocks.

---

## Résumé de la Nomenclature complète

| Suffixe | Type de Test | Framework privilégié |
|---------|--------------|----------------------|
| **.1**  | Unitaire (Logique) | JUnit / Mockito |
| **.2**  | Unitaire (State) | JUnit / LiveData |
| **.9**  | Technique (Bytecode) | JUnit / Surgical |
| **.11** | UI (Métier) | UI Automator |
| **.13** | UI (Erreurs) | UI Automator + Mocks |
| **.14** | UI (Navigation) | UI Automator |
| **.19** | UI (Saturation) | UI Automator (Precise) |

*Document de référence - Version 1.0 (08/07/2026)*
