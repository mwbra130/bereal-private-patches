package app.private.patches.shared

import app.morphe.patcher.patch.Compatibility
import app.morphe.patcher.patch.ApkFileType

object Constants {
    val COMPATIBILITY_BEREAL_PRIVATE = Compatibility(
        name = "BeReal",
        packageName = "com.bereal.ft",
        apkFileType = ApkFileType.XAPK,
    )
}
