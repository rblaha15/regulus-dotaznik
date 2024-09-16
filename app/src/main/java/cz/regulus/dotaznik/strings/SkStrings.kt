package cz.regulus.dotaznik.strings

val SkStrings = object : Strings {
    override val appName = "Regulus dotazník"
    override val contacts = object : ContactsStrings {
        override val contacts = "Kontakty a miesto realizácie"
        override val surname = "Priezvisko"
        override val name = "Meno"
        override val street = "Ulica, č. p."
        override val city = "Mesto"
        override val zip = "PSČ"
        override val phone = "Telefón"
        override val email = "Email"
        override val chooseAssemblyCompanyHere = "Vyberte montážnu firmu:"
        override val chooseCompany = "IČO monážní firmy"
        override val chooseAssemblyCompany = "Vybrat firmu"
        override val searchCompany = "Vyberte montážnu firmu"
        override val crn = "Vyhledat firmu"
        override val demandOrigin = "Pôvod dopytu"
        override val originQuestionEmail = "Otázka poslaná emailom od koncového zákazníka"
        override val originQuestionExhibition = "Otázka z výstavy"
        override val originQuestionInPerson = "Otázka pri osobnej návšteve u koncového zákazníka"
        override val originDistributionCompany = "Dopyt od distribučnej firmy"
        override val originAssemlbleres = "Dopyt od montážnikov"
        override val originDesigner = "Dopyt od projektanta"
    }
    override val objectDetail = object : ObjectDetailStrings {
        override val objectDetail = "Detail objektu"
        override val heatLoss = "Tepelná strata"
        override val heatNeedsForHeating = "Potreba tepla – kúrenie"
        override val heatNeedsForHotWater = "Potreba tepla – teplá voda"
        override val area = "Vykurovaná plocha"
        override val volume = "Vykurovaný objem"
        override val costs = "Náklady na kúrenie"
        override val type = "Druh paliva"
        override val usage = "Spotreba paliva"
        override val type2 = "Druh 2. paliva"
        override val usage2 = "Spotreba 2. paliva"
    }
    override val system = object : SystemStrings {
        override val system = "Systém"
        override val hPType = "Typ tepelného čerpadla"
        override val hPModel = "Model tepelného čerpadla"
        override val storeType = "Typ nádrže"
        override val storeVolume = "Objem nádrže"
        override val tankType = "Typ zásobníka"
        override val tankVolume = "Objem zásobníka"
        override val indoorUnitType = "Typ vnútornej jednotky"
        override val heatingSystem = "vykurovací systém"
        override val heatingSystem1circuit = "1 vykurovací okruh"
        override val heatingSystem2circuits = "2 vykurovacie okruhy"
        override val heatingSystem3circuits = "3 vykurovacie okruhy"
        override val heatingSystemInvertor = "Invertor na priamo"
        override val hotWaterCirculation = "Cirkulácia teplej vody"
        override val poolHeating = "Ohrev bazéna"
        override val airToWater = "vzduch/voda"
        override val groundToWater = "zem/voda"
    }
    override val pool = object : PoolStrings {
        override val pool = "Bazén"
        override val usagePeriod = "Doba využívania"
        override val periodYearlong = "celoročne"
        override val periodSeasonal = "sezónne"
        override val location = "Umiestnenie"
        override val locationOutdoor = "vonkajší"
        override val locationIndoor = "vnútorný"
        override val waterType = "Druh vody"
        override val freshType = "sladká"
        override val saltType = "slaná"
        override val shape = "tvar"
        override val shapeRectangle = "obdĺžnikový"
        override val shapeOval = "oválný"
        override val shapeCircle = "kruhový"
        override val length = "Dĺžka"
        override val width = "Šírka"
        override val radius = "Priemer"
        override val depth = "Hĺbka"
        override val coverage = "Zakrytie"
        override val covaregeSolid = "pevná strecha"
        override val coveragePolycarbonate = "polykarbonát"
        override val temperature = "požadovaná teplota"
    }
    override val additionalSources = object : AdditionalSourcesStrings {
        override val sources = "Doplnkové zdroje"
        override val hotWater = "Teplá voda"
        override val heating = "Kúrenie"
        override val heatingElement = "Vykurovacie teleso v nádrži"
        override val electricBoiler = "Elektrokotol"
        override val gasBoiler = "Plynový kotol"
        override val fireplace = "Krb alebo kotol na tuhé paliva"
        override val newNeuter = "nové"
        override val newMasculine = "nový"
        override val existing = "existujúce"
        override val toSocket = "do zásuvky"
        override val fromRegulation = "ovládané z regulácie"

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
        override val logIn = "Prihlásenie"
        override val internetNeeded = "Pre prihlásenie je potrebné pripojenie k internetu!"
        override val iAmEmploee = "Som zamestanec Regulusu"
        override val iAmNotEmploee = "Nie som zamestanec Regulusu"
        override val yourRepresentative = "Váš obchodný zástupca"
        override val yourName = "Vaše meno"
        override val yourSurname = "Vaše priezvisko"
        override val yourCrn = "Vaše IČO (nepovinné)"
        override val yourEmail = "Váš email"
        override val chooseYourself = "Vyber sa:"
        override val selectedFullName = { jmeno: String, prijmeni: String -> "Meno a priezvisko: $jmeno $prijmeni" }
        override val selectedEmail = { email: String -> "Email: $email" }
        override val selectedCode = { kod: String -> "Číslo KO: $kod" }
        override val selectedCrn = { ico: String -> "IČO: $ico" }
        override val nameAndSurname = "Meno a priezvisko:"
        override val email = "Email:"
        override val code = "Číslo KO:"
        override val crn = "IČO:"
        override val addMoreInfo = "Doplňte, prosím, ešte nejaké informácie o Vás:"
        override val representativeNeeded = "Je potrebné vybrať obchodného zástupcu"
        override val emailNeeded = "Je potrebné vyplniť email"
        override val surnameNeeded = "Je potrebné vyplniť priezvisko"
        override val nameNeeded = "Je potrebné vyplniť meno"
        override val youAreNotLoggedIn = "Nie ste prihlásený!"
        override val logOut = "Odhlásiť sa"
    }
    override val photos = object : PhotosStrings {
        override val photosManager = "Správa fotiek"
        override val takePhoto = "Odfotiť"
        override val choosePhoto = "Vybrať z galérie"
        override val photo = "Fotka"
        override val add = "Pridať fotku"
        override val maxPhotosReached = "Môžete pridať maximálne 5 fotiek"
        override val maxPhotosOvershoot = "Pretože môžete pridať maximálne 5 fotiek, neboli všetky vybrané fotky vložené"
        override val noPhotos = "Zatiaľ ste nepridali žiadne fotky"
        override val remove = "Odstrániť"
    }
    override val export = object : ExportStrings {
        override val sending = "Odosielanie"
        override val missingField = "Nevyplnené pole"
        override val pleaseFillInField = { field: String -> "Pre odoslanie prosím vyplňte $field." }
        override val emailIsBeingSend = "Email sa odosiela"
        override val emailSuccessfullySent = "Email bol úspešne odoslaný!"
        override val doYouWantToSend = "Odoslať?"
        override val doYouReallyWantToSend = { email: String -> "Naozaj chcete odoslať email na \"$email\"?" }
        override val emailNotSent = { chyba: String -> "Ospravedlňujem sa, ale email sa nepodarilo odoslať. $chyba" }
        override val doYouRellyWantToRemoveData = "Chcete odstrániť všetky dáta?"
        override val youAreOffline = "Pravdepodobne nie ste pripojení k internetu. Skontrolujte pripojenie a skúste to znovu"
        override val errorReported = "Chyba bola nahlásená."
        override val send = "Odoslať"
        override val nameAndSurnameNeeded = "Je potrebné zadať aspoň meno a priezvisko"
        override val removeAll = "Odstrániť všetko"
        override val thisIsTheIssue = "Toto je chyba:"
        override val moreInfo = "Podrobnejšie informácie"
    }
    override val note = "Poznámka"
    override val open = "Otvoriť menu"
    override val close = "Zavrieť menu"
    override val ok = "OK"
    override val yes = "Áno"
    override val no = "Nie"
    override val cancel = "Zrušiť"
    override val noneFeminine = "Žiadna"
    override val noneMasculine = "Žiadny"
    override val noneNeuter = "Žiadne"
    override val choose = "Vyberte"
    override val otherNeuter = "Iné"
    override val otherMasculine = "Iný"
    override val currency = "€"
    override val internetConnectionNeeded = "Je potrebné pripojenie k internetu"
    override val back = "Späť"
    override val chooseUnits = "Vybrať jednotky"
    override val remove = "Odobrať"
    override val add = "Pridať"
}