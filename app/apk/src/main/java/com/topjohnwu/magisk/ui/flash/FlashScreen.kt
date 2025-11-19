package com.topjohnwu.magisk.ui.flash

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.core.ktx.reboot
import com.topjohnwu.magisk.ui.MainActivity
import me.edgeatzero.compose.component.AutomateBottomLazyColumn
import me.edgeatzero.compose.scaffold.Basic
import me.edgeatzero.compose.scaffold.Decorable
import me.edgeatzero.compose.scaffold.NavigationIconButton
import me.edgeatzero.compose.scaffold.Scaffolds
import me.edgeatzero.compose.scaffold.TopBars
import me.edgeatzero.compose.util.dynamicBarColor
import me.edgeatzero.compose.util.onBackPressed

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FlashScreen(
    modifier: Modifier = Modifier,
    rootContentPadding: PaddingValues,
    viewModel: FlashViewModel
) {
    val scrollState = rememberLazyListState()
    val isFabExpanded by remember { derivedStateOf { scrollState.firstVisibleItemIndex == 0 && scrollState.firstVisibleItemScrollOffset == 0 } }

    BackHandler(viewModel.isFlashing) {}

    LaunchedEffect(viewModel) { viewModel.isConnected.value = true }

    DisposableEffect(Unit) {
        MainActivity.keyEventDispatcher = {
            when (it.keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN -> true
                else -> false
            }
        }
        onDispose {
            MainActivity.keyEventDispatcher = null
        }
    }

    Scaffolds.Basic(
        modifier = modifier,
        rootContentPadding = rootContentPadding,
        topBar = TopBars.Decorable(
            title = { Text(text = "刷写") },
            navigationIcon = {
                AnimatedVisibility(
                    visible = !viewModel.isFlashing,
                    enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
                    exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End)
                ) {
                    Scaffolds.NavigationIconButton()
                }
            }
        ) { topbar ->
            Column {
                topbar()
                AnimatedVisibility(
                    visible = viewModel.isFlashing,
                    enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
                    exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .dynamicBarColor()
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = viewModel.isRebootAvailable,
                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
            ) {
                ExtendedFloatingActionButton(
                    onClick = { reboot() },
                    expanded = isFabExpanded,
                    icon = { Icon(imageVector = Icons.Filled.RestartAlt, contentDescription = null) },
                    text = { Text(text = "重启") },
                )
            }
        }
    ) { contentPadding ->
        AutomateBottomLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp),
            contentPadding = contentPadding,
            count = viewModel.log.size
        ) {
            items(viewModel.log.size) {
                Text(
                    modifier = Modifier.animateItem(),
                    text = viewModel.log[it],
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
