package com.ayse.aroundyou.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ayse.aroundyou.R
import com.ayse.aroundyou.model.entities.MessageModel
import com.ayse.aroundyou.viewmodel.ChatViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.ayse.aroundyou.ui.theme.lightBlue


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AIScreen(
    modifier:Modifier,
    navController: NavHostController,
    chatViewModel: ChatViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Column içinde header ve mesaj listesi
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp) // MessageInput için alt boşluk
        ) {
            AppHeader(navController)
            // 2️⃣ LaunchedEffect ile ekran açıldığında sadece bir kere çalışacak kod

            LaunchedEffect(Unit) {
                // 3️⃣ ViewModel fonksiyonunu çağır, AI’ye başlangıç promptunu gönder
                chatViewModel.sendInitialPromptOnly()
            }

            MessageList(
                modifier = Modifier.fillMaxWidth(),
                messageList = chatViewModel.messageList
            )
        }

        // MessageInput için ayrı Box, ekranın altına sabitlenmiş
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart) // ekranın altına sabit
        ) {

            MessageInput(
                onMessageSend = { chatViewModel.sendMessage(it) },
               // onMessageSend = { chatViewModel.sendInitialAIPrompt() },
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding() // klavye açıldığında yukarı kayar

            )
        }
    }
}

@Composable
fun AppHeader(navController: NavHostController){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(lightBlue)
            .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
        /**
        top = 40.dp → Kutunun üstünden 40 dp boşluk bırakır. Yani kutu ekranın üstüne yapışmaz, 40 dp aşağıda başlar.
        start = 16.dp → Sol taraftan 16 dp boşluk bırakır. Ekranın kenarına yapışmaz.
        end = 16.dp → Sağ taraftan 16 dp boşluk bırakır. Ekranın kenarına yapışmaz.
        bottom = 12.dp → Alt taraftan 12 dp boşluk bırakır.
         */
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Sol ikon + metin
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Beyaz yuvarlak kutu içinde ikon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ai),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(25.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp)) // ikon ile metin arası boşluk

                // Metin
                Text(
                    text = "Chat AI",
                    color = Color.White,
                    fontSize = 22.sp
                )
            }

            // Sağdaki close butonu
            IconButton(onClick = {
                navController.navigate("mapScreen") {
                    launchSingleTop = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }

}

@Composable
fun MessageList(modifier: Modifier = Modifier, messageList: List<MessageModel>) {

    if(messageList.isEmpty()){
        Column (modifier=Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Icon(
                modifier=Modifier.size(60.dp),
                painter= painterResource(id= R.drawable.question),
                contentDescription = "Icon",
                tint = Color.LightGray
            )
            Text(text="Ask me anything", fontSize = 22.sp,
                color = Color.LightGray)
        }
    }else{
        LazyColumn(
            modifier= Modifier,
            reverseLayout = true //(En yeni mesaj en altta durur, yukarı kaydırınca eski mesajlar gelir)
        ) { //dikey liste
            items(messageList.reversed()){ //viewmodel sınıfındaki messagelist deki her bir indexi gex
                // Text(text = it.message) //messagelistteki messageleri dikey liste halinde ekranda göster
                MessageRow(messageModel = it)
            }
        }
    }

}

@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"

    Row(
       // verticalAlignment = Alignment.CenterVertically, //uzun mesaj ooldugunda iconu ortalıyor
        verticalAlignment = Alignment.Top, // iconu Top hizalama
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isModel) Arrangement.Start else Arrangement.End
    ) {
        if (isModel) {
            // AI mesajı → sol tarafta yuvarlak icon + balon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ai),
                    contentDescription = "AI Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp)) // icon ile balon arası boşluk
        }

        // Mesaj balonu
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(48f))
                .background(if (isModel) Color(0xFFFFFFFF) else Color(0xFF000000))
                .border(
                    width = if (isModel) 1.dp else 0.dp,
                    color = if (isModel) Color.Black else Color.Transparent,
                    shape = RoundedCornerShape(48f)
                )
                .padding(12.dp)
                .widthIn(max = 250.dp) // ✅ Maksimum genişlik ayarı, satırı kaplamasın
        ) {
            SelectionContainer {
                Text(
                    text = messageModel.message,
                    fontWeight = FontWeight.W500,
                    color = if (isModel) Color(0xFF000000) else Color(0xFFFFFFFF),
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun MessageInput(modifier: Modifier=Modifier,onMessageSend: (String) -> Unit) {

    /*
remember: Recomposition sırasında değeri hatırlatır.
mutableStateOf(""): Değişebilir bir state objesi oluşturur ve değiştiğinde UI’yi tetikler.
var message by remember { mutableStateOf("") }: UI ile senkronize, yeniden çizimlerde kaybolmayan bir değişken oluşturur.
 */

    var message by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding() // Klavye açıldığında yukarı kayar
            .padding(horizontal = 8.dp, vertical = 30.dp),
        //horizontal  => sol ve sağdan boşluk
        //vertical alttan ve üsten boşluk

        verticalAlignment = Alignment.CenterVertically
    ) {

        val scrollState = rememberScrollState()

        OutlinedTextField(
                modifier =
                    Modifier
                        .weight(1f)
                        .heightIn(min = 55.dp, max = 150.dp) // minimum ve maksimum yükseklik
                        .verticalScroll(scrollState)          // ⚡ scroll ekliyoruz
                        .border(
                            1.dp, Color.Black,
                            shape = RoundedCornerShape(11.dp)
                        ),

                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.White,          // TextField'a tıklanınca görünen çerçevenin rengi (focus border)
                    unfocusedIndicatorColor = Color.White,        // TextField boştayken görünen çerçevenin rengi
                    focusedContainerColor = Color.White,          // TextField focus olduğunda arka plan rengi
                    unfocusedContainerColor = Color.White,        // TextField focus olmadığında arka plan rengi
                    cursorColor = Color.Black,                    // Yazı yazarken yanıp sönen imlecin rengi
                    focusedTextColor = Color.Black,               // TextField'a tıklayınca yazı rengi
                    unfocusedTextColor = Color.Black              // TextField boştayken yazı rengi
                ),
                shape = RoundedCornerShape(16.dp), // köşeleri yuvarlak
                value = message,  //kullanıcın girdiği veya gösterildii metin
                onValueChange = {
                    message = it // kullanıcı yazdıkça message güncellenir
                }
            )
            val keyboardController = LocalSoftwareKeyboardController.current

            IconButton(onClick = {
                if (message.isNotEmpty()) {
                    onMessageSend(message) //send butonun abasıldığınd amesajı gönderip text i sıfırlıyor
                    message = ""
                    keyboardController?.hide() // klavyeyi kapat

                }


            }) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }



