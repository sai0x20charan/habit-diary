package com.charan.habitdiary.presentation.settings.components

import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.charan.habitdiary.presentation.common.components.CustomListItem
import com.charan.habitdiary.presentation.theme.IndexItem

@Composable
fun SettingsSwitchItem(
    title : String,
    index : IndexItem,
    isChecked : Boolean,
    onCheckedChange : (Boolean) -> Unit,
    leadingIcon : ImageVector
) {
    CustomListItem(
        indexItem = index,
        headLineContent = {
            Text(title)
        },
        trailingContent = {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )

        },
        leadingContent = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null
            )
        },

        onClick = {
            onCheckedChange(!isChecked)
        }
    )

}