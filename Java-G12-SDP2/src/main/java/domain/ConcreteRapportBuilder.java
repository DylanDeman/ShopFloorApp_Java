package domain;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Concrete implementation of the RapportBuilder interface. Builds and assembles
 * the parts of the Rapport object.
 */
public class ConcreteRapportBuilder implements RapportBuilder
{
	private String rapportId;
	private Site site;
	private String onderhoudsNr;
	private User technieker;
	private LocalDate startDate;
	private LocalTime startTime;
	private LocalDate endDate;
	private LocalTime endTime;
	private String reden;
	private String opmerkingen;

	public ConcreteRapportBuilder(String rapportId)
	{
		this.rapportId = rapportId;
	}

	@Override
	public RapportBuilder setSite(Site site)
	{
		this.site = site;
		return this;
	}

	@Override
	public RapportBuilder setOnderhoudsNr(String onderhoudsNr)
	{
		this.onderhoudsNr = onderhoudsNr;
		return this;
	}

	@Override
	public RapportBuilder setTechnieker(User technieker)
	{
		this.technieker = technieker;
		return this;
	}

	@Override
	public RapportBuilder setStartDate(LocalDate startDate)
	{
		this.startDate = startDate;
		return this;
	}

	@Override
	public RapportBuilder setStartTime(LocalTime startTime)
	{
		this.startTime = startTime;
		return this;
	}

	@Override
	public RapportBuilder setEndDate(LocalDate endDate)
	{
		this.endDate = endDate;
		return this;
	}

	@Override
	public RapportBuilder setEndTime(LocalTime endTime)
	{
		this.endTime = endTime;
		return this;
	}

	@Override
	public RapportBuilder setReden(String reden)
	{
		this.reden = reden;
		return this;
	}

	@Override
	public RapportBuilder setOpmerkingen(String opmerkingen)
	{
		this.opmerkingen = opmerkingen;
		return this;
	}

	@Override
	public Rapport build()
	{
		validateFields();

		// Create Rapport instance using the package-private constructor
		return new Rapport(rapportId, site, onderhoudsNr, technieker, startDate, startTime, endDate, endTime, reden,
				opmerkingen);
	}

	private void validateFields()
	{
		if (site == null)
		{
			throw new IllegalStateException("Site cannot be null");
		}
		if (onderhoudsNr == null || onderhoudsNr.trim().isEmpty())
		{
			throw new IllegalStateException("OnderhoudsNr cannot be null or empty");
		}
		if (technieker == null)
		{
			throw new IllegalStateException("Technieker cannot be null");
		}
		if (startDate == null)
		{
			throw new IllegalStateException("StartDate cannot be null");
		}
		if (startTime == null)
		{
			throw new IllegalStateException("StartTime cannot be null");
		}
		if (endDate == null)
		{
			throw new IllegalStateException("EndDate cannot be null");
		}
		if (endTime == null)
		{
			throw new IllegalStateException("EndTime cannot be null");
		}
		if (reden == null || reden.trim().isEmpty())
		{
			throw new IllegalStateException("Reden cannot be null or empty");
		}

		// Additional validation logic
		if (endDate.isBefore(startDate))
		{
			throw new IllegalStateException("End date cannot be before start date");
		}
		if (endDate.isEqual(startDate) && endTime.isBefore(startTime))
		{
			throw new IllegalStateException("End time cannot be before start time on the same day");
		}
	}
}