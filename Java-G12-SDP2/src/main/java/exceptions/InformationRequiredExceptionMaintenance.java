package exceptions;

import java.util.Collections;
import java.util.Map;

import util.RequiredElementMaintenance;

public class InformationRequiredExceptionMaintenance extends Exception {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE = "Het onderhoud kan niet worden ingepland omdat niet alle info is ingevuld";

	private Map<String, RequiredElementMaintenance> informationRequired;

	public InformationRequiredExceptionMaintenance(Map<String, RequiredElementMaintenance> itemsRequired)
	{
		super(MESSAGE);
		informationRequired = itemsRequired;
	}

	public Map<String, RequiredElementMaintenance> getInformationRequired()
	{
		return Collections.unmodifiableMap(informationRequired);
	}

}
