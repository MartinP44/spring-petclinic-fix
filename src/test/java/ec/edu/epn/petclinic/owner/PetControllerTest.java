package ec.edu.epn.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración tipo "web slice":
 * - Carga Spring MVC + el controlador
 * - Mockea dependencias (repos)
 * - Ejecuta requests reales con MockMvc
 */
@WebMvcTest(controllers = PetController.class)
@Import(PetTypeFormatter.class) // para que el binding de "type" por nombre funcione en los forms
class PetControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OwnerRepository owners;

    @MockBean
    PetTypeRepository types;

    private Owner mockOwnerFound(int ownerId) {
        Owner owner = mock(Owner.class);
        when(owners.findById(ownerId)).thenReturn(Optional.of(owner));
        return owner;
    }

    @Test
    void getNewPetForm_shouldReturnCreateOrUpdateView_andPopulateTypes() throws Exception {
        int ownerId = 1;

        mockOwnerFound(ownerId);

        PetType cat = new PetType(); cat.setName("cat");
        when(types.findPetTypes()).thenReturn(List.of(cat));

        mockMvc.perform(get("/owners/{ownerId}/pets/new", ownerId))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeExists("types"))
                .andExpect(model().attribute("types", hasSize(1)));

        verify(types).findPetTypes();
        verify(owners).findById(ownerId);
    }

    @Test
    void postNewPet_shouldReject_whenDuplicateName() throws Exception {
        int ownerId = 1;
        Owner owner = mockOwnerFound(ownerId);

        Pet existing = new Pet();
        existing.setName("Michi");

        // duplicate check uses owner.getPet(name, true)
        when(owner.getPet("Michi", true)).thenReturn(existing);

        PetType cat = new PetType(); cat.setName("cat");
        when(types.findPetTypes()).thenReturn(List.of(cat));

        mockMvc.perform(post("/owners/{ownerId}/pets/new", ownerId)
                        .param("name", "Michi")
                        .param("birthDate", "2020-01-01")
                        .param("type", "cat"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeHasFieldErrors("pet", "name"));

        verify(owners, never()).save(any());
    }

    @Test
    void postNewPet_shouldReject_whenBirthDateInFuture() throws Exception {
        int ownerId = 1;
        mockOwnerFound(ownerId);

        PetType cat = new PetType(); cat.setName("cat");
        when(types.findPetTypes()).thenReturn(List.of(cat));

        LocalDate future = LocalDate.now().plusDays(2);

        mockMvc.perform(post("/owners/{ownerId}/pets/new", ownerId)
                        .param("name", "Michi")
                        .param("birthDate", future.toString())
                        .param("type", "cat"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeHasFieldErrors("pet", "birthDate"));

        verify(owners, never()).save(any());
    }

    @Test
    void postNewPet_shouldSaveAndRedirect_whenValid() throws Exception {
        int ownerId = 1;
        Owner owner = mockOwnerFound(ownerId);

        // no duplicate
        when(owner.getPet("Michi", true)).thenReturn(null);

        PetType cat = new PetType(); cat.setName("cat");
        when(types.findPetTypes()).thenReturn(List.of(cat));

        mockMvc.perform(post("/owners/{ownerId}/pets/new", ownerId)
                        .param("name", "Michi")
                        .param("birthDate", "2020-01-01")
                        .param("type", "cat"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owners/" + ownerId));

        // se guarda owner (porque el controller hace owners.save(owner))
        verify(owners, times(1)).save(owner);

        // y se agrega el pet al owner
        verify(owner, times(1)).addPet(any(Pet.class));
    }

    @Test
    void postEditPet_shouldReject_whenDuplicateNameWithDifferentId() throws Exception {
        int ownerId = 1;
        int petId = 10;

        Owner owner = mockOwnerFound(ownerId);

        // findPet(...) cuando petId no es null hace owners.findById y luego owner.getPet(petId)
        Pet original = new Pet();
        original.setId(petId);
        when(owner.getPet(petId)).thenReturn(original);

        // En update: owner.getPet(name, false) devuelve "otro" pet con id distinto => duplicate
        Pet other = new Pet();
        other.setId(99);
        other.setName("Michi");
        when(owner.getPet("Michi", false)).thenReturn(other);

        PetType cat = new PetType(); cat.setName("cat");
        when(types.findPetTypes()).thenReturn(List.of(cat));

        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", ownerId, petId)
                        .param("id", String.valueOf(petId))
                        .param("name", "Michi")
                        .param("birthDate", "2020-01-01")
                        .param("type", "cat"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/createOrUpdatePetForm"))
                .andExpect(model().attributeHasFieldErrors("pet", "name"));

        verify(owners, never()).save(any());
    }
}
