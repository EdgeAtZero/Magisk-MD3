package com.topjohnwu.magisk.ui.superuser

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperUserDetail(
    modifier: Modifier = Modifier.fillMaxSize(),
    contentPadding: PaddingValues,
    item: AppInfo
) {
    Box(modifier = modifier.padding(contentPadding)) {
        AppInfoCard(
            item = item,
            isQuickSettingsEnable = false,
            isShowLabel = false,
            subtext = AppInfo::packageName
        )
    }
}
