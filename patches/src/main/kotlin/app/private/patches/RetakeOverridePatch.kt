package app.private.patches

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.private.patches.shared.Constants.COMPATIBILITY_BEREAL_PRIVATE

internal object EscToStringFingerprint : Fingerprint(
    returnType = "Ljava/lang/String;",
    strings = listOf("MomentPost("),
)

@Suppress("unused")
val retakeOverridePatch = bytecodePatch(
    name = "Set retake count",
    description = "Reads /sdcard/bereal_retakes.txt and substitutes its value for the retake count.",
    default = false,
) {
    compatibleWith(COMPATIBILITY_BEREAL_PRIVATE)
    execute {
        val classDef = EscToStringFingerprint.classDef
        val constructors = classDef.methods.filter {
            it.name == "<init>" && it.parameterTypes.size == 17
        }
        if (constructors.isEmpty()) {
            throw Exception("Set retake count: no 17-param constructor found in ${classDef.type}")
        }
        // Patch all matching constructors to handle the dual-constructor case
        constructors.forEach { method ->
            method.apply {
                addInstructions(0, """
                const-string v0, "/sdcard/bereal_retakes.txt"
                new-instance v1, Ljava/io/File;
                invoke-direct {v1, v0}, Ljava/io/File;-><init>(Ljava/lang/String;)V
                invoke-virtual {v1}, Ljava/io/File;->exists()Z
                move-result v0
                if-eqz v0, :no_override
                new-instance v0, Ljava/io/BufferedReader;
                new-instance v1, Ljava/io/FileReader;
                const-string v2, "/sdcard/bereal_retakes.txt"
                invoke-direct {v1, v2}, Ljava/io/FileReader;-><init>(Ljava/lang/String;)V
                invoke-direct {v0, v1}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V
                invoke-virtual {v0}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;
                move-result-object v0
                if-eqz v0, :no_override
                invoke-virtual {v0}, Ljava/lang/String;->trim()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I
                move-result v0
                move p19, v0
                :no_override
                nop
                """.trimIndent())
            }
        }
    }
}
