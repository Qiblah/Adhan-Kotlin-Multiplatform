package org.adhan


/**
 * Madhab for determining how Asr is calculated.
 */
enum class Madhab {
    /**
     * Shafi Madhab
     */
    shafi,

    /**
     * Hanafi Madhab
     */
    hanafi;

    fun getShadowLength(): ShadowLength {
        return when (this) {
            shafi -> ShadowLength.SINGLE
            hanafi -> ShadowLength.DOUBLE
        }
    }
}
