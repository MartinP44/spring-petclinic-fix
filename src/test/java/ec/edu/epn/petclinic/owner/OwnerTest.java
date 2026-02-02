package ec.edu.epn.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Owner Tests")
class OwnerTest {

	private Owner owner;

	private Pet pet1;

	private Pet pet2;

	private Pet pet3;

	@BeforeEach
	void setUp() {
		owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("123 Main St");
		owner.setCity("Springfield");
		owner.setTelephone("1234567890");

		// Create test pets
		pet1 = new Pet();
		pet1.setName("Fluffy");
		pet1.setId(1);
		pet1.setBirthDate(LocalDate.of(2020, 1, 15));

		pet2 = new Pet();
		pet2.setName("Rex");
		pet2.setId(2);
		pet2.setBirthDate(LocalDate.of(2019, 5, 20));

		pet3 = new Pet();
		pet3.setName("Whiskers");
		// pet3 has no ID (new pet)
		pet3.setBirthDate(LocalDate.of(2021, 3, 10));
	}

	@Test
	@DisplayName("Should set and get address")
	void testSetAndGetAddress() {
		// Arrange
		String address = "456 Oak Ave";

		// Act
		owner.setAddress(address);

		// Assert
		assertEquals(address, owner.getAddress());
	}

	@Test
	@DisplayName("Should set and get city")
	void testSetAndGetCity() {
		// Arrange
		String city = "New York";

		// Act
		owner.setCity(city);

		// Assert
		assertEquals(city, owner.getCity());
	}

	@Test
	@DisplayName("Should set and get telephone")
	void testSetAndGetTelephone() {
		// Arrange
		String telephone = "9876543210";

		// Act
		owner.setTelephone(telephone);

		// Assert
		assertEquals(telephone, owner.getTelephone());
	}

	@Test
	@DisplayName("Should return empty list when no pets")
	void testGetPetsEmpty() {
		// Arrange
		// Usando owner recién creado sin mascotas

		// Act
		List<Pet> pets = owner.getPets();

		// Assert
		assertNotNull(pets);
		assertTrue(pets.isEmpty());
	}

	@Test
	@DisplayName("Should add new pet to owner")
	void testAddNewPet() {
		// Arrange
		// pet3 es nuevo (sin ID)

		// Act
		owner.addPet(pet3);

		// Assert
		assertEquals(1, owner.getPets().size());
		assertTrue(owner.getPets().contains(pet3));
	}

	@Test
	@DisplayName("Should not add existing pet (with ID)")
	void testAddExistingPet() {
		// Arrange
		// pet1 tiene ID, por lo que no es nuevo

		// Act
		owner.addPet(pet1);

		// Assert
		assertEquals(0, owner.getPets().size());
	}

	@Test
	@DisplayName("Should add multiple new pets")
	void testAddMultipleNewPets() {
		// Arrange
		Pet newPet1 = new Pet();
		newPet1.setName("Buddy");
		Pet newPet2 = new Pet();
		newPet2.setName("Max");

		// Act
		owner.addPet(newPet1);
		owner.addPet(newPet2);

		// Assert
		assertEquals(2, owner.getPets().size());
	}

	@Test
	@DisplayName("Should get pet by name (case insensitive)")
	void testGetPetByName() {
		// Arrange
		owner.addPet(pet3);

		// Act
		Pet foundPet = owner.getPet("whiskers");

		// Assert
		assertNotNull(foundPet);
		assertEquals("Whiskers", foundPet.getName());
	}

	@Test
	@DisplayName("Should return null when pet name not found")
	void testGetPetByNameNotFound() {
		// Arrange
		owner.addPet(pet3);

		// Act
		Pet foundPet = owner.getPet("NonExistent");

		// Assert
		assertNull(foundPet);
	}

	@Test
	@DisplayName("Should get pet by name with exact case")
	void testGetPetByNameExactCase() {
		// Arrange
		owner.addPet(pet3);

		// Act
		Pet foundPet = owner.getPet("Whiskers");

		// Assert
		assertNotNull(foundPet);
		assertEquals(pet3, foundPet);
	}

	@Test
	@DisplayName("Should get pet by ID")
	void testGetPetById() {
		// Arrange
		owner.getPets().add(pet1);

		// Act
		Pet foundPet = owner.getPet(1);

		// Assert
		assertNotNull(foundPet);
		assertEquals(pet1, foundPet);
	}

	@Test
	@DisplayName("Should return null when pet ID not found")
	void testGetPetByIdNotFound() {
		// Arrange
		owner.getPets().add(pet1);

		// Act
		Pet foundPet = owner.getPet(999);

		// Assert
		assertNull(foundPet);
	}

	@Test
	@DisplayName("Should get pet by ID from multiple pets")
	void testGetPetByIdMultiplePets() {
		// Arrange
		owner.getPets().add(pet1);
		owner.getPets().add(pet2);

		// Act
		Pet foundPet = owner.getPet(2);

		// Assert
		assertNotNull(foundPet);
		assertEquals(pet2, foundPet);
		assertEquals("Rex", foundPet.getName());
	}

	@Test
	@DisplayName("Should not find new pet by ID (null ID)")
	void testGetPetByIdNewPet() {
		// Arrange
		owner.addPet(pet3);

		// Act
		Pet foundPet = owner.getPet((Integer) null);

		// Assert
		assertNull(foundPet);
	}

	@Test
	@DisplayName("Should get pet by name ignoring new pets")
	void testGetPetByNameIgnoreNew() {
		// Arrange
		owner.addPet(pet3);

		// Act
		Pet foundPet = owner.getPet("Whiskers", true);

		// Assert
		assertNull(foundPet);
	}

	@Test
	@DisplayName("Should get pet by name including new pets")
	void testGetPetByNameIncludeNew() {
		// Arrange
		owner.addPet(pet3);

		// Act
		Pet foundPet = owner.getPet("Whiskers", false);

		// Assert
		assertNotNull(foundPet);
		assertEquals(pet3, foundPet);
	}

	@Test
	@DisplayName("Should get existing pet by name even with ignoreNew true")
	void testGetPetByNameExistingPetIgnoreNew() {
		// Arrange
		owner.getPets().add(pet1);

		// Act
		Pet foundPet = owner.getPet("Fluffy", true);

		// Assert
		assertNotNull(foundPet);
		assertEquals(pet1, foundPet);
	}

	@Test
	@DisplayName("Should handle null pet name in getPet")
	void testGetPetWithNullName() {
		// Arrange
		owner.addPet(pet3);

		// Act
		Pet foundPet = owner.getPet((String) null);

		// Assert
		assertNull(foundPet);
	}

	@Test
	@DisplayName("Should handle pet with null name in collection")
	void testGetPetFromCollectionWithNullPetName() {
		// Arrange
		Pet petWithNoName = new Pet();
		owner.addPet(petWithNoName);

		// Act
		Pet foundPet = owner.getPet("SomeName");

		// Assert
		assertNull(foundPet);
	}

	@Test
	@DisplayName("Should generate correct toString")
	void testToString() {
		// Arrange
		owner.setId(123);

		// Act
		String result = owner.toString();
        System.out.println(result);

		// Assert
		assertTrue(result.contains("id = 123"));
		assertTrue(result.contains("lastName = 'Doe'"));
		assertTrue(result.contains("firstName = 'John'"));
		assertTrue(result.contains("address = '123 Main St'"));
		assertTrue(result.contains("city = 'Springfield'"));
		assertTrue(result.contains("telephone = '1234567890'"));
	}

	@Test
	@DisplayName("Should add visit to pet by pet ID")
	void testAddVisit() {
		// Arrange
		owner.getPets().add(pet1);
		Visit visit = new Visit();
		visit.setDescription("Annual checkup");
		visit.setDate(LocalDate.now());

		// Act
		owner.addVisit(1, visit);

		// Assert
		assertEquals(1, pet1.getVisits().size());
		assertTrue(pet1.getVisits().contains(visit));
	}

	@Test
	@DisplayName("Should throw exception when adding visit with null pet ID")
	void testAddVisitNullPetId() {
		// Arrange
		Visit visit = new Visit();
		visit.setDescription("Checkup");

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> owner.addVisit(null, visit));
	}

	@Test
	@DisplayName("Should throw exception when adding null visit")
	void testAddVisitNullVisit() {
		// Arrange
		owner.getPets().add(pet1);

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> owner.addVisit(1, null));
	}

	@Test
	@DisplayName("Should throw exception when adding visit to non-existent pet")
	void testAddVisitInvalidPetId() {
		// Arrange
		Visit visit = new Visit();
		visit.setDescription("Checkup");

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> owner.addVisit(999, visit));
	}

	@Test
	@DisplayName("Should add multiple visits to same pet")
	void testAddMultipleVisitsToSamePet() {
		// Arrange
		owner.getPets().add(pet1);
		Visit visit1 = new Visit();
		visit1.setDescription("Checkup 1");
		Visit visit2 = new Visit();
		visit2.setDescription("Checkup 2");

		// Act
		owner.addVisit(1, visit1);
		owner.addVisit(1, visit2);

		// Assert
		assertEquals(2, pet1.getVisits().size());
	}

	@Test
	@DisplayName("Should inherit Person properties")
	void testPersonProperties() {
		// Arrange
		// Dueño ya inicializado en setUp

		// Act
		// No hay necesidad de acción adicional

		// Assert
		assertEquals("John", owner.getFirstName());
		assertEquals("Doe", owner.getLastName());
	}

	@Test
	@DisplayName("Should inherit BaseEntity properties")
	void testBaseEntityProperties() {
		// Arrange
		// Dueño ya inicializado en setUp

		// Act
		boolean isNewBefore = owner.isNew();
		owner.setId(100);
		boolean isNewAfter = owner.isNew();

		// Assert
		assertTrue(isNewBefore);
		assertFalse(isNewAfter);
		assertEquals(100, owner.getId());
	}

	@Test
	@DisplayName("Should maintain pets list immutability reference")
	void testPetsListReference() {
		// Arrange
		// Dueño ya inicializado en setUp

		// Act
		List<Pet> pets1 = owner.getPets();
		List<Pet> pets2 = owner.getPets();

		// Assert
		assertSame(pets1, pets2);
	}

	@Test
	@DisplayName("Should handle getPet by name with mixed case")
	void testGetPetByNameMixedCase() {
		// Arrange
		owner.addPet(pet3);

		// Act
		Pet foundPet = owner.getPet("wHiSkErS");

		// Assert
		assertNotNull(foundPet);
		assertEquals("Whiskers", foundPet.getName());
	}

	@Test
	@DisplayName("Should return correct pet when multiple pets with same ID scenario")
	void testGetPetByIdWithMultiplePets() {
		// Arrange
		owner.getPets().add(pet1);
		owner.getPets().add(pet2);

		// Act
		Pet foundPet1 = owner.getPet(1);
		Pet foundPet2 = owner.getPet(2);

		// Assert
		assertEquals(pet1, foundPet1);
		assertEquals(pet2, foundPet2);
	}

	@Test
	@DisplayName("Should handle empty string pet name search")
	void testGetPetByEmptyName() {
		// Arrange
		Pet petWithEmptyName = new Pet();
		petWithEmptyName.setName("");
		owner.addPet(petWithEmptyName);

		// Act
		Pet foundPet = owner.getPet("");

		// Assert
		assertNotNull(foundPet);
		assertEquals("", foundPet.getName());
	}

	@Test
	@DisplayName("Should add visit to correct pet among multiple pets")
	void testAddVisitToCorrectPet() {
		// Arrange
		owner.getPets().add(pet1);
		owner.getPets().add(pet2);
		Visit visit = new Visit();
		visit.setDescription("Vaccination");

		// Act
		owner.addVisit(2, visit);

		// Assert
		assertEquals(0, pet1.getVisits().size());
		assertEquals(1, pet2.getVisits().size());
		assertTrue(pet2.getVisits().contains(visit));
	}

}
