package cz.regulus.dotaznik.strings

val CsStrings = object : Strings {
    override val appName = "Regulus dotazník"
    override val contacts = object : ContactsStrings {
        override val contacts = "Kontakty a místo realizace"
        override val surname = "Příjmení"
        override val name = "Jméno"
        override val street = "Ulice, č. p."
        override val city = "Město"
        override val zip = "PSČ"
        override val phone = "Telefon"
        override val email = "Email"
        override val chooseAssemblyCompanyHere = "Vyberte montážní firmu:"
        override val chooseCompany = "Vybrat firmu"
        override val chooseAssemblyCompany = "Vyberte montážní firmu"
        override val searchCompany = "Vyhledat firmu"
        override val crn = "IČO monážní firmy"
        override val demandOrigin = "Původ poptávky"
        override val originQuestionEmail = "Dotaz poslaný emailem od koncového zákazníka"
        override val originQuestionExhibition = "Dotaz z výstavy"
        override val originQuestionInPerson = "Dotaz při osobní návštěvě u koncového zákazníka"
        override val originDistributionCompany = "Poptávka od distribuční firmy"
        override val originAssemlbleres = "Poptávka od montážníků"
        override val originDesigner = "Poptávka od projektanta"
        override val demandSubject = "Předmět poptávky"
        override val heatPump = "Tepelné čerpadlo"
        override val fve = "FVE"
    }
    override val objectDetail = object : ObjectDetailStrings {
        override val objectDetail = "Detail objektu"
        override val heatLoss = "Tepelná ztráta"
        override val heatNeedsForHeating = "Potřeba tepla – vytápění"
        override val heatNeedsForHotWater = "Potřeba tepla – teplá voda"
        override val area = "Vytápěná plocha"
        override val volume = "Vytápěný objem"
        override val costs = "Náklady na vytápění"
        override val type = "Druh paliva"
        override val usage = "Spotřeba paliva"
        override val type2 = "Druh 2. paliva"
        override val usage2 = "Spotřeba 2. paliva"
    }
    override val fve = object : FVEStrings {
        override val fve = "FVE"
        override val currentSituation = "Současný stav"
        override val currentHeating = "Současný zdroj vytápění"
        override val currentHotWater = "Současný zdroj přípravy teplé vody"
        override val currentTanks = "Instalované nádrže/zásobníky"
        override val currentConsumption = "Aktuální roční spotřeba el. energie"
        override val breakerSize = "Velikost hlavního jističe"
        override val tariff = "Sazba"
        override val breakerBoxLocation = "Umístění domovního rozvaděče"
        override val requirements = "Požadavky na FVE"
        override val requiredPower = "Požadovaný výkon"
        override val locationBuidingType = "Typ budovy pro instalaci kolektorů"
        override val lightningRod = "Na střeše pro instalaci je umístěn hromosvod"
        override val familyHouseEtc = "Rodinný dům, stodola, pergola, …"
        override val avaiableAreas = "Dostupné plochy pro instalaci"
        override val roofMaterial = "Materiál střesní krytiny"
        override val roofAge = "Stáří střešní krytiny"
        override val tile = "Taška"
        override val tileType = "Typ tašek"
        override val metalSheetFolded = "Plech falcovaný"
        override val metalSheetTrapezoidal = "Plech trapézový"
        override val foil = "Fólie"
        override val asphaltShingle = "Asfaltový šindel"
        override val useOptimizers = "Použít optimizéry"
        override val size = { i: Int -> "Rozměr $i. plochy" }
        override val orientation = { i: Int -> "Orientace $i. plochy" }
        override val slope = { i: Int -> "Sklon $i. plochy" }
        override val battery = "Akumulace do baterií"
        override val batteryCapacity = "Kapacita baterií"
        override val water = "Akumulace do vody"
        override val network = "Přetoky do sítě"
        override val networkPower = "Přetoky do sítě – rezervovaný výkon"
        override val charging = "Dobíjecí stanice – wallbox"
    }
    override val system = object : SystemStrings {
        override val system = "Systém"
        override val hPType = "Typ tepelného čerpadla"
        override val hPModel = "Model tepelného čerpadla"
        override val storeType = "Typ nádrže"
        override val storeVolume = "Objem nádrže"
        override val tankType = "Typ zásobníku"
        override val tankVolume = "Objem zásobníku"
        override val indoorUnitType = "Typ vnitřní jednotky"
        override val heatingSystem = "Otopný systém"
        override val heatingSystem1circuit = "1 otopný okruh"
        override val heatingSystem2circuits = "2 otopné okruhy"
        override val heatingSystem3circuits = "3 otopné okruhy"
        override val heatingSystemInvertor = "Invertor na přímo"
        override val hotWaterCirculation = "Cirkulace teplé vody"
        override val poolHeating = "Ohřev bazénu"
        override val airToWater = "vzduch/voda"
        override val groundToWater = "země/voda"
    }
    override val pool = object : PoolStrings {
        override val pool = "Bazén"
        override val usagePeriod = "Doba využívání"
        override val periodYearlong = "celeroční"
        override val periodSeasonal = "sezónní"
        override val location = "Umístění"
        override val locationOutdoor = "venkovní"
        override val locationIndoor = "vnitřní"
        override val waterType = "Druh vody"
        override val freshType = "sladká"
        override val saltType = "slaná"
        override val shape = "Tvar"
        override val shapeRectangle = "obdélníkový"
        override val shapeOval = "oválný"
        override val shapeCircle = "kruhový"
        override val length = "Délka"
        override val width = "Šířka"
        override val radius = "Průměr"
        override val depth = "Hloubka"
        override val coverage = "Zakrytí"
        override val covaregeSolid = "pevná střecha"
        override val coveragePolycarbonate = "polykarbonát"
        override val temperature = "Požadovaná teplota"
    }
    override val additionalSources = object : AdditionalSourcesStrings {
        override val sources = "Doplňkové zdroje"
        override val hotWater = "Teplá voda"
        override val heating = "Topení"
        override val heatingElement = "Topné těleso v nádrži"
        override val electricBoiler = "Elektrokotel"
        override val gasBoiler = "Plynový kotel"
        override val fireplace = "Krb nebo kotel na tuhá paliva"
        override val newNeuter = "nové"
        override val newMasculine = "nový"
        override val existing = "stávající"
        override val toSocket = "do zásuvky"
        override val fromRegulation = "ovládané z regulace"

    }
    override val accessories = object : AccessoriesStrings {
        override val accessories = "Příslušenství"
        override val hose = "Hadice"
        override val wallSupportBracket = "Držák pro TČ"
        override val roomUnitsAndSensors = "Pokojové jednotky a čidla"
        override val heatingCable = "Topný kabel"
        override val onWall = "Na stěnu"
        override val onIsolatedWall = "Na izolovanou stěnu"
    }
    override val logIn = object : LogInStrings {
        override val logIn = "Přihlášení"
        override val internetNeeded = "Pro přihlášení je potřeba připojení k internetu!"
        override val iAmEmploee = "Jsem zaměstanec Regulusu"
        override val iAmNotEmploee = "Nejsem zaměstanec Regulusu"
        override val yourRepresentative = "Váš obchodní zástupce"
        override val yourName = "Vaše jméno"
        override val yourSurname = "Vaše příjmení"
        override val yourCrn = "Vaše IČO (nepovinné)"
        override val yourEmail = "Váš email"
        override val chooseYourself = "Vyber se:"
        override val selectedFullName = { jmeno: String, prijmeni: String -> "Jméno a příjmení: $jmeno $prijmeni" }
        override val selectedEmail = { email: String -> "Email: $email" }
        override val selectedCode = { kod: String -> "Číslo KO: $kod" }
        override val selectedCrn = { ico: String -> "IČO: $ico" }
        override val nameAndSurname = "Jméno a příjmení:"
        override val email = "Email:"
        override val code = "Číslo KO:"
        override val crn = "IČO:"
        override val addMoreInfo = "Doplntě, prosím, ještě nějaké informace o Vás:"
        override val youAreNotLoggedIn = "Nejste přihlášeni!"
        override val logOut = "Odhlásit se"
        override val representativeNeeded = "Je potřeba vybrat obchodního zástupce"
        override val emailNeeded = "Je potřeba vyplnit email"
        override val surnameNeeded = "Je potřeba vyplnit příjmení"
        override val nameNeeded = "Je potřeba vypllnit jméno"
    }
    override val photos = object : PhotosStrings {
        override val photosManager = "Správa fotek"
        override val takePhoto = "Vyfotit"
        override val choosePhoto = "Vybrat z galerie"
        override val photo = "Fotka"
        override val add = "Přidat fotku"
        override val maxPhotosReached = "Můžete přidat maximálně 5 fotek"
        override val maxPhotosOvershoot = "Protože můžete přidat maximálně 5 fotek, nebyly všechny vybrané fotky vloženy"
        override val noPhotos = "Zatím jste nepřidali žádné fotky"
        override val remove = "Odstranit"
    }
    override val export = object : ExportStrings {
        override val sending = "Odesílání"
        override val emailIsBeingSend = "Email se odesílá"
        override val emailSuccessfullySent = "Email byl úspěšně odeslán!"
        override val doYouWantToSend = "Odeslat?"
        override val missingField = "Nevyplněné pole"
        override val pleaseFillInField = { field: String -> "Pro odeslání prosím vyplňte $field." }
        override val doYouReallyWantToSend = { email: String -> "Opravdu chcete odeslat email na \"$email\"?" }
        override val emailNotSent = { chyba: String -> "Omlouvám se, ale email se nepodařilo odeslat. $chyba" }
        override val doYouRellyWantToRemoveData = "Chcete odstranit všechna data?"
        override val youAreOffline = "Pravděpodobně nejste připojeni k internetu. Zkontrolujte připojení a zkuste to znovu"
        override val errorReported = "Chyba byla nahlášena."
        override val send = "Odeslat"
        override val nameAndSurnameNeeded = "Je potřeba zadat alespoň jméno a příjmení"
        override val removeAll = "Odstranit vše"
        override val thisIsTheIssue = "Toto je chyba:"
        override val moreInfo = "Podrobnější informace"
    }
    override val note = "Poznámka"
    override val open = "Otevřít menu"
    override val close = "Zavřít menu"
    override val ok = "OK"
    override val yes = "Ano"
    override val no = "Ne"
    override val cancel = "Zrušit"
    override val noneFeminine = "Žádná"
    override val noneMasculine = "Žádný"
    override val noneNeuter = "Žádné"
    override val choose = "Vyberte"
    override val otherNeuter = "Jiné"
    override val otherMasculine = "Jiný"
    override val currency = "Kč"
    override val internetConnectionNeeded = "Je potřeba připojení k internetu"
    override val back = "Zpět"
    override val chooseUnits = "Vybrat jednotky"
    override val remove = "Odebrat"
    override val add = "Přidat"
}