package con4;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A Connect4 alkalmazás működését vizsgáló tesztesetek.
 */
class GameTest {

  @Test
  void TestBoardSizeLimits() {
    // 1.1 Teszteset: Játéktér méretének validálása
    Board SmallBoard = new Board(2, 2); // Túl kicsi
    assertEquals(4, SmallBoard.GetColumns(), "A minimum oszlopszámnak 4-nek kell lennie.");
    assertEquals(4, SmallBoard.GetRows(), "A minimum sorszámnak 4-nek kell lennie.");

    Board LargeBoard = new Board(15, 20); // Túl nagy
    assertEquals(12, LargeBoard.GetColumns(), "A maximum oszlopszámnak 12-nek kell lennie.");
    assertEquals(12, LargeBoard.GetRows(), "A maximum sorszámnak 12-nek kell lennie.");
  }

  @Test
  void TestInvalidMove() {
    // 1.3 Teszteset: Hibás lépést vizsgáló eset (telt oszlop)
    Board TestBoard = new Board(4, 4);
    assertTrue(TestBoard.DropPiece(0, 'S'));
    assertTrue(TestBoard.DropPiece(0, 'P'));
    assertTrue(TestBoard.DropPiece(0, 'S'));
    assertTrue(TestBoard.DropPiece(0, 'P'));

    // Az 5. elem lerakása a 4 magas oszlopba
    assertFalse(TestBoard.DropPiece(0, 'S'), "Nem szabad engedni a lépést betelt oszlop esetén.");
    assertTrue(TestBoard.IsColumnFull(0));
  }

  @Test
  void TestWinCondition() {
    // 1.x Teszteset: Biztonságos működés / Győzelem ellenőrzése
    Board TestBoard = new Board(6, 6);
    TestBoard.DropPiece(0, 'S');
    TestBoard.DropPiece(1, 'S');
    TestBoard.DropPiece(2, 'S');
    TestBoard.DropPiece(3, 'S'); // Horizontális győzelem

    assertTrue(TestBoard.CheckWinCondition('S'), "4 azonos színű korong egymás mellett győzelmet jelent.");
    assertFalse(TestBoard.CheckWinCondition('P'), "A másik játékos nem nyert.");
  }

  @Test
  void TestFileSaveLoadLogic() {
    // 1.2 Teszteset: Játékmenet kimentése és visszatöltése szimulálva
    FileManager FileHandler = new FileManager();
    FileManager.GameState MockState = new FileManager.GameState();
    MockState.Columns = 6;
    MockState.Rows = 6;
    MockState.Player1Name = "TestElek";
    MockState.Grid = new char[6][6];

    FileHandler.SaveGame(MockState);
    FileManager.GameState LoadedState = FileHandler.LoadGame();

    assertNotNull(LoadedState, "A betöltött állapot nem lehet null.");
    assertEquals("TestElek", LoadedState.Player1Name, "A betöltött névnek egyeznie kell a mentettel.");
  }
}
