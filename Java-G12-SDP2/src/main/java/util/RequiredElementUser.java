package util;

public enum RequiredElementUser implements RequiredElement
{
	FIRST_NAME_REQUIRED("Voornaam is verplicht!"), 
	LAST_NAME_REQUIRED("Achternaam is verplicht!"), 
	EMAIL_REQUIRED("Email is verplicht!"), 
	BIRTH_DATE_REQUIRED("Geboortedatum is verplicht!"),
	ADDRESS_REQUIRED("Adres is verplicht!"),
	ROLE_REQUIRED("Rol is verplicht!"),
	STATUS_REQUIRED("Status is verplicht!");
	
	private final String message;

	RequiredElementUser(String message)
	{
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}
}
