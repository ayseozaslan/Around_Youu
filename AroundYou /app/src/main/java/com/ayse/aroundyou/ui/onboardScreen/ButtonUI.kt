package com.ayse.aroundyou.ui.onboardScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayse.aroundyou.R

@Composable
fun ButtonUI(
    text:String ,
    backgroundColor: Color= colorResource(id = R.color.lightBlue),
    textColor: Color = colorResource(id = R.color.white),
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    fontSize : Int  = 14,
    onClick: () -> Unit

){

    Button(
        onClick = onClick,
        modifier = Modifier.padding(end = 16.dp), // 🔹 Sağ kenardan boşluk
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            style = textStyle
        )
    }

}

@Preview
@Composable
fun NextButton(){
    ButtonUI (text = stringResource(id=R.string.onboarding_next)){

    }

}

@Preview
@Composable
fun BackButton(){

    ButtonUI( text = stringResource(id=R.string.onboarding_back),
        backgroundColor = Color.Transparent,
        textColor = Color.Gray,
        textStyle = MaterialTheme.typography.bodySmall,
        fontSize = 13) {  }
}