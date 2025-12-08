package com.ayse.aroundyou.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayse.aroundyou.BuildConfig
import com.ayse.aroundyou.model.entities.MessageModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ChatViewModel @Inject constructor(): ViewModel() {

    val messageList by lazy { mutableStateListOf<MessageModel>() }

    val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    // Kullanıcı mesajını gönder ve AI cevabını al
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role) { text(it.message) }
                    }.toList()
                )

                // Kullanıcı mesajını ekle
                messageList.add(MessageModel(question, "user"))
                messageList.add(MessageModel("...", "model")) // placeholder

                // AI cevabını al
                val response = chat.sendMessage(question)
                messageList.removeLast() // placeholder'ı sil
                messageList.add(MessageModel(response.text.toString(), "model"))

            } catch (e: Exception) {
                messageList.removeLast()
                messageList.add(MessageModel("Error: ${e.message}", "model"))
            }
        }
    }

    // AIScreen açıldığında AI başlangıç promptu sabit eklenir
    fun sendInitialPromptOnly() {
        val prompt = "Merhaba." +
                "Sana nasıl yardımcı olabilirim?  Eğer bulunduğun ili söylersen, " +
                "hava durumuna göre yapabileceğin en iyi aktivite veya mekanı önerebilirim, ne dersin?"

        // AI cevabı üretmeden sadece prompt'u ekle
        messageList.add(MessageModel(prompt, "model"))
    }
}
