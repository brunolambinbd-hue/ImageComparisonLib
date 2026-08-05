# Requirement Fonctionnel 4.3 : Mesure par Référence (2€)

Ce document définit les critères SMART pour la mesure sur photo 2D via un étalon connu.

---

### 4.3.1 Calibration par Étalon (Spécifique)
*   **Requirement** : Le système doit utiliser une pièce de 2 Euros (diamètre fixe de 2.575 cm) pour calculer le ratio pixel/cm.
*   **Critère de succès** : L'utilisateur doit pouvoir ajuster un cercle bleu sur la pièce et un cadre rouge sur l'objet.
*   **Action Test** : Test unitaire sur `MeasurementOverlayView.calculateDimensions()` vérifiant que si le rayon de la pièce est de 50 pixels, le système calcule le bon nombre de cm pour un objet de 200 pixels.

### 4.3.2 Agnosticité de l'Orientation (Mesurable)
*   **Requirement** : Les dimensions (Largeur vs Hauteur) ne doivent pas dépendre de l'angle de prise de vue de la photo.
*   **Critère de succès** : Le système doit toujours renvoyer la plus grande valeur comme "Longueur" et la plus petite comme "Largeur" pour le filtrage DB.
*   **Action Test** : Test unitaire sur `DimensionUtils.isWithinTolerance` vérifiant que `Pair(20, 30)` est égal à `Pair(30, 20)`.
