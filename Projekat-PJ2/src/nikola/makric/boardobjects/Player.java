package nikola.makric.boardobjects;

import java.util.ArrayList;

import nikola.makric.boardobjects.figures.FigureColour;
import nikola.makric.boardobjects.figures.GameFigure;

public class Player {
    //ime igraca
     String name;
    //Svaki igrac ima cetiri figure
    ArrayList<GameFigure> playerFigures = new ArrayList<>(4);
    //Boja figura
    FigureColour bojaFigura;
    //unutrasnji brojac koji ce mi govoriti na kojoj sam figurici od igraca
    private int currentFigure=0;

    public void addFigure(GameFigure figure, int i)
    {
        playerFigures.add(figure);
    }
    public void setColourOfFigures(FigureColour colour)
    {
        bojaFigura = colour;
    }
    public FigureColour getColourOfFigures()
    {
        return bojaFigura;
    }
    public void setName(String name)
    {
    	this.name = name;
    }
    public String getName()
    {
    	return name;
    }
    public ArrayList<GameFigure> getPlayerFigures()
    {
    	return playerFigures;
    }
    public GameFigure getNextFigureToPlay()
    {
    	return playerFigures.get(currentFigure);
    }
    public boolean removeFigure()
    {
    	if(currentFigure<3) {
    	currentFigure++;
    	return true;
    	}
    	else
    	{
    		return false;
    	}
    }
   
}
