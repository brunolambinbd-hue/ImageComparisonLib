import java.io.File
import java.net.URL

/**
 * Tâche Gradle pour synchroniser les règles Cursor (.cursorrules) depuis le dépôt central GitHub.
 * S'exécute automatiquement lors de la synchronisation Gradle ou peut être lancée manuellement.
 */
tasks.register("syncCursorRules") {
    group = "Verification"
    description = "Synchronise le fichier .cursorrules avec la version de référence sur GitHub."

    doLast {
        val remoteUrl = "https://raw.githubusercontent.com/brunolambinbd-hue/ProjectStandards/main/.cursorrules"
        val localFile = File(projectDir, ".cursorrules")

        println("📡 Vérification des standards depuis : $remoteUrl")

        try {
            val remoteContent = URL(remoteUrl).readText()

            if (!localFile.exists() || localFile.readText() != remoteContent) {
                localFile.writeText(remoteContent)
                println("✅ Fichier .cursorrules mis à jour avec succès.")
            } else {
                println("👍 Le fichier .cursorrules est déjà à jour.")
            }
        } catch (e: Exception) {
            println("⚠️ Impossible de synchroniser .cursorrules (vérifiez votre connexion ou l'URL) : ${e.message}")
        }
    }
}
