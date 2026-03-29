package con4;

import com.diogonunes.jcolor.Attribute;
import static com.diogonunes.jcolor.Ansi.colorize;

/**
 * A játéktábla állapotát és a nyerési logikát kezelő osztály.
 * Felelős a korongok elhelyezéséért, a győzelem ellenőrzéséért és a konzolos megjelenítésért.
 */
public class Board {
  private final int Columns;
  private final int Rows;
  private char[][] Grid;
  private boolean[][] WinningCells;

  private int LastDropRow = -1;
  private int HighlightRow = -1;
  private int HighlightCol = -1;

  /**
   * Konstruktor, amely beállítja a tábla méretét.
   * * Megvalósított prompt: "A játéktér táblája minimum 4x4, maximum 12x12 méretű lehet."
   */
  public Board(int Cols, int Rows) {
    this.Columns = Math.max(4, Math.min(12, Cols));
    this.Rows = Math.max(4, Math.min(12, Rows));
    this.Grid = new char[this.Rows][this.Columns];
    this.WinningCells = new boolean[this.Rows][this.Columns];
    InitializeGrid();
  }

  /**
   * Alaphelyzetbe állítja a játékteret üres ('.') karakterekkel.
   */
  private void InitializeGrid() {
    for (int RowIterator = 0; RowIterator < Rows; RowIterator++) {
      for (int ColIterator = 0; ColIterator < Columns; ColIterator++) {
        Grid[RowIterator][ColIterator] = '.';
      }
    }
  }

  public char[][] GetGrid() { return Grid; }
  public void SetGrid(char[][] NewGrid) { this.Grid = NewGrid; }
  public int GetColumns() { return Columns; }
  public int GetRows() { return Rows; }

  public int GetLastDropRow() { return LastDropRow; }
  public int GetHighlightRow() { return HighlightRow; }
  public int GetHighlightCol() { return HighlightCol; }

  /**
   * Beállítja az utolsó lépés vizuális kiemelésének koordinátáit.
   */
  public void SetHighlightMove(int Row, int Col) {
    this.HighlightRow = Row;
    this.HighlightCol = Col;
  }

  /**
   * Leejt egy korongot a megadott oszlopba.
   * @param ColIndex Az oszlop indexe (0-tól számítva).
   * @param Piece A lerakandó korong ('S' vagy 'P').
   * @return Igaz, ha a lépés érvényes volt, Hamis, ha az oszlop megtelt vagy érvénytelen.
   */
  public boolean DropPiece(int ColIndex, char Piece) {
    if (ColIndex < 0 || ColIndex >= Columns) return false;
    for (int RowIterator = Rows - 1; RowIterator >= 0; RowIterator--) {
      if (Grid[RowIterator][ColIndex] == '.') {
        Grid[RowIterator][ColIndex] = Piece;
        LastDropRow = RowIterator;
        return true;
      }
    }
    return false;
  }

  /**
   * Visszavonja az utolsó lépést az adott oszlopban.
   * A gépi intelligencia (előreszámolás) használja.
   */
  public void UndoMove(int ColIndex) {
    if (ColIndex < 0 || ColIndex >= Columns) return;
    for (int RowIterator = 0; RowIterator < Rows; RowIterator++) {
      if (Grid[RowIterator][ColIndex] != '.') {
        Grid[RowIterator][ColIndex] = '.';
        // A nyertes cellákat is alaphelyzetbe állítjuk a visszavonáskor
        WinningCells = new boolean[Rows][Columns];
        break;
      }
    }
  }

  /**
   * Ellenőrzi, hogy egy adott oszlop betelt-e.
   */
  public boolean IsColumnFull(int ColIndex) {
    return Grid[0][ColIndex] != '.';
  }

  /**
   * Ellenőrzi a győzelmi feltételeket (4 azonos korong egymás mellett).
   * @param Piece A vizsgálandó játékos korongja.
   * @return Igaz, ha a játékos nyert.
   */
  public boolean CheckWinCondition(char Piece) {
    for (int R = 0; R < Rows; R++) {
      for (int C = 0; C < Columns; C++) {
        if (Grid[R][C] == Piece) {
          // Horizontális
          if (C + 3 < Columns && Grid[R][C+1] == Piece && Grid[R][C+2] == Piece && Grid[R][C+3] == Piece) {
            MarkWinningCells(R, C, 0, 1); return true;
          }
          // Vertikális
          if (R + 3 < Rows && Grid[R+1][C] == Piece && Grid[R+2][C] == Piece && Grid[R+3][C] == Piece) {
            MarkWinningCells(R, C, 1, 0); return true;
          }
          // Átlós (jobbra le)
          if (C + 3 < Columns && R + 3 < Rows && Grid[R+1][C+1] == Piece && Grid[R+2][C+2] == Piece && Grid[R+3][C+3] == Piece) {
            MarkWinningCells(R, C, 1, 1); return true;
          }
          // Átlós (balra le)
          if (C - 3 >= 0 && R + 3 < Rows && Grid[R+1][C-1] == Piece && Grid[R+2][C-2] == Piece && Grid[R+3][C-3] == Piece) {
            MarkWinningCells(R, C, 1, -1); return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Megjelöli a győztes vonalat alkotó cellákat a vizuális kiemeléshez.
   */
  private void MarkWinningCells(int StartR, int StartC, int StepR, int StepC) {
    for (int Iterator = 0; Iterator < 4; Iterator++) {
      WinningCells[StartR + (Iterator * StepR)][StartC + (Iterator * StepC)] = true;
    }
  }

  /**
   * Ellenőrzi, hogy a játék döntetlennel végződött-e (minden mező betelt).
   */
  public boolean IsDraw() {
    for (int C = 0; C < Columns; C++) {
      if (Grid[0][C] == '.') return false;
    }
    return true;
  }

  /**
   * Kirajzolja a játéktáblát a konzolra a megfelelő színezésekkel.
   * * Megvalósított promptok:
   * - "S esetében élénk sárga, P esetében élénk piros... győztes háttere fehér."
   * - "Kérem az utolsó lépés vizuális kiemelését." (Ciánkék háttérrel megoldva)
   * - "A tábla oszlop elnevezései 1 karakterrel balra el van csúszva." (A fejléc "   " behúzással korrigálva)
   */
  public void DrawBoard() {
    System.out.println();
    System.out.print("   ");
    for (int C = 0; C < Columns; C++) {
      System.out.print((char)('A' + C) + " ");
    }
    System.out.println();

    for (int R = 0; R < Rows; R++) {
      System.out.printf("%2d ", R + 1);
      for (int C = 0; C < Columns; C++) {
        char Cell = Grid[R][C];
        String DisplayStr = String.valueOf(Cell);

        if (Cell == 'S' || Cell == 'P') {
          Attribute TextColor = (Cell == 'S') ? Attribute.YELLOW_TEXT() : Attribute.RED_TEXT();

          if (WinningCells[R][C]) {
            DisplayStr = colorize(String.valueOf(Cell), TextColor, Attribute.WHITE_BACK());
          } else if (R == HighlightRow && C == HighlightCol) {
            DisplayStr = colorize(String.valueOf(Cell), TextColor, Attribute.CYAN_BACK());
          } else {
            DisplayStr = colorize(String.valueOf(Cell), TextColor);
          }
        }

        System.out.print(DisplayStr + " ");
      }
      System.out.println();
    }
    System.out.println();
  }
}
