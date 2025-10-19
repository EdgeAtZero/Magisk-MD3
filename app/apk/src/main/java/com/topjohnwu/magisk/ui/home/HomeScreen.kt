package com.topjohnwu.magisk.ui.home

import android.os.Build
import android.system.Os
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.core.BuildConfig
import com.topjohnwu.magisk.core.Info
import com.topjohnwu.magisk.ui.icon.Linux
import com.topjohnwu.magisk.ui.icon.Magisk
import com.topjohnwu.magisk.ui.util.*
import me.edgeatzero.compose.component.Column
import me.edgeatzero.compose.component.InsetsPaddingScaffold
import me.edgeatzero.compose.util.plus
import me.edgeatzero.compose.util.translucentTopAppBarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    drawPadding: PaddingValues = PaddingValues()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val colors = TopAppBarDefaults.translucentTopAppBarColors()
    scrollBehavior.updateBarBackgroundColor(colors)

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                modifier = Modifier
                    .dynamicBarBackgroundColor()
                    .statusBarsPadding(),
                title = { Text("Magisk") },
                scrollBehavior = scrollBehavior,
                colors = colors.copy(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            contentPadding = contentPadding + 16.dp,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusCard()
            InfoCard()
        }
    }
}

@Composable
private fun StatusCard(modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (Info.env.isActive) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                Info.env.isActive -> {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null
                    )
                    Column(Modifier.padding(start = 20.dp)) {
                        Text(text = "工作中")
                        Text(text = "版本: ${Info.env.versionString} (${Info.env.versionCode})")
                    }
                }

                else -> {
                    Icon(
                        imageVector = Icons.Filled.Cancel,
                        contentDescription = null
                    )
                    Column(Modifier.padding(start = 20.dp)) {
                        Text(text = "未安装 Magisk")
                        Text(text = "请点击安装以继续")
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(modifier: Modifier = Modifier, autoExpand: Boolean = false) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(autoExpand) {
        if (autoExpand) {
            expanded = true
        }
    }
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 24.dp)
        ) {
            InfoCardItem(
                label = "管理器版本",
                icon = Icons.Filled.Magisk,
                content = "${BuildConfig.APP_VERSION_NAME} (${BuildConfig.APP_VERSION_CODE})" +
                        if (BuildConfig.DEBUG) " (D)" else ""
            )
            val zygiskImplementation = getZygiskImplementation()
            if (zygiskImplementation.isNotBlank()) {
                Spacer(Modifier.height(16.dp))
                InfoCardItem(
                    label = "Zygisk 状态",
                    content = "已启用 | $zygiskImplementation | ${getZygiskVersion()}",
                    icon = Icons.Filled.Vaccines
                )
            }
            if (!expanded) {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Show more"
                        )
                    }
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    val uname = Os.uname()
                    Spacer(Modifier.height(16.dp))
                    InfoCardItem(
                        label = "内核版本",
                        content = "${uname.release} (${uname.machine})",
                        icon = Icons.Filled.Linux,
                    )

                    Spacer(Modifier.height(16.dp))
                    InfoCardItem(
                        label = "Android 版本",
                        content = "${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})",
                        icon = Icons.Filled.Android,
                    )

                    Spacer(Modifier.height(16.dp))
                    InfoCardItem(
                        label = "ABI 类型",
                        content = Build.SUPPORTED_ABIS.joinToString(", "),
                        icon = Icons.Filled.Memory,
                    )

                    Spacer(Modifier.height(16.dp))
                    InfoCardItem(
                        label = "SELinux状态",
                        content = getSELinuxStatus(),
                        icon = Icons.Filled.Security,
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCardItem(label: String, content: String, icon: Any? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
            when (icon) {
                is ImageVector -> Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 20.dp)
                )

                is Painter -> Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 20.dp)
                )
            }
        }
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
