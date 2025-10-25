package com.topjohnwu.magisk.ui.install

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.core.Info
import com.topjohnwu.magisk.util.CommonActivityResultProvider
import me.edgeatzero.compose.component.Column
import me.edgeatzero.compose.scaffold.Basic
import me.edgeatzero.compose.scaffold.Scaffolds
import me.edgeatzero.compose.scaffold.TopBars
import me.edgeatzero.compose.util.onBackPressed
import me.edgeatzero.compose.util.rememberToastAction

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InstallScreen(
    modifier: Modifier = Modifier,
    rootContentPadding: PaddingValues,
    viewModel: InstallViewModel,
    onNavigateToFlash: (InstallMethod, List<InstallOption>, String?) -> Unit
) {
    val scrollState = rememberScrollState()
    val selectFileToast = rememberToastAction(
        message = "选择一个原始映像文件（*.img）、Odin 包（*.tar）或 payload.bin（*.bin）",
        duration = Toast.LENGTH_LONG
    )
    val isFabExpanded by remember { derivedStateOf { scrollState.value == 0 } }

    when (viewModel.method) {
        InstallMethod.Patch -> {
            val activity = LocalContext.current as CommonActivityResultProvider
            LaunchedEffect(activity) {
                activity.GetContentHandler.launch("*/*") {
                    viewModel.patchFile = it.toString()
                }
                selectFileToast()
            }
        }

        InstallMethod.InactiveSlot -> {
            var isConfirmed by rememberSaveable { mutableStateOf(false) }
            if (!isConfirmed) {
                InactiveSlotWarningDialog { isConfirmed = true }
            }
        }

        else -> Unit
    }

    Scaffolds.Basic(
        modifier = modifier,
        rootContentPadding = rootContentPadding,
        topBar = TopBars.Basic(
            title = { Text(text = "安装") },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        ),
        floatingActionButton = {
            AnimatedVisibility(
                visible = when (viewModel.method) {
                    InstallMethod.Patch -> viewModel.patchFile != null
                    null -> false
                    else -> true
                },
                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
            ) {
                ExtendedFloatingActionButton(
                    onClick = onClick@{
                        onNavigateToFlash(viewModel.method ?: return@onClick, viewModel.options, viewModel.patchFile)
                    },
                    expanded = isFabExpanded,
                    icon = { Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null) },
                    text = { Text(text = "开始") },
                )
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(all = 16.dp),
            contentPadding = contentPadding
        ) {
            if (!Info.isEmulator && (!Info.isSAR || Info.isFDE || !Info.ramdisk)) {
                InstallOptionsCard(options = viewModel.options)
                Spacer(modifier = Modifier.size(8.dp))
            }
            InstallMethodCard(method = viewModel.method, onMethodChanged = viewModel::method::set)
            AnimatedContent(targetState = viewModel.markdown) { targetState ->
                targetState?.let {
                    Spacer(modifier = Modifier.size(8.dp))
                    InstallMarkdownCard(markdown = it)
                }
            }
        }
    }
}
