package exceptions;

import java.util.Map;

import util.RequiredElementReport;

public class InformationRequiredExceptionReport extends IllegalArgumentException
{
	private static final long serialVersionUID = 1L;

	private Map<String, RequiredElementReport> missingElements;

	public InformationRequiredExceptionReport(Map<String, RequiredElementReport> missingElements)
	{
		super("Required information missing for report creation");
		this.missingElements = missingElements;
	}

	public Map<String, RequiredElementReport> getMissingElements()
	{
		return missingElements;
	}
}