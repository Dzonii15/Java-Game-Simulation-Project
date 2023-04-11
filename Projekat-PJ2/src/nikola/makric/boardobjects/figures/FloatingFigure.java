package nikola.makric.boardobjects.figures;

import nikola.makric.boardobjects.figures.FigureColour;
import nikola.makric.boardobjects.figures.GameFigure;
import nikola.makric.interfaces.CanFloat;

public class FloatingFigure extends GameFigure implements CanFloat {

    public FloatingFigure(FigureColour colour,String figureName)
    {
        super(colour,figureName);
    }
    @Override
    public String getType()
    {
    	return "FF";
    }
    @Override
    public int calculateMoves(int cardNumber)
    {
    	return cardNumber;
    }
}
