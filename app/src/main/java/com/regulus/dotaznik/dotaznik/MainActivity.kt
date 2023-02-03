package com.regulus.dotaznik.dotaznik

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
import com.regulus.dotaznik.*
import com.regulus.dotaznik.databinding.ActivityMainBinding
import com.regulus.dotaznik.prihlaseni.PrihlaseniActivity
import com.regulus.dotaznik.spravaFotek.FotkyActivity
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
        setContentView(view)

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

        binding.viewPager.adapter = ViewPagerAdapter(this)

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        binding.viewPager.addItemDecoration(dividerItemDecoration)

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

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)

                binding.navigationView.setCheckedItem(
                    listOf(
                        R.id.kontaktyFragment,
                        R.id.detailObjektuFragment,
                        R.id.systemFragment,
                        R.id.bazenFragment,
                        R.id.zdrojeFragment,
                        R.id.prislusenstviFragment,
                    )[position]
                )

                title = binding.navigationView.checkedItem!!.title
            }
        })

        binding.navigationView.setNavigationItemSelectedListener { item -> when (item.itemId) {
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

                binding.viewPager.currentItem = listOf(
                    R.id.kontaktyFragment,
                    R.id.detailObjektuFragment,
                    R.id.systemFragment,
                    R.id.bazenFragment,
                    R.id.zdrojeFragment,
                    R.id.prislusenstviFragment,
                ).indexOf(item.itemId)

                binding.drawerLayout.closeDrawers()

                title = binding.navigationView.checkedItem!!.title

                true
            }
        }
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
        val detailObjektu = stranky.detailObjektu
        val system = stranky.system
        val bazen = stranky.bazen
        val zdrojeTop = stranky.zdrojeTop
        val zdrojeTv = stranky.zdrojeTv
        val prislusenstvi = stranky.prislusenstvi


        if (kontakty.jmeno == "" || kontakty.prijmeni == "") {
            Toast.makeText(this, R.string.je_potreba_zadat_jmeno_a_prijmeni, Toast.LENGTH_SHORT).show()
            return
        }

        // odstranit stary soubor
        File(cacheDir, "dotaznik_app.xml").delete()

        // vytvorit novy soubor
        File.createTempFile("dotaznik_app.xml", null, cacheDir)

        val file = File(this.cacheDir, "dotaznik_app.xml")

        file.writeText(
            """
                <?xml version="1.0" encoding="utf-8"?>
                <?xml-stylesheet type="text/xsl" href="dotaznik_app.xsl"?>
                
                <!-- Tento soubor byl vygenerován automaticky aplikací Regulus Dotazník; verze: 2.1 -->
                
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
                        <topne_teleso>${if (zdrojeTop.topneTeleso) zdrojeTop.topneTelesoTyp else "Ne"}</topne_teleso>
                        <elektrokotel>${if (zdrojeTop.elektrokotel) zdrojeTop.elektrokotelTyp else "Ne"}</elektrokotel>
                        <plyn_kotel>${if (zdrojeTop.plynKotel) zdrojeTop.plynKotelTyp else "Ne"}</plyn_kotel>
                        <krb_KTP>${if (zdrojeTop.krb) zdrojeTop.krbTyp else "Ne"}</krb_KTP>
                        <jiny_zdroj>${if (zdrojeTop.jiny) zdrojeTop.ktery else "Ne"}</jiny_zdroj>
                    </zdrojeTop>
                    <tv>
                        <zasobnik>${system.zasobnikTyp} ${system.zasobnikObjem}</zasobnik>
                        <cirkulace>${if (system.cirkulace) "Ano" else "Ne"}</cirkulace>
                    </tv>
                    <zdrojeTV>
                        <topne_teleso>${if (zdrojeTv.topneTeleso) zdrojeTv.topneTelesoTyp else "Ne"}</topne_teleso>
                        <elektrokotel>${if (zdrojeTv.elektrokotel) "Ano" else "Ne"}</elektrokotel>
                        <plyn_kotel>${if (zdrojeTv.plynKotel) "Ano" else "Ne"}</plyn_kotel>
                        <krb_KTP>${if (zdrojeTv.krb) "Ano" else "Ne"}</krb_KTP>
                        <jiny_zdroj>${if (zdrojeTv.jiny) zdrojeTv.ktery else "Ne"}</jiny_zdroj>
                    </zdrojeTV>
                    <bazen>
                        <ohrev>${if (bazen.chciBazen) "Ano" else "Ne"}</ohrev>
                        <doba_vyuzivani>${if (bazen.chciBazen) bazen.doba else ""}</doba_vyuzivani>
                        <umisteni>${if (bazen.chciBazen) bazen.umisteni else ""}</umisteni>
                        <zakryti>${if (bazen.chciBazen) bazen.zakryti else ""}</zakryti>
                        <tvar>${if (bazen.chciBazen) bazen.tvar else ""}</tvar>
                        <sirka>${bazen.sirka}</sirka>
                        <delka>${bazen.delka}</delka>
                        <hloubka>${bazen.hloubka}</hloubka>
                        <prumer>${bazen.prumer}</prumer>
                        <teplota>${bazen.teplota}</teplota>
                        <voda>${if (bazen.chciBazen) bazen.druhVody else ""}</voda>
                    </bazen>
                    <prislusenstvi>
                        <hadice>${if (prislusenstvi.hadice) prislusenstvi.hadiceTyp else "Ne"}</hadice>
                        <topny_kabel>${if (prislusenstvi.topnyKabel) prislusenstvi.topnyKabelTyp else "Ne"}</topny_kabel>
                        <drzak_na_tc>${if (prislusenstvi.drzakNaStenu) prislusenstvi.drzakNaStenuTyp else "Ne"}</drzak_na_tc>
                        <pokojova_jednotka>${if (prislusenstvi.pokojovaJednotka) prislusenstvi.pokojovaJednotkaTyp else "Ne"}</pokojova_jednotka>
                        <pokojove_cidlo>${if (prislusenstvi.pokojoveCidlo) prislusenstvi.pokojoveCidloTyp else "Ne"}</pokojove_cidlo>
                    </prislusenstvi>
                    <poznamka>
                        <kontakty>${kontakty.poznamka}</kontakty>
                        <detail_objektu>${detailObjektu.poznamka}</detail_objektu>
                        <tv_tc_nadrz_a_os>${system.poznamka}</tv_tc_nadrz_a_os>
                        <bazen>${bazen.poznamka}</bazen>
                        <doplnkove_zdroje>${zdrojeTv.poznamka}</doplnkove_zdroje>
                        <prislusenstvi>${prislusenstvi.poznamka}</prislusenstvi>
                    </poznamka>
                </xml>
            """.trimIndent()
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
                sendEmail(kontakty.jmeno, kontakty.prijmeni, file)
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
