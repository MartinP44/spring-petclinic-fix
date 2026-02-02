package ec.edu.epn.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PetTypeRepositoryTest {

    @Autowired
    private PetTypeRepository petTypeRepository;

    @Test
    void findPetTypes_shouldReturnTypesOrderedByName() {
        PetType z = new PetType(); z.setName("zebra");
        PetType a = new PetType(); a.setName("ant");
        PetType m = new PetType(); m.setName("monkey");

        petTypeRepository.saveAll(List.of(z, a, m));

        List<PetType> types = petTypeRepository.findPetTypes();

        assertTrue(types.size() >= 3);

        // Tomamos los 3 últimos insertados (porque pueden existir seed-data según tu proyecto)
        List<String> names = types.stream().map(PetType::getName).toList();

        // Verificamos que contenga los nuestros
        assertTrue(names.contains("ant"));
        assertTrue(names.contains("monkey"));
        assertTrue(names.contains("zebra"));

        // Verificamos orden global (ascendente) para nuestra secuencia dentro de la lista
        int idxA = names.indexOf("ant");
        int idxM = names.indexOf("monkey");
        int idxZ = names.indexOf("zebra");

        assertTrue(idxA < idxM);
        assertTrue(idxM < idxZ);
    }
}
