package ec.edu.epn.petclinic.owner;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PetTest {

    @Test
    void shouldSetAndGetBirthDate() {
        Pet pet = new Pet();
        LocalDate date = LocalDate.of(2022, 5, 10);

        pet.setBirthDate(date);

        assertEquals(date, pet.getBirthDate());
    }

    @Test
    void shouldSetAndGetType() {
        Pet pet = new Pet();
        PetType type = new PetType();
        type.setName("dog");

        pet.setType(type);

        assertSame(type, pet.getType());
    }

    @Test
    void addVisit_shouldAddToVisitsCollection() {
        Pet pet = new Pet();
        Visit visit = mock(Visit.class);

        assertEquals(0, pet.getVisits().size());

        pet.addVisit(visit);

        assertEquals(1, pet.getVisits().size());
        assertTrue(pet.getVisits().contains(visit));
    }
}
