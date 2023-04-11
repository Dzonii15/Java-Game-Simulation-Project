package nikola.makric.exceptions;

public class InvalidNumberOfPlayersException extends Exception {
	public InvalidNumberOfPlayersException()
	{
		super("Broj igraca mora da bude izmedju 2 i 4");
	}
	public InvalidNumberOfPlayersException(String msg)
	{
		super(msg);
	}
	

}
