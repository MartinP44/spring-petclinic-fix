package ec.edu.epn.petclinic.owner;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("Visit Tests")
class VisitTest {

	private Visit visit;

	@BeforeEach
	void setUp() {
		visit = new Visit();
		visit.setDescription("General checkup");
	}

	@Test
	@DisplayName("Should initialize visit with current date")
	void testDefaultConstructorSetsCurrentDate() {
		// Arrange
		LocalDate today = LocalDate.now();

		// Act
		LocalDate visitDate = visit.getDate();

		// Assert
		assertNotNull(visitDate);
		assertEquals(today, visitDate);
	}

	@Test
	@DisplayName("Should set and get visit date")
	void testSetAndGetDate() {
		// Arrange
		LocalDate date = LocalDate.of(2024, 5, 20);

		// Act
		visit.setDate(date);

		// Assert
		assertEquals(date, visit.getDate());
	}

	@Test
	@DisplayName("Should set and get description")
	void testSetAndGetDescription() {
		// Arrange
		String description = "Annual vaccination";

		// Act
		visit.setDescription(description);

		// Assert
		assertEquals(description, visit.getDescription());
	}

	@Test
	@DisplayName("Should allow description with normal text")
	void testDescriptionNotBlank() {
		// Arrange
		String description = "Dental check";

		// Act
		visit.setDescription(description);

		// Assert
		assertNotNull(visit.getDescription());
		assertFalse(visit.getDescription().isBlank());
	}

	@Test
	@DisplayName("Should allow empty description assignment (validation handled elsewhere)")
	void testSetEmptyDescription() {
		// Arrange
		String description = "";

		// Act
		visit.setDescription(description);

		// Assert
		assertEquals(description, visit.getDescription());
	}

	@Test
	@DisplayName("Should allow null description assignment (Bean Validation handles constraint)")
	void testSetNullDescription() {
		// Act
		visit.setDescription(null);

		// Assert
		assertNull(visit.getDescription());
	}

	@Test
	@DisplayName("Should inherit BaseEntity properties")
	void testBaseEntityProperties() {
		// Arrange
		boolean isNewBefore = visit.isNew();

		// Act
		visit.setId(50);
		boolean isNewAfter = visit.isNew();

		// Assert
		assertTrue(isNewBefore);
		assertFalse(isNewAfter);
		assertEquals(50, visit.getId());
	}

	@Test
    @DisplayName("Should generate non-null toString")
    void testToString() {
        // Arrange
        visit.setId(10);
        visit.setDescription("Emergency visit");

        // Act
        String result = visit.toString();
        System.out.println(result);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

	@Test
	@DisplayName("Should update visit date after initialization")
	void testUpdateDateAfterConstructor() {
		// Arrange
		LocalDate newDate = LocalDate.of(2023, 12, 1);

		// Act
		visit.setDate(newDate);

		// Assert
		assertEquals(newDate, visit.getDate());
	}

	@Test
	@DisplayName("Should handle multiple changes to description")
	void testMultipleDescriptionChanges() {
		// Arrange & Act
		visit.setDescription("Checkup");
		visit.setDescription("Vaccination");
		visit.setDescription("Surgery follow-up");

		// Assert
		assertEquals("Surgery follow-up", visit.getDescription());
	}

}
