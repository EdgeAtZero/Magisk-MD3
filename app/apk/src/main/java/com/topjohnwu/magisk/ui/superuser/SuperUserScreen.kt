package com.topjohnwu.magisk.ui.superuser

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneExpansionAnchor
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.topjohnwu.magisk.ui.component.LabelItem
import com.topjohnwu.magisk.ui.util.dynamicBarBackgroundColor
import com.topjohnwu.magisk.ui.util.updateBarBackgroundColor
import kotlinx.coroutines.launch
import me.edgeatzero.compose.component.SearchBar
import me.edgeatzero.compose.util.copy
import me.edgeatzero.compose.util.top
import me.edgeatzero.compose.util.translucentTopAppBarColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SuperUserScreen(modifier: Modifier = Modifier) {
    val viewModel = viewModel<SuperUserViewModel>()
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val colors = TopAppBarDefaults.translucentTopAppBarColors()
    scrollBehavior.updateBarBackgroundColor(colors)
    val paneNavigator = rememberListDetailPaneScaffoldNavigator<String>()
    val paneExpansionAnchors = remember {
        listOf(
            PaneExpansionAnchor.Proportion(0f),
            PaneExpansionAnchor.Offset.fromStart(400.dp),
            PaneExpansionAnchor.Offset.fromEnd(400.dp),
            PaneExpansionAnchor.Proportion(1f)
        )
    }
    val paneExpansionState = rememberPaneExpansionState(
        keyProvider = paneNavigator.scaffoldValue,
        initialAnchoredIndex = 3,
        anchors = paneExpansionAnchors
    )
    val currentPkg = paneNavigator.currentDestination?.contentKey
    var isSearchBarExpanded by remember { mutableStateOf(false) }

    BackHandler(paneExpansionState.currentAnchor != paneExpansionAnchors.last()) {
        if (currentPkg == null) {
            coroutineScope.launch {
                paneExpansionState.animateTo(paneExpansionAnchors.last())
            }
        }
    }
    BackHandler(currentPkg != null) {
        coroutineScope.launch {
            paneNavigator.navigateBack()
        }
        if (paneExpansionState.currentAnchor == paneExpansionAnchors.first()) {
            coroutineScope.launch {
                paneExpansionState.animateTo(paneExpansionAnchors[1])
            }
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AnimatedContent(
                modifier = Modifier
                    .dynamicBarBackgroundColor()
                    .statusBarsPadding(),
                targetState = isSearchBarExpanded && currentPkg == null,
            ) {
                if (it) {
                    SearchBar(
                        value = viewModel.searchText,
                        onValueChange = { viewModel.searchText = it },
                        onRequestBack = { isSearchBarExpanded = false },
                        containerColor = Color.Transparent
                    )
                } else {
                    LargeTopAppBar(
                        title = {
                            Crossfade(currentPkg != null) {
                                Text(text = if (it) "应用信息" else "超级用户")
                            }
                        },
                        scrollBehavior = scrollBehavior,
                        colors = colors.copy(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent
                        ),
                        actions = {
                            AnimatedVisibility(currentPkg == null) {
                                IconButton(onClick = { isSearchBarExpanded = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { contentPadding ->
        NavigableListDetailPaneScaffold(
            navigator = paneNavigator,
            paneExpansionState = paneExpansionState,
            paneExpansionDragHandle = {
                val interactionSource = remember { MutableInteractionSource() }
                VerticalDragHandle(
                    modifier = Modifier.paneExpansionDraggable(
                        state = paneExpansionState,
                        minTouchTargetSize = LocalMinimumInteractiveComponentSize.current,
                        interactionSource = interactionSource,
                        semanticsProperties = {}
                    ),
                    interactionSource = interactionSource
                )
            },
            listPane = {
                AnimatedPane {
                    SuperUserListPane(contentPadding = contentPadding, viewModel = viewModel, currentPkg = currentPkg) {
                        coroutineScope.launch {
                            paneNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail, it)
                        }
                        if (paneExpansionState.currentAnchor == paneExpansionAnchors.last()) {
                            coroutineScope.launch {
                                paneExpansionState.animateTo(paneExpansionAnchors[1])
                            }
                        }
                    }
                }
            },
            detailPane = {
                AnimatedPane {
                    Crossfade(targetState = viewModel.apps.find { currentPkg == it.packageName }) {
                        if (it != null) {
                            SuperUserDetailPane(contentPadding = contentPadding, item = it)
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("选择一个程序以查看选项")
                            }
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SuperUserListPane(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    viewModel: SuperUserViewModel,
    currentPkg: String? = null,
    onNavigateToDetail: (String) -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        onRefresh = { viewModel.refreshApps() },
        isRefreshing = viewModel.isLoading,
        state = refreshState,
        indicator = {
            PullToRefreshDefaults.LoadingIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = contentPadding.top),
                isRefreshing = viewModel.isLoading,
                state = refreshState,
            )
        }
    ) {
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Adaptive(minSize = 300.dp),
            contentPadding = contentPadding.copy(top = 0.dp)
        ) {
            stickyHeader {
                Row(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = contentPadding.top + 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    fun filterAppsText(filterApps: SuperUserViewModel.FilterApps) = when (filterApps) {
                        SuperUserViewModel.FilterApps.ALL -> "全部"
                        SuperUserViewModel.FilterApps.SYSTEM -> "系统"
                        SuperUserViewModel.FilterApps.USER -> "用户"
                    }
                    ExposedDropdownMenuBox(
                        modifier = Modifier.wrapContentSize(Alignment.TopStart),
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        SplitButtonLayout(
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            leadingButton = {
                                SplitButtonDefaults.TonalLeadingButton(
                                    onClick = {}
                                ) {
                                    Icon(
                                        Icons.Filled.FilterList,
                                        modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
                                        contentDescription = null,
                                    )
                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(text = filterAppsText(viewModel.filterApps))
                                }
                            },
                            trailingButton = {
                                SplitButtonDefaults.TonalTrailingButton(
                                    checked = expanded,
                                    onCheckedChange = { expanded = it }
                                ) {
                                    val rotation: Float by
                                    animateFloatAsState(
                                        targetValue = if (expanded) 180f else 0f,
                                        label = "Trailing Icon Rotation",
                                    )
                                    Icon(
                                        Icons.Filled.KeyboardArrowDown,
                                        modifier =
                                            Modifier
                                                .size(SplitButtonDefaults.TrailingIconSize)
                                                .graphicsLayer {
                                                    this.rotationZ = rotation
                                                },
                                        contentDescription = null,
                                    )
                                }
                            }
                        )
                        ExposedDropdownMenu(
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            SuperUserViewModel.FilterApps.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(filterAppsText(option)) },
                                    onClick = {
                                        viewModel.filterApps = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            items(items = viewModel.apps, key = { it.packageName }) { item ->
                AppInfoCard(
                    modifier = Modifier.animateItem(),
                    viewModel = viewModel,
                    item = item,
                    isQuickSettingsEnable = true,
                    isShowLabel = true,
                    isSelected = currentPkg == item.packageName
                ) {
                    onNavigateToDetail(item.packageName)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuperUserDetailPane(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    item: AppInfo
) {
    Box(modifier = modifier.padding(contentPadding)) {
        AppInfoCard(
            item = item,
            isQuickSettingsEnable = false,
            isShowLabel = false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppInfoCard(
    modifier: Modifier = Modifier,
    item: AppInfo,
    viewModel: SuperUserViewModel? = null,
    isQuickSettingsEnable: Boolean = true,
    isSelected: Boolean = false,
    isShowLabel: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .let {
                if (onClick != null) it.clickable(onClick = onClick, role = Role.Button) else it
            }
            .background(
                if (isSelected) MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp) else Color.Unspecified
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
                .padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                modifier = Modifier.size(38.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.packageInfo)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                if (item.label == item.packageName) {
                    Text(
                        text = item.label,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    Text(
                        text = item.label,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = item.packageName,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                if (isShowLabel) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .animateContentSize(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (item.isSystemApp) {
                            LabelItem("SYSTEM")
                        }
                        if (item.isSharedUID) {
                            LabelItem("SharedUID")
                        }
                        AnimatedVisibility(item.isSuperUserActive) {
                            AnimatedContent(item.isSuperUser) {
                                LabelItem(if (it) "SU" else "SU REJECT")
                            }
                        }
                        AnimatedVisibility(item.isDeny) {
                            LabelItem("DENY")
                        }
                    }
                }
            }
            if (isQuickSettingsEnable && viewModel != null) {
                val coroutineScope = rememberCoroutineScope()
                val tooltipState = rememberTooltipState()
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                    state = tooltipState,
                    hasAction = false,
                    tooltip = { AppInfoCardTooltip(item, viewModel) }
                ) {
                    IconButton(onClick = { coroutineScope.launch { tooltipState.show(MutatePriority.PreventUserInput) } }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun TooltipScope.AppInfoCardTooltip(item: AppInfo, viewModel: SuperUserViewModel) {
    RichTooltip(
        caretShape = TooltipDefaults.caretShape()
    ) {
        Column(
            modifier = Modifier
                .width(200.dp)
        ) {
            @Composable
            fun Item(
                icon: ImageVector,
                text: String,
                checked: Boolean,
                onCheckedChange: (Boolean) -> Unit
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                Row(
                    modifier = Modifier.clickable(
                        onClick = { onCheckedChange(!checked) },
                        role = Role.Button,
                        interactionSource = interactionSource,
                        indication = null
                    ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.padding(top = 8.dp, end = 8.dp, bottom = 8.dp),
                        imageVector = icon,
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = text
                    )
                    Switch(
                        checked = checked,
                        onCheckedChange = null,
                        interactionSource = interactionSource
                    )
                }
            }
            AnimatedVisibility(item.isSuperUserActive) {
                Column {
                    Item(
                        icon = Icons.Filled.BugReport,
                        text = "日志",
                        checked = item.isLogging,
                        onCheckedChange = { viewModel.updateLogging(item, it) }
                    )
                    Item(
                        icon = Icons.Filled.Notifications,
                        text = "通知",
                        checked = item.isNotify,
                        onCheckedChange = { viewModel.updateNotify(item, it) }
                    )
                }
            }
            Item(
                icon = Icons.Filled.SearchOff,
                text = "排除列表",
                checked = item.isDeny,
                onCheckedChange = { viewModel.updateDeny(item, it) }
            )
            Item(
                icon = Icons.Filled.AdminPanelSettings,
                text = "超级用户",
                checked = item.isSuperUser,
                onCheckedChange = { viewModel.updateSuperUser(item, it) }
            )
            AnimatedVisibility(item.isSuperUserActive) {
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.deleteSuperUser(item) }
                ) {
                    Text(
                        text = "撤销超级用户",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
