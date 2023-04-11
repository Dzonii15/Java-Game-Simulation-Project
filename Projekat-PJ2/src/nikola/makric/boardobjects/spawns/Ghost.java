package nikola.makric.boardobjects.spawns;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import nikola.makric.Simulation;
import nikola.makric.GUI.MainFrame;

public class Ghost extends Thread {
	ArrayList<String> oldDiamonds = new ArrayList<>();
	public static Object objectToLockGhost = new String("");
	public Ghost()
	{
		//setDaemon(true);
	}
	public void run()
	{
		while(Simulation.simulationChecker) {
			synchronized(Simulation.referenceOnGUI) {
				synchronized(Simulation.fieldMatrix) {
					if(MainFrame.paused)
                	{
                		synchronized(objectToLockGhost)
                		{
                			try {
                			objectToLockGhost.wait();
                			}catch(Exception e)
                			{
                				Simulation.logger.log(Level.WARNING,null,e);
                			}
                		}
                	}
			Random rand = new Random();
			int brojDijamanataZaPostaviti = rand.nextInt(Simulation.matrixDimensions-2)+2;
			if(oldDiamonds.size()>0) {
				
			for(String exDiamond : oldDiamonds)
			{
			String koordinateTekst[] = exDiamond.split("-");
			int koordinate[] = new int[] {Integer.parseInt(koordinateTekst[0]),Integer.parseInt(koordinateTekst[1])};
			Simulation.fieldMatrix[koordinate[0]][koordinate[1]].removeDiamond();
			Simulation.referenceOnGUI.removeDiamond(Simulation.matrixDimensions*koordinate[0]+koordinate[1]+1);
			}
			}
			
			
			oldDiamonds.clear();
			for(int i=0;i<brojDijamanataZaPostaviti;) {
			int pozicijaNaPutanji = Simulation.pathOfTheGame.get(rand.nextInt(Simulation.pathOfTheGame.size()-1));
			String koordinateString =  Simulation.getCoordinatesBasedOnNumebr(pozicijaNaPutanji-1);
			String koordinateTekst[] = koordinateString.split("-");
			int koordinate[] = new int[] {Integer.parseInt(koordinateTekst[0]),Integer.parseInt(koordinateTekst[1])};
			var polje = Simulation.fieldMatrix[koordinate[0]][koordinate[1]];
			if(!polje.hasItDiamond()&& !polje.hasItFigure() && !(Simulation.pathOfTheGame.get(0)== pozicijaNaPutanji))
			{
				polje.setDiamond();
				i++;
				oldDiamonds.add(koordinateString);
					Simulation.referenceOnGUI.drawDiamond(pozicijaNaPutanji);
			}
			}
		}
		}
			try {
				sleep(5000);
				}catch(Exception e)
				{Simulation.logger.log(Level.WARNING,null,e);}}
		
	}
}
