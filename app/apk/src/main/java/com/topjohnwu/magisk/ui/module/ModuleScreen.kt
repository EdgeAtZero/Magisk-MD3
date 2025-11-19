package com.topjohnwu.magisk.ui.module

import android.net.Uri
import android.os.Parcel
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SimCardDownload
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.core.Info
import com.topjohnwu.magisk.core.base.ContentResultCallback
import com.topjohnwu.magisk.core.base.IActivityExtension
import com.topjohnwu.magisk.core.download.DownloadEngine
import com.topjohnwu.magisk.ui.MainActivity
import com.topjohnwu.magisk.ui.web.WebActivity
import me.edgeatzero.compose.component.Card
import me.edgeatzero.compose.component.SearchBar
import me.edgeatzero.compose.scaffold.Basic
import me.edgeatzero.compose.scaffold.Scaffolds
import me.edgeatzero.compose.scaffold.Switchable
import me.edgeatzero.compose.scaffold.TopBars
import me.edgeatzero.compose.theme.Elevation
import me.edgeatzero.compose.util.plus
import me.edgeatzero.compose.util.rememberToastAction
import me.edgeatzero.compose.util.top

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ModuleScreen(
    modifier: Modifier = Modifier,
    rootContentPadding: PaddingValues,
    viewModel: ModuleViewModel,
    onModuleAction: (String, String) -> Unit,
    onModuleInstall: (String) -> Unit
) {
    val context = LocalContext.current
    val pullToRefreshState = rememberPullToRefreshState()
    val noConnectionToastAction = rememberToastAction(message = "无法连接", duration = Toast.LENGTH_LONG)
    var isAdvancedMenuSheetVisible by rememberSaveable { mutableStateOf(false) }
    var isSearchBarVisible by rememberSaveable { mutableStateOf(false) }
    var isNeedSelectFile by rememberSaveable { mutableStateOf(false) }
    var isNeedUpdateDialog by rememberSaveable { mutableStateOf<ModuleInfo?>(null) }

    if (isAdvancedMenuSheetVisible) {
        ModuleAdvancedMenu(viewModel = viewModel) {
            isAdvancedMenuSheetVisible = false
        }
    }

    LaunchedEffect(isNeedSelectFile) {
        if (isNeedSelectFile) {
            (context as IActivityExtension).getContent(
                "*/*",
                object : ContentResultCallback {

                    override fun onActivityResult(result: Uri) {
                        onModuleInstall(result.toString())
                        viewModel.refresh()
                    }

                    override fun describeContents(): Int = 0

                    override fun writeToParcel(dest: Parcel, flags: Int) = Unit

                }
            )
            isNeedSelectFile = false
        }
    }

    isNeedUpdateDialog?.let {
        if (Info.isConnected.value == true && context is MainActivity) {
            ModuleUpdateDialog(
                item = it,
                onDismissRequest = { isNeedUpdateDialog = null },
                onDownload = { flash -> DownloadEngine.startWithActivity(context, it.toSubject(autoLaunch = flash)) }
            )
        } else {
            noConnectionToastAction()
            isNeedUpdateDialog = null
        }
    }

    Scaffolds.Basic(
        modifier = modifier,
        rootContentPadding = rootContentPadding,
        topBar = TopBars.Switchable(
            isSwitched = isSearchBarVisible,
            title = { Text(text = "模块") },
            actions = {
                IconButton(onClick = { isSearchBarVisible = true }) {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = null)
                }
                IconButton(
                    onClick = { isAdvancedMenuSheetVisible = true },
                    content = { Icon(imageVector = Icons.Filled.FilterList, contentDescription = null) }
                )
            },
            switchedBar = {
                SearchBar(
                    value = viewModel.searchText,
                    onValueChange = { viewModel.searchText = it },
                    onRequestBack = { isSearchBarVisible = false },
                    containerColor = Color.Transparent
                )
            }
        )
    ) { contentPadding ->
        Crossfade(modifier = Modifier.fillMaxSize(), targetState = viewModel.isLoading) { targetState ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (targetState) {
                    CircularWavyProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    PullToRefreshBox(
                        modifier = Modifier.fillMaxSize(),
                        onRefresh = { viewModel.refresh() },
                        isRefreshing = viewModel.isRefreshing,
                        state = pullToRefreshState,
                        indicator = {
                            PullToRefreshDefaults.LoadingIndicator(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = contentPadding.top),
                                state = pullToRefreshState,
                                elevation = Elevation.Level3,
                                isRefreshing = viewModel.isRefreshing
                            )
                        }
                    ) {
                        LazyVerticalStaggeredGrid(
                            modifier = Modifier.fillMaxSize(),
                            columns = StaggeredGridCells.Adaptive(300.dp),
                            contentPadding = contentPadding + 16.dp,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalItemSpacing = 16.dp
                        ) {
                            if (!viewModel.isRefreshing) item {
                                Card(
                                    modifier = Modifier.animateItem(),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    onClick = { isNeedSelectFile = true }
                                ) {
                                    Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                                        Row(
                                            modifier = Modifier.align(Alignment.Center).padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(imageVector = Icons.Filled.SimCardDownload, contentDescription = null)
                                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                            Text(text = "从本地安装")
                                        }
                                    }
                                }
                            }
                            items(viewModel.modules, key = { it.id }) { item ->
                                ModuleInfoCard(
                                    modifier = Modifier.animateItem(),
                                    item = item,
                                    isShowId = viewModel.sort == ModuleSort.ID,
                                    onAction = { onModuleAction(item.id, item.name) },
                                    onUpdate = { p0, p1 -> viewModel.updateModule(item, p0, p1) },
                                    onUpdateRequest = { isNeedUpdateDialog = item },
                                    onWeb = { WebActivity.launch(context, item) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
