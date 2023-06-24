package cz.regulus.dotaznik.dotaznik

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.content.edit
import androidx.core.view.WindowCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.navigate
import com.regulus.dotaznik.R
import cz.regulus.dotaznik.*
import cz.regulus.dotaznik.destinations.PrihlaseniSceenDestination
import cz.regulus.dotaznik.theme.DotaznikTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.android.ext.android.get
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.concurrent.Executors
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart


class MainActivity : AppCompatActivity() {

    companion object {
        const val VERZE: Int = 4310
    }

    override fun onResume() {
        super.onResume()

        prefsPrihlaseni.edit {
            putInt("verze", VERZE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (intent.getBooleanExtra("delete", false)) {

            saver.delete(this)

            val pocetFotek = prefs.getInt("fotky", 0)

            repeat(pocetFotek) { i ->
                val file = File(filesDir, "photo${i + 1}.jpg")

                file.delete()
            }

            prefs.edit {
                putInt("fotky", 0)
            }

            return
        }

        if (prefsPrihlaseni.getInt("verze", 0) < VERZE) {
            prefsPrihlaseni.edit {
                putBoolean("prihlasen", false)
            }
        }

        val repo = get<Repository>()

        setContent {
            val navController = rememberNavController()

            val prihlasen by repo.prihlasenState.collectAsStateWithLifecycle(false)

            DotaznikTheme(
                useDynamicColor = false
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    DestinationsNavHost(navController = navController, navGraph = NavGraphs.root)
                }
            }
            LaunchedEffect(prihlasen) {
                launch(Dispatchers.IO) {
                    if (prihlasen is PrihlasenState.Odhasen) {
                        withContext(Dispatchers.Main) {
                            navController.navigate(PrihlaseniSceenDestination)
                        }
                    }
                }
            }
        }
    }

    fun odeslat() {

        val stranky = saver.get()

        val kontakty = stranky.kontakty as Stranky.Stranka.Kontakty

//        if (kontakty.jmeno.text == "" || kontakty.prijmeni.text == "") {
//            Toast.makeText(this, R.string.je_potreba_zadat_jmeno_a_prijmeni, Toast.LENGTH_SHORT).show()
//            return
//        }

        // odstranit stary soubor
        File(cacheDir, "dotaznik_app.xml").delete()

        // vytvorit novy soubor
        File.createTempFile("dotaznik_app.xml", null, cacheDir)

        val file = File(this.cacheDir, "dotaznik_app.xml")

//        file.writeText(
//            stranky.createXml()
//        )

        MaterialAlertDialogBuilder(this).apply {

            setIcon(R.drawable.ic_baseline_send_24)
            setTitle(R.string.export_chcete_odeslat)
            @Suppress("IntroduceWhenSubject")
            setMessage(
                getString(
                    R.string.export_opravdu_chcete_odeslat_na, when {
//                        debugMode -> prefsPrihlaseni.getString("email", "")
                        Locale.getDefault().language == Locale("sk").language -> "obchod@regulus.sk"
                        else -> "poptavky@regulus.cz"
                    }
                )
            )
            setCancelable(true)

            setPositiveButton(getString(R.string.ano)) { dialog, _ ->
//                sendEmail(kontakty.jmeno.text, kontakty.prijmeni.text, file)
                dialog.cancel()
            }

            setNegativeButton(getString(R.string.zrusit)) { dialog, _ -> dialog.cancel() }

            show()
        }
    }

    private fun sendEmail(jmeno: String, prijmeni: String, xml: File) {

        val dialog = MaterialAlertDialogBuilder (this).apply {
            setTitle(R.string.export_odesilani)
            val pb = ProgressBar(context)
            pb.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            pb.updateLayoutParams<LinearLayout.LayoutParams> {
                updateMargins(top = 8, bottom = 16)
            }
            setView(pb)
        }.create()
        dialog.show()


        Executors.newSingleThreadExecutor().execute {

            val props = System.getProperties()
            props["mail.smtp.host"] = "smtp.gmail.com"
            props["mail.smtp.socketFactory.port"] = "465"
            props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.port"] = "465"

            val session = Session.getInstance(props,
                object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(EmailCredentials.EMAIL, EmailCredentials.PASSWORD)
                    }
                })

            try {

                MimeMessage(session).apply {

                    setFrom(InternetAddress(EmailCredentials.EMAIL))

                    @Suppress("IntroduceWhenSubject")
                    addRecipient(
                        Message.RecipientType.TO,
                        InternetAddress(
                            when {
//                                debugMode -> prefsPrihlaseni.getString("email", "")
                                Locale.getDefault().language == Locale("sk").language -> "obchod@regulus.sk"
                                else -> "poptavky@regulus.cz"
                            }
                        )
                    )
//                    if (!debugMode) {
//                        addRecipient(
//                            Message.RecipientType.CC,
//                            InternetAddress(prefsPrihlaseni.getString("email", ""))
//                        )
//                    }
                    subject = "REGULUS – Apka – OSOBA: $jmeno $prijmeni"


                    MimeMultipart().apply {

                        MimeBodyPart().apply {
                            setText("Prosím o přípravu nabídky. Děkuji.\n\n${prefsPrihlaseni.getString("jmeno", "Error")}")
                            addBodyPart(this)
                        }
                        MimeBodyPart().apply {
                            dataHandler = DataHandler(FileDataSource(xml))
                            fileName = xml.name
                            addBodyPart(this)
                        }
                        repeat(prefs.getInt("fotky", 0)) { i ->
                            MimeBodyPart().apply {
                                attachFile(File(filesDir, "photo${i + 1}.jpg"))
                                setHeader("Content-Type", "image/jpg; charset=UTF-8 name=\"fotka $i\"")
                                addBodyPart(this)
                            }
                        }
                        setContent(this)
                    }
                    Transport.send(this)
                }

                dialog.dismiss()


                MaterialAlertDialogBuilder(this).apply {

                    setIcon(R.drawable.ic_baseline_delete_24)
                    setTitle(getString(R.string.export_opravdu_odstranit_data))
                    setMessage(getString(R.string.export_email_uspesne_odeslan))
                    setCancelable(false)
                    setPositiveButton(getString(R.string.ano)) { dialog, _ ->

                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                        intent.putExtra("delete", true)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        finishAffinity()
                        startActivity(intent)

                        dialog.cancel()
                    }
                    setNegativeButton(getString(R.string.ne)) { dialog, _ ->
                        dialog.cancel()
                    }

                    runOnUiThread {
                        show()
                    }
                }

                Log.i("email", "odeslano")

            } catch (e: MessagingException) {
                e.printStackTrace()

                Log.e("email", "CHYBA", e)

                dialog.dismiss()

                with(e.toString()) {
                    when {
                        contains("Couldn't connect to host") -> {
                            MaterialAlertDialogBuilder(this@MainActivity).apply {

                                setMessage(getString(R.string.export_nejste_pripojeni))
                                setTitle(getString(R.string.export_email_neodeslan, ""))
                                setCancelable(false)
                                setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                                    dialog.cancel()
                                }
                                setNeutralButton(getString(R.string.podrobnejsi_info)) { dialog, _ ->
                                    dialog.cancel()


                                    MaterialAlertDialogBuilder(this@MainActivity).apply {

                                        setMessage(e.stackTraceToString())
                                        setTitle(getString(R.string.export_email_neodeslan, getString(R.string.toto_je_chyba)))
                                        setCancelable(false)
                                        setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                                            dialog.cancel()
                                        }

                                        runOnUiThread {
                                            show()
                                        }
                                    }
                                }

                                runOnUiThread {
                                    show()
                                }
                            }
                        }
                        else -> {
                            MaterialAlertDialogBuilder(this@MainActivity).apply {

                                setMessage(e.stackTraceToString())
                                setTitle(getString(R.string.export_email_neodeslan, getString(R.string.toto_je_chyba)))
                                setCancelable(false)
                                setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                                    dialog.cancel()
                                }

                                show()
                            }
                        }
                    }
                }
            }
        }
    }
}
