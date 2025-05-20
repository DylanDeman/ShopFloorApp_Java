package exceptions;

import java.util.Map;

import util.RequiredElement;

public abstract class InformationRequired extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;
	
	public InformationRequired(String message) {
		super(message);
	}
	
	public abstract Map<String, RequiredElement> getRequiredElements();
}
