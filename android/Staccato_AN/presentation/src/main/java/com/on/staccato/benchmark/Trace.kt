package com.on.staccato.benchmark

import android.os.Trace

inline fun <T> trace(
    sectionName: String,
    block: () -> T,
): T {
    Trace.beginSection(sectionName)
    try {
        return block()
    } finally {
        Trace.endSection()
    }
}
