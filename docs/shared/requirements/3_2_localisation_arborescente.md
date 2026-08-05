# Requirement Fonctionnel 3.2 : Localisation Arborescente

Ce document définit les critères SMART pour la gestion logistique des lieux de stockage.

---

### 3.2.1 Structure Récursive (Spécifique)
*   **Requirement** : Le système doit permettre de créer une hiérarchie infinie de lieux de stockage (ex: Pièce > Meuble > Étagère > Boîte).
*   **Critère de succès** : Chaque localisation (`Location`) peut posséder un `parentId` pointant vers une autre localisation.
*   **Action Test** : Test instrumenté (`.11`) vérifiant qu'une localisation enfant est correctement affichée sous son parent dans la liste.

### 3.2.2 Calcul du Chemin d'Accès (Mesurable)
*   **Requirement** : L'interface doit afficher le chemin complet ("Breadcrumb") pour chaque objet afin de faciliter sa localisation physique.
*   **Critère de succès** : Pour une boîte dans un meuble dans le salon, l'UI doit afficher "Salon > Meuble A > Boîte 12".
*   **Action Test** : Test instrumenté (`.11`) vérifiant la reconstruction correcte de la chaîne de caractères dans les formulaires et listes.

### 3.2.3 Intégrité lors de la Suppression (Atteignable)
*   **Requirement** : La suppression d'un parent doit être gérée de manière sécurisée pour les enfants et les objets liés.
*   **Critère de succès** : Le système doit interdire la suppression d'un lieu s'il contient des sous-lieux ou des objets, ou proposer une migration vers la racine.
*   **Action Test** : Test d'intégration DAO vérifiant le comportement des contraintes de clé étrangère.
