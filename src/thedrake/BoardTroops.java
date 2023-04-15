package thedrake;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BoardTroops implements JSONSerializable {
    private final PlayingSide playingSide;
    private final Map<BoardPos, TroopTile> troopMap;
    private final TilePos leaderPosition;
    private final int guards;

    public BoardTroops(PlayingSide playingSide) {
        // Místo pro váš kód
        this.playingSide = playingSide;
        troopMap = Collections.emptyMap();
        guards = 0;
        leaderPosition = TilePos.OFF_BOARD;
    }

    public BoardTroops(
            PlayingSide playingSide,
            Map<BoardPos, TroopTile> troopMap,
            TilePos leaderPosition,
            int guards) {
        // Místo pro váš kód
        this.playingSide = playingSide;
        this.troopMap = troopMap;
        this.leaderPosition = leaderPosition;
        this.guards = guards;
    }

    public Optional<TroopTile> at(TilePos pos) {
        // Místo pro váš kód
        return Optional.ofNullable(troopMap.get(pos));
    }

    public PlayingSide playingSide() {
        // Místo pro váš kód
        return playingSide;
    }

    public TilePos leaderPosition() {
        // Místo pro váš kód
        return leaderPosition;
    }

    public int guards() {
        // Místo pro váš kód
        return guards;
    }

    public boolean isLeaderPlaced() {
        // Místo pro váš kód
        return leaderPosition != TilePos.OFF_BOARD;
    }

    public boolean isPlacingGuards() {
        // Místo pro váš kód
        return isLeaderPlaced() && guards < 2;
    }

    public Set<BoardPos> troopPositions() {
        // Místo pro váš kód
        Set<BoardPos> troopPos = new HashSet<BoardPos>();
        for(Map.Entry<BoardPos, TroopTile> entry : troopMap.entrySet()){
            if(entry.getValue() != null)
                troopPos.add(entry.getKey());
        }
        return troopPos;
    }

    // PUT NEW TROOP ON A GIVEN POSITION (CREATE DEEP COPY OF MAP)
    public BoardTroops placeTroop(Troop troop, BoardPos target) {
        if(at(target).isPresent())
            throw new IllegalArgumentException();

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        newTroops.put(target, new TroopTile(troop, playingSide(), TroopFace.AVERS));

        if(!isLeaderPlaced())
            return new BoardTroops(playingSide(), newTroops, target, guards);
        else if(isPlacingGuards())
            return new BoardTroops(playingSide(), newTroops, leaderPosition, guards+1);
        return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
    }

    // MOVE TROOP FROM POSITION ORIGIN TO TARGET POSITION AND TURN CARD (CREATE DEEP COPY OF MAP)
    public BoardTroops troopStep(BoardPos origin, BoardPos target) {
        // Místo pro váš kód
        if (!isLeaderPlaced()) {
            throw new IllegalStateException(
                    "Cannot move troops before the leader is placed.");
        }

        if (isPlacingGuards()) {
            throw new IllegalStateException(
                    "Cannot move troops before guards are placed.");
        }

        if (!at(origin).isPresent() || at(target).isPresent())
            throw new IllegalArgumentException();

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        TroopTile tile = newTroops.remove(origin);
        newTroops.put(target, tile.flipped());

        if(origin.equals(leaderPosition))
            return new BoardTroops(playingSide(), newTroops, target, guards);
        return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
    }

    // FLIP CARD ON GIVEN POSITION (CREATE DEEP COPY OF MAP)
    public BoardTroops troopFlip(BoardPos origin) {
        if (!isLeaderPlaced()) {
            throw new IllegalStateException(
                    "Cannot move troops before the leader is placed.");
        }

        if (isPlacingGuards()) {
            throw new IllegalStateException(
                    "Cannot move troops before guards are placed.");
        }

        if (!at(origin).isPresent() || origin == null)
            throw new IllegalArgumentException();

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        TroopTile tile = newTroops.remove(origin);
        newTroops.put(origin, tile.flipped());

        return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
    }

    // REMOVE CARD ON GIVEN POSITION (CREATE DEEP COPY OF MAP)
    public BoardTroops removeTroop(BoardPos target) {
        if (!isLeaderPlaced()) {
            throw new IllegalStateException(
                    "Cannot move troops before the leader is placed.");
        }

        if (isPlacingGuards()) {
            throw new IllegalStateException(
                    "Cannot move troops before guards are placed.");
        }

        if(!at(target).isPresent())
            throw new IllegalArgumentException();

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        newTroops.remove(target);

        if(target.equals(leaderPosition))
            return new BoardTroops(playingSide(), newTroops, TilePos.OFF_BOARD, guards);
        return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.print("{\"side\":");
        playingSide().toJSON(writer);

        writer.print(",\"leaderPosition\":");
        leaderPosition().toJSON(writer);

        writer.print(",\"guards\":" + guards() + ",");

        // This part of code sorts the map so we print it in correct order
        Comparator<BoardPos> boardPosComparator = Comparator.comparing(BoardPos::column)
                .thenComparingInt(BoardPos::row);

        Map<BoardPos, TroopTile> sortedTroopMap = new TreeMap<>(boardPosComparator);
        sortedTroopMap.putAll(troopMap);

        writer.print("\"troopMap\":{");
        AtomicInteger index = new AtomicInteger();
        sortedTroopMap.forEach((pos, tile) -> {
            pos.toJSON(writer);
            writer.print(":");
            tile.toJSON(writer);
            if (index.getAndIncrement() < sortedTroopMap.size() - 1) {
                writer.print(",");
            }
        });
        writer.print("}}");
    }
}
