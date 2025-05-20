package util;

public enum RequiredElementAddress implements RequiredElement {
	STREET_REQUIRED("Straat is verplicht!"), 
	NUMBER_REQUIRED("Huisnummer is verplicht!"),
	POSTAL_CODE_REQUIRED("Postcode is verplicht!"), 
	CITY_REQUIRED("Stadsnaam is verplicht!");

	private final String message;

	RequiredElementAddress(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
