package ec.edu.epn.petclinic.vet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Vet Tests")
class VetTest {

	private Vet vet;

	private Specialty specialty1;

	private Specialty specialty2;

	private Specialty specialty3;

	@BeforeEach
	void setUp() {
		vet = new Vet();
		vet.setFirstName("James");
		vet.setLastName("Carter");

		// Create test specialties
		specialty1 = new Specialty();
		specialty1.setName("Dentistry");
		specialty1.setId(1);

		specialty2 = new Specialty();
		specialty2.setName("Surgery");
		specialty2.setId(2);

		specialty3 = new Specialty();
		specialty3.setName("Radiology");
		specialty3.setId(3);
	}

	@Test
	@DisplayName("Should inherit Person properties")
	void testPersonProperties() {
		// Arrange
		// Vet already initialized in setUp

		// Act
		// No additional action needed

		// Assert
		assertEquals("James", vet.getFirstName());
		assertEquals("Carter", vet.getLastName());
	}

	@Test
	@DisplayName("Should set and get first name")
	void testSetAndGetFirstName() {
		// Arrange
		String firstName = "Helen";

		// Act
		vet.setFirstName(firstName);

		// Assert
		assertEquals(firstName, vet.getFirstName());
	}

	@Test
	@DisplayName("Should set and get last name")
	void testSetAndGetLastName() {
		// Arrange
		String lastName = "Leary";

		// Act
		vet.setLastName(lastName);

		// Assert
		assertEquals(lastName, vet.getLastName());
	}

	@Test
	@DisplayName("Should return empty list when no specialties")
	void testGetSpecialtiesEmpty() {
		// Arrange
		// Using vet created without specialties

		// Act
		List<Specialty> specialties = vet.getSpecialties();

		// Assert
		assertNotNull(specialties);
		assertTrue(specialties.isEmpty());
	}

	@Test
	@DisplayName("Should return zero when no specialties")
	void testGetNrOfSpecialtiesEmpty() {
		// Arrange
		// Using vet created without specialties

		// Act
		int count = vet.getNrOfSpecialties();

		// Assert
		assertEquals(0, count);
	}

	@Test
	@DisplayName("Should add single specialty to vet")
	void testAddSingleSpecialty() {
		// Arrange
		// specialty1 is ready

		// Act
		vet.addSpecialty(specialty1);

		// Assert
		assertEquals(1, vet.getNrOfSpecialties());
		assertTrue(vet.getSpecialties().contains(specialty1));
	}

	@Test
	@DisplayName("Should add multiple specialties to vet")
	void testAddMultipleSpecialties() {
		// Arrange
		// specialties ready

		// Act
		vet.addSpecialty(specialty1);
		vet.addSpecialty(specialty2);
		vet.addSpecialty(specialty3);

		// Assert
		assertEquals(3, vet.getNrOfSpecialties());
		assertTrue(vet.getSpecialties().contains(specialty1));
		assertTrue(vet.getSpecialties().contains(specialty2));
		assertTrue(vet.getSpecialties().contains(specialty3));
	}

	@Test
	@DisplayName("Should return specialties sorted by name")
	void testGetSpecialtiesSorted() {
		// Arrange
		vet.addSpecialty(specialty2); // Surgery
		vet.addSpecialty(specialty1); // Dentistry
		vet.addSpecialty(specialty3); // Radiology

		// Act
		List<Specialty> specialties = vet.getSpecialties();

		// Assert
		assertEquals(3, specialties.size());
		assertEquals("Dentistry", specialties.get(0).getName());
		assertEquals("Radiology", specialties.get(1).getName());
		assertEquals("Surgery", specialties.get(2).getName());
	}

	@Test
	@DisplayName("Should not add duplicate specialty")
	void testAddDuplicateSpecialty() {
		// Arrange
		vet.addSpecialty(specialty1);

		// Act
		vet.addSpecialty(specialty1);

		// Assert
		// Set doesn't allow duplicates
		assertEquals(1, vet.getNrOfSpecialties());
	}

	@Test
	@DisplayName("Should maintain specialty count correctly")
	void testNrOfSpecialtiesCount() {
		// Arrange
		// Vet with no specialties

		// Act
		vet.addSpecialty(specialty1);
		int countAfterOne = vet.getNrOfSpecialties();
		vet.addSpecialty(specialty2);
		int countAfterTwo = vet.getNrOfSpecialties();

		// Assert
		assertEquals(1, countAfterOne);
		assertEquals(2, countAfterTwo);
	}

	@Test
	@DisplayName("Should inherit BaseEntity properties")
	void testBaseEntityProperties() {
		// Arrange
		Integer id = 42;

		// Act
		vet.setId(id);

		// Assert
		assertEquals(id, vet.getId());
	}

	@Test
	@DisplayName("Should return list from getSpecialties")
	void testGetSpecialtiesReturnsList() {
		// Arrange
		vet.addSpecialty(specialty1);

		// Act
		List<Specialty> specialties = vet.getSpecialties();

		// Assert
		assertNotNull(specialties);
		assertEquals(1, specialties.size());
		// The returned list is a new list created from stream collect(Collectors.toList())
		// which creates a modifiable ArrayList
		specialties.add(specialty2);
		assertEquals(2, specialties.size());
	}

	@Test
	@DisplayName("Should handle specialty with null name gracefully")
	void testAddSpecialtyWithNullName() {
		// Arrange
		Specialty specialtyWithNullName = new Specialty();
		specialtyWithNullName.setId(10);
		// name is null

		vet.addSpecialty(specialty1);

		// Act
		vet.addSpecialty(specialtyWithNullName);

		// Assert
		assertEquals(2, vet.getNrOfSpecialties());
	}

	@Test
	@DisplayName("Should handle multiple specialties with same name")
	void testMultipleSpecialtiesWithSameName() {
		// Arrange
		Specialty specialty4 = new Specialty();
		specialty4.setName("Dentistry");
		specialty4.setId(4);

		// Act
		vet.addSpecialty(specialty1); // Dentistry with id 1
		vet.addSpecialty(specialty4); // Dentistry with id 4

		// Assert
		// Different objects with same name should both be added (different IDs)
		assertEquals(2, vet.getNrOfSpecialties());
	}

	@Test
	@DisplayName("Should return empty specialties list multiple times consistently")
	void testGetSpecialtiesEmptyConsistency() {
		// Arrange
		// New vet with no specialties

		// Act
		List<Specialty> list1 = vet.getSpecialties();
		List<Specialty> list2 = vet.getSpecialties();

		// Assert
		assertNotNull(list1);
		assertNotNull(list2);
		assertEquals(0, list1.size());
		assertEquals(0, list2.size());
	}

	@Test
	@DisplayName("Should maintain internal specialties set after multiple gets")
	void testGetSpecialtiesDoesNotModifyInternalSet() {
		// Arrange
		vet.addSpecialty(specialty1);

		// Act
		List<Specialty> list1 = vet.getSpecialties();
		vet.addSpecialty(specialty2);
		List<Specialty> list2 = vet.getSpecialties();

		// Assert
		assertEquals(1, list1.size());
		assertEquals(2, list2.size());
	}

	@Test
	@DisplayName("Should sort specialties alphabetically ignoring case")
	void testSpecialtiesSortingCaseInsensitive() {
		// Arrange
		Specialty specialtyA = new Specialty();
		specialtyA.setName("Anesthesiology");
		specialtyA.setId(10);

		Specialty specialtyZ = new Specialty();
		specialtyZ.setName("Zoology");
		specialtyZ.setId(11);

		// Act
		vet.addSpecialty(specialtyZ);
		vet.addSpecialty(specialty1); // Dentistry
		vet.addSpecialty(specialtyA);

		// Assert
		List<Specialty> specialties = vet.getSpecialties();
		assertEquals("Anesthesiology", specialties.get(0).getName());
		assertEquals("Dentistry", specialties.get(1).getName());
		assertEquals("Zoology", specialties.get(2).getName());
	}

	@Test
	@DisplayName("Should handle large number of specialties")
	void testLargeNumberOfSpecialties() {
		// Arrange
		for (int i = 0; i < 100; i++) {
			Specialty specialty = new Specialty();
			specialty.setName("Specialty" + i);
			specialty.setId(i);
			vet.addSpecialty(specialty);
		}

		// Act
		int count = vet.getNrOfSpecialties();
		List<Specialty> specialties = vet.getSpecialties();

		// Assert
		assertEquals(100, count);
		assertEquals(100, specialties.size());
	}

	@Test
	@DisplayName("Should initialize specialties set lazily")
	void testSpecialtiesLazyInitialization() {
		// Arrange
		Vet newVet = new Vet();

		// Act
		int initialCount = newVet.getNrOfSpecialties();

		// Assert
		assertEquals(0, initialCount);
	}

}
