package util;

import interfaces.RequiredElement;

public enum RequiredElementMachine implements RequiredElement
{
	CODE_REQUIRED("Code is verplicht"),
	MACHINESTATUS_REQUIRED("Machinestatus is verplicht!"), 
	PRODUCTIONSTATUS_REQUIRED("Productiestatus is verplicht!"),
	LOCATION_REQUIRED("Locatie is verplicht!"), 
	PRODUCTINFO_REQUIRED("Productinformatie is verplicht!"),
	SITE_REQUIRED("Site is verplicht!"),
	TECHNICIAN_REQUIRED("Technieker is verplicht!"), 
	LAST_MAINTENANCE_REQUIRED("Datum laatste onderhoud is verplicht!"), 
	FUTURE_MAINTENANCE_REQUIRED("Datum volgende onderhoud is verplicht!");
	
	private final String message;

	RequiredElementMachine(String message)
	{
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}
}
