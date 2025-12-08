package com.ayse.aroundyou.ui.screen


import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ayse.aroundyou.R
import com.ayse.aroundyou.viewmodel.GoogleLoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import com.google.android.gms.auth.api.signin.GoogleSignIn

@Composable
fun LoginScreen(
    navController: NavController,
    googleLoginViewModel : GoogleLoginViewModel,
    googleSignInLauncher: ActivityResultLauncher<Intent>

) {
    val context = LocalContext.current
    val token = stringResource(id = R.string.default_web_client_id)

    // 🔹 Google istemcisi
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(token)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val isLoading by googleLoginViewModel.isLoading
    val loginSuccess by googleLoginViewModel.loginSuccess
    val errorMessage by googleLoginViewModel.errorMessage

    // 🔹 Login başarılıysa yönlendir
    LaunchedEffect(loginSuccess) {
        if (loginSuccess == true) {
            Toast.makeText(context, "Giriş başarılı 🎉", Toast.LENGTH_SHORT).show()
            navController.navigate("mapScreen") {
                popUpTo("loginScreen") { inclusive = true }
            }
        }
    }

    // 🔹 Hata mesajı göster
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    // UI
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.size(150.dp))

            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(220.dp)      // 🔥 ikon küçülür
                    .padding(20.dp, 5.dp)
                    .align(Alignment.CenterHorizontally),
            )


            Spacer(modifier = Modifier.size(5.dp))

            Text(
                text = "Around You",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            Spacer(modifier = Modifier.size(5.dp))

            Text(
                text = stringResource(id = R.string.start_exploring),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            Spacer(modifier = Modifier.size(10.dp))

            // 🔥 login_image’i ortala (fillMaxSize KALDIRILDI)
            Image(
                painter = painterResource(R.drawable.login_image),
                contentDescription = null,
                modifier = Modifier
                    .size(240.dp)      // boyut verebilirsin
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.weight(1f)) // Alt butonu aşağı iter

            // Google Buton
            GoogleLoginButton(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                },
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.size(50.dp))
        }
    }
}



    @Composable
fun GoogleLoginButton(
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // 🔹 Nabız animasyonu (sadece isLoading değilken çalışsın)
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Button(
        onClick = { if (!isLoading) onClick() }, // 🔹 loading durumunda tıklanamaz
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.darkBlue),
            contentColor = Color.Black
        ),
        border = BorderStroke(1.dp, Color.LightGray),
        shape = RoundedCornerShape(50),
        modifier = modifier
            .scale(if (!isLoading) scale else 1f)
            .height(60.dp)
            .fillMaxWidth(0.75f),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(25.dp)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google2),
                    contentDescription = "Google Logo",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 10.dp)
                )
                Text(
                    text = stringResource(id = R.string.google_login_btn),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(){
    LoginScreen(navController = NavController(context = LocalContext.current), googleLoginViewModel = hiltViewModel())
}

 */