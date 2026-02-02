package ec.edu.epn.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PetValidatorTest {

    private final PetValidator validator = new PetValidator();

    @Test
    void supports_shouldReturnTrueForPet() {
        assertTrue(validator.supports(Pet.class));
    }

    @Test
    void validate_shouldRejectAll_whenMissingNameTypeAndBirthDate() {
        Pet pet = new Pet(); // new pet, name null, type null, birthDate null
        Errors errors = new BeanPropertyBindingResult(pet, "pet");

        validator.validate(pet, errors);

        assertTrue(errors.hasFieldErrors("name"));
        assertTrue(errors.hasFieldErrors("type"));      // because isNew() and type is null
        assertTrue(errors.hasFieldErrors("birthDate"));
    }

    @Test
    void validate_shouldNotRejectType_whenPetIsNotNew() {
        Pet pet = new Pet();
        pet.setName("Michi");
        pet.setBirthDate(LocalDate.of(2020, 1, 1));
        pet.setId(10); // hace que pet.isNew() sea false en PetClinic (id != null)

        Errors errors = new BeanPropertyBindingResult(pet, "pet");
        validator.validate(pet, errors);

        assertFalse(errors.hasFieldErrors("name"));
        assertFalse(errors.hasFieldErrors("type")); // no se valida type si no es nuevo
        assertFalse(errors.hasFieldErrors("birthDate"));
    }

    @Test
    void validate_shouldPass_whenValidNewPet() {
        PetType type = new PetType();
        type.setName("cat");

        Pet pet = new Pet();
        pet.setName("Michi");
        pet.setBirthDate(LocalDate.of(2020, 1, 1));
        pet.setType(type);

        Errors errors = new BeanPropertyBindingResult(pet, "pet");
        validator.validate(pet, errors);

        assertFalse(errors.hasErrors());
    }
}
