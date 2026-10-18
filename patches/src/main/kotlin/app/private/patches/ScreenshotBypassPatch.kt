package app.private.patches

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.private.patches.shared.Constants.COMPATIBILITY_BEREAL_PRIVATE

internal object ScreenshotFingerprint : Fingerprint(
    definingClass = "Lokhttp3/Request$Builder;",
    name = "url",
    returnType = "Lokhttp3/Request$Builder;",
    parameters = listOf("Ljava/lang/String;"),
)

@Suppress("unused")
val screenshotBypassPatch = bytecodePatch(
    name = "Hide screenshot notification",
    description = "Blocks the screenshot notification endpoint.",
    default = false,
) {
    compatibleWith(COMPATIBILITY_BEREAL_PRIVATE)
    execute {
        ScreenshotFingerprint.method.apply {
            addInstructions(0, """
                const-string v0, "content/screenshots"
                invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v0
                if-eqz v0, :no_block
                const-string p1, "https://127.0.0.1/screenshot-blocked"
                :no_block
                nop
            """.trimIndent())
        }
    }
}
