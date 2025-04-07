package dev.whysoezzy.bduiproj.ui.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier

fun Modifier.padding(paddingValues: PaddingValues): Modifier {
    return this.padding(
        start = paddingValues.calculateStartPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
        top = paddingValues.calculateTopPadding(),
        end = paddingValues.calculateEndPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
        bottom = paddingValues.calculateBottomPadding()
    )
}