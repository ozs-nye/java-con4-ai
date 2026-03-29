package con4;

import com.diogonunes.jcolor.Attribute;
import static com.diogonunes.jcolor.Ansi.colorize;

import java.util.Random;
import java.util.Scanner;

/**
 * A játékhurokért, a felhasználói bemenetekért és a mesterséges intelligencia vezérléséért felelős osztály.
 */
public class GameEngine {
  private final Scanner InputScanner = new Scanner(System.in);
  private final FileManager FileHandler = new FileManager();
  private Board GameBoard;
  private Player[] Players = new Player[2];
  private int CurrentPlayerIndex;
  private int StepCount;
  private long StartTimeMillis;
  private int GlobalAiDifficulty = 1;

  /**
   * Elindítja az alkalmazást a megadott parancssori opciók alapján.
   */
  public void StartApplication(boolean ShowRankOnly, boolean LoadGame, String BoardSizeStr, String FirstPlayerName) {
    if (ShowRankOnly) {
      FileHandler.DisplayRankings();
      return;
    }

    if (LoadGame) {
      if (!LoadSavedGame()) return;
    } else {
      ShowWelcomeScreen();
      InitializeNewGame(BoardSizeStr, FirstPlayerName);
    }

    RunGameLoop();
  }

  /**
   * Megjeleníti az üdvözlő képernyőt és kezeli a főmenü választásait.
   * * Megvalósított promptok:
   * - "Amikor az üdvözlő képernyőn vagyok, nem tudom a futást megszakítani a Ctrl+C kombinációval." -> '[K] Kilépés' bevezetése.
   * - "A kezdő képernyőn legyen beállítható a gépi játékos erősségi foka 1-3 skálán." -> '[D] Gépi ellenfél nehézsége' menüpont.
   */
  private void ShowWelcomeScreen() {
    boolean InMenu = true;
    while (InMenu) {
      System.out.println("=================================");
      System.out.println("   Üdvözöl a Connect4 Játék!   ");
      System.out.println("=================================");
      System.out.println("[N] Új játék");
      System.out.println("[L] Mentett játék betöltése");
      System.out.println("[R] Rangsor megtekintése");
      System.out.println("[D] Gépi ellenfél nehézsége (Jelenleg: " + GlobalAiDifficulty + ")");
      System.out.println("[K] Kilépés");
      System.out.print("Válassz egy opciót: ");

      String Choice = InputScanner.nextLine().trim().toUpperCase();

      if (Choice.equals("R")) {
        FileHandler.DisplayRankings();
      } else if (Choice.equals("L")) {
        if (LoadSavedGame()) return;
      } else if (Choice.equals("N")) {
        InMenu = false;
      } else if (Choice.equals("D")) {
        System.out.print("Add meg a szintet (1: Kezdő, 2: Haladó, 3: Profi): ");
        try {
          int Level = Integer.parseInt(InputScanner.nextLine().trim());
          if (Level >= 1 && Level <= 3) {
            GlobalAiDifficulty = Level;
            System.out.println("Nehézség sikeresen beállítva: " + GlobalAiDifficulty);
          } else {
            System.out.println("Érvénytelen szint! (1-3)");
          }
        } catch (NumberFormatException Ex) {
          System.out.println("Kérlek, számot adj meg!");
        }
      } else if (Choice.equals("K")) {
        System.out.println("Kilépés a programból. Viszontlátásra!");
        System.exit(0);
      } else {
        System.out.println("Érvénytelen választás. Kérlek, próbáld újra.");
      }
    }
  }

  /**
   * Visszatölti az elmentett játékállást a FileManager segítségével.
   */
  private boolean LoadSavedGame() {
    FileManager.GameState State = FileHandler.LoadGame();
    if (State == null) return false;

    GameBoard = new Board(State.Columns, State.Rows);
    GameBoard.SetGrid(State.Grid);

    // AI nehézség visszatöltése a JSON-ből
    GlobalAiDifficulty = State.AiLevel > 0 ? State.AiLevel : 1;

    Players[0] = new Player(State.Player1Name, 'S', false, 0);
    Players[1] = new Player(State.Player2Name, 'P', State.Player2Name.equals("Sz.Gép"), GlobalAiDifficulty);

    CurrentPlayerIndex = State.NextPlayerIndex;
    StartTimeMillis = System.currentTimeMillis() - (State.DurationSeconds * 1000);

    // Vizuális kiemelés visszatöltése
    if (State.HighlightRow >= 0 && State.HighlightCol >= 0) {
      GameBoard.SetHighlightMove(State.HighlightRow, State.HighlightCol);
    } else {
      GameBoard.SetHighlightMove(-1, -1);
    }

    System.out.println("Játékállás sikeresen betöltve! Gépi nehézség: " + GlobalAiDifficulty);
    return true;
  }

  /**
   * Inicializál egy új játékot (táblaméret, játékosok, sorsolás).
   */
  private void InitializeNewGame(String BoardSizeStr, String FirstPlayerName) {
    int Cols = 6, Rows = 6;
    if (BoardSizeStr != null && BoardSizeStr.contains("x")) {
      try {
        String[] Parts = BoardSizeStr.split("x");
        Cols = Integer.parseInt(Parts[0]);
        Rows = Integer.parseInt(Parts[1]);
      } catch (NumberFormatException Ex) {
        System.out.println("Hibás táblaméret, alapértelmezett 6x6 lesz beállítva.");
      }
    } else {
      System.out.print("Add meg a tábla méretét (pl. 7x6): ");
      String Input = InputScanner.nextLine();
      if (Input.contains("x")) {
        try {
          String[] Parts = Input.split("x");
          Cols = Integer.parseInt(Parts[0]);
          Rows = Integer.parseInt(Parts[1]);
        } catch (NumberFormatException Ex) {
          System.out.println("Hibás formátum, marad a 6x6.");
        }
      }
    }
    GameBoard = new Board(Cols, Rows);

    String P1Name = FirstPlayerName != null ? FirstPlayerName : AskForInput("1. játékos neve: ");
    String P2Name = AskForInput("2. játékos neve (Vagy üss Entert a gép elleni játékhoz): ");
    boolean IsComputer = P2Name.isEmpty();

    if (IsComputer) P2Name = "Sz.Gép";
    else if (P1Name.equals(P2Name)) {
      P1Name += "-1";
      P2Name += "-2";
    }

    Players[0] = new Player(P1Name, 'S', false, 0);
    Players[1] = new Player(P2Name, 'P', IsComputer, GlobalAiDifficulty);

    CurrentPlayerIndex = new Random().nextInt(2);
    System.out.println("A sorsolás alapján " + Players[CurrentPlayerIndex].GetPlayerName() + " kezd.");
    StartTimeMillis = System.currentTimeMillis();
    StepCount = 0;
  }

  private String AskForInput(String Message) {
    System.out.print(Message);
    return InputScanner.nextLine().trim();
  }

  /**
   * A fő játékhurok, amely addig fut, amíg nincs győztes vagy döntetlen.
   * * Megvalósított promptok:
   * - "Kérlek, hogy a játék mentéséhez implementáld a '[S] Mentés' karakter értelmezést is."
   * - "Továbbra is ezt a szöveget látom... változtasd 'Mentés [S]' karakterláncra."
   * - "A játékos neve melletti betű legyen a jelentésének megfelelő színű."
   */
  private void RunGameLoop() {
    boolean GameOver = false;

    while (!GameOver) {
      GameBoard.DrawBoard();
      Player CurrentPlayer = Players[CurrentPlayerIndex];
      boolean ValidMove = false;

      while (!ValidMove) {
        if (CurrentPlayer.GetIsComputer()) {
          int BestCol = CalculateAiMoveWithAnimation(CurrentPlayer, GameBoard);
          ValidMove = GameBoard.DropPiece(BestCol, CurrentPlayer.GetPlayerPiece());
          if (ValidMove) {
            GameBoard.SetHighlightMove(GameBoard.GetLastDropRow(), BestCol);
          }
        } else {
          // Játékos színének kiemelése a promptban
          Attribute PieceColor = (CurrentPlayer.GetPlayerPiece() == 'S') ? Attribute.YELLOW_TEXT() : Attribute.RED_TEXT();
          String ColoredPiece = colorize(String.valueOf(CurrentPlayer.GetPlayerPiece()), PieceColor);

          System.out.print(CurrentPlayer.GetPlayerName() + " (" + ColoredPiece + "), válassz oszlopot (A-" + (char)('A' + GameBoard.GetColumns() - 1) + "), vagy írd be: Mentés [S]: ");
          String Input = InputScanner.nextLine().trim().toUpperCase();

          // Mentés elfogadása S betűvel is
          if (Input.equals("MENTÉS") || Input.equals("MENTES") || Input.equals("S")) {
            SaveCurrentState();
            continue;
          }

          if (Input.length() > 0) {
            int ColIndex = Input.charAt(0) - 'A';
            if (ColIndex >= 0 && ColIndex < GameBoard.GetColumns()) {
              if (!GameBoard.IsColumnFull(ColIndex)) {
                ValidMove = GameBoard.DropPiece(ColIndex, CurrentPlayer.GetPlayerPiece());
                if (ValidMove) {
                  GameBoard.SetHighlightMove(GameBoard.GetLastDropRow(), ColIndex);
                }
              } else {
                System.out.println("Ez az oszlop betelt, válassz másikat!");
              }
            } else {
              System.out.println("Érvénytelen oszlop azonosító!");
            }
          }
        }
      }

      StepCount++;

      if (GameBoard.CheckWinCondition(CurrentPlayer.GetPlayerPiece())) {
        GameBoard.DrawBoard();
        long Duration = (System.currentTimeMillis() - StartTimeMillis) / 1000;
        System.out.println("Gratulálunk! " + CurrentPlayer.GetPlayerName() + " megnyerte a játékot!");
        FileHandler.SaveWinner(CurrentPlayer.GetPlayerName(), GameBoard.GetColumns(), GameBoard.GetRows(), StepCount, Duration);
        GameOver = true;
      } else if (GameBoard.IsDraw()) {
        GameBoard.DrawBoard();
        System.out.println("A játék döntetlennel zárult. Nincs több hely a táblán.");
        GameOver = true;
      } else {
        CurrentPlayerIndex = (CurrentPlayerIndex + 1) % 2;
      }
    }
  }

  /**
   * Összeállítja a játékállást és átadja a FileManager-nek mentésre.
   */
  private void SaveCurrentState() {
    FileManager.GameState State = new FileManager.GameState();
    State.Columns = GameBoard.GetColumns();
    State.Rows = GameBoard.GetRows();
    State.Grid = GameBoard.GetGrid();
    State.Player1Name = Players[0].GetPlayerName();
    State.Player2Name = Players[1].GetPlayerName();
    State.NextPlayerIndex = CurrentPlayerIndex;
    State.DurationSeconds = (System.currentTimeMillis() - StartTimeMillis) / 1000;
    State.AiLevel = GlobalAiDifficulty;
    State.HighlightRow = GameBoard.GetHighlightRow();
    State.HighlightCol = GameBoard.GetHighlightCol();

    FileHandler.SaveGame(State);
  }

  /**
   * Számolási animációt indít és meghívja az AI logikát.
   * * Megvalósított prompt: "A számolás közben a régi, hagyományos pörgő karakter... és a 'számolás..' felirat legyen látható."
   */
  private int CalculateAiMoveWithAnimation(Player AiPlayer, Board CurrentBoard) {
    boolean[] IsCalculating = {true};
    Thread AnimationThread = new Thread(() -> {
      char[] Spinner = {'\\', '-', '/', '|'};
      int SpinnerIndex = 0;
      while (IsCalculating[0]) {
        System.out.print("\rSzámolás.. " + Spinner[SpinnerIndex]);
        SpinnerIndex = (SpinnerIndex + 1) % Spinner.length;
        try {
          Thread.sleep(150);
        } catch (InterruptedException Ex) {
          Thread.currentThread().interrupt();
        }
      }
      // Tisztítás az animáció után
      System.out.print("\r                           \r");
    });
    AnimationThread.start();

    long StartTime = System.currentTimeMillis();
    int SelectedCol = 0;
    int Level = AiPlayer.GetAiLevel();

    // Gépi nehézség szintek logikája a kért 1-3 skála alapján
    if (Level == 1) {
      SelectedCol = GetRandomMove(CurrentBoard);
    } else if (Level == 2) {
      SelectedCol = GetBestMoveDepth(CurrentBoard, AiPlayer.GetPlayerPiece(), 1);
    } else if (Level == 3) {
      SelectedCol = GetBestMoveDepth(CurrentBoard, AiPlayer.GetPlayerPiece(), 2);
    }

    // Minimum 1.5 másodperc gondolkodási idő szimulálása a látvány kedvéért
    long ElapsedTime = System.currentTimeMillis() - StartTime;
    if (ElapsedTime < 1500) {
      try { Thread.sleep(1500 - ElapsedTime); } catch (InterruptedException Ex) { Thread.currentThread().interrupt(); }
    }

    IsCalculating[0] = false;
    try { AnimationThread.join(); } catch (InterruptedException Ex) { Thread.currentThread().interrupt(); }

    return SelectedCol;
  }

  /** Visszaad egy véletlenszerű, még nem betelt oszlopot. */
  private int GetRandomMove(Board CurrentBoard) {
    java.util.List<Integer> ValidCols = new java.util.ArrayList<>();
    for (int Iterator = 0; Iterator < CurrentBoard.GetColumns(); Iterator++) {
      if (!CurrentBoard.IsColumnFull(Iterator)) ValidCols.add(Iterator);
    }
    return ValidCols.get(new java.util.Random().nextInt(ValidCols.size()));
  }

  /** Segédfüggvény az ellenfél színének megállapítására. */
  private char GetOpponentPiece(char MyPiece) {
    return MyPiece == 'S' ? 'P' : 'S';
  }

  /**
   * Előreszámoló algoritmus a gép intelligenciájához.
   * Megvizsgálja a saját nyerési és az ellenfél blokkolási lehetőségeit.
   * @param Depth 1 esetén egy lépést, 2 esetén két lépést vizsgál előre.
   */
  private int GetBestMoveDepth(Board CurrentBoard, char AiPiece, int Depth) {
    char OpponentPiece = GetOpponentPiece(AiPiece);
    int Columns = CurrentBoard.GetColumns();

    // 1. Van-e azonnali nyerő lépés?
    for (int C = 0; C < Columns; C++) {
      if (!CurrentBoard.IsColumnFull(C)) {
        CurrentBoard.DropPiece(C, AiPiece);
        boolean Wins = CurrentBoard.CheckWinCondition(AiPiece);
        CurrentBoard.UndoMove(C);
        if (Wins) return C;
      }
    }

    // 2. Muszáj-e blokkolni az ellenfelet?
    for (int C = 0; C < Columns; C++) {
      if (!CurrentBoard.IsColumnFull(C)) {
        CurrentBoard.DropPiece(C, OpponentPiece);
        boolean OpponentWins = CurrentBoard.CheckWinCondition(OpponentPiece);
        CurrentBoard.UndoMove(C);
        if (OpponentWins) return C;
      }
    }

    // Ha 2. szinten vagyunk, itt megáll az értékelés egy random lépéssel
    if (Depth == 1) {
      return GetRandomMove(CurrentBoard);
    }

    // 3. szint (Profi): Két lépéses mélységű elemzés a középpont preferálásával
    int BestScore = -10000;
    int BestCol = GetRandomMove(CurrentBoard);

    for (int C = 0; C < Columns; C++) {
      if (!CurrentBoard.IsColumnFull(C)) {
        CurrentBoard.DropPiece(C, AiPiece);
        int Score = EvaluateOpponentResponse(CurrentBoard, AiPiece, OpponentPiece);
        CurrentBoard.UndoMove(C);

        if (Score > BestScore) {
          BestScore = Score;
          BestCol = C;
        }
      }
    }
    return BestCol;
  }

  /** Értékeli a játéktáblát aszerint, hogy az ellenfél mit tudna lépni a mi lépésünk után. */
  private int EvaluateOpponentResponse(Board CurrentBoard, char AiPiece, char OpponentPiece) {
    int Columns = CurrentBoard.GetColumns();
    int WorstScoreForAi = 10000;
    int CenterPreference = Columns / 2;

    for (int C = 0; C < Columns; C++) {
      if (!CurrentBoard.IsColumnFull(C)) {
        CurrentBoard.DropPiece(C, OpponentPiece);
        if (CurrentBoard.CheckWinCondition(OpponentPiece)) {
          CurrentBoard.UndoMove(C);
          // Ha az ellenfél ezzel nyerne, az egy nagyon rossz lépés számunkra
          return -1000;
        }

        // Középre húzó pontozás a tábla kontrollálásához
        int Score = -Math.abs(CenterPreference - C);
        if (Score < WorstScoreForAi) {
          WorstScoreForAi = Score;
        }
        CurrentBoard.UndoMove(C);
      }
    }
    return WorstScoreForAi;
  }
}
