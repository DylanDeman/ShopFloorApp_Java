package util;

import interfaces.RequiredElement;

public enum RequiredElementSite implements RequiredElement
{
	SITE_NAME_REQUIRED("Sitenaam is verplicht!"),
	EMPLOYEE_REQUIRED("Verantwoordelijke is verplicht!"),
	ADDRESS_REQUIRED("Adres is verplicht!"), 
	STATUS_REQUIRED("Status is verplicht!");
	
	private final String message;

	RequiredElementSite(String message)
	{
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}
}
