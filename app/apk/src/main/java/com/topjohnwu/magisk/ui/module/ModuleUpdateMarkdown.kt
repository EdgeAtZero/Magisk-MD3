package com.topjohnwu.magisk.ui.module

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.DefaultMarkdownTypography

@Composable
fun ModuleUpdateMarkdown(
    modifier: Modifier = Modifier,
    markdown: String
) {
    Markdown(
        modifier = modifier,
        content = markdown,
        typography = (markdownTypography() as DefaultMarkdownTypography).let {
            it.copy(
                h1 = it.h6,
                h2 = it.h6,
                h3 = it.h6,
                h4 = it.h6,
                h5 = it.h6,
            )
        }
    )
}

