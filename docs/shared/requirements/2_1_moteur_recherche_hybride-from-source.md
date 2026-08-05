# Documentation Technique 2.0 (from-source) : Moteur de Recherche Hybride

Ce document détaille l'implémentation de la fusion Vision + OCR et sa validation par les tests.

---

### 1. Stratégie de Fusion (IA + Texte)
Le moteur de recherche hybride combine deux sources de données distinctes pour identifier un objet physique.

**Composants implémentés :**
*   **Vision IA :** Utilisation de MediaPipe pour générer un vecteur d'embedding (512 dimensions).
*   **OCR Intelligent :** Utilisation de Google ML Kit pour extraire les indices textuels (Éditeur, Titre).
*   **Ajustement des Scores :** Système de boost non-cumulatif (+10% pour l'éditeur, +5% pour le titre) plafonné à 0.99 pour préserver le tri visuel.

### 2. Algorithme de Robustesse (Double Essai)
Pour pallier les problèmes d'éclairage, le système implémente une stratégie de retraitement automatique dans `SearchViewModel.kt`.

**Fonctionnement :**
1. Premier essai sur l'image brute.
2. Si le résultat est incertain (Fallback), application d'un traitement d'auto-contraste (`ImageProcessingUtils`).
3. Second essai sur l'image améliorée.

### 3. État de la Validation (Preuve par les tests)

| Requirement SMART | Test Source | Description de la validation |
| :--- | :--- | :--- |
| **Point 2 : Extraction OCR** | `TextRecognitionHelperTest.kt` | Valide le nettoyage des bruits textuels et la reconnaissance des mots-clés métier. |
| **Point 3 : Scoring Hybride** | `HybridSearchIntegrationTest.kt` | Scénario de conflit : prouve qu'un item moins proche visuellement mais identifié par l'OCR remonte en première position. |
| **Point 4 : Feedback UI** | `SearchFallbackTest.kt` | Vérifie que l'utilisateur est averti lorsque la recherche bascule en mode "Meilleure estimation". |

### 4. Conclusion Technique
Le moteur de recherche hybride est l'élément le plus **innovant** du projet. Sa structure en couches (IA -> OCR -> Amélioration d'image) lui confère une précision nettement supérieure à une recherche purement visuelle, tout en restant robuste face aux contraintes du terrain.
