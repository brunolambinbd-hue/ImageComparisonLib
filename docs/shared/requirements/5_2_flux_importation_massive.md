# Requirement Fonctionnel 5.2 : Flux d'Importation Massive

Ce document définit les critères SMART pour l'ingestion de données externes (CSV).

---

### 5.2.1 Robustesse du Parsing (Spécifique)
*   **Requirement** : Le moteur doit supporter des fichiers CSV encodés en Windows-1252 (Standard Excel) contenant des milliers de lignes.
*   **Critère de succès** : 100% des lignes valides doivent être intégrées. Les lignes malformées doivent être logguées sans stopper l'import.
*   **Action Test** : Test d'intégration sur `ImportViewModel` avec un fichier CSV de test contenant des points-virgules et des caractères spéciaux.

### 5.2.2 Gestion de la Mémoire (Mesurable)
*   **Requirement** : L'importation de 5000 images et signatures ne doit pas saturer la RAM de l'appareil.
*   **Critère de succès** : Le système doit traiter les objets par lots de 20 (Batch processing) avec libération des bitmaps via Coil après chaque calcul de signature.
*   **Action Test** : Test de stress-test surveillant l'allocation mémoire (`Heap`) pendant un import massif.
