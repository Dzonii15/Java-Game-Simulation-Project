package nikola.makric.boardobjects.figures;

import nikola.makric.boardobjects.figures.FigureColour;
import nikola.makric.boardobjects.figures.GameFigure;

public class RegularFigure extends GameFigure {
    public RegularFigure(FigureColour colour,String figureName)
    {
     super(colour,figureName);
    }
    @Override
    public String getType()
    {
    	return "RF";
    }
    public int calculateMoves(int cardNumber)
    {
    	return cardNumber;
    }
}
