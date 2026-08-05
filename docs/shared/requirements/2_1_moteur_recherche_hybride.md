# Requirements Fonctionnels : 2.1 Moteur de Recherche Hybride

Ce document définit les critères SMART pour le système d'identification intelligente combinant la reconnaissance visuelle (IA) et l'analyse textuelle (OCR).

---

### 2.1.1 Génération de Signatures Visuelles (Spécifique)
*   **Requirement** : Chaque image capturée ou importée doit être convertie en une signature mathématique unique (embedding) pour permettre la comparaison biométrique.
*   **Critère de succès** : Le système doit produire un `FloatArray` standardisé via le modèle MediaPipe. La signature doit être persistée en format binaire (`ByteArray`) dans la base de données.
*   **Action Test** : Test unitaire sur `SearchViewModel` vérifiant que l'appel à `calculateSignatureForPreview` produit une chaîne non vide pour une image valide.

### 2.1.2 Extraction Textuelle Contextuelle (Mesurable)
*   **Requirement** : Le moteur doit être capable d'extraire les mots-clés (Éditeur, Titre) présents physiquement sur l'objet photographié.
*   **Critère de succès** : Le système doit identifier correctement au moins 2 mots-clés pertinents sur une photo nette d'une couverture de BD ou d'une étiquette de figurine.
*   **Action Test** : Test unitaire sur `TextRecognitionHelper` vérifiant que l'OCR identifie "Dupuis" sur une image de test contenant ce texte.

### 2.1.3 Précision du Scoring Hybride (Pertinent)
*   **Requirement** : Le score de pertinence final doit fusionner la similarité visuelle et la correspondance textuelle pour augmenter la confiance.
*   **Critère de succès** : Un objet dont le texte (OCR) correspond à la base de données doit recevoir un "boost" de score (ex: +10% pour l'éditeur) par rapport à une recherche purement visuelle.
*   **Action Test** : Test d'intégration sur `CollectionRepository.advancedSearch` vérifiant qu'un item avec correspondance OCR remonte plus haut dans les résultats qu'un item sans correspondance textuelle à similarité visuelle égale.

### 2.1.4 Robustesse via le Mode Fallback (Atteignable)
*   **Requirement** : Si aucun objet ne dépasse le seuil de confiance de 65%, le système ne doit pas renvoyer une page vide mais les "meilleures chances".
*   **Critère de succès** : L'interface doit afficher la mention "Résultats les plus proches" (Fallback) et lister les 15 meilleurs scores, même s'ils sont inférieurs au seuil de certitude.
*   **Action Test** : Test UI Espresso vérifiant que la mention `search_fallback_results` est visible lorsque la similarité maximale trouvée est faible.

### 2.1.5 Performance de l'Analyse d'Image (Temporel)
*   **Requirement** : Le cycle complet d'identification (Prise de vue -> Signature -> OCR -> Recherche DB) doit être quasi-instantané.
*   **Critère de succès** : L'analyse d'une image (génération d'embedding + OCR) doit s'exécuter en moins de 1,5 seconde sur un appareil de milieu de gamme.
*   **Action Test** : Test de performance mesurant le temps écoulé entre l'appel à `viewModel.advancedSearch` et l'émission du `SearchResultState.Success`.
