package com.charan.habitdiary.presentation.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.charan.habitdiary.R


object AppFonts {
    @OptIn(ExperimentalTextApi::class)
    val GoogleSans =
        FontFamily(
            Font(
                resId = R.font.google_sans_flex_variable,
                variationSettings = FontVariation.Settings(
                    FontVariation.width(100f),
                    FontVariation.weight(FontWeight.Normal.weight),

                        FontVariation.Setting("ROND", 100f)

                )
            )
        )

    @OptIn(ExperimentalTextApi::class)
    val GoogleSansWide =
        FontFamily(
            Font(
                resId = R.font.google_sans_flex_variable,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(FontWeight.Bold.weight),
                    FontVariation.width(125f),
                )
            )
        )
}
private val defaultTypography = Typography()

private fun TextStyle.applyGoogleSans() = copy(
    fontFamily = AppFonts.GoogleSans
)

private fun TextStyle.applyGoogleSansWide() = copy(
    fontFamily = AppFonts.GoogleSansWide
)

val DefaultTypography = Typography()


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val GoogleSansTypography = Typography(
    // Display
    displayLarge = defaultTypography.displayLarge.applyGoogleSansWide(),
    displayMedium = defaultTypography.displayMedium.applyGoogleSansWide(),
    displaySmall = defaultTypography.displaySmall.applyGoogleSansWide(),

    displayLargeEmphasized = defaultTypography.displayLargeEmphasized.applyGoogleSansWide(),
    displayMediumEmphasized = defaultTypography.displayMediumEmphasized.applyGoogleSansWide(),
    displaySmallEmphasized = defaultTypography.displaySmallEmphasized.applyGoogleSansWide(),

    // Headline
    headlineLarge = defaultTypography.headlineLarge.applyGoogleSansWide(),
    headlineMedium = defaultTypography.headlineMedium.applyGoogleSansWide(),
    headlineSmall = defaultTypography.headlineSmall.applyGoogleSansWide(),

    headlineLargeEmphasized = defaultTypography.headlineLargeEmphasized.applyGoogleSansWide(),
    headlineMediumEmphasized = defaultTypography.headlineMediumEmphasized.applyGoogleSansWide(),
    headlineSmallEmphasized = defaultTypography.headlineSmallEmphasized.applyGoogleSansWide(),

    // Title
    titleLarge = defaultTypography.titleLarge.applyGoogleSans(),
    titleMedium = defaultTypography.titleMedium.applyGoogleSans(),
    titleSmall = defaultTypography.titleSmall.applyGoogleSans(),

    titleLargeEmphasized = defaultTypography.titleLargeEmphasized.applyGoogleSans(),
    titleMediumEmphasized = defaultTypography.titleMediumEmphasized.applyGoogleSans(),
    titleSmallEmphasized = defaultTypography.titleSmallEmphasized.applyGoogleSans(),

    // Body
    bodyLarge = defaultTypography.bodyLarge.applyGoogleSans(),
    bodyMedium = defaultTypography.bodyMedium.applyGoogleSans(),
    bodySmall = defaultTypography.bodySmall.applyGoogleSans(),

    bodyLargeEmphasized = defaultTypography.bodyLargeEmphasized.applyGoogleSans(),
    bodyMediumEmphasized = defaultTypography.bodyMediumEmphasized.applyGoogleSans(),
    bodySmallEmphasized = defaultTypography.bodySmallEmphasized.applyGoogleSans(),

    // Label
    labelLarge = defaultTypography.labelLarge.applyGoogleSans(),
    labelMedium = defaultTypography.labelMedium.applyGoogleSans(),
    labelSmall = defaultTypography.labelSmall.applyGoogleSans(),

    labelLargeEmphasized = defaultTypography.labelLargeEmphasized.applyGoogleSans(),
    labelMediumEmphasized = defaultTypography.labelMediumEmphasized.applyGoogleSans(),
    labelSmallEmphasized = defaultTypography.labelSmallEmphasized.applyGoogleSans(),
)
