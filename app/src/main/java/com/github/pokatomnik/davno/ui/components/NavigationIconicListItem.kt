package com.github.pokatomnik.davno.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconicListNavItem(
    title: String,
    icon: ImageVector,
    iconContentDescription: String = "",
    description: String? = null,
    onLongPress: (() -> Unit)? = null,
    onPress: (() -> Unit)? = null,
) {
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SMALL_LIST_ITEM_HEIGHT.dp)
            .combinedClickable(
                indication = rememberRipple(bounded = true),
                interactionSource = remember { MutableInteractionSource() },
                onClick = { onPress?.invoke() },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongPress?.invoke()
                }
            )

            .padding(horizontal = LARGE_PADDING.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.requiredHeight(SMALL_LIST_ITEM_HEIGHT.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconContentDescription
            )
        }
        Spacer(modifier = Modifier.fillMaxHeight().width(SMALL_PADDING.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.h6,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.alpha(0.8f)
            )
            if (description != null) {
                Text(
                    text = description,
                    modifier = Modifier.alpha(ALPHA_GHOST),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body2,
                )
            }
        }
        Column(
            modifier = Modifier
                .requiredHeight(SMALL_LIST_ITEM_HEIGHT.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Открыть $title"
            )
        }
    }
}