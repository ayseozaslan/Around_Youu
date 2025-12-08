package com.ayse.aroundyou.ui.onboardScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ayse.aroundyou.R

@Composable
fun IndicatorUI(pageSize: Int,
                currentPage: Int,
                selectedColor: Color = colorResource(id = R.color.lightBlue),
                unselectedColor: Color = colorResource(id = R.color.darkBlue))
                //selectedColor:Color =  MaterialTheme.colorScheme.secondary,
                //unselectedColor: Color = MaterialTheme.colorScheme.secondaryContainer)
                        {

    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        repeat(pageSize) {
            Box(modifier= Modifier
                .height(16.dp)
                .width(width = if(it == currentPage) 24.dp else 16.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = if(it == currentPage) selectedColor else unselectedColor))

        }

    }
    Spacer(modifier= Modifier.size(2.5.dp))
}

@Preview(showBackground = true)
@Composable
fun IndicatorUIPreview1(){
    IndicatorUI(pageSize = 4, currentPage = 0)
}

@Preview(showBackground = true)
@Composable
fun IndicatorUIPreview2(){
    IndicatorUI(pageSize = 3, currentPage = 1)
}

@Preview(showBackground = true)
@Composable
fun IndicatorUIPreview3(){
    IndicatorUI(pageSize = 2, currentPage = 2)
}

@Preview(showBackground = true)
@Composable
fun IndicatorUIPreview4(){
    IndicatorUI(pageSize = 1, currentPage = 3)
}