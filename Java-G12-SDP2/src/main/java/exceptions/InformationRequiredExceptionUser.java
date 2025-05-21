package exceptions;

import java.util.Collections;
import java.util.Map;

import util.RequiredElement;
import util.RequiredElementUser;

public class InformationRequiredExceptionUser extends InformationRequired
{

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE = "De gebruiker kan niet worden aangemaakt omdat niet alle info is ingevuld";

	private Map<String, RequiredElement> informationRequired;

	public InformationRequiredExceptionUser(Map<String, RequiredElement> itemsRequired)
	{
		super(MESSAGE);
		informationRequired = itemsRequired;
	}

	public Map<String, RequiredElement> getRequiredElements() 
	{
		return Collections.unmodifiableMap(informationRequired);
	}

}