package com.regulus.dotaznik

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_kontakty.*
import kotlinx.android.synthetic.main.navigation_header.*
import java.io.File
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart


class MainActivity : AppCompatActivity(), DrawerLayout.DrawerListener {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var appExecutors: AppExecutors
    private var debugMode = false

    fun recreateKontaktyFragment() {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment?
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


        val sharedPref = this.getSharedPreferences("PREFS_PRIHLASENI", Context.MODE_PRIVATE)

        val header = navigationView.getHeaderView(0)

        val tvNavJmeno = header.findViewById<TextView>(R.id.tvNavJmeno)
        val tvNavEmail = header.findViewById<TextView>(R.id.tvNavEmail)
        val tvNavIco = header.findViewById<TextView>(R.id.tvNavIco)
        val tvNavKod = header.findViewById<TextView>(R.id.tvNavKod)

        tvNavIco.visibility = if (sharedPref.getString("ico", "") != "") View.VISIBLE else View.GONE

        tvNavJmeno.text = getString(R.string.neco_mezera_neco, getString(R.string.prihlaseni_jmeno_prijmeni), sharedPref.getString("jmeno","Error"))
        tvNavEmail.text = getString(R.string.neco_mezera_neco, getString(R.string.prihlaseni_email), sharedPref.getString("email","Error"))
        tvNavIco.text = getString(R.string.neco_mezera_neco, getString(R.string.prihlaseni_ico), sharedPref.getString("ico", ""))
        tvNavKod.text = getString(R.string.neco_mezera_neco, getString(R.string.prihlaseni_kod), sharedPref.getString("kod","Error"))

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.getBooleanExtra("delete", false)) {

            val sharedPrefs = getSharedPreferences("PREFS_DOTAZNIK", Context.MODE_PRIVATE)
            val s = sharedPrefs.getInt("fotky", 0)

            val saver = Saver(this@MainActivity)

            saver.delete()

            repeat(s) { i ->
                val file = File(filesDir, "photo${i + 1}.jpg")

                file.delete()
            }

            sharedPrefs.edit().putInt("fotky", 0).apply()


        }

        appExecutors = AppExecutors()


        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.otevrit, R.string.zavrit)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment?
        val navController = navHostFragment!!.navController


        fabDalsi.visibility = View.GONE

        setDrawerOpen()

        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val menu2 = navigationView.menu
        var chci2 = false

        val t2 = Thread {
            val saver = Saver(this)
            val stranky = saver.get()




            chci2 = stranky.system.chciBazen
        }

        t2.start()
        t2.join()


        //Log.d("bazen", chci.toString())

        menu2.findItem(R.id.bazenFragment).isVisible = chci2

        navigationView.setupWithNavController(navController)

        val appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)

        setupActionBarWithNavController(navController, appBarConfiguration)

        drawer_layout.addDrawerListener(toggle)
        drawer_layout.addDrawerListener(this)

        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        /*toggle.setToolbarNavigationClickListener {
            if (drawer_layout.isDrawerOpen(drawer_layout)) {
                drawer_layout.closeDrawer(drawer_layout)
            }
        }*/


        val sharedPref = getSharedPreferences("PREFS_PRIHLASENI", Context.MODE_PRIVATE)


        if (!sharedPref.getBoolean("prihlasen", false)) {

            val intent = Intent(this, PrihlaseniActivity::class.java)
            startActivity(intent)

        }

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.actionOdeslat -> {

                    odeslat()

                    return@setNavigationItemSelectedListener true

                }
                R.id.actionOdstranit -> {
                    val builder2 = AlertDialog.Builder(this).apply {

                        setMessage(getString(R.string.export_opravdu_odstranit_data))
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
                    }

                    val alertDialog = builder2.create()

                    alertDialog.show()

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

            val fragment =
                supportFragmentManager.findFragmentById(R.id.fragment)?.childFragmentManager?.fragments!![0]

            if (fragment is ZdrojeFragment) {
                debugMode = true
                odeslat()
            }

            return@setOnLongClickListener true
        }

        fabDalsi.setOnClickListener {
            debugMode = false
            var chci = false

            val t = Thread {
                val saver = Saver(this)
                val stranky = saver.get()

                chci = stranky.system.chciBazen
            }

            //Log.d("bazen", chci.toString())

            t.start()
            t.join()


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
            var chci = false

            val t = Thread {
                val saver = Saver(this)
                val stranky = saver.get()

                chci = stranky.system.chciBazen

            }

            //Log.d("bazen", chci.toString())

            t.start()
            t.join()










            val fragment =
                supportFragmentManager.findFragmentById(R.id.fragment)?.childFragmentManager?.fragments!![0]


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

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
    override fun onDrawerOpened(drawerView: View) {
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val menu2 = navigationView.menu
        var chci = false

        val t = Thread {
            val saver = Saver(this)
            val stranky = saver.get()


            chci = stranky.system.chciBazen
        }

        t.start()
        t.join()


        //Log.d("bazen", chci.toString())

        menu2.findItem(R.id.bazenFragment).isVisible = chci
    }

    override fun onDrawerClosed(drawerView: View) {

        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragment)!!.childFragmentManager.fragments[0]

        fabZpet.visibility = if (fragment is KontaktyFragment) View.GONE else View.VISIBLE
        fabDalsi.visibility = View.VISIBLE


        if (fragment is ZdrojeFragment)
            fabDalsi.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_send_24))
        else
            fabDalsi.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.next))
    }

    override fun onDrawerStateChanged(newState: Int) {

        toggle.syncState()
    }

    private fun odeslat() {

        lateinit var kontakty: Stranky.Kontakty
        lateinit var detailObjektu: Stranky.DetailObjektu
        lateinit var system: Stranky.System_
        lateinit var bazen: Stranky.Bazen
        lateinit var zdrojeTop: Stranky.ZdrojeTop
        lateinit var zdrojeTv: Stranky.ZdrojeTv


        //database stuff
        val saver = Saver(this)
        val stranky = saver.get()
        //easier access
        kontakty = stranky.kontakty
        detailObjektu = stranky.detailObjektu
        system = stranky.system
        bazen = stranky.bazen
        zdrojeTop = stranky.zdrojeTop
        zdrojeTv = stranky.zdrojeTv



        if (kontakty.jmeno == "") {
            Toast.makeText(this, R.string.je_potreba_zadat_jmeno_a_prijmeni, Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (kontakty.prijmeni == "") {
            Toast.makeText(this, R.string.je_potreba_zadat_jmeno_a_prijmeni, Toast.LENGTH_SHORT)
                .show()
            return
        }


        //try {
        var file = File(this.cacheDir, "dotaznik_app.xml")
        file.delete()
        //}

        File.createTempFile("dotaznik_app.xml", null, this.cacheDir)

        //file
        file = File(this.cacheDir, "dotaznik_app.xml")


        val sharedPref = this.getSharedPreferences("PREFS_PRIHLASENI", Context.MODE_PRIVATE)

        Log.i("Stránky", stranky.toString())

        file.writeText(
            """
                <?xml version="1.0" encoding="utf-8"?>
                <?xml-stylesheet type="text/xsl" href="dotaznik_app.xsl"?>
                
                <!-- Tento soubor byl vygenerován automaticky aplikací Regulus Dotazník; verze: 2.0.2 -->
                
                <xml>
                    <system>
                        <resi_tc>Ano</resi_tc>
                        <cislo_ko>${sharedPref.getString("kod", "Error")}</cislo_ko>
                        <odesilatel>${sharedPref.getString("email", "Error")}</odesilatel>
                        <odberatel_ico>${sharedPref.getString("ico", "")}</odberatel_ico>
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


        val builder2 = AlertDialog.Builder(this).apply {

            setMessage(getString(R.string.export_chcete_odeslat))
            setTitle(
                getString(R.string.export_opravdu_chcete_odeslat_na, when {
                    debugMode -> sharedPref.getString("email", "")
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


        }

        val alertDialog: AlertDialog = builder2.create()

        alertDialog.show()



    }

    private fun sendEmail(jmeno: String, prijmeni: String, xml: File) {

        val dialog = ProgressDialog(this@MainActivity).apply {
            setTitle(R.string.export_odesilani)
            setMessage(getString(R.string.export_email_se_odesila))
        }
        dialog.show()


        appExecutors.diskIO().execute {


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
                val mm = MimeMessage(session)


                val sharedPref =
                    this.getSharedPreferences("PREFS_PRIHLASENI", Context.MODE_PRIVATE)



                mm.setFrom(InternetAddress(Credentials.EMAIL))


                mm.addRecipient(
                    Message.RecipientType.TO,
                    InternetAddress(
                        when {
                            debugMode -> sharedPref.getString("email", "")
                            Locale.getDefault().language == Locale("sk").language -> "obchod@regulus.sk"
                            else -> "poptavky@regulus.cz"
                        }
                    )
                )

                if (!debugMode)
                mm.addRecipient(
                    Message.RecipientType.CC,
                    InternetAddress(
                        sharedPref.getString("email", "")
                    )
                )


                mm.subject = "REGULUS – Apka – OSOBA: $jmeno $prijmeni"


                val multipart: Multipart = MimeMultipart()


                val telo = MimeBodyPart()

                telo.setText("Prosím o přípravu nabídky. Děkuji.\n\n${sharedPref.getString("jmeno", "Error")}" )

                multipart.addBodyPart(telo)


                val pripona1 = MimeBodyPart()

                val source = FileDataSource(xml)
                pripona1.dataHandler = DataHandler(source)
                pripona1.fileName = xml.name

                multipart.addBodyPart(pripona1)


                val sharedPrefs = getSharedPreferences("PREFS_DOTAZNIK", Context.MODE_PRIVATE)

                val s = sharedPrefs.getInt("fotky", 0)

                repeat(s) { i ->
                    val pripona = MimeBodyPart()

                    val file = File(filesDir, "photo${i+1}.jpg")

                    pripona.attachFile(file)
                    pripona.setHeader("Content-Type", "image/jpg; charset=UTF-8 name=\"fotka $i\"")

                    multipart.addBodyPart(pripona)
                }



                mm.setContent(multipart)



                Transport.send(mm)


                dialog.dismiss()


                val builder2 = AlertDialog.Builder(this).apply {

                    setMessage(getString(R.string.export_opravdu_odstranit_data))
                    setTitle(getString(R.string.export_email_uspesne_odeslan))
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
                }

                runOnUiThread {
                    val alertDialog: AlertDialog = builder2.create()

                    alertDialog.show()
                }

                Log.i("email", "odeslano")


            } catch (e: MessagingException) {
                e.printStackTrace()

                Log.e("email", "CHYBA", e)

                dialog.dismiss()


                val builder2 = with(e.toString()) {
                    when {
                        contains("Couldn't connect to host") -> {
                            AlertDialog.Builder(this@MainActivity).apply {

                                setMessage(getString(R.string.export_nejste_pripojeni))
                                setTitle(getString(R.string.export_email_neodeslan, ""))
                                setCancelable(false)
                                setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                                    dialog.cancel()
                                }
                                setNeutralButton(getString(R.string.podrobnejsi_info)) { dialog, _ ->
                                    dialog.cancel()


                                    val builder3 = AlertDialog.Builder(this@MainActivity).apply {

                                        setMessage(e.toString())
                                        setTitle(getString(R.string.export_email_neodeslan, getString(R.string.toto_je_chyba)))
                                        setCancelable(false)
                                        setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                                            dialog.cancel()
                                        }
                                    }


                                    runOnUiThread {
                                        val alertDialog: AlertDialog = builder3.create()

                                        alertDialog.show()

                                    }
                                }
                            }
                        }
                        else -> {
                            AlertDialog.Builder(this@MainActivity).apply {

                                setMessage(e.toString())
                                setTitle(getString(R.string.export_email_neodeslan, getString(R.string.toto_je_chyba)))
                                setCancelable(false)
                                setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                                    dialog.cancel()
                                }
                            }
                        }
                    }
                }

                runOnUiThread {
                    val alertDialog: AlertDialog = builder2.create()

                    alertDialog.show()

                }
            }
        }
    }


}

