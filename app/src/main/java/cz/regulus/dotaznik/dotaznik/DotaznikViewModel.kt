package cz.regulus.dotaznik.dotaznik

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.regulus.dotaznik.BuildConfig
import com.regulus.dotaznik.R
import cz.regulus.dotaznik.EmailCredentials
import cz.regulus.dotaznik.PrihlasenState
import cz.regulus.dotaznik.Repository
import cz.regulus.dotaznik.Stranky
import cz.regulus.dotaznik.uzivatel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel
import java.io.File
import java.util.Locale
import java.util.concurrent.Executors
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import kotlin.time.Duration.Companion.seconds

@KoinViewModel
class DotaznikViewModel(
    private val repo: Repository,
    private val res: Resources,
    private val cacheDir: File,
) : ViewModel() {
    val stranky = repo.stranky
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), Stranky())

    fun upravitStranky(stranky: Stranky) = viewModelScope.launch {
        repo.upravitStranky(stranky)
    }

    val firmy = repo.firmy
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), emptyList())

    fun odhlasit() = viewModelScope.launch {
        repo.odhlasit()
    }

    val prihlasen = repo.prihlasenState.map {
        when (it) {
            PrihlasenState.Odhasen -> null
            is PrihlasenState.Prihlasen -> it.uzivatel
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), null)

    private val _odeslaniState = MutableStateFlow<OdesilaniState>(OdesilaniState.Nic)
    val odesilaniState = _odeslaniState.asStateFlow()

    private lateinit var chyba: String

    fun zmenitState(positive: Boolean) {
        val state = odesilaniState.value
        if (positive) viewModelScope.launch(Dispatchers.IO) {
            when (state) {
                OdesilaniState.Nic -> zacitOdesilani()
                is OdesilaniState.OpravduOdeslat -> odeslat()
                OdesilaniState.Odesilani -> Unit
                OdesilaniState.UspechAOdstranitData -> odstranitData()
                OdesilaniState.Error.Offline -> zobrazitChybu()
                is OdesilaniState.Error.Podrobne -> Unit
            }
        }
        else _odeslaniState.value = OdesilaniState.Nic
    }

    private suspend fun emailDoruceni() = when {
        BuildConfig.DEBUG -> repo.prihlasenState.first().uzivatel!!.email
        Locale.getDefault().language == Locale("sk").language -> "obchod@regulus.sk"
        else -> "poptavky@regulus.cz"
    }

    private suspend fun zacitOdesilani() {
        delay(1000)
        _odeslaniState.value = OdesilaniState.OpravduOdeslat(emailDoruceni())
    }

    private val session = Session.getInstance(
        System.getProperties().apply {
            set("mail.smtp.host", "smtp.gmail.com")
            set("mail.smtp.socketFactory.port", "465")
            set("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            set("mail.smtp.auth", "true")
            set("mail.smtp.port", "465")
        },
        object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(EmailCredentials.EMAIL, EmailCredentials.PASSWORD)
            }
        },
    )!!

    private suspend fun odeslat() {
        _odeslaniState.value = OdesilaniState.Odesilani

        val stranky = repo.stranky.first()

        val uzivatel = repo.prihlasenState.first().uzivatel!!

        val jmeno = stranky.kontakty.jmeno.text
        val prijmeni = stranky.kontakty.prijmeni.text

        val file = File(cacheDir, "dotaznik_app.xml")

        with(res) {
            file.writeText(
                stranky.createXml(
                    repo.prihlasenState.first().uzivatel!!
                )
            )
        }

        try {
            Transport.send(MimeMessage(session).apply {
                setFrom(InternetAddress(EmailCredentials.EMAIL))

                subject = "REGULUS – Apka – OSOBA: $jmeno $prijmeni"

                addRecipient(
                    Message.RecipientType.TO,
                    InternetAddress(emailDoruceni())
                )
                if (!BuildConfig.DEBUG) {
                    addRecipient(
                        Message.RecipientType.CC,
                        InternetAddress(uzivatel.email)
                    )
                }

                setContent(MimeMultipart().apply {

                    addBodyPart(MimeBodyPart().apply {
                        setText("Prosím o přípravu nabídky. Děkuji.\n\n${uzivatel.jmeno}")
                    })

                    addBodyPart(MimeBodyPart().apply {
                        dataHandler = DataHandler(FileDataSource(file))
                        fileName = file.name
                    })

//                        repeat(prefs.getInt("fotky", 0)) { i ->
//                            MimeBodyPart().apply {
//                                attachFile(File(filesDir, "photo${i + 1}.jpg"))
//                                setHeader("Content-Type", "image/jpg; charset=UTF-8 name=\"fotka $i\"")
//                                addBodyPart(this)
//                            }
//                        }
                })
            })

            _odeslaniState.value = OdesilaniState.UspechAOdstranitData

        } catch (e: MessagingException) {
            e.printStackTrace()

            Log.e("email", "CHYBA", e)

            chyba = e.stackTraceToString()

            _odeslaniState.value =
                if (e.toString().contains("Couldn't connect to host"))
                    OdesilaniState.Error.Offline
                else
                    OdesilaniState.Error.Podrobne(e.stackTraceToString())
        }
    }

    private suspend fun odstranitData() {
        _odeslaniState.value = OdesilaniState.Nic

        repo.upravitStranky(Stranky())



//            val pocetFotek = prefs.getInt("fotky", 0)
//
//            repeat(pocetFotek) { i ->
//                val file = File(filesDir, "photo${i + 1}.jpg")
//
//                file.delete()
//            }
//
//            prefs.edit {
//                putInt("fotky", 0)
//            }
    }

    private fun zobrazitChybu() {
        _odeslaniState.value = OdesilaniState.Error.Podrobne(chyba)
    }
}