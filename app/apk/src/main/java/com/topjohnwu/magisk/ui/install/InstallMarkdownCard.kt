package com.topjohnwu.magisk.ui.install

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.DefaultMarkdownTypography

@Composable
fun InstallMarkdownCard(
    modifier: Modifier = Modifier,
    markdown: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(),
        shape = MaterialTheme.shapes.medium
    ) {
        Markdown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
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
}
