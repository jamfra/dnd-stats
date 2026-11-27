package com.dndstats.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dndstats.android.ui.DndStatsApp
import com.dndstats.android.ui.theme.DndStatsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as DndStatsApplication).container
        setContent {
            DndStatsTheme {
                DndStatsApp(container)
            }
        }
    }
}
