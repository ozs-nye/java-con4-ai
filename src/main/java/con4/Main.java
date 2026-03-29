package con4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Az alkalmazás belépési pontja.
 * Felelős a parancssori argumentumok feldolgozásáért és a játékmotor elindításáért.
 * * Megvalósított prompt: "Az alkalmazás legyen parancsori kapcsolók fogadására felkészítve... -r, -l, -t, -u"
 */
public class Main {
  private static final Logger LoggerInstance = LoggerFactory.getLogger(Main.class);

  /**
   * A program fő futtatási metódusa.
   * @param args A parancssori argumentumok tömbje.
   */
  public static void main(String[] args) {
    Boolean ShowRankOnly = false;
    Boolean LoadGame = false;
    String BoardSizeStr = null;
    String FirstPlayerName = null;

    try {
      // Eredeti prompt: Parancssori kapcsolók feldolgozása, hibás paraméter esetén leállás.
      for (int Iterator = 0; Iterator < args.length; Iterator++) {
        switch (args[Iterator]) {
          case "-r":
          case "--rank":
            ShowRankOnly = true;
            break;
          case "-l":
          case "--load":
            LoadGame = true;
            break;
          case "-t":
          case "--table":
            if (Iterator + 1 < args.length) {
              BoardSizeStr = args[++Iterator];
            } else {
              throw new IllegalArgumentException("A tábla mérete nincs megadva!");
            }
            break;
          case "-u":
          case "--user":
            if (Iterator + 1 < args.length) {
              FirstPlayerName = args[++Iterator];
            } else {
              throw new IllegalArgumentException("A felhasználónév nincs megadva!");
            }
            break;
          default:
            throw new IllegalArgumentException("Ismeretlen paraméter: " + args[Iterator]);
        }
      }
    } catch (Exception Ex) {
      LoggerInstance.error("Hiba a paraméterek feldolgozásakor: {}", Ex.getMessage());
      System.exit(1);
    }

    GameEngine Engine = new GameEngine();
    Engine.StartApplication(ShowRankOnly, LoadGame, BoardSizeStr, FirstPlayerName);
  }
}
