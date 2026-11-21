package com.kev.fdjapptest.ui.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.kev.domain.model.Team
import com.kev.domain.model.TeamLink
import com.kev.domain.util.DomainError
import com.kev.fdjapptest.R
import com.kev.fdjapptest.ui.components.ErrorScreen
import java.util.Locale

/** Detail page of [team]: stadium, year of creation, description and web links. */
@Composable
fun TeamDetailScreen(
    team: Team,
    onBack: () -> Unit,
    viewModel: TeamDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(team.id) { viewModel.open(team) }
    val state by viewModel.state.collectAsState()
    // Until the view model has taken the new team, show it as loading rather than the previous one.
    val shown = if (state.team?.id == team.id) state else TeamDetailUiState(team = team, isLoading = true)
    TeamDetailContent(state = shown, onBack = onBack, onRetry = viewModel::retry)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailContent(
    state: TeamDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    language: String = Locale.getDefault().language
) {
    BackHandler(onBack = onBack)
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.team_detail_back)
                        )
                    }
                },
                title = { Text(state.name, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val error = state.error
            when {
                state.isLoading -> Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TeamDetailHeader(state)
                    Spacer(Modifier.height(32.dp))
                    CircularProgressIndicator()
                }

                error != null -> ErrorScreen(
                    message = stringResource(
                        when (error) {
                            DomainError.Network -> R.string.team_detail_error_network
                            DomainError.NotFound -> R.string.team_detail_error_not_found
                            else -> R.string.team_detail_error
                        }
                    ),
                    onRetry = onRetry
                )

                else -> TeamDetailBody(state, language)
            }
        }
    }
}

@Composable
private fun TeamDetailHeader(state: TeamDetailUiState) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SubcomposeAsyncImage(
            model = state.badgeUrl,
            contentDescription = stringResource(R.string.team_detail_badge, state.name),
            modifier = Modifier.size(120.dp),
            error = {
                Icon(Icons.Default.SportsSoccer, contentDescription = null, modifier = Modifier.size(56.dp))
            }
        )
        Spacer(Modifier.height(12.dp))
        Text(
            state.name,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading() }
        )
    }
}

@Composable
private fun TeamDetailBody(state: TeamDetailUiState, language: String) {
    val details = state.details ?: return
    val uriHandler = LocalUriHandler.current
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TeamDetailHeader(state)

        if (state.hasNoInformation) {
            Text(
                stringResource(R.string.team_detail_no_information),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        details.stadium?.let { stadium ->
            InfoRow(
                icon = Icons.Default.Stadium,
                label = stringResource(R.string.team_detail_stadium),
                value = listOfNotNull(stadium, details.location).joinToString(" · ")
            )
        }
        details.formedYear?.let { year ->
            InfoRow(
                icon = Icons.Default.CalendarMonth,
                label = stringResource(R.string.team_detail_formed),
                value = year.toString()
            )
        }
        details.descriptionFor(language)?.let { description ->
            Column {
                SectionTitle(stringResource(R.string.team_detail_about))
                Spacer(Modifier.height(4.dp))
                Text(description, style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (details.links.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionTitle(stringResource(R.string.team_detail_links))
                details.links.forEach { link ->
                    OutlinedButton(
                        // Opened in the browser (or the matching app); ignored when nothing can open it.
                        onClick = { runCatching { uriHandler.openUri(link.url) } },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(link.kind.labelRes()), modifier = Modifier.weight(1f))
                        Icon(
                            Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.semantics { heading() }
    )
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

private fun TeamLink.Kind.labelRes(): Int = when (this) {
    TeamLink.Kind.Website -> R.string.team_detail_link_website
    TeamLink.Kind.Facebook -> R.string.team_detail_link_facebook
    TeamLink.Kind.Twitter -> R.string.team_detail_link_twitter
    TeamLink.Kind.Instagram -> R.string.team_detail_link_instagram
    TeamLink.Kind.Youtube -> R.string.team_detail_link_youtube
}
