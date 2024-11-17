package cz.regulus.dotaznik.dotaznik

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.sun.mail.util.MailConnectException
import cz.regulus.dotaznik.BuildConfig
import cz.regulus.dotaznik.Repository
import cz.regulus.dotaznik.User
import cz.regulus.dotaznik.userOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Named
import java.io.File
import java.lang.System
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
class QuestionnaireViewModel(
    private val repo: Repository,
    @Named("cache") private val cacheDir: File,
    @InjectedParam private val reset: () -> Unit,
) : ViewModel() {
    val isDebug = repo.isDebug

    val sites = repo.sites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), null)

    fun editSites(sites: Sites) = viewModelScope.launch {
        repo.editSites(sites)
    }

    val companies = repo.companies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), emptyList())

    fun logOut() = viewModelScope.launch {
        repo.logOut()
    }

    val userOrNull = repo.authenticationState.map {
        it.userOrNull
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), null)

    private val _sendState = MutableStateFlow<SendState>(SendState.Nothing)
    val sendState = _sendState.asStateFlow()

    private lateinit var error: String

    fun changeState(moveOn: Boolean) {
        if (moveOn) viewModelScope.launch(Dispatchers.IO) {
            when (sendState.value) {
                SendState.Nothing -> askForConfirmation()
                is SendState.MissingField -> Unit
                is SendState.ConfirmSend -> sendEmail()
                SendState.Sending -> Unit
                SendState.Success -> askForRemoval()
                SendState.Error.Offline -> showError()
                SendState.Error.Other -> showError()
                is SendState.Error.Details -> Unit
                SendState.ConfirmDataRemoval -> removeData()
            }
        }
        else _sendState.value = SendState.Nothing
    }

    private fun recipientAdress(user: User) = when {
        isDebug -> user.email
        Locale.getDefault().language == Locale("sk").language -> "obchod@regulus.sk"
        else -> "poptavky@regulus.cz"
    }

    private suspend fun askForConfirmation() {
        val sites = repo.sites.first()
        val demandOrigin = sites.contacts.demandOrigin

        _sendState.value = if (demandOrigin.getChosenIndex(sites) == 0) SendState.MissingField(
            demandOrigin.getLabel(sites)
        ) else SendState.ConfirmSend(recipientAdress(repo.authenticationState.first().userOrNull!!))
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

    val firstStart = repo.firstStart.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), true)

    fun started() {
        viewModelScope.launch {
            repo.started()
        }
    }

    private suspend fun sendEmail() {
        _sendState.value = SendState.Sending

        val sites = repo.sites.first()

        val user = repo.authenticationState.first().userOrNull!!

        val name = sites.contacts.name.text
        val surname = sites.contacts.surname.text

        val file = File(cacheDir, "dotaznik_app.xml")

        file.writeText(
            sites.createXml(repo.authenticationState.first().userOrNull!!)
        )

        try {
            Transport.send(MimeMessage(session).apply {
                setFrom(InternetAddress(EmailCredentials.EMAIL, "Aplikace Regulus"))
                replyTo = arrayOf(InternetAddress(user.email))

                subject = "REGULUS – Apka – OSOBA: $name $surname"

                addRecipient(
                    Message.RecipientType.TO,
                    InternetAddress(recipientAdress(user))
                )
                if (!isDebug) {
                    addRecipient(
                        Message.RecipientType.CC,
                        InternetAddress(user.email)
                    )
                }

                setContent(MimeMultipart().apply {
                    addBodyPart(MimeBodyPart().apply {
                        setText(user.constructEmail(), null, "html")
                    })

                    addBodyPart(MimeBodyPart().apply {
                        dataHandler = DataHandler(FileDataSource(file))
                        fileName = file.name
                    })

                    repo.getPhotosForExport().forEach { file ->
                        addBodyPart(MimeBodyPart().apply {
                            attachFile(file)
                            setHeader("Content-Type", "image/jpg; charset=UTF-8 name=\"${file.name}\"")
                        })
                    }
                })
            })

            _sendState.value = SendState.Success

        } catch (e: MessagingException) {
            val wrapper = RuntimeException("Could not send email", e)
            wrapper.printStackTrace()
            Firebase.crashlytics.recordException(wrapper)

            error = wrapper.stackTraceToString()

            _sendState.value =
                if (e is MailConnectException)
                    SendState.Error.Offline
                else
                    SendState.Error.Other
        }
    }

    private suspend fun removeData() {
        _sendState.value = SendState.Nothing

        repo.editSites(Sites())

        repo.deleteAllPhotos()

        viewModelScope.launch(Dispatchers.Main) {
            reset()
        }
    }

    private fun showError() {
        _sendState.value = SendState.Error.Details(error)
    }

    fun askForRemoval() {
        _sendState.value = SendState.ConfirmDataRemoval
    }
}

@Language("html")
private fun User.constructEmail() = """
    <p>Prosím o přípravu nabídky. Děkuji.</p>
    ${if (crn.isNotBlank()) "<p>$name $surname, IČO: $crn</p>" else "<p>$name $surname</p>"}
    <hr style='color: gray' />
    <p style='color: gray'>Tento email byl vygenerován automaticky, pokud chcete odpovědět, zvolte Odpovědět všem.</p>
""".trimIndent()