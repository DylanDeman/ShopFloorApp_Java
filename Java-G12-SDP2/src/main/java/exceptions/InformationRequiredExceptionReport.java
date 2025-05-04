package exceptions;

import java.util.Collections;
import java.util.Map;

import util.RequiredElementReport;

public class InformationRequiredExceptionReport extends Exception
{

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE = "Het rapport kan niet worden aangemaakt omdat niet alle info is ingevuld";

	private Map<String, RequiredElementReport> informationRequired;

	public InformationRequiredExceptionReport(Map<String, RequiredElementReport> itemsRequired)
	{
		super(MESSAGE);
		informationRequired = itemsRequired;
	}

	public Map<String, RequiredElementReport> getInformationRequired()
	{
		return Collections.unmodifiableMap(informationRequired);
	}

}
