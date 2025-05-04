package exceptions;

import java.util.Collections;
import java.util.Map;

import util.RequiredElementMachine;

public class InformationRequiredExceptionMachine extends Exception
{

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE = "De machine kan niet worden aangemaakt omdat niet alle info is ingevuld";

	private Map<String, RequiredElementMachine> informationRequired;

	public InformationRequiredExceptionMachine(Map<String, RequiredElementMachine> itemsRequired)
	{
		super(MESSAGE);
		informationRequired = itemsRequired;
	}

	public Map<String, RequiredElementMachine> getInformationRequired()
	{
		return Collections.unmodifiableMap(informationRequired);
	}

}
