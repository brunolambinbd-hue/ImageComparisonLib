# Documentation Technique 2.2 (from-source) : Analyse Textuelle OCR

Ce document détaille l'implémentation de l'extraction de mots-clés par OCR et le système de boosting associé, incluant la gestion avancée des cas limites (Edge Cases).

---

### 1. Pipeline de Nettoyage Avancé (Source: `TextRecognitionHelper.kt`)
Le moteur OCR ne se contente pas de lire le texte ; il applique un pipeline de transformation pour ne conserver que la "substance" sémantique nécessaire à la recherche.

**Logique de traitement par étapes :**
1.  **Extraction brute :** Récupération du texte via Google ML Kit.
2.  **Normalisation :** Conversion en minuscules.
3.  **Nettoyage Alphanumérique :** Suppression de tous les caractères non-alphanumériques (points, parenthèses, symboles monétaires, apostrophes).
4.  **Filtrage Sémantique :**
    *   **Seuil de longueur :** Seuls les mots dont le contenu "utile" (alphanumérique) est strictement supérieur à 3 caractères sont conservés. Cela élimine les articles ("le", "la"), les abréviations ("vol.", "ed.") et le terme trop générique "BD".
    *   **Élimination numérique :** Les nombres purs (ex: "2024", "118") et les prix/décimales (ex: "45.00€", "1.250,50") sont exclus après nettoyage des symboles.

### 2. Matching Métier (Libraires et Éditeurs)
Le système reconnaît que dans le para-BD, le champ "Éditeur" contient souvent le nom d'un **libraire** ou d'un galeriste.

**Exemples de librairies supportées et validées en série :**
*   Sans Titre, Durango, Espace BD, Hobby Folie, Paradise BD, Multi BD.

Le matching s'effectue dans le `CollectionRepository` en comparant les mots-clés OCR avec le champ `editeur` de la table Room via une comparaison insensible à la casse.

### 3. Système de Boosting de Score
Lorsqu'un mot détecté par l'OCR correspond à l'éditeur ou au titre en base de données, un bonus de pertinence est appliqué :
*   **Bonus Éditeur :** +0.10 (+10%).
*   **Bonus Titre :** +0.05 (+5%).
*   **Seuil de visibilité inclusif :** Pour garantir qu'un objet reconnu *uniquement* par l'OCR (similarité visuelle de 0.0) soit affiché, le filtre de résultat a été ajusté à **`>= 0.1`**.

### 4. État de la Validation (Preuve par les tests)

| Cas Testé | Test Source | Résultat Validé |
| :--- | :--- | :--- |
| **Normalisation** | `TextRecognitionHelperTest.kt` | "SANS TITRE" -> ["sans", "titre"] (Match database possible). |
| **Cas Limites (Prix)** | `TextRecognitionHelperTest.kt` | "Prix: 45.00€" -> ["prix"] (Les chiffres et symboles sont gommés). |
| **Cas Limites (Dimensions)**| `TextRecognitionHelperTest.kt` | "15.5 x 21.0 cm" -> [] (Aucun mot-clé parasite créé). |
| **Matching Casse DB** | `CollectionRepositoryScoringTest.kt` | Valide en série que l'OCR majuscule match les entrées Room mixtes pour les 6 librairies cibles. |
| **Seuil de visibilité** | `CollectionRepositoryScoringTest.kt` | Prouve qu'un score de 0.1 (boost pur) est suffisant pour apparaître dans les résultats. |
| **Conflit Visuel/OCR** | `HybridSearchIntegrationTest.kt` | Un boost OCR permet de dépasser un item visuellement plus proche. |

### 5. Conclusion Technique
L'implémentation de l'OCR est **robuste et contextuelle**. En isolant les mots-clés sémantiques et en ajustant les seuils de filtrage pour inclure les bonus textuels, elle fournit au moteur de recherche une précision et une visibilité accrues, même en l'absence de correspondance visuelle forte.
