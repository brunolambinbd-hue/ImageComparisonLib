# Requirement Fonctionnel 4.2 : Mesure ARCore

Ce document définit les critères SMART pour la mesure de dimensions réelles via la Réalité Augmentée.

---

### 4.2.1 Détection de Plan (Spécifique)
*   **Requirement** : Le système doit détecter les surfaces horizontales stables pour servir de base aux mesures.
*   **Critère de succès** : Le `TrackingState` doit passer à `TRACKING` et au moins un `Plane` doit être identifié avant d'autoriser la pose de points.
*   **Action Test** : Test de diagnostic matériel dans `MeasureActivity` vérifiant le support d'ARCore sur l'appareil.

### 4.2.2 Précision Métrique (Mesurable)
*   **Requirement** : Le calcul de la distance entre deux points AR doit être converti en centimètres avec une précision de +/- 1cm.
*   **Critère de succès** : La formule de distance Euclidienne 3D (`sqrt(dx²+dy²+dz²)`) doit être appliquée sur les `hitPose` de l'utilisateur.
*   **Action Test** : Test unitaire sur `MeasureActivity.addPoint()` simulant deux poses à 10cm de distance et vérifiant la valeur de `lastDistance`.
