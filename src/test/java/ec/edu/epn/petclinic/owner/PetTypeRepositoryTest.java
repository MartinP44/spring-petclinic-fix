package ec.edu.epn.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("PetTypeRepository Tests")
class PetTypeRepositoryTest {

    @Autowired
    private PetTypeRepository petTypeRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @BeforeEach
    void setUp() {
        // Clean up before each test - must delete pets first due to foreign key constraint
        ownerRepository.deleteAll();
        petTypeRepository.deleteAll();
    }

    @Test
    @DisplayName("findPetTypes - Should return all pet types ordered by name in ascending order")
    void findPetTypes_shouldReturnTypesOrderedByName() {
        // Arrange
        PetType zebra = new PetType();
        zebra.setName("zebra");

        PetType ant = new PetType();
        ant.setName("ant");

        PetType monkey = new PetType();
        monkey.setName("monkey");

        petTypeRepository.saveAll(List.of(zebra, ant, monkey));

        // Act
        List<PetType> types = petTypeRepository.findPetTypes();

        // Assert
        assertTrue(types.size() >= 3, "Should have at least 3 pet types");

        List<String> names = types.stream().map(PetType::getName).toList();

        assertTrue(names.contains("ant"), "Should contain 'ant' type");
        assertTrue(names.contains("monkey"), "Should contain 'monkey' type");
        assertTrue(names.contains("zebra"), "Should contain 'zebra' type");

        // Verify ascending order
        int idxA = names.indexOf("ant");
        int idxM = names.indexOf("monkey");
        int idxZ = names.indexOf("zebra");

        assertTrue(idxA < idxM, "ant should appear before monkey");
        assertTrue(idxM < idxZ, "monkey should appear before zebra");
    }
}
