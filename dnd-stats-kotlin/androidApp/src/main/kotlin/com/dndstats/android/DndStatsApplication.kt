package com.dndstats.android

import android.app.Application

class DndStatsApplication : Application() {
    val container: AppContainer by lazy { AppContainer() }
}
