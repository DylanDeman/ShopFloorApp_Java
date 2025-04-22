package domain.rapport;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import domain.User;
import domain.site.Site;

@ExtendWith(MockitoExtension.class)
class RapportTest
{

	private static final String VALID_RAPPORT_ID = "RAP123";
	private static final String VALID_ONDERHOUDS_NR = "OND456";
	private static final LocalDate VALID_START_DATE = LocalDate.of(2025, 4, 20);
	private static final LocalTime VALID_START_TIME = LocalTime.of(9, 0);
	private static final LocalDate VALID_END_DATE = LocalDate.of(2025, 4, 20);
	private static final LocalTime VALID_END_TIME = LocalTime.of(12, 0);
	private static final String VALID_REDEN = "Regulier onderhoud";
	private static final String VALID_OPMERKINGEN = "Alles in orde";

	@Mock
	private Site mockSite;

	@Mock
	private User mockTechnieker;

	private Rapport rapport;

	@BeforeEach
	void setUp()
	{
		rapport = new Rapport(VALID_RAPPORT_ID, mockSite, VALID_ONDERHOUDS_NR, mockTechnieker, VALID_START_DATE,
				VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_REDEN, VALID_OPMERKINGEN);
	}

	@Test
	void constructor_validParameters_createsRapport()
	{
		assertAll(() -> assertEquals(VALID_RAPPORT_ID, rapport.getRapportId()),
				() -> assertEquals(mockSite, rapport.getSite()),
				() -> assertEquals(VALID_ONDERHOUDS_NR, rapport.getOnderhoudsNr()),
				() -> assertEquals(mockTechnieker, rapport.getTechnieker()),
				() -> assertEquals(VALID_START_DATE, rapport.getStartDate()),
				() -> assertEquals(VALID_START_TIME, rapport.getStartTime()),
				() -> assertEquals(VALID_END_DATE, rapport.getEndDate()),
				() -> assertEquals(VALID_END_TIME, rapport.getEndTime()),
				() -> assertEquals(VALID_REDEN, rapport.getReden()),
				() -> assertEquals(VALID_OPMERKINGEN, rapport.getOpmerkingen()));
	}

	@Test
	void equalsAndHashCode_sameId_areEqual()
	{
		Rapport sameIdRapport = new Rapport(VALID_RAPPORT_ID, mock(Site.class), "Different", mock(User.class),
				LocalDate.now(), LocalTime.now(), LocalDate.now(), LocalTime.now(), "Different", "Different");

		assertEquals(rapport, sameIdRapport);
		assertEquals(rapport.hashCode(), sameIdRapport.hashCode());
	}

	@Test
	void equalsAndHashCode_differentId_notEqual()
	{

		Rapport differentIdRapport = new Rapport("DIFFERENT_ID", mockSite, VALID_ONDERHOUDS_NR, mockTechnieker,
				VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_REDEN, VALID_OPMERKINGEN);

		assertNotEquals(rapport, differentIdRapport);
		assertNotEquals(rapport.hashCode(), differentIdRapport.hashCode());
	}

	@Test
	void getters_returnCorrectValues()
	{
		assertEquals(VALID_RAPPORT_ID, rapport.getRapportId());
		assertEquals(mockSite, rapport.getSite());
		assertEquals(VALID_ONDERHOUDS_NR, rapport.getOnderhoudsNr());
		assertEquals(mockTechnieker, rapport.getTechnieker());
		assertEquals(VALID_START_DATE, rapport.getStartDate());
		assertEquals(VALID_START_TIME, rapport.getStartTime());
		assertEquals(VALID_END_DATE, rapport.getEndDate());
		assertEquals(VALID_END_TIME, rapport.getEndTime());
		assertEquals(VALID_REDEN, rapport.getReden());
		assertEquals(VALID_OPMERKINGEN, rapport.getOpmerkingen());
	}

}