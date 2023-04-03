package thedrake;

import java.util.List;
import java.util.Optional;

import static thedrake.PlayingSide.BLUE;
import static thedrake.PlayingSide.ORANGE;
import static thedrake.TilePos.OFF_BOARD;

public class GameState {
    private final Board board;
    private final PlayingSide sideOnTurn;
    private final Army blueArmy;
    private final Army orangeArmy;
    private final GameResult result;

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy) {
        this(board, blueArmy, orangeArmy, BLUE, GameResult.IN_PLAY);
    }

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy,
            PlayingSide sideOnTurn,
            GameResult result) {
        this.board = board;
        this.sideOnTurn = sideOnTurn;
        this.blueArmy = blueArmy;
        this.orangeArmy = orangeArmy;
        this.result = result;
    }

    public Board board() {
        return board;
    }

    public PlayingSide sideOnTurn() {
        return sideOnTurn;
    }

    public GameResult result() {
        return result;
    }

    public Army army(PlayingSide side) {
        if (side == BLUE) {
            return blueArmy;
        }

        return orangeArmy;
    }

    public Army armyOnTurn() {
        return army(sideOnTurn);
    }

    public Army armyNotOnTurn() {
        if (sideOnTurn == BLUE)
            return orangeArmy;

        return blueArmy;
    }

    // Return tile if not occupied by player
    public Tile tileAt(TilePos pos) {
        TroopTile blue = blueArmy.boardTroops().at(pos).orElse(null);
        TroopTile orange = orangeArmy.boardTroops().at(pos).orElse(null);

        if(blue != null)
            return blue;
        if(orange != null)
            return orange;

        return board.at(pos);
    }

    // Return if move is possible from certain tile
    private boolean canStepFrom(TilePos origin) {
        if(result() != GameResult.IN_PLAY || origin == OFF_BOARD)
            return false;

        if(armyOnTurn().boardTroops().isPlacingGuards())
            return false;

        TroopTile playerTroop = armyOnTurn().boardTroops().at(origin).orElse(null);
        return playerTroop != null;
    }

    private boolean canStepTo(TilePos target) {
        if(result != GameResult.IN_PLAY || target == OFF_BOARD)
            return false;

        return board.at(target).canStepOn() &&
                armyOnTurn().boardTroops().at(target).isEmpty() &&
                armyNotOnTurn().boardTroops().at(target).isEmpty();
    }

    private boolean canCaptureOn(TilePos target) {
        if(result != GameResult.IN_PLAY)
            return false;

        return armyNotOnTurn().boardTroops().at(target).isPresent();
    }

    public boolean canStep(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canStepTo(target);
    }

    public boolean canCapture(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canCaptureOn(target);
    }

    public boolean canPlaceFromStack(TilePos target) {
        if(result != GameResult.IN_PLAY || target == OFF_BOARD)
            return false;

        // Check if empty stack
        if(armyOnTurn().stack().isEmpty())
            return false;

        // Check if place is occupied
        if(board.at(target) != BoardTile.EMPTY ||
            armyOnTurn().boardTroops().at(target).isPresent() ||
            armyNotOnTurn().boardTroops().at(target).isPresent())
            return false;

        // Check if leader is placed in correct row
        int leaderRow = 0;
        if(armyOnTurn().side() == ORANGE)
            leaderRow = board.dimension() - 1;

        if (!armyOnTurn().boardTroops().isLeaderPlaced()) {
            return target.j() == leaderRow;
        }

        // Check if guards are placed  next to the leader
        if (armyOnTurn().boardTroops().isPlacingGuards()) {
            return armyOnTurn().boardTroops().leaderPosition().neighbours().contains(target);
        }

        // Check if new tile has at least one neighbour of same color
        List<BoardPos> neighbours = (List<BoardPos>) target.neighbours();
        for(BoardPos neighbour : neighbours){
            if(armyOnTurn().boardTroops().at(neighbour).isPresent())
                return true;
        }

        return false;
    }

    public GameState stepOnly(BoardPos origin, BoardPos target) {
        if (canStep(origin, target))
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().troopStep(origin, target), GameResult.IN_PLAY);

        throw new IllegalArgumentException();
    }

    public GameState stepAndCapture(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopStep(origin, target).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState captureOnly(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopFlip(origin).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState placeFromStack(BoardPos target) {
        if (canPlaceFromStack(target)) {
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().placeFromStack(target),
                    GameResult.IN_PLAY);
        }

        throw new IllegalArgumentException();
    }

    public GameState resign() {
        return createNewGameState(
                armyNotOnTurn(),
                armyOnTurn(),
                GameResult.VICTORY);
    }

    public GameState draw() {
        return createNewGameState(
                armyOnTurn(),
                armyNotOnTurn(),
                GameResult.DRAW);
    }

    private GameState createNewGameState(Army armyOnTurn, Army armyNotOnTurn, GameResult result) {
        if (armyOnTurn.side() == BLUE) {
            return new GameState(board, armyOnTurn, armyNotOnTurn, BLUE, result);
        }

        return new GameState(board, armyNotOnTurn, armyOnTurn, PlayingSide.ORANGE, result);
    }
}
