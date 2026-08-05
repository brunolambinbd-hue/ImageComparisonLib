# Documentation Technique 1.2 (from-source) : Structuration Hiérarchique

Ce document détaille le fonctionnement du moteur d'organisation automatique et sa validation technique.

---

### 1. Moteur de Mapping (Single Source of Truth)
L'organisation hiérarchique ne repose pas sur une saisie utilisateur manuelle, mais sur l'objet `CategoryMapper.kt`.

**Fonctionnement interne :**
*   **Dictionnaire statique :** Une map immuable associe plus de 30 catégories détaillées (ex: "Bronze", "Sérigraphies") à 8 Super-Catégories standardisées (Albums, Image, Objets, etc.).
*   **Agnosticisme de la base :** Le mapping est calculé dynamiquement, permettant de mettre à jour la structure de toute la collection simplement en modifiant le code du Mapper.

### 2. Système d'Audit et de Réparation
Pour garantir l'intégrité après des imports CSV imparfaits, le système utilise le `CategoryAuditViewModel.kt`.

**Capacités de détection :**
*   Identifie les objets avec des super-catégories vides, nulles ou contenant des erreurs de type "N/D" (Excel).
*   Applique les règles du Mapper de manière asynchrone pour réparer la base de données.

### 3. État de la Validation (Preuve par les tests)

| Requirement SMART | Test Source | Description de la validation |
| :--- | :--- | :--- |
| **Point 1 : Centralisation** | `CategoryMapperTest.kt` | Vérifie que le dictionnaire produit des listes triées et des associations correctes sans doublons. |
| **Point 3 : Réparation** | `CategoryAuditViewModelTest.kt` | Simule des erreurs de données ("#N/D") et valide que le moteur les transforme bien en catégories valides. |
| **Point 5 : Assistance UI** | `CategoryAutoFillTest.kt` | Teste l'interaction réelle : choisir une Super-Catégorie dans l'interface filtre instantanément les choix de catégories disponibles. |

### 4. Conclusion Technique
La structuration hiérarchique est **déterministe et automatisée**. Elle réduit la charge cognitive de l'utilisateur en déduisant les catégories parentes et assure une base de données propre grâce aux outils d'audit intégrés.
