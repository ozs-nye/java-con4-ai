package con4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Fájlműveleteket végző osztály.
 * Felelős a játékállás mentéséért (JSON formátumban) és a győzelmi rangsor kezeléséért (CSV formátumban).
 */
public class FileManager {
  private static final Logger LoggerInstance = LoggerFactory.getLogger(FileManager.class);
  private final Path DirectoryPath;
  private final Path JsonFilePath;
  private final Path CsvFilePath;

  /**
   * Konstruktor, amely inicializálja az elérési utakat.
   * * Megvalósított prompt: "A fájl... a felhasználó könyvtárában létrehozott 'Connect4-MI' könyvtárban legyen elhelyezve."
   */
  public FileManager() {
    String UserHome = System.getProperty("user.home");
    DirectoryPath = Paths.get(UserHome, "Connect4-MI");
    JsonFilePath = DirectoryPath.resolve("cn4-ment.json");
    CsvFilePath = DirectoryPath.resolve("cn4-gyoz.csv");
    InitializeDirectory();
  }

  /**
   * Létrehozza a munkakönyvtárat, ha az még nem létezik.
   */
  private void InitializeDirectory() {
    try {
      if (!Files.exists(DirectoryPath)) {
        Files.createDirectories(DirectoryPath);
      }
    } catch (IOException Ex) {
      LoggerInstance.error("Nem sikerült létrehozni a munkakönyvtárat: {}", Ex.getMessage());
    }
  }

  /**
   * Kimenti az aktuális játékállást JSON formátumban.
   * @param State A kimentendő játékállás objektuma.
   */
  public void SaveGame(GameState State) {
    Gson GsonParser = new GsonBuilder().setPrettyPrinting().create();
    try (Writer FileWriter = new FileWriter(JsonFilePath.toFile())) {
      GsonParser.toJson(State, FileWriter);
      System.out.println("Játékállás sikeresen elmentve: " + JsonFilePath);
    } catch (IOException Ex) {
      LoggerInstance.error("Mentés sikertelen: {}", Ex.getMessage());
    }
  }

  /**
   * Visszatölti a korábban kimentett játékállást.
   * @return A betöltött GameState objektum, vagy null hiba esetén.
   */
  public GameState LoadGame() {
    if (!Files.exists(JsonFilePath)) {
      System.out.println("Nem található elmentett játékállás.");
      return null;
    }
    Gson GsonParser = new Gson();
    try (Reader FileReader = new FileReader(JsonFilePath.toFile())) {
      return GsonParser.fromJson(FileReader, GameState.class);
    } catch (IOException Ex) {
      LoggerInstance.error("Betöltés sikertelen: {}", Ex.getMessage());
      return null;
    }
  }

  /**
   * Kimenti a győztes adatait a CSV fájlba.
   * * Megvalósított prompt: "Időbélyeggel egy sorban a győztes nevét, a tábla méretét, a lépések számát, a játékra fordított időt mentsük ki egy UTF-8 kódolású CSV fájlba."
   */
  public void SaveWinner(String WinnerName, int Cols, int Rows, int Steps, long DurationSeconds) {
    String TimeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    String CsvLine = String.format("%s;%s;%dx%d;%d;%d%n", TimeStamp, WinnerName, Cols, Rows, Steps, DurationSeconds);
    try (BufferedWriter Writer = new BufferedWriter(new FileWriter(CsvFilePath.toFile(), true))) {
      Writer.write(CsvLine);
    } catch (IOException Ex) {
      LoggerInstance.error("Győztes mentése sikertelen: {}", Ex.getMessage());
    }
  }

  /**
   * Beolvassa a CSV fájlt és csökkenő sorrendben megjeleníti a győzelmek számát.
   */
  public void DisplayRankings() {
    if (!Files.exists(CsvFilePath)) {
      System.out.println("Még nincsenek letárolt győzelmek.");
      return;
    }
    Map<String, Integer> WinCounts = new HashMap<>();
    try (BufferedReader Reader = new BufferedReader(new FileReader(CsvFilePath.toFile()))) {
      String Line;
      while ((Line = Reader.readLine()) != null) {
        String[] Parts = Line.split(";");
        if (Parts.length >= 2) {
          String Name = Parts[1];
          WinCounts.put(Name, WinCounts.getOrDefault(Name, 0) + 1);
        }
      }
    } catch (IOException Ex) {
      LoggerInstance.error("Hiba a rangsor beolvasásakor: {}", Ex.getMessage());
      return;
    }

    System.out.println("\n--- GYŐZELMI RANGSOR ---");
    WinCounts.entrySet().stream()
            .sorted((E1, E2) -> E2.getValue().compareTo(E1.getValue()))
            .forEach(E -> System.out.println(E.getKey() + ": " + E.getValue() + " győzelem"));
    System.out.println("------------------------\n");
  }

  /**
   * Belső segédosztály a Gson könyvtár általi JSON szerializációhoz és deszerializációhoz.
   * * Megvalósított promptok:
   * - "Kérem a kiegészítést arra vonatkozóan, hogy a mentési JSON fájlba a gépi erősségi szint is megjelenjen." -> AiLevel
   * - "Az utolsó lépés vizuális kiemelésének koordinátái is szerepeljenek a JSON fájlban." -> HighlightRow, HighlightCol
   */
  public static class GameState {
    public int Columns;
    public int Rows;
    public char[][] Grid;
    public long DurationSeconds;
    public String Player1Name;
    public String Player2Name;
    public int NextPlayerIndex;
    public int AiLevel;
    public int HighlightRow;
    public int HighlightCol;
  }
}
