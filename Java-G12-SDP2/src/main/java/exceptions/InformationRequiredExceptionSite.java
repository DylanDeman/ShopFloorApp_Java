package exceptions;

import java.util.Collections;
import java.util.Map;

import util.RequiredElementSite;

public class InformationRequiredExceptionSite extends Exception
{

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE = "De site kan niet worden aangemaakt omdat niet alle info is ingevuld";

	private Map<String, RequiredElementSite> informationRequired;

	public InformationRequiredExceptionSite(Map<String, RequiredElementSite> itemsRequired)
	{
		super(MESSAGE);
		informationRequired = itemsRequired;
	}

	public Map<String, RequiredElementSite> getInformationRequired()
	{
		return Collections.unmodifiableMap(informationRequired);
	}

}
