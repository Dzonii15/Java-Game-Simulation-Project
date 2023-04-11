package nikola.makric.boardobjects;

import nikola.makric.boardobjects.figures.GameFigure;
import nikola.makric.boardobjects.spawns.Diamond;
public class boardCell {
    int numberOfCell;
    public GameFigure Figure = null;
    Diamond diamond = null;

    public boardCell(int x, int y, int dimension) {
        numberOfCell = x * dimension + y;
    }

    public int getNumberOfCell() {
        return numberOfCell;
    }

    public void setFigure(GameFigure figure) {
        Figure = figure;
    }

    public boolean hasItFigure() {
        return Figure != null;
    }

    public void setDiamond() {
        diamond = new Diamond();
    }
    public void removeDiamond()
    {
    	diamond = null;
    }
    public boolean hasItDiamond() {
        return diamond != null;
    }
}
