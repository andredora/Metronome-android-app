package com.metronome.app

/**
 * Classic Italian tempo markings, looked up by BPM.
 */
object Tempo {

    private val ranges = listOf(
        0..24 to "Larghissimo",
        25..40 to "Grave",
        41..60 to "Largo",
        61..66 to "Larghetto",
        67..76 to "Adagio",
        77..108 to "Andante",
        109..120 to "Moderato",
        121..156 to "Allegro",
        157..176 to "Vivace",
        177..200 to "Presto",
        201..400 to "Prestissimo"
    )

    fun nameFor(bpm: Int): String {
        return ranges.firstOrNull { bpm in it.first }?.second ?: "Prestissimo"
    }
}
