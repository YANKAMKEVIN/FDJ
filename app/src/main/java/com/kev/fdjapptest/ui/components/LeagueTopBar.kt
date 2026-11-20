package com.kev.fdjapptest.ui.components

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kev.fdjapptest.R
import kotlinx.coroutines.delay

private const val MINUTE_MS = 60_000L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueTopBar(
    lastUpdatedMillis: Long?,
    title: String = stringResource(R.string.top_bar_title),
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    // Re-evaluated every minute so "Updated 2 min. ago" keeps up with time.
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(lastUpdatedMillis) {
        while (true) {
            now = System.currentTimeMillis()
            delay(MINUTE_MS)
        }
    }
    val justNow = stringResource(R.string.top_bar_updated_just_now)
    val template = stringResource(R.string.top_bar_updated)

    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        navigationIcon = { navigationIcon?.invoke() },
        title = {
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge)
                lastUpdatedMillis?.let { ts ->
                    Text(
                        text = lastUpdatedLabel(ts, now, justNow, template) { time, current ->
                            DateUtils.getRelativeTimeSpanString(
                                time, current, DateUtils.MINUTE_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_RELATIVE
                            )
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = actions
    )
}

/**
 * "Updated just now" for less than a minute (or a clock in the past), otherwise [template]
 * filled with the relative time ("2 min. ago") given by [relative].
 */
internal fun lastUpdatedLabel(
    updatedAt: Long,
    now: Long,
    justNow: String,
    template: String,
    relative: (Long, Long) -> CharSequence
): String =
    if (now - updatedAt < MINUTE_MS) justNow
    else template.format(relative(updatedAt, now))
