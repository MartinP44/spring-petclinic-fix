package ec.edu.epn.petclinic.owner;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
@DisplayName("VisitController Integration Tests")
class VisitControllerTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private PetTypeRepository petTypeRepository;

	private Owner owner;
	private Pet pet;
	private PetType petType;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

		ownerRepository.deleteAll();
		petTypeRepository.deleteAll();

		petType = new PetType();
		petType.setName("dog");
		petType = petTypeRepository.save(petType);

		owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("123 Main St");
		owner.setCity("Springfield");
		owner.setTelephone("1234567890");

		pet = new Pet();
		pet.setName("Fluffy");
		pet.setBirthDate(LocalDate.of(2020, 1, 1));
		pet.setType(petType);

		owner.addPet(pet);
		owner = ownerRepository.save(owner);
	}

	@Test
	@DisplayName("GET visit creation form")
	void testInitNewVisitForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new",
				owner.getId(), pet.getId()))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("visit"))
			.andExpect(model().attributeExists("pet"))
			.andExpect(model().attributeExists("owner"))
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	@DisplayName("POST visit success")
	void testProcessNewVisitFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new",
				owner.getId(), pet.getId())
			.param("description", "Annual checkup"))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("message", "Your visit has been booked"));

		Owner updated = ownerRepository.findById(owner.getId()).orElseThrow();
		assertEquals(1, updated.getPets().iterator().next().getVisits().size());
	}

	@Test
	@DisplayName("POST visit validation error")
	void testProcessNewVisitFormWithErrors() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new",
				owner.getId(), pet.getId())
			.param("description", ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("visit", "description"))
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	@DisplayName("Visit pre-created in model")
	void testVisitPreCreatedInModel() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new",
				owner.getId(), pet.getId()))
			.andExpect(status().isOk())
			.andExpect(model().attribute("visit",
				hasProperty("date", notNullValue())));
	}
}
