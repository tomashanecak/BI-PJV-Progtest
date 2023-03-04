package thedrake;

import static thedrake.TroopFace.AVERS;

public class Troop {
     private final String name;
     private final Offset2D aversPivot, reversPivot;

    public Troop(String name, Offset2D aversPivot, Offset2D reversPivot) {
        this.name = name;
        this.aversPivot = aversPivot;
        this.reversPivot = reversPivot;
    }

    // Constructor that sets both pivots to same value
    public Troop(String name, Offset2D pivot){
        this.name = name;
        aversPivot = reversPivot = pivot;
    }

    // Constructor that sets both pivots to 1,1
    public Troop(String name){
        this(name, new Offset2D(1,1));
    }

    // Getter for name of the Troop
    public String name() {
        return name;
    }

    // Return pivot on the selected face of the troop
    public Offset2D pivot(TroopFace face){
        if(face == AVERS)
            return aversPivot;
        else
            return reversPivot;
    }
}
