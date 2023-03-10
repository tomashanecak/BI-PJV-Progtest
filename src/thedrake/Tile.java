package thedrake;

public interface Tile {
    // Return true if Tile is empty and can be stepped on
    public boolean canStepOn();

    // Return true if there is a Troop on a tile
    public boolean hasTroop();
}
