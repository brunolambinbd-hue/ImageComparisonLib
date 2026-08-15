# Plan de Création : Librairie Autonome `ImageComparisonLib`

Ce document définit la stratégie pour extraire le module `:imagecomparison` du projet principal et le transformer en une librairie Android indépendante et réutilisable.

## 🎯 Objectifs
1.  **Sanctuariser l'IA** : Isoler MediaPipe des instabilités de mise à jour du projet principal.
2.  **Modularité** : Permettre l'utilisation de la comparaison d'images dans d'autres projets futurs.
3.  **Performance** : Réduire le temps de build de ParaBDCollector en ne recompilant plus le cœur IA systématiquement.

---

## 🏗️ Phase 1 : Initialisation du Nouveau Projet (30 min)
*Action : Création d'un nouveau projet "Android Library" dans Android Studio.*

1.  **Nom du projet** : `ImageComparisonLib`.
2.  **Package racine** : `com.parabd.lib.imagecomparison`.
3.  **Configuration SDK** :
    *   `minSdk` : 26 (cohérent avec ParaBDCollector).
    *   `targetSdk / compileSdk` : 35 (Android 15).
4.  **Moteur de build** : Gradle 8.10.2 / AGP 8.7.3.

---

## 📦 Phase 2 : Transfert des Actifs IA (45 min)
*Action : Migration chirurgicale du code et des modèles.*

1.  **Code Source** : Copier `ImageEmbedderHelper.kt` et `EmbeddingUtils.kt`.
2.  **Modèles TFLite** : Déplacer `mobilenet_v3_large.tflite` et `mobilenet_v3_small.tflite` vers le nouveau dossier `src/main/assets/`.
3.  **Contrat de Traçabilité** : Copier les fichiers `SatisfiesRequirement.kt` et `CoveredRequirement.kt` pour que la librairie reste auto-documentée.

---

## ⚙️ Phase 3 : Configuration du Build & Publication (1h)
*Action : Mise en place du mécanisme de distribution locale.*

1.  **Version Catalog** : Créer un `libs.versions.toml` dédié, contenant uniquement MediaPipe et les dépendances de test (Truth, JUnit).
2.  **Script de Publication** : Ajouter le plugin `maven-publish` dans le `build.gradle.kts` de la librairie.
3.  **Installation Locale** : Exécuter la tâche `./gradlew publishToMavenLocal`.
    *   Ceci créera un binaire dans votre dossier utilisateur : `.m2/repository/com/example/lib/imagecomparison/1.0.0/`.

---

## 🧪 Phase 4 : Validation de la Librairie (1h)
*Action : S'assurer que le binaire est fonctionnel avant de l'intégrer.*

1.  **Tests Instrumentés** : Exécuter `ImageEmbedderHelperTest` dans le nouveau projet autonome.
2.  **Vérification des Ressources** : Vérifier que les fichiers `.tflite` sont bien embarqués dans le fichier `.aar` généré.

---

## 🔗 Phase 5 : Intégration dans ParaBDCollector (45 min)
*Action : Remplacement du module local par la dépendance externe.*

1.  **Nettoyage** : Suppression du répertoire `:imagecomparison` dans ParaBDCollector.
2.  **Mise à jour `settings.gradle.kts`** : Retirer `include(":imagecomparison")`.
3.  **Consommation** :
    *   Ajouter `mavenLocal()` dans les dépôts de `dependencyResolutionManagement`.
    *   Dans `app/build.gradle.kts`, remplacer la dépendance projet par :
        `implementation("com.example.lib:imagecomparison:1.0.0")`
4.  **Test Final** : Lancer l'application ParaBDCollector et vérifier qu'une recherche par image fonctionne toujours.

---

## 💡 Avantages Stratégiques
*   **Versionnage** : Vous pourrez faire évoluer la librairie vers une version `1.1.0` sans impacter immédiatement l'application.
*   **Protection Protobuf** : Si MediaPipe nécessite une version spécifique de Protobuf, elle sera encapsulée dans le fichier `.aar`, limitant les conflits globaux.
*   **Indépendance Studio** : Vous pourrez ouvrir `ImageComparisonLib` avec une version plus ancienne d'Android Studio si une version future de l'IDE devient incompatible avec MediaPipe.

