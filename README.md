# Connect4-MI Alkalmazás Dokumentáció

## 1. Általános bemutató
A "java-con4-ai" egy klasszikus Connect4 konzolos játék, amely Java 21 LTS környezetre épült. A cél, hogy a játékosok 4 azonos színű korongot helyezzenek el egymás mellett horizontálisan, vertikálisan vagy diagonálisan.

**Főbb funkciók és jellemzők:**
* **Játékmódok:** Játszható két élő játékos, vagy egy élő játékos és a Számítógép ("Sz.Gép") között.
* **Mesterséges Intelligencia:** A gépi ellenfél 3 nehézségi szinttel rendelkezik (1: Kezdő/Random, 2: Haladó/1 lépés előre, 3: Profi/2 lépés előre), melyet látványos számolási animáció kísér.
* **Állapotmentés és betöltés:** A játékállás bármikor kimenthető egy `cn4-ment.json` fájlba az "S" vagy a "Mentés" parancs megadásával. A mentés kiterjed a tábla állapotára, a játékosok adataira, a gépi ellenfél nehézségi szintjére, valamint az utolsó lépés vizuális kiemelésének koordinátáira is.
* **Győzelmi rangsor:** A nyertes mérkőzések adatai egy `cn4-gyoz.csv` fájlban gyűlnek, amiből a főmenüben rangsor generálható.
* **Vizuális élmény:** A terminálos megjelenítést a `JColor` könyvtár teszi színessé (sárga és piros korongok, győztes vonal fehér hátterű kiemelése, az utolsó lépés ciánkék hátterű jelzése).

## 2. Kód értelmezés (Technikai, Logikai és Szemantikai)
A projekt tiszta, objektumorientált alapelvekre épül, a változók és metódusok a Pascal-case konvenciót követik a könnyű olvashatóság érdekében. A kód minden eleme JavaDoc megjegyzésekkel van ellátva.

* **`Main.java`**: A belépési pont. Kizárólag a parancssori kapcsolókat értelmezi és átadja a paramétereket a játékmotornak.
* **`GameEngine.java`**: A központi vezérlő. Tartalmazza a főmenüt, a játékhurkot, a beviteli mezők kezelését, valamint a gépi játékos (AI) döntéshozó logikáját és számolási animációját.
* **`Board.java`**: A játéktér adatmodellje és vizualizációs felelőse. Itt történik a lépések érvényesítése, a győzelmi feltételek ellenőrzése, és a konzolos tábla pontos, elcsúszásmentes kirajzolása a megfelelő színkiemelésekkel.
* **`FileManager.java`**: Felelős a külső állományokkal való kommunikációért a `Gson` könyvtár segítségével, biztosítva a mentési adatok (AI szint, kiemelés koordináták) írását és olvasását.
* **`Player.java`**: A játékosok tulajdonságait (név, szín, típus, nehézségi szint) összefogó egyszerű adatmodell.
* **`GameTest.java`**: A JUnit 5 alapú tesztosztály, amely a táblaméreteket, az érvénytelen lépéseket és a győzelmi feltételeket validálja.

## 3. Futtatással kapcsolatos információk
Az alkalmazás a szabványos Maven `pom.xml` segítségével fordítható. A külső függőségek: `SLF4J`, `JColor`, `Gson`, `JUnit 5`.

**Elérhető parancssori kapcsolók:**
* `-r` vagy `--rank`: A győzelmi rangsor azonnali megjelenítése, majd kilépés.
* `-l` vagy `--load`: Utolsó elmentett állás azonnali betöltése és a játék folytatása.
* `-t [OSZLOPxSOR]` vagy `--table`: Kezdő táblaméret explicit megadása (pl. `-t 7x6`).
* `-u [NÉV]` vagy `--user`: Az első játékos nevének előzetes definiálása.

*Példa indításra:* `java -jar java-con4-ai.jar -t 8x8 -u Lajos`

---

## 4. A projekt története (Evolúciós prompt napló)

Az alábbi lista tartalmazza a fejlesztés során elhangzott kéréseket (a jelenlegi munkamenet idővonalára vetítve).

* **[14:15:00]** Készíts egy konzolos Java alkalmazást... [Részletes kezdeti specifikáció: Java 21, Maven, IntelliJ, ConsoleTable, JColor, parancssori argumentumok, 6x6 alapértelmezett tábla, S és P korongok, mentés/betöltés, tesztesetek, dokumentáció].
* **[14:20:12]** Amikor az üdvözlő képernyőn vagyok, nem tudom a futást megszakítani a "Ctrl+C" kombinációval.
* **[14:22:30]** Kérlek, hogy implementáld a "[K] Kilépés" lehetőséget.
* **[14:25:45]** Kérlek, hogy a játék mentéséhez implementáld a "[S] Mentés" karakter értelmezést is.
* **[14:31:10]** A kezdő képernyőn legyen beállítható a gépi játékos erősségi foka 1-3 skálán... A számolás közben a régi, hagyományos pörgő karakter és a "számolás.." felirat legyen látható.
* **[14:35:22]** Igen, kérem a kiegészítést arra vonatkozóan, hogy a mentési JSON fájlba a gépi erősségi szint is megjelenjen.
* **[14:39:05]** Igen, kérem az utolsó lépés vizuális kiemelését.
* **[14:42:18]** Igen, kérem, hogy az utolsó lépés vizuális kiemelésének koordinátái is szerepeljenek a JSON fájlban.
* **[14:45:50]** Kérlek, hogy jelenítsd meg a utolsó, minden módosítást és javítást tartalmazó kódokat.
* **[14:52:11]** Apró megjegyzések: 1) A tábla oszlop elnevezései 1 karakterrel balra el van csúszva... 2) "MENTES [S]" változtasd "Mentés [S]" karakterláncra. 3) A játékos neve melletti betű legyen a jelentésének megfelelő színű. 4) Enter nélküli adatbevitel.
* **[15:05:30]** A "ConsoleTable"-t vegyük ki a specifikációból, mivel nem használjuk. Az Enter nélküli adatbevitel... hagyd figyelmen kívül. Ennek megfelelően kérem a jelenlegi, minden módosítást és javítást tartalmazó kódokat.
* **[15:15:45]** Kérlek, hogy először minden kódot részletesen magyarázz a forráskódokban a JavaDoc ajánlásának megfelelően. Továbbá kérlek, hogy használd fel kiegészítő információként a bementi és a menet közbeni promptokat...
* **[15:29:24]** Kérlek, hogy generáld le az új, minden módosításra és javításra kiterjedő README.md fájl... (Jelenlegi prompt).

---

## 5. Újrageneráló Master Prompt

Az alábbi utasítássorozat (prompt) úgy lett megtervezve, hogy egy tiszta munkamenetben is pontosan a jelenlegi, végleges és minden javítást tartalmazó projektet eredményezze.

**Másold be az alábbi szöveget egy új AI asszisztensnek a projekt újraalkotásához:**

> Készíts egy konzolos Connect4 (4 a sorban) Java alkalmazást az alábbi specifikációk és szigorú kódolási irányelvek alapján.
>
> **Kódolási és formázási elvárások:**
> 1. Java 21 LTS környezet, Maven projektstruktúra (`pom.xml`).
> 2. Használandó külső könyvtárak: `SLF4J` (naplózás), `JColor` (terminál színezés), `Gson` (JSON kezelés), `JUnit 5` (tesztelés). Szigorúan TILOS a `ConsoleTable` vagy egyéb bemenetkezelő (pl. JLine) könyvtár használata!
> 3. Tiszta, jól konfigurálható OOP kód. A program főbb logikai részeit külön fájlokba szervezd (`Main`, `GameEngine`, `Board`, `Player`, `FileManager`, `GameTest`).
> 4. Szigorúan alkalmazd a Pascal-case névkonvenciót az összes változó, metódus és osztály esetén.
> 5. Minden osztály, metódus és fontos osztályszintű változó legyen ellátva részletes, magyar nyelvű JavaDoc megjegyzéssel.
>
> **Parancssori argumentumok (`Main.java`):**
> Kezeld a következő opcionális paramétereket:
> - `-r` vagy `--rank`: Csak a győzelmi rangsort jelenítse meg, majd lépjen ki.
> - `-l` vagy `--load`: Töltse be az utolsó mentett játékot és folytassa.
> - `-t [OSZLOPxSOR]` vagy `--table`: Állítsa be a tábla méretét.
> - `-u [NÉV]` vagy `--user`: Állítsa be az első játékos nevét.
    > Hibás paraméter esetén a program hibaüzenettel álljon le.
>
> **Főmenü és Inicializálás (`GameEngine.java`):**
> 1. Induláskor legyen egy menü: `[N] Új játék`, `[L] Mentett játék betöltése`, `[R] Rangsor megtekintése`, `[D] Gépi ellenfél nehézsége (1-3)`, `[K] Kilépés`.
> 2. A `[K]` parancsra a program `System.exit(0)`-val szabályosan álljon le.
> 3. Két játékos van (Sárga 'S' és Piros 'P'). A 2. játékos alapértelmezetten a gép ("Sz.Gép"). A kezdés sorsolással dől el.
>
> **Játékmenet és Tábla (`Board.java` és `GameEngine.java`):**
> 1. A tábla alapértelmezett mérete 6x6, minimum 4x4, maximum 12x12.
> 2. A tábla fejlécében angol ABC betűi szerepeljenek (A, B, C...), a sorok sorszámozottak (1, 2, 3...). A fejléc betűit úgy igazítsd (3 szóközzel eltolva), hogy tökéletesen a mezők felett legyenek.
> 3. A játéktér kirajzolásakor a `JColor` segítségével az 'S' karakter legyen élénk sárga, a 'P' élénk piros.
> 4. Lépés bekérésekor a szöveg formátuma ez legyen: `JátékosNeve (SzínesKarakter), válassz oszlopot (A-F), vagy írd be: Mentés [S]:`.
> 5. Ha a felhasználó egy érvényes oszlopbetűt ad meg, a korong essen le. Ha az "S", "MENTES" vagy "MENTÉS" szót írja be, a játékállás mentődjön ki.
> 6. Bármely játékos érvényes lépése után a letett korong háttere a tábla kirajzolásakor legyen ciánkék (utolsó lépés kiemelése). Ha valaki nyer, a nyertes 4 korong háttere legyen fehér (ez felülírja a ciánkéket).
>
> **Gépi Ellenfeél (AI):**
> 1. A `[D]` menüpontban beállítható szint (1: random lépés, 2: 1 lépést előreszámol a blokkoláshoz/nyeréshez, 3: 2 lépést előreszámol és a középpontot preferálja).
> 2. Amíg a gép gondolkodik, egy külön Thread-en jelenjen meg a `Számolás..` felirat és egy pörgő karakter (`\`, `-`, `/`, `|`), ami a számolás végén eltűnik.
>
> **Fájlkezelés (`FileManager.java`):**
> 1. A mentési mappa a felhasználó home könyvtárában a `Connect4-MI`.
> 2. Győztes esetén a `cn4-gyoz.csv` fájlba (UTF-8) íródjon be: Időbélyeg, Név, TáblaMéret, Lépésszám, Játékidő(mp). A `[R]` menü ebből olvassa ki és jelenítse meg csökkenő sorrendben a győzelmeket.
> 3. Mentéskor (`cn4-ment.json` fájlba a `Gson` segítségével) kerüljön bele: oszlopok, sorok, tábla aktuális állása, játékosok nevei, következő játékos indexe, eltelt idő. Ezen felül **kötelezően menteni kell** a gépi játékos aktuális `AiLevel` értékét és az utolsó lépés vizuális kiemelésének koordinátáit (`HighlightRow`, `HighlightCol`), és ezeket betöltéskor vissza is kell állítani!
>
> **Tesztek (`GameTest.java`):**
> Készíts JUnit 5 teszteket legalább a következőkre: táblaméret limitálásának ellenőrzése, betelt oszlopba lépés tiltása, 4 azonos korong horizontális felismerése.