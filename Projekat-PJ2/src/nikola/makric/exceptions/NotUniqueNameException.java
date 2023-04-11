package nikola.makric.exceptions;

public class NotUniqueNameException extends Exception{
	public NotUniqueNameException()
	{
		super("Imena igraca trebaju biti jedinstvena");
	}
	public NotUniqueNameException(String msg)
	{
		super(msg);
	}

}
