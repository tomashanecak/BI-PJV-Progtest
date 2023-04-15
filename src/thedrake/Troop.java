package thedrake;

import java.io.PrintWriter;
import java.util.List;

import static thedrake.TroopFace.AVERS;

public class Troop implements JSONSerializable {
     private final String name;
     private final Offset2D aversPivot, reversPivot;
     private final List<TroopAction> aversActions, reversActions;

    public Troop(String name,
                 Offset2D aversPivot,
                 Offset2D reversPivot,
                 List<TroopAction> aversActions,
                 List<TroopAction> reversActions
    ) {
        this.name = name;
        this.aversPivot = aversPivot;
        this.reversPivot = reversPivot;
        this.aversActions = aversActions;
        this.reversActions = reversActions;
    }

    // Constructor that sets both pivots to same value
    public Troop(String name,
                 Offset2D pivot,
                 List<TroopAction> aversActions,
                 List<TroopAction> reversActions){
        this.name = name;
        this.aversActions = aversActions;
        this.reversActions = reversActions;
        aversPivot = reversPivot = pivot;
    }

    // Constructor that sets both pivots to 1,1
    public Troop(String name,
                 List<TroopAction> aversActions,
                 List<TroopAction> reversActions){
        this(name, new Offset2D(1,1), aversActions, reversActions);
    }

    //Vrací seznam akcí pro zadanou stranu jednotky
    public List<TroopAction> actions(TroopFace face){
        return face == AVERS ? aversActions : reversActions;
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

    @Override
    public void toJSON(PrintWriter writer) {
        writer.print("\"" + this.name() + "\"");
    }
}
