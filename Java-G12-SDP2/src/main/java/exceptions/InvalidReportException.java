package exceptions;

public class InvalidReportException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public InvalidReportException(String message)
	{
		super(message);
	}
}
