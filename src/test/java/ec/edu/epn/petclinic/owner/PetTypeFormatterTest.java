package ec.edu.epn.petclinic.owner;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetTypeFormatterTest {

    @Test
    void print_shouldReturnName_whenNameIsNotNull() {
        PetTypeRepository repo = mock(PetTypeRepository.class);
        PetTypeFormatter formatter = new PetTypeFormatter(repo);

        PetType type = new PetType();
        type.setName("cat");

        assertEquals("cat", formatter.print(type, Locale.ENGLISH));
    }

    @Test
    void print_shouldReturnNullPlaceholder_whenNameIsNull() {
        PetTypeRepository repo = mock(PetTypeRepository.class);
        PetTypeFormatter formatter = new PetTypeFormatter(repo);

        PetType type = new PetType(); // name null
        assertEquals("<null>", formatter.print(type, Locale.ENGLISH));
    }

    @Test
    void parse_shouldReturnMatchingType_whenTextExists() throws Exception {
        PetTypeRepository repo = mock(PetTypeRepository.class);

        PetType cat = new PetType();
        cat.setName("cat");
        PetType dog = new PetType();
        dog.setName("dog");

        when(repo.findPetTypes()).thenReturn(List.of(cat, dog));

        PetTypeFormatter formatter = new PetTypeFormatter(repo);

        PetType parsed = formatter.parse("dog", Locale.ENGLISH);
        assertSame(dog, parsed);
        verify(repo).findPetTypes();
    }

    @Test
    void parse_shouldThrowParseException_whenTypeDoesNotExist() {
        PetTypeRepository repo = mock(PetTypeRepository.class);

        PetType cat = new PetType();
        cat.setName("cat");

        when(repo.findPetTypes()).thenReturn(List.of(cat));

        PetTypeFormatter formatter = new PetTypeFormatter(repo);

        ParseException ex = assertThrows(ParseException.class,
                () -> formatter.parse("lion", Locale.ENGLISH));

        assertTrue(ex.getMessage().contains("type not found"));
        verify(repo).findPetTypes();
    }
}
