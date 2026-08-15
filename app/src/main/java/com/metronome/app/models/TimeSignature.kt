package com.metronome.app.models

data class TimeSignature(val numerator: Int, val denominator: Int)

val timeSignatures = listOf(
    TimeSignature(2, 4),
    TimeSignature(3, 4),
    TimeSignature(4, 4),
    TimeSignature(5, 4),
    TimeSignature(6, 8),
    TimeSignature(7, 8)
)
