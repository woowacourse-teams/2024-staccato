package com.on.staccato.benchmark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.on.staccato.presentation.timeline.component.Categories
import com.on.staccato.presentation.timeline.model.CategoryUiModel
import com.on.staccato.presentation.timeline.model.dummyCategoryUiModels

class CategoriesBenchmarkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FakeCategoryScreen(
                dummyCategoryUiModels,
                onCategoryClick = {},
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun FakeCategoryScreen(
    categories: List<CategoryUiModel>,
    onCategoryClick: (Long) -> Unit,
) {
    Categories(
        modifier =
            Modifier.semantics {
                testTagsAsResourceId = true
            },
        categories = categories,
        onCategoryClicked = onCategoryClick,
    ) {
    }
}
