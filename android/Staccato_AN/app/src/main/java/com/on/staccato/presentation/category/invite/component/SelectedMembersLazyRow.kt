package com.on.staccato.presentation.category.invite.component

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.dummyMembers

@Composable
fun SelectedMembersLazyRow(
    members: List<Member>,
    onDeselect: (Member) -> Unit,
) {
    val listState = rememberLazyListState()
    val prevSize = remember { mutableIntStateOf(members.size) }

    LaunchedEffect(members.size) {
        if (members.size > prevSize.intValue) {
            listState.animateScrollToItem(0)
        }
        prevSize.intValue = members.size
    }

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 10.dp),
        modifier =
            Modifier
                .fillMaxWidth(),
    ) {
        items(
            items = members,
            key = { item -> item.memberId },
        ) { item ->
            SelectedMemberItem(
                item = item,
                onDeselect = onDeselect,
                modifier =
                    Modifier
                        .animateItem(
                            fadeOutSpec = tween(durationMillis = 100),
                        )
                        .padding(bottom = 16.dp),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SelectedMembersLazyRowPreview() {
    SelectedMembersLazyRow(
        dummyMembers.members,
    ) {}
}
