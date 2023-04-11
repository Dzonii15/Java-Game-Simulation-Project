package nikola.makric;

import java.util.Date;
import java.util.logging.Level;

import nikola.makric.GUI.MainFrame;

public class TimeCounterThread extends Thread{
	public static Object objectToLockTimeThread = new String("");
	public TimeCounterThread()
	{
		setPriority(10);
	}
	public void run()
	{
		
		while(Simulation.simulationChecker)
		{
			if(MainFrame.paused)
			{
				synchronized(objectToLockTimeThread)
				{
					try {
					objectToLockTimeThread.wait();
					}catch(Exception e)
					{
						Simulation.logger.log(Level.WARNING,null,e);
					}
				}
			}
		  long currentTime = new Date().getTime();
		  String timeOfSimulation = "Vrijeme trajanja igre:"+((currentTime-Simulation.startTime-MainFrame.pauseTime)/1000);
		  Simulation.referenceOnGUI.setTime(timeOfSimulation);
		
		try {
			sleep(1000);
		}catch(Exception e)
		{Simulation.logger.log(Level.WARNING,null,e);}
		}
	}

}
