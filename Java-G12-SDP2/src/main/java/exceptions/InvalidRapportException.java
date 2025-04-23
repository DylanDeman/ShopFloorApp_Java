package exceptions;

public class InvalidRapportException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public InvalidRapportException(String message)
	{
		super(message);
	}
}
