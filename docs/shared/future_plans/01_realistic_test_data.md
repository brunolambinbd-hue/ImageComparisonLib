# Plan 1 : Réalisme des Données de Test (Instrumentés)

## Objectif
Passer d'une base de données de test minimaliste à un jeu de données "Production-Like" pour détecter les régressions liées à la volumétrie et aux relations complexes.

## Étapes Précises
1. **Conception du Schema de Données** : Définir un profil type de collectionneur (ex: 500 BDs, 50 comics, 20 objets divers) répartis sur 10 emplacements hiérarchiques.
2. **Générateur de Données (SQL/Kotlin)** : Créer un script capable d'insérer massivement des données cohérentes (dates, prix, métadonnées, embeddings de signatures factices) dans la base SQLite de test.
3. **Mise à jour du socle de test** : Modifier la règle `ActivityScenario` ou le `@Before` des tests instrumentés pour permettre de choisir entre la base "Minimal" et la base "Realistic".
4. **Validation des Performances** : Ajouter des assertions de temps de réponse sur les activités de liste et de recherche lorsqu'elles tournent sur la base réaliste.
