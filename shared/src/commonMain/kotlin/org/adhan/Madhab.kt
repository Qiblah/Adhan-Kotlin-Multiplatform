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

    /**
     * Gets the shadow length for calculating Asr.
     * @return the shadow length
     */
    fun getShadowLength(): ShadowLength {
        return when (this) {
            Shafi -> ShadowLength.Single
            Hanafi -> ShadowLength.Double
        }
    }
}
