package org.adhan

import org.adhan.internal.ShadowLength

/**
 * Madhab for determining how Asr is calculated.
 */
enum class Madhab {
    /**
     * Shafi Madhab
     */
    Shafi,

    /**
     * Hanafi Madhab
     */
    Hanafi;

    fun getShadowLength(): ShadowLength {
        return when (this) {
            Shafi -> ShadowLength.Single
            Hanafi -> ShadowLength.Double
        }
    }
}
