package cz.regulus.dotaznik.dotaznik

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.sun.mail.util.MailConnectException
import cz.regulus.dotaznik.BuildConfig
import cz.regulus.dotaznik.PrihlasenState
import cz.regulus.dotaznik.Repository
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
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Named
import java.io.File
import java.util.Locale
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
    @Named("cache") private val cacheDir: File,
    @InjectedParam private val reset: () -> Unit
) : ViewModel() {
    val debug = BuildConfig.DEBUG || '-' in BuildConfig.VERSION_NAME

    val stranky = repo.stranky
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), null)

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
                OdesilaniState.Uspech -> odstranitVse()
                OdesilaniState.Error.Offline -> zobrazitChybu()
                is OdesilaniState.Error.Podrobne -> Unit
                OdesilaniState.OdstranitData -> odstranitData()
            }
        }
        else _odeslaniState.value = OdesilaniState.Nic
    }

    private suspend fun emailDoruceni() = when {
        debug -> repo.prihlasenState.first().uzivatel!!.email
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
                return PasswordAuthentication(EmailCredentials.EMAIL, BuildConfig.HESLO)
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
                setFrom(InternetAddress(EmailCredentials.EMAIL, "Aplikace Regulus"))

                subject = "REGULUS – Apka – OSOBA: $jmeno $prijmeni"

                addRecipient(
                    Message.RecipientType.TO,
                    InternetAddress(emailDoruceni())
                )
                if (!debug) {
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

                    repo.pripravitFotkyNaExport()
                    repo.fotky.first().forEachIndexed { i, (_, file) ->
                        addBodyPart(MimeBodyPart().apply {
                            attachFile(file)
                            setHeader("Content-Type", "image/jpg; charset=UTF-8 name=\"fotka ${i + 1}\"")
                        })
                    }
                })
            })

            _odeslaniState.value = OdesilaniState.Uspech

        } catch (e: MessagingException) {
            e.printStackTrace()
            Firebase.crashlytics.recordException(RuntimeException("Could not send email", e))

            Log.e("email", "CHYBA", e)

            chyba = e.stackTraceToString()

            _odeslaniState.value =
                if (e is MailConnectException)
                    OdesilaniState.Error.Offline
                else
                    OdesilaniState.Error.Podrobne(e.stackTraceToString())
        }
    }

    private suspend fun odstranitData() {
        _odeslaniState.value = OdesilaniState.Nic

        repo.upravitStranky(Stranky())

        repo.odstranitVsechnyFotky()

        viewModelScope.launch(Dispatchers.Main) {
            reset()
        }
    }

    private fun zobrazitChybu() {
        _odeslaniState.value = OdesilaniState.Error.Podrobne(chyba)
    }

    fun odstranitVse() {
        _odeslaniState.value = OdesilaniState.OdstranitData
    }
}