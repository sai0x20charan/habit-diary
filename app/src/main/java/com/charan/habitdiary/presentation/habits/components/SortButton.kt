package com.charan.habitdiary.presentation.habits.components

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.charan.habitdiary.data.model.enums.HabitSortType
import com.charan.habitdiary.presentation.common.mapper.toResId
import com.charan.habitdiary.presentation.common.mapper.fromResId
import com.charan.habitdiary.presentation.common.components.CustomDropDown

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SortButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onSortSelected: (HabitSortType) -> Unit,
    selectedSortTypeRes: Int,
    isExpanded: Boolean
) {

    TextButton(
        onClick = onClick,
        modifier = modifier,
        shapes = ButtonDefaults.shapes()
    ) {
        Text(stringResource(selectedSortTypeRes))

        CustomDropDown(
            items = HabitSortType.entries.map { it.toResId() },
            selectedItem = selectedSortTypeRes,
            onItemSelected = {
                onSortSelected(HabitSortType.fromResId(it as Int))
            },
            isExpanded = isExpanded,
            onDismiss = onClick
        )
    }
}
