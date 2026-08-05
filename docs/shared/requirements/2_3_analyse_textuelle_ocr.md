# Requirement Fonctionnel 2.3 : Analyse Textuelle OCR

Ce document définit les critères SMART pour l'extraction de mots-clés depuis l'image afin d'affiner la recherche.

---

### 2.3.1 Extraction de Mots-clés Métier (Spécifique)
*   **Requirement** : Le système doit isoler les termes textuels présents sur l'objet (Éditeur, Titre, Volume). Les mots-clés extraits doivent être prioritairement recherchés dans le champ **Editeur** des objets de la collection.
*   **Note métier** : Sur les objets de type para-BD (ex: ex-libris), le nom imprimé identifié comme "éditeur" correspond souvent à un **libraire** ou un galeriste.
*   **Critère de succès** : Identification correcte de noms d'éditeurs/libraires connus (ex: Dupuis, Dargaud, Pixi, Forbidden Planet) avec un taux de réussite > 80% on des images moyennement éclairées.
*   **Action Test** : Test unitaire sur `TextRecognitionHelper.extractText` avec des bitmaps contenant des textes prédéfinis.

### 2.3.2 Filtrage des Bruits (Mesurable)
*   **Requirement** : Le moteur doit filtrer les mots non pertinents (mots courts, codes-barres, artefacts).
*   **Critère de succès** : Le système doit ignorer systématiquement les chaînes de moins de 3 caractères et les suites numériques isolées.
*   **Action Test** : Test unitaire sur `TextRecognitionHelper.filterWords` vérifiant la suppression des "le", "la", "123".

### 2.3.3 Système de Bonus au Score (Pertinent)
*   **Requirement** : Toute correspondance textuelle confirmée par l'OCR doit augmenter artificiellement le score de l'objet dans les résultats de recherche.
*   **Critère de succès** : Un bonus de +10% (éditeur) ou +5% (titre) est appliqué au score visuel initial, avec un plafond de sécurité à 0.99.
*   **Action Test** : Test d'intégration sur la logique de recherche avancée vérifiant le dépassement d'un item grâce au bonus OCR.
