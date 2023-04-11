package nikola.makric.boardobjects.figures;

import java.util.ArrayList;

import nikola.makric.GUI.MainFrame;

public abstract class GameFigure {
    //boja figure
    protected FigureColour colour;
    //indeks u nizu putanje
    protected int pozicija = 0;
    //ime figure
    String figureName;
    //broj dijamanata u trenutnom posjedu
    int diamonds = 0;
    //putanja date figure
    protected ArrayList<Integer>figurePath = new ArrayList<>();
    //vrijeme kada je figurica krenula da se krece
    long figureStartTime;
    //vrijeme kada je zavrsila
    long figureEndTime;
    //vrijeme za koje je figura bila aktivna
    long activeTime;
    public GameFigure(){}
    public GameFigure(FigureColour colour,String figureName)
    {
        this.colour = colour;
        this.figureName = figureName;
    }
    public void setColour(FigureColour colour)
    {
        this.colour = colour;
    }
    public FigureColour getColour()
    {
    	return colour;
    }
    public String getName()
    {
    	return figureName;
    }
    public void setPozicija(int pozicija)
    {
    	this.pozicija = pozicija;
    }
    public int getPozicija()
    {
    	return this.pozicija;
    }
    @Override
    public int hashCode()
    {
		return colour.hashCode()*pozicija*figureName.hashCode();
    	
    }
    @Override
    public boolean equals(Object obj)
    {
    	if(obj instanceof GameFigure)
    	{
    		GameFigure tmp = (GameFigure)obj;
    		if(tmp.colour.equals(this.colour) && tmp.pozicija == this.pozicija && tmp.figureName.equals(this.figureName))
				return true;
				else 
					return false;
    	}else return false;
    }
    public void addDiamond()
    {
    	diamonds++;
    }
    public int consumeDiamonds()
    {
    	int tmp = diamonds;
    	diamonds = 0;
    	return tmp;
    }
    public void addToPath(Integer field)
    {
    	figurePath.add(field);
    }
    public ArrayList<Integer> getFigurePathClone()
    {
    	return (ArrayList<Integer>)figurePath.clone();
    }
    public void setStartTime(long sTime)
    {
    	this.figureStartTime = sTime;
    }
    public void setEndTime(long eTime)
    {
    	this.figureEndTime = eTime;
    	this.activeTime = (this.figureEndTime-this.figureStartTime - MainFrame.pauseTime)/1000;
    }
    public abstract String getType();
    public abstract int calculateMoves(int cardNumber);
   
}
