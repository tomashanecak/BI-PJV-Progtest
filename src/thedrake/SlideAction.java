package thedrake;

import java.util.ArrayList;
import java.util.List;

public class SlideAction extends TroopAction {

    public SlideAction(Offset2D offset) {
        super(offset);
    }

    public SlideAction(int offsetX, int offsetY) {
        super(offsetX, offsetY);
    }

    @Override
    public List<Move> movesFrom(BoardPos origin, PlayingSide side, GameState state) {
        List<Move> result = new ArrayList<>();

        int x = offset().x;
        int y = offset().y;
        TilePos target = origin.stepByPlayingSide(new Offset2D(x,y), side);

        while(true){
            if (state.canStep(origin, target)) {
                result.add(new StepOnly(origin, (BoardPos) target));
            } else if (state.canCapture(origin, target)) {
                // If we capture we can't pass through
                result.add(new StepAndCapture(origin, (BoardPos) target));
                return result;
            }
            else
                return result;

            x = (x > 0) ? x + 1 : (x < 0) ? x - 1 : x;
            y = (y > 0) ? y + 1 : (y < 0) ? y - 1 : y;
            target = origin.stepByPlayingSide(new Offset2D(x,y), side);
        }
    }
}
