package nikola.makric.boardobjects.figures;

import nikola.makric.boardobjects.figures.FigureColour;
import nikola.makric.boardobjects.figures.GameFigure;
import nikola.makric.interfaces.IsExtraFast;

public class ExtraFastFigure extends GameFigure implements IsExtraFast {
    public ExtraFastFigure(FigureColour colour,String figureName)
    {
        super(colour,figureName);
    }
    @Override
    public String getType()
    {
    	return "EFF";
    }
    @Override
    public int calculateMoves(int cardNumber)
    {
    	return 2*cardNumber;
    }
}
