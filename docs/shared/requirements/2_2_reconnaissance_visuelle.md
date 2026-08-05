# Requirement Fonctionnel 2.2 : Reconnaissance Visuelle

Ce document définit les critères SMART pour l'identification par signature mathématique d'image (Embeddings).

---

### 2.2.1 Génération d'Embeddings (Spécifique)
*   **Requirement** : Le système doit transformer toute image d'objet en un vecteur de caractéristiques numériques (`FloatArray`) de dimension fixe.
*   **Critère de succès** : L'utilisation de l'outil `ImageEmbedderHelper` doit produire une signature valide (non nulle) pour 100% des images nettes d'objets de collection.
*   **Action Test** : Test unitaire vérifiant que la méthode `computeSignature` renvoie un vecteur exploitable.

### 2.2.2 Calcul de Similarité Cosinus (Mesurable)
*   **Requirement** : La comparaison entre deux signatures doit s'appuyer sur l'algorithme de la similarité cosinus pour déterminer la proximité visuelle.
*   **Critère de succès** : Deux photos du même objet doivent présenter un score de similarité > 0.85, tandis que deux objets radicalement différents doivent être < 0.50.
*   **Action Test** : Test d'intégration sur `CollectionRepository.cosineSimilarity` validant les scores sur un jeu de signatures connues.

### 2.2.3 Persistance Binaire (Atteignable)
*   **Requirement** : Les signatures calculées lors de l'import ou de la saisie doivent être stockées de manière compacte dans SQLite.
*   **Critère de succès** : Le champ `imageEmbedding` doit être de type `BLOB` (ByteArray en Kotlin) pour optimiser l'espace disque.
*   **Action Test** : Test DAO vérifiant que le stockage et la récupération d'un ByteArray de 512 flottants ne subit aucune dégradation de précision.

### 2.2.4 Analyse des Ratios de Dimensions
*   **Requirement** : Le moteur de comparaison doit prendre en compte les proportions physiques de l'objet (Ratio Largeur/Hauteur).
*   **Critère de succès** : Un objet dont le ratio dimensionnel s'écarte de plus de 10% de l'image de recherche doit subir une pénalité de score.

### 2.2.5 Normalisation des Scores (Clamping)
*   **Requirement** : Les scores combinés (Visuel + OCR) doivent être plafonnés pour rester dans une échelle de probabilité humaine.
*   **Critère de succès** : Aucun score final ne doit excéder 99%, même avec des boosts cumulés.

### 2.2.6 Seuils de Confiance de Recherche
*   **Requirement** : Le système doit catégoriser les résultats selon des seuils de confiance mathématiques.
*   **Critère de succès** : Un résultat est considéré comme "Haute Confiance" au-delà de 0.65 de similarité.

### 2.2.7 Pondération Multi-Critères
*   **Requirement** : La recherche doit pouvoir fusionner les métadonnées (OCR, Dimensions) et la signature visuelle.
*   **Critère de succès** : L'algorithme doit appliquer des coefficients de boost spécifiques pour chaque correspondance de métadonnée.

### 2.2.8 Outils de Formatage de Signatures
*   **Requirement** : Les signatures binaires doivent pouvoir être converties en représentations lisibles pour le débogage.
*   **Critère de succès** : L'utilitaire `SignatureUtils` doit fournir un aperçu hexadécimal tronqué des premiers octets de l'embedding.
