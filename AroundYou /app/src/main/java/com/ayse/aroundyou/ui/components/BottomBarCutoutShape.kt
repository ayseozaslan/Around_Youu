package com.ayse.aroundyou.ui.components

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class BottomBarCutoutShape(
    private val fabRadius: Float,
    private val cutoutRadius: Float,
    private val cutoutVerticalOffset: Float = 30f
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val path = Path().apply {
            val centerX = size.width / 2

            // Oyuk merkezini biraz aşağı kaydırıyoruz
            val centerY = -cutoutVerticalOffset

            // Üst düz çizgi
            moveTo(0f, 0f)

            // Oyuk (arc)
            arcTo(
                Rect(
                    centerX - cutoutRadius,
                    centerY - cutoutRadius,
                    centerX + cutoutRadius,
                    centerY + cutoutRadius
                ),
                180f,
                -180f,
                false
            )

            // Sağ taraf
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)

            close()
        }

        return Outline.Generic(path)
    }
}
