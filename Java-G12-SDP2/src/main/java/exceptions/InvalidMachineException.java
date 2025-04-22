package exceptions;

public class InvalidMachineException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidMachineException(String message) {
		super(message);
	}

}
