package con4;

/**
 * Egy játékost reprezentáló osztály.
 * Tárolja a játékos nevét, a korongjának karakterét, a típusát (élő vagy gép),
 * valamint a gépi intelligencia nehézségi fokát.
 */
public class Player {
  private String PlayerName;
  private char PlayerPiece;
  private boolean IsComputer;
  private int AiLevel;

  /**
   * Konstruktor a játékos példányosításához.
   * * Megvalósított prompt: "A kezdő képernyőn legyen beállítható a gépi játékos erősségi foka 1-3 skálán."
   * Ez indokolta az AiLevel paraméter bevezetését az adatmodellbe.
   * * @param Name A játékos neve.
   * @param Piece A játékos korongja (S vagy P).
   * @param IsComputer Igaz, ha a játékost a számítógép irányítja.
   * @param AiLevel A gépi játékos intelligencia szintje (1-3). Élő játékosnál ez 0.
   */
  public Player(String Name, char Piece, boolean IsComputer, int AiLevel) {
    this.PlayerName = Name;
    this.PlayerPiece = Piece;
    this.IsComputer = IsComputer;
    this.AiLevel = AiLevel;
  }

  public String GetPlayerName() { return PlayerName; }
  public void SetPlayerName(String Name) { this.PlayerName = Name; }
  public char GetPlayerPiece() { return PlayerPiece; }
  public boolean GetIsComputer() { return IsComputer; }
  public int GetAiLevel() { return AiLevel; }
}
