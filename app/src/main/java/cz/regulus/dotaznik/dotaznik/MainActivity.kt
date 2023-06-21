package cz.regulus.dotaznik.dotaznik

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cz.regulus.dotaznik.*
import com.regulus.dotaznik.R
import com.regulus.dotaznik.databinding.ActivityMainBinding
import cz.regulus.dotaznik.prihlaseni.PrihlaseniActivity
import cz.regulus.dotaznik.spravaFotek.FotkyActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
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

    private lateinit var toggle: ActionBarDrawerToggle
    var debugMode = false

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)

        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun onResume() {
        super.onResume()

        prefsPrihlaseni.edit {
            putInt("verze", VERZE)
        }

        val header = binding.navigationView.getHeaderView(0)

        val tvNavJmeno = header.findViewById<TextView>(R.id.tvNavJmeno)
        val tvNavEmail = header.findViewById<TextView>(R.id.tvNavEmail)
        val tvNavIco = header.findViewById<TextView>(R.id.tvNavIco)
        val tvNavKod = header.findViewById<TextView>(R.id.tvNavKod)

        tvNavIco.visibility = if (prefsPrihlaseni.getString("ico","")!!.isNotEmpty()) View.VISIBLE else View.GONE

        tvNavJmeno.text = getString(R.string.neco_mezera_neco, getString(R.string.prihlaseni_jmeno_prijmeni), prefsPrihlaseni.getString("jmeno","Error"))
        tvNavEmail.text = getString(R.string.neco_mezera_neco, getString(R.string.prihlaseni_email), prefsPrihlaseni.getString("email","Error"))
        tvNavIco.text = getString(R.string.neco_mezera_neco, getString(R.string.prihlaseni_ico), prefsPrihlaseni.getString("ico", ""))
        tvNavKod.text = getString(R.string.neco_mezera_neco, getString(R.string.prihlaseni_kod), prefsPrihlaseni.getString("kod","Error"))

    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
//        setContentView(view)

        setSupportActionBar(binding.topAppBar)

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

//        binding.viewPager.adapter = ViewPagerAdapter(this)

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
//        binding.viewPager.addItemDecoration(dividerItemDecoration)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.otevrit, R.string.zavrit)

        openDrawer()

        binding.drawerLayout.addDrawerListener(toggle)

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {
                toggle.syncState()
            }
        })

        toggle.syncState()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (prefsPrihlaseni.getInt("verze", 0) < VERZE) {
            prefsPrihlaseni.edit {
                putBoolean("prihlasen", false)
            }
        }

        if (!prefsPrihlaseni.getBoolean("prihlasen", false)) {
            val registerForActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                recreate()
            }
            registerForActivityResult.launch(Intent(this, PrihlaseniActivity::class.java))
        }

//        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
//
//                binding.navigationView.setCheckedItem(
//                    listOf(
//                        R.id.kontaktyFragment,
//                        R.id.detailObjektuFragment,
//                        R.id.systemFragment,
//                        R.id.bazenFragment,
//                        R.id.zdrojeFragment,
//                        R.id.prislusenstviFragment,
//                    )[position]
//                )
//
//                title = binding.navigationView.checkedItem!!.title
//            }
//        })

        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.actionOdeslat -> {

                    odeslat()
                    true
                }

                R.id.actionOdstranit -> {
                    MaterialAlertDialogBuilder(this).apply {

                        setIcon(R.drawable.ic_baseline_delete_24)
                        setTitle(getString(R.string.export_opravdu_odstranit_data))
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

                        show()
                    }
                    true
                }

                else -> {

//                binding.viewPager.currentItem = listOf(
//                    R.id.kontaktyFragment,
//                    R.id.detailObjektuFragment,
//                    R.id.systemFragment,
//                    R.id.bazenFragment,
//                    R.id.zdrojeFragment,
//                    R.id.prislusenstviFragment,
//                ).indexOf(item.itemId)

                    binding.drawerLayout.closeDrawers()

                    title = binding.navigationView.checkedItem!!.title

                    true
                }
            }
        }

        setContent {

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.actionFotky) {
            val intent = Intent(this, FotkyActivity::class.java)
            startActivity(intent)

            return true
        }

        openDrawer()

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        }

        return if (toggle.onOptionsItemSelected(item)) true

        else super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {

        openDrawer()

        return super.onSupportNavigateUp()
    }

    fun odeslat() {

        val stranky = saver.get()

        val kontakty = stranky.kontakty

        if (kontakty.jmeno.text == "" || kontakty.prijmeni.text == "") {
            Toast.makeText(this, R.string.je_potreba_zadat_jmeno_a_prijmeni, Toast.LENGTH_SHORT).show()
            return
        }

        // odstranit stary soubor
        File(cacheDir, "dotaznik_app.xml").delete()

        // vytvorit novy soubor
        File.createTempFile("dotaznik_app.xml", null, cacheDir)

        val file = File(this.cacheDir, "dotaznik_app.xml")

        file.writeText(
            stranky.createXml()
        )

        MaterialAlertDialogBuilder(this).apply {

            setIcon(R.drawable.ic_baseline_send_24)
            setTitle(R.string.export_chcete_odeslat)
            setMessage(
                getString(
                    R.string.export_opravdu_chcete_odeslat_na, when {
                        debugMode -> prefsPrihlaseni.getString("email", "")
                        Locale.getDefault().language == Locale("sk").language -> "obchod@regulus.sk"
                        else -> "poptavky@regulus.cz"
                    }
                )
            )
            setCancelable(true)

            setPositiveButton(getString(R.string.ano)) { dialog, _ ->
                sendEmail(kontakty.jmeno.text, kontakty.prijmeni.text, file)
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

                    addRecipient(
                        Message.RecipientType.TO,
                        InternetAddress(
                            when {
                                debugMode -> prefsPrihlaseni.getString("email", "")
                                Locale.getDefault().language == Locale("sk").language -> "obchod@regulus.sk"
                                else -> "poptavky@regulus.cz"
                            }
                        )
                    )
                    if (!debugMode) {
                        addRecipient(
                            Message.RecipientType.CC,
                            InternetAddress(prefsPrihlaseni.getString("email", ""))
                        )
                    }
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
