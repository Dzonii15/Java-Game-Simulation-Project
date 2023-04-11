package nikola.makric.exceptions;

public class InvalidDimensionException extends Exception {
	public InvalidDimensionException()
	{
		super("Dimenzije matrice moraju biti izmedju 7 i 10");
	}
	public InvalidDimensionException(String msg)
	{
		super(msg);
	}

}
