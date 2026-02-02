package ec.edu.epn.petclinic.vet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Vets Tests")
class VetsTest {

	private Vets vets;

	private Vet vet1;

	private Vet vet2;

	private Vet vet3;

	@BeforeEach
	void setUp() {
		vets = new Vets();

		// Create test vets
		vet1 = new Vet();
		vet1.setFirstName("James");
		vet1.setLastName("Carter");
		vet1.setId(1);

		vet2 = new Vet();
		vet2.setFirstName("Helen");
		vet2.setLastName("Leary");
		vet2.setId(2);

		vet3 = new Vet();
		vet3.setFirstName("Linda");
		vet3.setLastName("Douglas");
		vet3.setId(3);
	}

	@Test
	@DisplayName("Should return empty list when no vets added")
	void testGetVetListEmpty() {
		// Arrange
		// Using vets created without any entries

		// Act
		List<Vet> vetList = vets.getVetList();

		// Assert
		assertNotNull(vetList);
		assertTrue(vetList.isEmpty());
	}

	@Test
	@DisplayName("Should initialize vet list lazily")
	void testLazyInitialization() {
		// Arrange
		Vets newVets = new Vets();

		// Act
		List<Vet> vetList = newVets.getVetList();

		// Assert
		assertNotNull(vetList);
		assertEquals(0, vetList.size());
	}

	@Test
	@DisplayName("Should add single vet to list")
	void testAddSingleVet() {
		// Arrange
		// vet1 is ready

		// Act
		vets.getVetList().add(vet1);

		// Assert
		assertEquals(1, vets.getVetList().size());
		assertTrue(vets.getVetList().contains(vet1));
	}

	@Test
	@DisplayName("Should add multiple vets to list")
	void testAddMultipleVets() {
		// Arrange
		// vets are ready

		// Act
		vets.getVetList().add(vet1);
		vets.getVetList().add(vet2);
		vets.getVetList().add(vet3);

		// Assert
		assertEquals(3, vets.getVetList().size());
		assertTrue(vets.getVetList().contains(vet1));
		assertTrue(vets.getVetList().contains(vet2));
		assertTrue(vets.getVetList().contains(vet3));
	}

	@Test
	@DisplayName("Should maintain insertion order")
	void testInsertionOrder() {
		// Arrange
		// vets are ready

		// Act
		vets.getVetList().add(vet2);
		vets.getVetList().add(vet1);
		vets.getVetList().add(vet3);

		// Assert
		List<Vet> vetList = vets.getVetList();
		assertEquals(vet2, vetList.get(0));
		assertEquals(vet1, vetList.get(1));
		assertEquals(vet3, vetList.get(2));
	}

	@Test
	@DisplayName("Should allow duplicate vets")
	void testAddDuplicateVet() {
		// Arrange
		vets.getVetList().add(vet1);

		// Act
		vets.getVetList().add(vet1);

		// Assert
		// ArrayList allows duplicates
		assertEquals(2, vets.getVetList().size());
	}

	@Test
	@DisplayName("Should return same list reference on multiple calls")
	void testGetVetListReference() {
		// Arrange
		vets.getVetList().add(vet1);

		// Act
		List<Vet> list1 = vets.getVetList();
		List<Vet> list2 = vets.getVetList();

		// Assert
		assertSame(list1, list2);
		assertEquals(1, list1.size());
		assertEquals(1, list2.size());
	}

	@Test
	@DisplayName("Should support list modification operations")
	void testListModificationOperations() {
		// Arrange
		vets.getVetList().add(vet1);
		vets.getVetList().add(vet2);
		vets.getVetList().add(vet3);

		// Act
		vets.getVetList().remove(vet2);

		// Assert
		assertEquals(2, vets.getVetList().size());
		assertFalse(vets.getVetList().contains(vet2));
	}

	@Test
	@DisplayName("Should support clearing the vet list")
	void testClearVetList() {
		// Arrange
		vets.getVetList().add(vet1);
		vets.getVetList().add(vet2);

		// Act
		vets.getVetList().clear();

		// Assert
		assertEquals(0, vets.getVetList().size());
		assertTrue(vets.getVetList().isEmpty());
	}

	@Test
	@DisplayName("Should handle vets with specialties")
	void testVetsWithSpecialties() {
		// Arrange
		Specialty specialty = new Specialty();
		specialty.setName("Surgery");
		specialty.setId(1);
		vet1.addSpecialty(specialty);

		// Act
		vets.getVetList().add(vet1);

		// Assert
		assertEquals(1, vets.getVetList().size());
		Vet retrievedVet = vets.getVetList().get(0);
		assertEquals(1, retrievedVet.getNrOfSpecialties());
	}

	@Test
	@DisplayName("Should handle large number of vets")
	void testLargeNumberOfVets() {
		// Arrange
		for (int i = 0; i < 100; i++) {
			Vet vet = new Vet();
			vet.setFirstName("Vet" + i);
			vet.setLastName("LastName" + i);
			vet.setId(i);
			vets.getVetList().add(vet);
		}

		// Act
		int count = vets.getVetList().size();

		// Assert
		assertEquals(100, count);
	}

	@Test
	@DisplayName("Should support addAll operation")
	void testAddAllVets() {
		// Arrange
		List<Vet> additionalVets = List.of(vet1, vet2, vet3);

		// Act
		vets.getVetList().addAll(additionalVets);

		// Assert
		assertEquals(3, vets.getVetList().size());
	}

	@Test
	@DisplayName("Should allow null elements in list")
	void testNullElementsInList() {
		// Arrange
		// ArrayList allows null elements

		// Act
		vets.getVetList().add(vet1);
		vets.getVetList().add(null);
		vets.getVetList().add(vet2);

		// Assert
		assertEquals(3, vets.getVetList().size());
		assertNull(vets.getVetList().get(1));
	}

	@Test
	@DisplayName("Should support indexed access")
	void testIndexedAccess() {
		// Arrange
		vets.getVetList().add(vet1);
		vets.getVetList().add(vet2);
		vets.getVetList().add(vet3);

		// Act
		Vet retrievedVet = vets.getVetList().get(1);

		// Assert
		assertEquals(vet2, retrievedVet);
		assertEquals("Helen", retrievedVet.getFirstName());
	}

	@Test
	@DisplayName("Should support set operation")
	void testSetOperation() {
		// Arrange
		vets.getVetList().add(vet1);
		vets.getVetList().add(vet2);

		// Act
		Vet oldVet = vets.getVetList().set(1, vet3);

		// Assert
		assertEquals(vet2, oldVet);
		assertEquals(vet3, vets.getVetList().get(1));
		assertEquals(2, vets.getVetList().size());
	}

	@Test
	@DisplayName("Should support indexOf operation")
	void testIndexOfOperation() {
		// Arrange
		vets.getVetList().add(vet1);
		vets.getVetList().add(vet2);
		vets.getVetList().add(vet3);

		// Act
		int index = vets.getVetList().indexOf(vet2);

		// Assert
		assertEquals(1, index);
	}

	@Test
	@DisplayName("Should return consistent empty list")
	void testConsistentEmptyList() {
		// Arrange
		Vets newVets = new Vets();

		// Act
		List<Vet> list1 = newVets.getVetList();
		List<Vet> list2 = newVets.getVetList();

		// Assert
		assertSame(list1, list2);
		assertEquals(0, list1.size());
	}

}
