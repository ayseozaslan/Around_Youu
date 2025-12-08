package com.ayse.aroundyou.ui.onboardScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun  OnboardingGraphUI(onboardingModel: OnboardingModel) {

    Column (modifier= Modifier.fillMaxWidth()){
        Spacer(
            modifier = Modifier.size(90.dp)
        )

        Image(
            painter= painterResource(id = onboardingModel.image),
            contentDescription = null,
            modifier = Modifier
            .fillMaxWidth()
                .padding(20.dp, 0.dp),
            alignment = Alignment.Center


        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(130.dp)

        )

        Text(
            text = stringResource(id = onboardingModel.title),
            modifier = Modifier
                .fillMaxWidth(),
            fontSize = 20.sp,
            lineHeight = 28.sp, // Satır arası boşluk
            softWrap = true,    // 🔹 Uzun satırları otomatik alt satıra geçirir
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black,
            style = MaterialTheme.typography.bodySmall,
          //  color = MaterialTheme.colorScheme.onBackground

        )


        Spacer(
            modifier = Modifier
            .fillMaxWidth()
            .size(20.dp)

        )
        Text(
            text = stringResource(id = onboardingModel.description),
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp, 0.dp),
            fontSize = 15.sp,
            lineHeight = 28.sp, // Satır arası boşluk
            softWrap = true,    // 🔹 Uzun satırları otomatik alt satıra geçirir
            textAlign = TextAlign.Center,
            color = Color.Black,
            style = MaterialTheme.typography.bodySmall,
            //color = MaterialTheme.colorScheme.onSurface
        )

    }
   }



@Preview(showBackground = true)
@Composable
fun OnboardingGraphUIPreview1(){
    OnboardingGraphUI(OnboardingModel.FirstPage)
}

@Preview(showBackground = true)
@Composable
fun OnboardingGraphUIPreview2(){
    OnboardingGraphUI(OnboardingModel.SecondPage)
}

@Preview(showBackground = true)
@Composable
fun OnboardingGraphUIPreview3(){
    OnboardingGraphUI(OnboardingModel.ThirdPage)
}

@Preview(showBackground = true)
@Composable
fun OnboardingGraphUIPreview4(){
    OnboardingGraphUI(OnboardingModel.FourPage)
}