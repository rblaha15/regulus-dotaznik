package com.regulus.dotaznik

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
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
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    private var debugMode = false

    fun recreateKontaktyFragment() {

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment?
        val navController = navHostFragment!!.navController
        navController.navigate(R.id.action_kontaktyFragment_self)

        toggle.syncState()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun setDrawerOpen() {
        drawer_layout.openDrawer(GravityCompat.START)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment)?.childFragmentManager?.fragments!![0]

        drawer_layout.setDrawerLockMode(when(fragment) {
            is UvodFragment -> DrawerLayout.LOCK_MODE_LOCKED_OPEN
            else -> DrawerLayout.LOCK_MODE_UNLOCKED
        })
    }


    override fun onResume() {
        super.onResume()


        val prefsPrihlaseni = this.getSharedPreferences("PREFS_PRIHLASENI", Context.MODE_PRIVATE)

        prefsPrihlaseni.edit {
            putInt("verze", 4200)
        }

        val header = navigationView.getHeaderView(0)

        val tvNavJmeno = header.findViewById<TextView>(R.id.tvNavJmeno)
        val tvNavEmail = header.findViewById<TextView>(R.id.tvNavEmail)
        val tvNavIco = header.findViewById<TextView>(R.id.tvNavIco)
        val tvNavKod = header.findViewById<TextView>(R.id.tvNavKod)

        tvNavIco.visibility = if (prefsPrihlaseni.getString("ico", "") != "") View.VISIBLE else View.GONE

        tvNavJmeno.text = getString(R.string.neco_mezera_neco, getString(R.string.prihlaseni_jmeno_prijmeni), prefsPrihlaseni.getString("jmeno","Error"))
        tvNavEmail.text = getString(R.string.neco_mezera_neco, getString(R.string.prihlaseni_email), prefsPrihlaseni.getString("email","Error"))
        tvNavIco.text = getString(R.string.neco_mezera_neco, getString(R.string.prihlaseni_ico), prefsPrihlaseni.getString("ico", ""))
        tvNavKod.text = getString(R.string.neco_mezera_neco, getString(R.string.prihlaseni_kod), prefsPrihlaseni.getString("kod","Error"))

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(topAppBar)

        if (intent.getBooleanExtra("delete", false)) {

            val s = prefs.getInt("fotky", 0)

            saver.delete()

            repeat(s) { i ->
                val file = File(filesDir, "photo${i + 1}.jpg")

                file.delete()
            }

            prefs.edit {
                putInt("fotky", 0)
            }
        }

        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.otevrit, R.string.zavrit)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment?
        val navController = navHostFragment!!.navController

        fabDalsi.visibility = View.GONE

        setDrawerOpen()

        val menu2 = navigationView.menu
        val stranky2 = saver.get()
        val chci2 = stranky2.system.chciBazen

        menu2.findItem(R.id.bazenFragment).isVisible = chci2

        navigationView.setupWithNavController(navController)
        val appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val listener = object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {
                val menu = navigationView.menu
                val stranky = saver.get()
                val chci = stranky.system.chciBazen
                menu.findItem(R.id.bazenFragment).isVisible = chci
            }

            override fun onDrawerClosed(drawerView: View) {

                val fragment = supportFragmentManager.findFragmentById(R.id.fragment)!!.childFragmentManager.fragments[0]

                fabZpet.visibility = if (fragment is KontaktyFragment) View.GONE else View.VISIBLE
                fabDalsi.visibility = View.VISIBLE


                fabDalsi.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,
                    if (fragment is ZdrojeFragment) R.drawable.ic_baseline_send_24 else R.drawable.next
                ))
            }

            override fun onDrawerStateChanged(newState: Int) {
                toggle.syncState()
            }
        }

        drawer_layout.addDrawerListener(toggle)
        drawer_layout.addDrawerListener(listener)

        toggle.syncState()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val prefsPrihlaseni = getSharedPreferences("PREFS_PRIHLASENI", Context.MODE_PRIVATE)

        if (!prefsPrihlaseni.getBoolean("prihlasen", false)) {
            startActivity(Intent(this, PrihlaseniActivity::class.java))
        }

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.actionOdeslat -> {

                    odeslat()
                    return@setNavigationItemSelectedListener true
                }
                R.id.actionOdstranit -> {
                    MaterialAlertDialogBuilder(this).apply {

                        setIcon(R.drawable.ic_baseline_delete_24)
                        setTitle(getString(R.string.export_opravdu_odstranit_data))
                        setCancelable(false)

                        setPositiveButton(getString(R.string.ano)) { dialog, _ ->

                            val intent = Intent(this@MainActivity, MainActivity::class.java)
                            intent.putExtra("delete", true)
                            val fragment = supportFragmentManager.findFragmentById(R.id.fragment)!!
                            supportFragmentManager.beginTransaction().remove(fragment).commit()
                            finishAffinity()
                            startActivity(intent)

                            dialog.cancel()
                        }
                        setNegativeButton(getString(R.string.ne)) { dialog, _ ->
                            dialog.cancel()
                        }

                        show()
                    }
                    return@setNavigationItemSelectedListener true
                }
                else -> {

                    navController.navigate(item.itemId)
                    drawer_layout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }
            }
        }


        fabDalsi.setOnLongClickListener {

            val fragment = supportFragmentManager.findFragmentById(R.id.fragment)?.childFragmentManager?.fragments!![0]

            if (fragment is ZdrojeFragment) {
                debugMode = true
                odeslat()
            }
            return@setOnLongClickListener true
        }

        fabDalsi.setOnClickListener {
            debugMode = false

            val stranky = saver.get()
            val chci = stranky.system.chciBazen

            val id = when (supportFragmentManager.findFragmentById(R.id.fragment)?.childFragmentManager?.fragments!![0]) {
                is KontaktyFragment -> R.id.action_kontaktyFragment_to_detailObjektuFragment
                is DetailObjektuFragment -> R.id.action_detailObjektuFragment_to_systemFragment
                is SystemFragment -> if (chci) R.id.action_systemFragment_to_bazenFragment
                                     else R.id.action_systemFragment_to_zdrojeFragment
                is BazenFragment -> R.id.action_bazenFragment_to_zdrojeFragment
                is ZdrojeFragment -> {odeslat(); R.id.action_zdrojeFragment_self}
                else -> R.id.uvodFragment
            }

            fabZpet.visibility = View.VISIBLE
            fabDalsi.visibility = View.VISIBLE

            when (supportFragmentManager.findFragmentById(R.id.fragment)?.childFragmentManager?.fragments!![0]) {
                is BazenFragment -> fabDalsi.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_send_24))
                is SystemFragment -> if (!chci) fabDalsi.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_send_24))
                else -> fabDalsi.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.next))
            }

            navController.navigate(id)

            toggle.syncState()
        }

        fabZpet.setOnClickListener {

            val stranky = saver.get()
            val chci = stranky.system.chciBazen

            val fragment = supportFragmentManager.findFragmentById(R.id.fragment)?.childFragmentManager?.fragments!![0]

            val id = when (fragment) {
                is ZdrojeFragment -> if (chci) R.id.action_zdrojeFragment_to_bazenFragment
                                        else R.id.action_zdrojeFragment_to_systemFragment
                is BazenFragment -> R.id.action_bazenFragment_to_systemFragment
                is SystemFragment -> R.id.action_systemFragment_to_detailObjektuFragment
                is DetailObjektuFragment -> R.id.action_detailObjektuFragment_to_kontaktyFragment
                else -> R.id.uvodFragment
            }

            fabDalsi.visibility = View.VISIBLE
            fabZpet.visibility = if (fragment is DetailObjektuFragment) View.GONE else View.VISIBLE

            fabDalsi.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.next))

            navController.navigate(id)

            toggle.syncState()
        }

    }

    private var x1: Float = 0F
    private var x2: Float = 0F

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> x1 = event.x
            MotionEvent.ACTION_UP -> {
                x2 = event.x
                val deltaX = x2 - x1
                if (abs(deltaX) > 150) {
                    if (deltaX > 0) {
                        // <-
                        fabZpet.callOnClick()
                    } else if (deltaX < 0) {
                        // ->
                        fabDalsi.callOnClick()
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.actionFotky) {
            val intent = Intent(this, FotkyActivity::class.java)
            startActivity(intent)

            return true
        }

        setDrawerOpen()

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawers()
        }
        return if (toggle.onOptionsItemSelected(item)) true
        else super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {

        setDrawerOpen()

        return super.onSupportNavigateUp()
    }

    private fun odeslat() {

        val stranky = saver.get()

        val kontakty = stranky.kontakty
        val detailObjektu = stranky.detailObjektu
        val system = stranky.system
        val bazen = stranky.bazen
        val zdrojeTop = stranky.zdrojeTop
        val zdrojeTv = stranky.zdrojeTv


        if (kontakty.jmeno == "" || kontakty.prijmeni == "") {
            Toast.makeText(this, R.string.je_potreba_zadat_jmeno_a_prijmeni, Toast.LENGTH_SHORT).show()
            return
        }

        // odstranit stary soubor
        File(cacheDir, "dotaznik_app.xml").delete()

        // vytvorit novy soubor
        File.createTempFile("dotaznik_app.xml", null, cacheDir)

        val file = File(this.cacheDir, "dotaznik_app.xml")

        val prefsPrihlaseni = this.getSharedPreferences("PREFS_PRIHLASENI", Context.MODE_PRIVATE)

        file.writeText(
            """
                <?xml version="1.0" encoding="utf-8"?>
                <?xml-stylesheet type="text/xsl" href="dotaznik_app.xsl"?>
                
                <!-- Tento soubor byl vygenerován automaticky aplikací Regulus Dotazník; verze: 2.0.2 -->
                
                <xml>
                    <system>
                        <resi_tc>Ano</resi_tc>
                        <cislo_ko>${prefsPrihlaseni.getString("kod", "Error")}</cislo_ko>
                        <odesilatel>${prefsPrihlaseni.getString("email", "Error")}</odesilatel>
                        <odberatel_ico>${prefsPrihlaseni.getString("ico", "")}</odberatel_ico>
                    </system>
                    <kontakt>
                        <jmeno>${kontakty.jmeno}</jmeno>
                        <prijmeni>${kontakty.prijmeni}</prijmeni>
                        <telefon>${kontakty.telefon}</telefon>
                        <email>${kontakty.email}</email>
                        <ulice>${kontakty.ulice}</ulice>
                        <psc>${
                            if (kontakty.psc.length != 5) "" 
                            else kontakty.psc.substring(0, 3) + " " + kontakty.psc.substring(3, 5)
                        }</psc>
                        <mesto>${kontakty.mesto}</mesto>
                        <partner_ico>${kontakty.ico}</partner_ico>
                    </kontakt>
                    <detailobjektu>
                        <os_popis>${system.os}</os_popis>
                        <tepelna_ztrata>${detailObjektu.ztrata}</tepelna_ztrata>
                        <rocni_spotreba_vytapeni>${detailObjektu.potrebaVytapeni}</rocni_spotreba_vytapeni>
                        <rocni_spotreba_tv>${detailObjektu.potrebaTv}</rocni_spotreba_tv>
                        <vytapena_plocha>${detailObjektu.plocha}</vytapena_plocha>
                        <vytapeny_objem>${detailObjektu.objem}</vytapeny_objem>
                        <spotreba_paliva_druh>${detailObjektu.druhPaliva}</spotreba_paliva_druh>
                        <spotreba_paliva_mnozstvi>${detailObjektu.spotreba}</spotreba_paliva_mnozstvi>
                        <spotreba_paliva_jednotky>${detailObjektu.spotrebaJednotky}</spotreba_paliva_jednotky>
                        <spotreba_paliva_2_druh>${detailObjektu.druhPaliva2}</spotreba_paliva_2_druh>
                        <spotreba_paliva_2_mnozstvi>${detailObjektu.spotreba2}</spotreba_paliva_2_mnozstvi>
                        <spotreba_paliva_2_jednotky>${detailObjektu.spotrebaJednotky2}</spotreba_paliva_2_jednotky>
                        <rocni_platba_vytapeni>${detailObjektu.naklady}</rocni_platba_vytapeni>
                    </detailobjektu>
                    <tc>
                        <typ>${system.tcTyp}</typ>
                        <model>${system.tcModel}</model>
                        <nadrz>${system.nadrzTyp} ${if (system.nadrzTypPos != 0) system.nadrzTyp2 else ""} ${system.nadrzObjem}</nadrz>
                        <vnitrni_jednotka>${system.jednotkaTyp}</vnitrni_jednotka>
                    </tc>
                    <zdrojeTop>
                        <topne_teleso>${if (zdrojeTop.topTopneTeleso) zdrojeTop.topTopneTelesoTyp else "Ne"}</topne_teleso>
                        <elektrokotel>${if (zdrojeTop.topElektrokotel) zdrojeTop.topElektrokotelTyp else "Ne"}</elektrokotel>
                        <plyn_kotel>${if (zdrojeTop.topPlynKotel) zdrojeTop.topPlynKotelTyp else "Ne"}</plyn_kotel>
                        <krb_KTP>${if (zdrojeTop.topKrb) zdrojeTop.topKrbTyp else "Ne"}</krb_KTP>
                        <jiny_zdroj>${if (zdrojeTop.topJiny) zdrojeTop.topKtery else "Ne"}</jiny_zdroj>
                    </zdrojeTop>
                    <tv>
                        <zasobnik>${system.zasobnikTyp} ${system.zasobnikObjem}</zasobnik>
                        <cirkulace>${if (system.cirkulace) "Ano" else "Ne"}</cirkulace>
                    </tv>
                    <zdrojeTV>
                        <topne_teleso>${if (zdrojeTv.tvTopneTeleso) zdrojeTv.tvTopneTelesoTyp else "Ne"}</topne_teleso>
                        <elektrokotel>${if (zdrojeTv.tvElektrokotel) "Ano" else "Ne"}</elektrokotel>
                        <plyn_kotel>${if (zdrojeTv.tvPlynKotel) "Ano" else "Ne"}</plyn_kotel>
                        <krb_KTP>${if (zdrojeTv.tvKrb) "Ano" else "Ne"}</krb_KTP>
                        <jiny_zdroj>${if (zdrojeTv.tvJiny) zdrojeTv.tvKtery else "Ne"}</jiny_zdroj>
                    </zdrojeTV>
                    <bazen>
                        <ohrev>${if (system.chciBazen) "Ano" else "Ne"}</ohrev>
                        <doba_vyuzivani>${if (system.chciBazen) bazen.doba else ""}</doba_vyuzivani>
                        <umisteni>${if (system.chciBazen) bazen.umisteni else ""}</umisteni>
                        <zakryti>${if (system.chciBazen) bazen.zakryti else ""}</zakryti>
                        <tvar>${if (system.chciBazen) bazen.tvar else ""}</tvar>
                        <sirka>${bazen.sirka}</sirka>
                        <delka>${bazen.delka}</delka>
                        <hloubka>${bazen.hloubka}</hloubka>
                        <prumer>${bazen.prumer}</prumer>
                        <teplota>${bazen.teplota}</teplota>
                        <voda>${if (system.chciBazen) bazen.druhVody else ""}</voda>
                    </bazen>
                    <poznamka>
                        <kontakty>${kontakty.poznamka}</kontakty>
                        <detail_objektu>${detailObjektu.poznamka}</detail_objektu>
                        <tv_tc_nadrz_a_os>${system.poznamka}</tv_tc_nadrz_a_os>
                        <bazen>${bazen.poznamka}</bazen>
                        <doplnkove_zdroje>${zdrojeTv.poznamka}</doplnkove_zdroje>
                    </poznamka>
                </xml>
            """.trimIndent()
        )

        MaterialAlertDialogBuilder(this).apply {

            setIcon(R.drawable.ic_baseline_send_24)
            setTitle(R.string.export_chcete_odeslat)
            setMessage(
                getString(R.string.export_opravdu_chcete_odeslat_na, when {
                    debugMode -> prefsPrihlaseni.getString("email", "")
                    Locale.getDefault().language == Locale("sk").language -> "obchod@regulus.sk"
                    else -> "poptavky@regulus.cz"
                })
            )
            setCancelable(true)

            setPositiveButton(getString(R.string.ano)) { dialog, _ ->
                sendEmail(kontakty.jmeno, kontakty.prijmeni, file)
                dialog.cancel()
            }

            setNegativeButton(getString(R.string.zrusit)) { dialog, _ ->
                dialog.cancel()
            }

            show()
        }
    }

    private fun sendEmail(jmeno: String, prijmeni: String, xml: File) {

        val dialog = MaterialAlertDialogBuilder (this).apply {
            setTitle(R.string.export_odesilani)
            setView(ProgressBar(context))
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
                object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(Credentials.EMAIL, Credentials.PASSWORD)
                    }
                })

            try {
                val prefsPrihlaseni = getSharedPreferences("PREFS_PRIHLASENI", Context.MODE_PRIVATE)

                MimeMessage(session).apply MimeMessage@ {

                    setFrom(InternetAddress(Credentials.EMAIL))

                    addRecipient(
                        Message.RecipientType.TO,
                        InternetAddress(when {
                            debugMode -> prefsPrihlaseni.getString("email", "")
                            Locale.getDefault().language == Locale("sk").language -> "obchod@regulus.sk"
                            else -> "poptavky@regulus.cz"
                        })
                    )
                    if (!debugMode) {
                        addRecipient(
                            Message.RecipientType.CC,
                            InternetAddress(prefsPrihlaseni.getString("email", ""))
                        )
                    }
                    subject = "REGULUS – Apka – OSOBA: $jmeno $prijmeni"


                    MimeMultipart().apply Multipart@ {

                        MimeBodyPart().apply {
                            setText("Prosím o přípravu nabídky. Děkuji.\n\n${prefsPrihlaseni.getString("jmeno", "Error")}" )
                            this@Multipart.addBodyPart(this)
                        }
                        MimeBodyPart().apply {
                            dataHandler = DataHandler(FileDataSource(xml))
                            fileName = xml.name
                            this@Multipart.addBodyPart(this)
                        }
                        repeat(prefs.getInt("fotky", 0)) { i ->
                            MimeBodyPart().apply {
                                attachFile(File(filesDir, "photo${i+1}.jpg"))
                                setHeader("Content-Type", "image/jpg; charset=UTF-8 name=\"fotka $i\"")
                                this@Multipart.addBodyPart(this)
                            }
                        }
                        this@MimeMessage.setContent(this)
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
                        val fragment = supportFragmentManager.findFragmentById(R.id.fragment)!!
                        supportFragmentManager.beginTransaction().remove(fragment).commit()
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

