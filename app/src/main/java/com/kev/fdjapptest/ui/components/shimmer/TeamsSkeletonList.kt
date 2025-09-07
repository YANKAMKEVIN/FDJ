package com.kev.fdjapptest.ui.components.shimmer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TeamsSkeletonList(
    listState: LazyListState,
    count: Int = 8
) {
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(count) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar placeholder
                ShimmerBox(
                    modifier = Modifier
                        .size(40.dp),
                    isCircle = true
                )

                // Text lines
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShimmerBox(modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .height(16.dp))
                    ShimmerBox(modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .height(12.dp))
                }
            }
        }
    }
}
