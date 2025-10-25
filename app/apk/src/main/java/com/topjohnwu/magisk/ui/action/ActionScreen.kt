package com.topjohnwu.magisk.ui.action

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.edgeatzero.compose.scaffold.Basic
import me.edgeatzero.compose.scaffold.Decorable
import me.edgeatzero.compose.scaffold.Scaffolds
import me.edgeatzero.compose.scaffold.TopBars
import me.edgeatzero.compose.util.dynamicBarColor
import me.edgeatzero.compose.util.onBackPressed
import me.edgeatzero.compose.util.rememberToastAction

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ActionScreen(
    modifier: Modifier = Modifier,
    rootContentPadding: PaddingValues,
    viewModel: ActionViewModel
) {
    val actionDoneToast = rememberToastAction(
        message = "%s 操作运行完成",
        duration = Toast.LENGTH_LONG
    )

    BackHandler(viewModel.isExecuting) {}

    LaunchedEffect(viewModel) { viewModel.isConnected.value = true }

    LaunchedEffect(viewModel.isSuccess) {
        if (viewModel.isSuccess == true) {
            actionDoneToast(viewModel.name)
        }
    }

    Scaffolds.Basic(
        modifier = modifier,
        rootContentPadding = rootContentPadding,
        topBar = TopBars.Decorable(
            title = { Text(text = "执行 - ${viewModel.name}") },
            navigationIcon = {
                AnimatedVisibility(
                    visible = !viewModel.isExecuting,
                    enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
                    exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End)
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            }
        ) { topbar ->
            Column {
                topbar()
                AnimatedVisibility(
                    visible = viewModel.isExecuting,
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
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp),
            contentPadding = contentPadding
        ) {
            items(viewModel.log.size) {
                Text(text = viewModel.log[it], style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
