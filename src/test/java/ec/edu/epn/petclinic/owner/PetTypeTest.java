package ec.edu.epn.petclinic.owner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PetTypeTest {

    @Test
    void shouldSetAndGetName() {
        PetType type = new PetType();
        type.setName("hamster");

        assertEquals("hamster", type.getName());
    }
}
