package ec.edu.epn.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para PetController:
 * - Carga el contexto completo de Spring Boot
 * - Usa repositorios reales
 * - Ejecuta requests reales con MockMvc
 */
@SpringBootTest
@Transactional
@DisplayName("PetController Integration Tests")
class PetControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PetTypeRepository petTypeRepository;

    private Owner testOwner;

    private PetType catType;

    private PetType dogType;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Limpiar datos previos
        ownerRepository.deleteAll();
        petTypeRepository.deleteAll();

        // Crear tipos de mascotas
        catType = new PetType();
        catType.setName("cat");
        catType = petTypeRepository.save(catType);

        dogType = new PetType();
        dogType.setName("dog");
        dogType = petTypeRepository.save(dogType);

        // Crear propietario de prueba
        testOwner = new Owner();
        testOwner.setFirstName("John");
        testOwner.setLastName("Doe");
        testOwner.setAddress("123 Main St");
        testOwner.setCity("Springfield");
        testOwner.setTelephone("1234567890");
        testOwner = ownerRepository.save(testOwner);
    }


    @Test
    @DisplayName("POST /owners/{ownerId}/pets/new - Should reject when pet name is duplicate")
    void postNewPet_shouldReject_whenDuplicateName() throws Exception {
        // Arrange
        Integer ownerId = testOwner.getId();

        Pet existing = new Pet();
        existing.setName("Michi");
        existing.setType(catType);
        existing.setBirthDate(LocalDate.of(2020, 1, 1));
        testOwner.addPet(existing);
        ownerRepository.save(testOwner);

        // Act
        var result = mockMvc.perform(post("/owners/{ownerId}/pets/new", ownerId)
                        .param("name", "Michi")
                        .param("birthDate", "2020-01-01")
                        .param("type", "cat"));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeHasFieldErrors("pet", "name"));
    }

    @Test
    @DisplayName("POST /owners/{ownerId}/pets/new - Should reject when birth date is in future")
    void postNewPet_shouldReject_whenBirthDateInFuture() throws Exception {
        // Arrange
        Integer ownerId = testOwner.getId();
        LocalDate future = LocalDate.now().plusDays(2);

        // Act
        var result = mockMvc.perform(post("/owners/{ownerId}/pets/new", ownerId)
                        .param("name", "Michi")
                        .param("birthDate", future.toString())
                        .param("type", "cat"));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeHasFieldErrors("pet", "birthDate"));
    }

    @Test
    @DisplayName("POST /owners/{ownerId}/pets/new - Should save pet successfully and redirect when valid")
    void postNewPet_shouldSaveAndRedirect_whenValid() throws Exception {
        // Arrange
        Integer ownerId = testOwner.getId();
        int initialPetCount = testOwner.getPets().size();

        // Act
        var result = mockMvc.perform(post("/owners/{ownerId}/pets/new", ownerId)
                        .param("name", "Michi")
                        .param("birthDate", "2020-01-01")
                        .param("type", "cat"));

        // Assert
        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owners/" + ownerId));

        // Verify pet was added
        Owner updatedOwner = ownerRepository.findById(ownerId).orElse(null);
        assertNotNull(updatedOwner);
        assertEquals(initialPetCount + 1, updatedOwner.getPets().size());
    }

    @Test
    @DisplayName("POST /owners/{ownerId}/pets/{petId}/edit - Should reject when duplicate name with different pet id")
    void postEditPet_shouldReject_whenDuplicateNameWithDifferentId() throws Exception {
        // Arrange
        Integer ownerId = testOwner.getId();

        // Create first pet
        Pet firstPet = new Pet();
        firstPet.setName("Michi");
        firstPet.setType(catType);
        firstPet.setBirthDate(LocalDate.of(2020, 1, 1));
        testOwner.addPet(firstPet);
        testOwner = ownerRepository.save(testOwner);

        // Create second pet to edit - refresh testOwner to get the first pet with ID
        testOwner = ownerRepository.findById(ownerId).get();
        Pet secondPet = new Pet();
        secondPet.setName("Fluffy");
        secondPet.setType(catType);
        secondPet.setBirthDate(LocalDate.of(2021, 1, 1));
        testOwner.addPet(secondPet);
        testOwner = ownerRepository.save(testOwner);

        // Get the IDs of both pets
        Pet savedSecondPet = testOwner.getPet("Fluffy", false);
        Integer secondPetId = savedSecondPet.getId();

        // Act - Try to rename second pet to match first pet's name
        var result = mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", ownerId, secondPetId)
                        .param("id", String.valueOf(secondPetId))
                        .param("name", "Michi")
                        .param("birthDate", "2021-01-01")
                        .param("type", "cat"));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeHasFieldErrors("pet", "name"));
    }

    @Test
    @DisplayName("POST /owners/{ownerId}/pets/{petId}/edit - Should reject when birth date is in future")
    void postEditPet_shouldReject_whenBirthDateInFuture() throws Exception {
        // Arrange
        Integer ownerId = testOwner.getId();

        // Create and save a pet
        Pet pet = new Pet();
        pet.setName("Michi");
        pet.setType(catType);
        pet.setBirthDate(LocalDate.of(2020, 1, 1));
        testOwner.addPet(pet);
        testOwner = ownerRepository.save(testOwner);

        Pet savedPet = testOwner.getPet("Michi", false);
        Integer petId = savedPet.getId();

        LocalDate future = LocalDate.now().plusDays(5);

        // Act
        var result = mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", ownerId, petId)
                        .param("id", String.valueOf(petId))
                        .param("name", "Michi")
                        .param("birthDate", future.toString())
                        .param("type", "cat"));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeHasFieldErrors("pet", "birthDate"));
    }

    @Test
    @DisplayName("POST /owners/{ownerId}/pets/{petId}/edit - Should update pet successfully and redirect when valid")
    void postEditPet_shouldUpdateAndRedirect_whenValid() throws Exception {
        // Arrange
        Integer ownerId = testOwner.getId();

        // Create and save a pet
        Pet pet = new Pet();
        pet.setName("Michi");
        pet.setType(catType);
        pet.setBirthDate(LocalDate.of(2020, 1, 1));
        testOwner.addPet(pet);
        testOwner = ownerRepository.save(testOwner);

        Pet savedPet = testOwner.getPet("Michi", false);
        Integer petId = savedPet.getId();

        // Act - Update pet with new name, birthdate, and type
        var result = mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", ownerId, petId)
                        .param("id", String.valueOf(petId))
                        .param("name", "Fluffy")
                        .param("birthDate", "2019-06-15")
                        .param("type", "dog"));

        // Assert
        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owners/" + ownerId));

        // Verify pet was updated
        Owner updatedOwner = ownerRepository.findById(ownerId).orElse(null);
        assertNotNull(updatedOwner);
        Pet updatedPet = updatedOwner.getPet(petId);
        assertNotNull(updatedPet);
        assertEquals("Fluffy", updatedPet.getName());
        assertEquals(LocalDate.of(2019, 6, 15), updatedPet.getBirthDate());
        assertEquals("dog", updatedPet.getType().getName());
    }
}
