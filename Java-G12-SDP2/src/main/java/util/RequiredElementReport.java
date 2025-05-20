package util;

public enum RequiredElementReport implements RequiredElement
{
	MAINTENANCE_REQUIRED("Maintenance information is required"),
	TECHNICIAN_REQUIRED("Technician information is required"), 
	STARTDATE_REQUIRED("Start date is required"),
	STARTTIME_REQUIRED("Start time is required"), 
	ENDDATE_REQUIRED("End date is required"),
	ENDTIME_REQUIRED("End time is required"), 
	REASON_REQUIRED("Reason is required"),
	SITE_REQUIRED("Site information is required"), 
	END_DATE_BEFORE_START("End date cannot be before start date"),
	END_TIME_BEFORE_START("End time cannot be before start time on the same day");

	private final String message;

	RequiredElementReport(String message)
	{
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}
}